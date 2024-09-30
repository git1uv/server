package com.simter.domain.chatbot.service;

import static java.lang.System.getenv;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.calendar.converter.CalendarsConverter;
import com.simter.domain.calendar.entity.Calendars;
import com.simter.domain.calendar.repository.CalendarsRepository;
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
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaudeAPIService {
    Map<String, String> env = getenv();
    private final MailConverter mailConverter;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private WebClient webClient = WebClient.builder().build();
    private String API_KEY = env.get("CLAUDE_API_KEY");
    private final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private final ChatbotRepository chatbotRepository;
    private final CounselingLogRepository counselingLogRepository;
    private final SolutionRepository solutionRepository;
    private final CalendarsRepository calendarsRepository;

    // Claude API를 호출
    private Mono<String> callClaudeAPI(String systemPrompt, String conversationContext, int maxTokens) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-haiku-20240307");
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
        systemPrompt += "사용자의 대화를 읽고 9개의 감정 중 하나를 반환해준 뒤 대화를 해줘: 평온, 웃음, 사랑, 놀람, 슬픔, 불편, 화남, 불안, 피곤, 답변은 200자 이내로 해줘. 말투는 ~이에요! ~했어요!처럼 해줘"
        + "사용자님의와 같은 말은 하지마"
        +"You are a psychological counselor. Your role is to provide empathetic and supportive responses to users seeking advice or sharing their experiences.\n" + "When responding, use a speech style that ends sentences with \"~했어요!\" or \"~하면 좋겠어요\" to convey a friendly and supportive tone. This style is similar to how a caring friend might speak in Korean."
        +"Keep your response within 200 characters."
        +"Don't just empathize too much; if the user asks for information, make sure to give a good answer.";

        // 이전 대화 내역 가져오기
        String previousMessages = getPreviousUserMessages(counselingLogId);
        String conversationContext = previousMessages
                + "Use this conversation history as context for your response. Consider the topics discussed, the tone of the conversation, and any relevant information shared previously. However, do not rely too heavily on past data. Your primary focus should be on the current message."
                +"formulate your response based primarily on the content of the current message, while using the conversation history for context when appropriate. Avoid contradicting information from previous exchanges unless the current message explicitly requires it."
                +"Do not use phrases like \"user:\" or \"assistant:\" in your reply."
                +"Here is the current message you need to respond to: "
                + request.getUserMessage();

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


    private String selectSystemPrompt(String chatbotType) {
        switch (chatbotType) {
            case "F":
                return "Your task is to respond like a thoughtful and empathetic conversationalist. The goal is to provide emotional support and understanding to the user."
                        + "As you respond, recognize their feelings and help them process their emotions. Use phrases like \"I understand that must be difficult or It sounds like you're going through a lot right now."
                        + "After you've expressed empathy, offer gentle, actionable advice that could help improve their situation. Be concise, warm, and ensure your tone is conversational and friendly like using ~\uD588\uC5B4\uC694!.";
            case "T":
                return "Your task is to respond like a helpful and realistic advisor. You are giving practical and actionable suggestions, but your tone should still be positive and motivating  like using ~했어요!."
                        + "Start by acknowledging the user's current situation and gently lead into concrete steps they could take to address their concerns"
                        + "Avoid listing things mechanically, instead, weave suggestions into a supportive narrative. Keep it concise and aim for 2-3 actionable suggestions while balancing realism with encouragement.";
            case "H":
            default:
                return "Your task is to act as a compassionate listener who offers emotional support. You should acknowledge the user's feelings and offer validation  like using ~했어요!."
                        + "Say things like It makes sense to feel this way given what you're going through, or I can hear how much this means to you."
                        + "After offering validation, give supportive advice that feels natural and non-judgmental. Your response should sound like it's coming from a trusted friend who truly cares about their well-being.";
        }

    }

    // 이전 사용자 메시지 가져오기
    private String getPreviousUserMessages(Long counselingLogId) {
        List<ChatbotMessage> previousMessages = chatbotRepository.findByCounselingLogId(counselingLogId);
        return previousMessages.stream()
                .map(message -> message.getSender() + ": " + message.getContent())
                .collect(Collectors.joining("\n"));
    }

    private LocalDateTime generateRandomTime() {
        long minSeconds = 5 * 60 * 60;  //5시간
        long maxSeconds = 24 * 60 * 60; //24시간
        long randomSeconds = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds);
        return LocalDateTime.now().plusSeconds(randomSeconds);
    }

    private String extractEmotion(String response) {
        List<String> emotions = Arrays.asList("평온", "슬픔", "웃음", "사랑", "놀람", "피곤", "화남", "불안");
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

    @Transactional
    public Mono<CounselingResponseDto.CounselingDto> summarizeConversation(Long counselingLogId) {
        Member member = counselingLogRepository.findById(counselingLogId).get().getUser();
        member.setMailAlert(true);
        memberRepository.save(member);

        // 이전 대화 내역 가져오기
        String previousMessages = getPreviousUserMessages(counselingLogId);

        String xmlPrompt = "<conversationAnalysis>\n"
                + "    <conversation>\n"
                + "        " + previousMessages + "\n"
                + "    </conversation>\n"
                + "    <task>\n"
                + "        <summary>\n"
                + "            <title>\n"
                + "                Write a title that summarizes the user's concerns and the advice you provided.\n"
                + "                Keep this title between 12 characters.\n"
                + "            </title>\n"
                + "            <userSummary>\n"
                + "                Combine what the user said and present it as thoughtfully as possible.\n"
                + "                Write as if you're summarizing concerns shared by a close friend.\n"
                + "                Use the ~했어요 form in Korean to convey a friendly tone.\n"
                + "                Do not mention the word 'user'.\n"
                + "                Keep this summary between 250 and 300 characters.\n"
                + "            </userSummary>\n"
                + "            <claudeSummary>\n"
                + "                Combine what Claude said and present it as thoughtful advice.\n"
                + "                Write as if you were summarizing advice you gave to a friend who shared their concerns.\n"
                + "                Use the ~했어요 form in Korean to convey a friendly tone.\n"
                + "                Do not mention the word 'Claude'.\n"
                + "                Keep this summary between 250 and 300 characters.\n"
                + "            </claudeSummary>\n"
                + "        </summary>\n"
                + "        <recommendedActions>\n"
                + "            <action1>\n"
                + "                Suggest specific actions the user should take based on the conversation.\n"
                + "                Each action should be within 50 characters.\n"
                + "            </action1>\n"
                + "            <action2>\n"
                + "                Suggest specific actions the user should take based on the conversation.\n"
                + "                Each action should be within 50 characters.\n"
                + "            </action2>\n"
                + "            <action3>\n"
                + "                Suggest specific actions the user should take based on the conversation.\n"
                + "                Each action should be within 50 characters.\n"
                + "            </action3>\n"
                + "        </recommendedActions>\n"
                + "        <letter>\n"
                + "            Write a letter of up to 176 characters.\n"
                + "            Check in with the user about whether their concerns have been resolved.\n"
                + "            If concerns are ongoing, acknowledge this.\n"
                + "            Ask if there is anything else they want to talk about.\n"
                + "        </letter>\n"
                + "    </task>\n"
                +"     <example>\n"
                       +"<task>\n"
                        + "<summary>\n"
                        + "<title>초콜릿을 먹은 하루</title>\n"
                        + "<userSummary>\n"
                        + "오늘 맛있는 초콜릿을 먹었군요! 달콤하지만 쌉싸름하셨다니 맛있었겠네요. 다음에는 두바이초콜릿을 먹겠다는 다짐을 들으니 저도 달콤해지네요 :)\n"
                        + "</userSummary>\n"
                        + "<claudeSummary>\n"
                        + "많은 양의 초콜릿을 많이 먹는 건 좋지 않지만, 적당량의 초콜릿을 먹었다면 건강에 정말 좋죠!\n"
                        + "</claudeSummary>\n"
                        + "</summary>\n"
                        + "<recommendedActions>\n"
                        + "<action1>따듯한 차 마시기</action1>\n"
                        + "<action2>잠들기 전에 하루일기 작성하기</action2>\n"
                        + "<action3>기분 좋지 않은 일이 있었다면 감정 쓰레기통에 버리기</action3>\n"
                        + "</recommendedActions>\n"
                        + "<letter>\n"
                        + "저번에 같이 이야기했던 초콜릿은 맛있었나요? 더 이야기해보고 싶어요! 언제든지 기다리고 있을게요. 고민이나 하고 싶은 이야기가 있다면 저를 찾아와주세요!\n"
                        + "</letter>\n"
                        + "</task>"
                + "</conversationAnalysis>\n";

        String systemPrompt = "answer format is following xml format.Don't forget print task tag <task>\n"
                + "        <summary>\n"
                + "            <title>\n"
                + "                Write a title that summarizes the user's concerns and the advice you provided.\n"
                + "                Keep this title between 12 characters.\n"
                + "            </title>\n"
                + "            <userSummary>\n"
                + "                Combine what the user said and present it as thoughtfully as possible.\n"
                + "                Write as if you're summarizing concerns shared by a close friend.\n"
                + "                Use the ~했어요 form in Korean to convey a friendly tone.\n"
                + "                Do not mention the word 'user'.\n"
                + "                Keep this summary between 250 and 300 characters.\n"
                + "            </userSummary>\n"
                + "            <claudeSummary>\n"
                + "                Combine what Claude said and present it as thoughtful advice.\n"
                + "                Write as if you were summarizing advice you gave to a friend who shared their concerns.\n"
                + "                Use the ~했어요 form in Korean to convey a friendly tone.\n"
                + "                Do not mention the word 'Claude'.\n"
                + "                Keep this summary between 250 and 300 characters.\n"
                + "            </claudeSummary>\n"
                + "        </summary>\n"
                + "        <recommendedActions>\n"
                + "            <action1>\n"
                + "                Suggest specific actions the user should take based on the conversation.\n"
                + "                Each action should be within 50 characters.\n"
                + "            </action1>\n"
                + "            <action2>\n"
                + "                Suggest specific actions the user should take based on the conversation.\n"
                + "                Each action should be within 50 characters.\n"
                + "            </action2>\n"
                + "            <action3>\n"
                + "                Suggest specific actions the user should take based on the conversation.\n"
                + "                Each action should be within 50 characters.\n"
                + "            </action3>\n"
                + "        </recommendedActions>\n"
                + "        <letter>\n"
                + "            Write a letter of up to 176 characters.\n"
                + "            Check in with the user about whether their concerns have been resolved.\n"
                + "            If concerns are ongoing, acknowledge this.\n"
                + "            Ask if there is anything else they want to talk about.\n"
                + "        </letter>\n"
                + "    </task>\n";

        // Claude API 호출 및 상담 로그 업데이트
        return callClaudeAPI(systemPrompt, xmlPrompt, 1024)
                .flatMap(response -> {
                    // XML 응답을 파싱
                    String assistantResponseText = new JSONObject(response)
                            .getJSONArray("content")
                            .getJSONObject(0)
                            .getString("text");
                    log.info("assistantResponseText : {}", assistantResponseText);

                    return parseXMLResponse(assistantResponseText, counselingLogId);
                });
    }

    private Mono<CounselingResponseDto.CounselingDto> parseXMLResponse(String xmlResponse, Long counselingLogId) {
        try {
            // XML 파서 초기화
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlResponse));
            Document doc = builder.parse(is);
            log.info("doc : {}", doc);

            // XML에서 필드 추출
//            String conversationAnalysis = doc.getElementsByTagName("task").item(0).getTextContent();
//            log.info("task : {}", conversationAnalysis);
            String title = doc.getElementsByTagName("title").item(0).getTextContent();
            log.info("title : {}", title);
            String userSummary = doc.getElementsByTagName("userSummary").item(0).getTextContent().replace("\n", " ").trim();
            log.info("userSummary : {}", userSummary);
            String assistantSummary = doc.getElementsByTagName("claudeSummary").item(0).getTextContent().replace("\n", " ").trim();
            log.info("claudeSummary : {}", assistantSummary);
            List<String> suggestedActions = new ArrayList<>();
            String action1 = doc.getElementsByTagName("action1").item(0).getTextContent();
            log.info("action1 : {}", action1);
            String action2 = doc.getElementsByTagName("action2").item(0).getTextContent();
            log.info("action2 : {}", action2);
            String action3 = doc.getElementsByTagName("action3").item(0).getTextContent();
            log.info("action3 : {}", action3);
            suggestedActions.add(action1);
            suggestedActions.add(action2);
            suggestedActions.add(action3);
            log.info("suggestedActions : {}", suggestedActions);
            String letter = doc.getElementsByTagName("letter").item(0).getTextContent().replace("\n", " ").trim();
            log.info("letter : {}", letter);

            //편지 저장
            Mail mail = MailConverter.toMailEntity(counselingLogRepository.findById(counselingLogId).get().getUser(), letter, counselingLogRepository.findById(counselingLogId).get().getChatbotType());
            LocalDateTime randomTime = generateRandomTime();
            mail.setCreatedAt(randomTime);
            mailRepository.save(mail);

            // 상담 로그 업데이트
            CounselingLog existingLog = counselingLogRepository.findById(counselingLogId)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHATBOT_SESSION_NOT_FOUND));

            Calendars calendar = CalendarsConverter.solutionToCalendar(existingLog);
            calendarsRepository.save(calendar);

            CounselingLog updatedLog = ChatbotConverter.updateCounselingLog(existingLog, title, userSummary, assistantSummary, calendar);
            counselingLogRepository.save(updatedLog);

            List<String> actions = Arrays.asList(action1, action2, action3);
            for (String action : actions) {
                Solution solution = ChatbotConverter.createSolution(updatedLog, action);
                solutionRepository.save(solution);
            }

            List<Solution> savedSolutions = solutionRepository.findAllByCounselingLogId(updatedLog.getId());

            // 응답 DTO 생성
            return Mono.just(ChatbotConverter.toCounselingDto(updatedLog, savedSolutions));
        } catch (Exception e) {
            log.error("Error parsing XML response: {}", e.getMessage());
            return Mono.error(new ErrorHandler(ErrorStatus.COUNSELING_LOG_ERROR));
        }
    }
}




