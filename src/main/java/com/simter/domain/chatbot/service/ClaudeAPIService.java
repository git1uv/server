package com.simter.domain.chatbot.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.chatbot.converter.ChatbotConverter;
import com.simter.domain.chatbot.dto.ClaudeRequestDto;
import com.simter.domain.chatbot.dto.ClaudeResponseDto;
import com.simter.domain.chatbot.dto.CounselingResponseDto;
import com.simter.domain.chatbot.entity.ChatbotMessage;
import com.simter.domain.chatbot.entity.CounselingLog;
import com.simter.domain.chatbot.entity.Solution;
import com.simter.domain.chatbot.repository.ChatbotRepository;
import com.simter.domain.chatbot.repository.CounselingLogRepository;
import com.simter.domain.chatbot.repository.SolutionRepository;
import com.simter.domain.mail.converter.MailConverter;
import com.simter.domain.mail.entity.Mail;
import com.simter.domain.mail.repository.MailRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaudeAPIService {

    private final MailConverter mailConverter;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private Dotenv dotenv = Dotenv.load();
    private WebClient webClient = WebClient.builder().build();
    private String API_KEY = dotenv.get("CLAUDE_API_KEY");
    private final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private final ChatbotRepository chatbotRepository;
    private final CounselingLogRepository counselingLogRepository;
    private final SolutionRepository solutionRepository;

    private static final String OVERALL_SUMMARY = "전체 요약:";
    private static final String USER_SUMMARY = "사용자 요약:";
    private static final String CLAUDE_SUMMARY = "Claude 요약:";
    private static final String SUGGESTED_ACTION = "추천 행동:";
    private static final String LETTER = "편지:";

    private static final int OVERALL_SUMMARY_OFFSET = OVERALL_SUMMARY.length();
    private static final int USER_SUMMARY_OFFSET = USER_SUMMARY.length();
    private static final int CLAUDE_SUMMARY_OFFSET = CLAUDE_SUMMARY.length();
    private static final int SUGGESTED_ACTION_OFFSET = SUGGESTED_ACTION.length();
    private static final int LETTER_OFFSET = LETTER.length();

    // Claude API를 호출
    private Mono<String> callClaudeAPI(String systemPrompt, String conversationContext, int maxTokens) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-5-sonnet-20240620");
        requestBody.put("max_tokens", maxTokens);

        Map<String, Object> userMessageContent = new HashMap<>();
        userMessageContent.put("role", "user");
        userMessageContent.put("content", conversationContext);

        requestBody.put("messages", new Object[]{userMessageContent});
        requestBody.put("system", systemPrompt + "\n" + conversationContext);

        // Claude API 호출
        return webClient.post()
                .uri(CLAUDE_API_URL)
                .header("x-api-key", API_KEY)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    // Claude 채팅 서비스
    @Transactional
    public Mono<ClaudeResponseDto> chatWithClaude(ClaudeRequestDto request, Long counselingLogId) {
        CounselingLog counselingLog = counselingLogRepository.findById(counselingLogId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHATBOT_SESSION_NOT_FOUND));
        String chatbotType = counselingLog.getChatbotType();

        //프롬프트 선택
        String systemPrompt = selectSystemPrompt(chatbotType);
        systemPrompt += "사용자의 대화를 읽고 9개의 감정 중 하나를 반환해준 뒤 대화를 해줘: 평온, 웃음, 사랑, 놀람, 슬픔, 불편, 화남, 불안, 피곤, 답변은 600자 이내로 해줘";

        // 이전 대화 내역 가져오기
        String previousMessages = getPreviousUserMessages(counselingLogId);
        String conversationContext = previousMessages + "과거의 대화를 알려줄게! 이 맥락을 기억해 \nUser: " + request.getUserMessage() + "\nAssistant:";

        // Claude API 호출
        return callClaudeAPI(systemPrompt, conversationContext, 1024)
                .map(response -> {
                    // 응답을 JSON 파싱
                    String assistantResponseText = new JSONObject(response)
                            .getJSONArray("content")
                            .getJSONObject(0)
                            .getString("text");

                    String emotion = extractEmotion(assistantResponseText);
                    String messageWithoutEmotion = removeEmotionLine(assistantResponseText);

                    // 사용자 메시지와 챗봇 응답을 DB에 저장
                    ChatbotMessage userMessage = ChatbotConverter.toUserMessage(counselingLog, request);
                    chatbotRepository.save(userMessage);

                    ChatbotMessage assistantMessage = ChatbotConverter.toAssistantMessage(counselingLog, messageWithoutEmotion, emotion);
                    chatbotRepository.save(assistantMessage);

                    return ChatbotConverter.toClaudeResponseDto(assistantMessage);
                });
    }

    // 챗봇 대화 종료 서비스 (종료 요청 시 상담일지 내용 생성)
    @Transactional
    public Mono<CounselingResponseDto.CounselingDto> summarizeConversation(Long counselingLogId) {
        Member member = counselingLogRepository.findById(counselingLogId).get().getUser();
        member.setMailAlert(true);
        memberRepository.save(member);
        // 이전 대화 내역 가져오기
        String previousMessages = getPreviousUserMessages(counselingLogId);
        String conversationContext = previousMessages
                + "사용자와 챗봇이 각각 말한 부분을 따로 요약해주고, 사용자가 해야 할 3가지 행동을 추천해줘. "
                + "그리고 최대 176로 이 서비스에 다시 올 사용자를 위해 고민하던 것들은 사라졌는지, 해결되었는지, 또는 계속 고민 중인지 걱정하면서 더 이야기할 것들은 없는지 확인하는 편지를 써줘"
                + "답변 형식은 전체 요약 : ~. 사용자 요약 : ~, Claude 요약 : ~ 추천 행동 : ~, 편지 : ~, 전체 요약는 20자, 사용자 요약, Claude 요약은 300자 이내로, 추천 행동은 각각 50자 이내로, 편지는 176자 이내로 해줘";

        // 프롬프트 작성
        String systemPrompt = "너의 임무는 아래 대화를 요약해서 사용자에게 보여주는 것이다. " +
                "사용자가 말한 내용과 챗봇이 답변한 내용을 각각 300자 이내로 요약하고, 사용자가 하면 좋을 것 같은 행동 3가지,편지를 만들어줘.";

        // Claude API 호출 및 상담 로그 업데이트
        return callClaudeAPI(systemPrompt, conversationContext, 1024)
                .flatMap(response -> {
                    // 응답을 JSON 파싱
                    String assistantResponseText = new JSONObject(response)
                            .getJSONArray("content")
                            .getJSONObject(0)
                            .getString("text");

                    // Claude의 응답에서 title, summary, suggestion 추출
                    String title = extractTitle(assistantResponseText);
                    String userSummary = extractUserSummary(assistantResponseText); // 사용자 요약 추출
                    String assistantSummary = extractAssistantSummary(assistantResponseText); // 챗봇 요약 추출
                    extractLetter(assistantResponseText, counselingLogRepository.findById(counselingLogId).get().getUser(), counselingLogRepository.findById(counselingLogId).get().getChatbotType());
                    log.info("assistantResponseText : {} ", assistantResponseText);

                    CounselingLog existingLog = counselingLogRepository.findById(counselingLogId)
                            .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHATBOT_SESSION_NOT_FOUND));

                    CounselingLog updatedLog = ChatbotConverter.updateCounselingLog(existingLog, title, userSummary, assistantSummary);
                    counselingLogRepository.save(updatedLog);
                    List<String> suggestedActions = extractSuggestedActions(assistantResponseText, updatedLog);
                    log.info("suggestedActions : {}", suggestedActions);

                    List<Solution> savedSolutions = solutionRepository.findAllByCounselingLogId(updatedLog.getId());
                    return Mono.just(ChatbotConverter.toCounselingDto(updatedLog,savedSolutions));
                });
    }

    // chatbot_type에 맞는 프롬프트 선택
    private String selectSystemPrompt(String chatbotType) {
        switch (chatbotType) {
            case "F":
                return "Your task is to generate a personalized motivational message or affirmation based on the user’s input. Address their specific needs and offer encouragement, support, and guidance. Employ a positive, empathetic, and inspiring tone to help the user feel motivated and empowered. Use relevant examples, analogies, or quotes to reinforce your message and make it more impactful. Ensure that the message is concise, authentic, and easy to understand. ";
            case "T":
                return "Your task is to create a personalized motivational message or affirmation based on the user's input. In addition to addressing their specific needs, offer realistic advice that aligns with their current situation. Encourage and support them while providing practical steps they can take to improve or move forward. Use a positive, empathetic, and inspiring tone, while also incorporating real-life examples or experiences to ground the advice. Make the message concise, authentic, and easy to understand without listing solutions in a numbered format.";
            case "H":
            default:
                return "Your task is to create a personalized motivational message based on the user’s input. Start by empathizing with their situation, acknowledging their feelings, and offering emotional support. Then, gently introduce realistic advice, offering guidance or practical steps that could help them improve their circumstances. Use a positive and inspiring tone while blending emotional support with constructive suggestions. Incorporate relatable examples to make your message impactful, but avoid listing solutions in a numbered format to ensure the advice feels more conversational and natural.";
        }
    }

    // 이전 사용자 메시지 가져오기
    private String getPreviousUserMessages(Long counselingLogId) {
        List<ChatbotMessage> previousMessages = chatbotRepository.findByCounselingLogId(counselingLogId);
        return previousMessages.stream()
                .map(message -> message.getSender() + ": " + message.getContent())
                .collect(Collectors.joining("\n"));
    }

    // Claude 응답에서 title 추출
    private String extractTitle(String response) {
        int startIndex = response.indexOf(OVERALL_SUMMARY) + OVERALL_SUMMARY_OFFSET;
        int endIndex = response.indexOf("\n", startIndex);
        return response.substring(startIndex, endIndex).trim();
    }

    // Claude 응답에서 사용자 발언 요약 부분 추출
    private String extractUserSummary(String response) {
        int startIndex = response.indexOf(USER_SUMMARY) + USER_SUMMARY_OFFSET;
        int endIndex = response.indexOf("\n", startIndex);
        return response.substring(startIndex, endIndex).trim();
    }

    // Claude 응답에서 Claude 발언 요약 부분 추출
    private String extractAssistantSummary(String response) {
        int startIndex = response.indexOf(CLAUDE_SUMMARY) + CLAUDE_SUMMARY_OFFSET;
        int endIndex = response.indexOf("\n", startIndex);
        return response.substring(startIndex, endIndex).trim();
    }

    // Claude 응답에서 추천 행동 3가지를 추출
    private List<String> extractSuggestedActions(String response, CounselingLog counselingLog) {
        List<String> actions = new ArrayList<>();
        int START_INDEX = response.indexOf(SUGGESTED_ACTION) + SUGGESTED_ACTION_OFFSET;
        if (START_INDEX == -1) return actions;
        String actionsText = response.substring(START_INDEX).trim();

        // 각 줄을 개행(\n) 기준으로 분리
        String[] actionLines = actionsText.split("\\n");

        // 각 행동을 처리
        for (String line : actionLines) {
            // 숫자로 시작하는 포맷 (예: "1. 가벼운 스트레칭" 같은 형식) 처리
            if (line.matches("\\d+\\.\\s.*")) {
                String action = line.substring(line.indexOf(". ") + 2).trim(); // 숫자와 마침표, 공백 제거 후 추출
                log.info("action : {}", action);
                Solution solution = ChatbotConverter.createSolution(counselingLog, action);
                solutionRepository.save(solution);

                if (!action.isEmpty()) {
                    actions.add(action);  // 비어있지 않으면 리스트에 추가
                }
            }
            if (actions.size() == 3) break; // 최대 3개만 추가
        }
        return actions;
    }

    // Claude 응답에서 편지 내용 추출해서 DB에 저장
    public String extractLetter(String response, Member member, String chatbotType) {
        int startIndex = response.indexOf(LETTER) + LETTER_OFFSET;
        int endIndex = response.length();
        String mailContent = response.substring(startIndex, endIndex).trim();
        log.info("mailContent : {}", mailContent);
        Mail mail = MailConverter.toMailEntity(member, mailContent, chatbotType);
        LocalDateTime randomTime = generateRandomTime();
        mail.setCreatedAt(randomTime);
        return null;
    }

    private LocalDateTime generateRandomTime() {
        long minSeconds = 5 * 60 * 60;  //5시간
        long maxSeconds = 24 * 60 * 60; //24시간
        long randomSeconds = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds);
        return LocalDateTime.now().plusSeconds(randomSeconds);
    }

    private String extractEmotion(String response) {
        List<String> emotions = Arrays.asList("평온", "슬픔", "웃음", "사람", "놀람", "피곤", "화남", "불안");
        return emotions.stream()
                .filter(emotion -> response.contains(emotion))
                .findFirst()
                .orElse("평온");
    }

    // 감정 라인을 제거한 본문 추출
    private String removeEmotionLine(String response) {
        int startOfMessage = response.indexOf("\n\n") + 2;
        return response.substring(startOfMessage).trim();
    }
}
