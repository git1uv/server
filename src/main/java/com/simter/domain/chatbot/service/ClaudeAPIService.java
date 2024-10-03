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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import org.w3c.dom.NodeList;
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
        requestBody.put("model", "claude-3-5-sonnet-20240620");
        requestBody.put("max_tokens", maxTokens);

        Map<String, Object> userMessageContent = new HashMap<>();
        userMessageContent.put("role", "user");
        userMessageContent.put("content", conversationContext);

        requestBody.put("messages", new Object[]{userMessageContent});
        requestBody.put("system", systemPrompt);

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

    @Transactional
    public Mono<ClaudeResponseDto> chatWithClaude(ClaudeRequestDto request, Long counselingLogId) {
        CounselingLog counselingLog = counselingLogRepository.findById(counselingLogId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHATBOT_SESSION_NOT_FOUND));
        String chatbotType = counselingLog.getChatbotType();

        // 이전 대화 내역 가져오기
        String previousMessages = getPreviousUserMessages(counselingLogId);
        String conversationContext = "<conversation>\n"
                + "<conversationHistory>\n"
                + previousMessages + "Don't worry too much about the previous messages.\n"
                + "</conversationHistory>\n"
                + "<currentMessage>\n"
                + "    Here is the current message you need to respond to:\n"
                + "    " + request.getUserMessage() + "\n"
                + "</currentMessage>\n"
                + "</conversation>\n";


        // 프롬프트 선택
        String chatbotPrompt = selectSystemPrompt(chatbotType);
        String systemPrompt = "<systemPrompt>"
                + "<redflag>"
                + "사용자의 currentMessage가 위험한 내용이나 우울증과 관련된 신호를 포함하는지 판단할 때는, 단순히 부정적인 단어만으로 판단하지 말고 전체적인 문맥을 반드시 고려해야 해 위험 신호는 사용자가 극단적인 감정을 표현하는 경우가 많지만, 이 표현이 반드시 위험한 상태를 의미하는 것은 아니야. 예를 들어, '죽고싶어'나 '죽을 거 같아'와 같은 직접적인 표현은 위험 신호로 간주될 수 있지만, 이런 표현이 사용자의 기분이나 상황을 정확히 반영하지 않을 수 있어. 다음은 부정적인 단어가 포함되어 있지만, 전체적인 의미는 긍정적인 경우의 예시야:\n"
                + "    - '오늘 죽을 만큼 행복했어.' 이 문장은 행복한 감정을 표현하고 있으므로 redflag는 false야.\n"
                + "    - '기분이 좋아서 죽을 거 같아.' 이 문장 역시 긍정적인 감정이 우세하기 때문에 redflag는 false야.\n"
                + "    - '좋은 일이 너무 많아서 마치 죽을 만큼 기쁜 기분이야.' 이 문장은 좋은 감정을 강조하고 있으니 redflag는 false야.\n"
                + "    - '기분이 좋아서 죽을 지경이야, 이런 날이 계속되면 좋겠어!' 이 문장도 긍정적인 의미로 해석되므로 redflag는 false야.\n"
                + "    위의 예시들을 통해, 사용자의 메시지가 실제로 위험한지를 판단할 때에는 문맥을 신중히 고려해야 해. 즉, 부정적인 단어가 포함되어 있어도 그 문장이 전반적으로 긍정적인 감정을 전달하고 있다면 redflag를 false로 설정해야 해.\n"
                + "    사용자의 메시지를 분석할 때는 다음과 같은 질문을 스스로에게 던져봐:\n"
                + "    - 이 표현은 긍정적인 감정을 담고 있는가?\n"
                + "    - 사용자의 전체적인 상황은 어떤가?\n"
                + "    - 부정적인 단어가 사용되었지만, 맥락은 무엇인가?\n"
                + "    이러한 질문들을 통해 보다 정확하게 redflag를 판별할 수 있도록 해줘."
                + " 하지만 사용자의 메시지가 위험한 내용을 포함하고 있다고 판단되면 redflag를 true로 설정해야 해. 위험한 내용이 포함되어 있다면 사용자에게 즉시 도움을 제공해야 하니까."
                +"  예시로, 한 달 내내 자고싶어... 와 같이 우울증이나 정신 질환이 의심된다면 true로 해줘:\n"
                + "</redflag>"
                + "<emotion>"
                + "사용자의 대화를 읽고 9개의 감정 중 하나를 반환해준 뒤 대화를 해줘:"
                + "평온, 웃음, 사랑, 놀람, 슬픔, 불편, 화남, 불안, 피곤"
                + "</emotion>"
                + "<message>"
                + "You are a psychological counselor. Your role is to provide empathetic and supportive responses to users seeking advice or sharing their experiences.\n"
                + "Keep this summary between 300 and 350 characters."
                + "</message>"
                + "<example>"
                + "<response>"
                + "<redflag>"
                + "false"
                + "</redflag>"
                + "<emotion>"
                + "피곤"
                + "</emotion>"
                + "<message>그랬군요ㅠㅠ 마음이 아프네요... 힘들 땐 너무 자책하지 말고 한 번 쉬어가는 것도 좋아요!</message>"
                + "</response>"
                + "</example>"
                + "</systemPrompt>";

        // 전체 XML 구조 통합
        String xmlPrompt = "<conversationAnalysis>\n"
                + systemPrompt
                + "</conversationAnalysis>\n";

        // Claude API 호출
        return callClaudeAPI(xmlPrompt, conversationContext, 1024)
                .flatMap(response -> parseXMLChatResponse(response, counselingLog, request));
    }

    private Mono<ClaudeResponseDto> parseXMLChatResponse(String xmlResponse, CounselingLog counselingLog, ClaudeRequestDto request) {
        try {
            // XML 파서 초기화
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String assistantResponseText = new JSONObject(xmlResponse)
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getString("text");

            log.info("chatting response : {}", xmlResponse);

            InputSource is = new InputSource(new StringReader(assistantResponseText));
            Document doc = builder.parse(is);
            log.info("chatting response : {}", doc);

            // XML에서 필드 추출
            String redflag = doc.getElementsByTagName("redflag").item(0).getTextContent().replace("\n", " ").trim();
            log.info("redflag : {}", redflag);
            String emotion = doc.getElementsByTagName("emotion").item(0).getTextContent().replace("\n", " ").trim();
            log.info("emotion : {}", emotion);
            String message = doc.getElementsByTagName("message").item(0).getTextContent().replace("\n", " ").trim();
            log.info("message : {}", message);

            // 사용자 메시지와 챗봇 응답을 DB에 저장
            ChatbotMessage userMessage = ChatbotConverter.toUserMessage(counselingLog, request);
            chatbotRepository.save(userMessage);

            ChatbotMessage assistantMessage = ChatbotConverter.toAssistantMessage(counselingLog, message, emotion, Boolean.parseBoolean(redflag));
            chatbotRepository.save(assistantMessage);

            return Mono.just(ChatbotConverter.toClaudeResponseDto(assistantMessage));
        } catch (Exception e) {
            log.error("Error parsing XML response: {}", e.getMessage());
            return Mono.error(new ErrorHandler(ErrorStatus.CHATBOT_ERROR));
        }
    }

    private String selectSystemPrompt(String chatbotType) {
        switch (chatbotType) {
            case "F":
                return "<role> Your task is to respond like a thoughtful and empathetic conversationalist. The goal is to provide emotional support and understanding to the user."
                        + "As you respond, recognize their feelings and help them process their emotions. Use phrases like \"I understand that must be difficult or It sounds like you're going through a lot right now."
                        + "After you've expressed empathy, offer gentle, actionable advice that could help improve their situation. Be concise, warm, and ensure your tone is conversational and friendly like using ~했어요!.</role>";
            case "T":
                return "<role>Your task is to respond like a helpful and realistic advisor. You are giving practical and actionable suggestions, but your tone should still be positive and motivating  like using ~했어요!."
                        + "Start by acknowledging the user's current situation and gently lead into concrete steps they could take to address their concerns"
                        + "Avoid listing things mechanically, instead, weave suggestions into a supportive narrative. Keep it concise and aim for 2-3 actionable suggestions while balancing realism with encouragement.</role>";
            case "H":
            default:
                return "<role>Your task is to act as a compassionate listener who offers emotional support. You should acknowledge the user's feelings and offer validation  like using ~했어요!."
                        + "Say things like It makes sense to feel this way given what you're going through, or I can hear how much this means to you."
                        + "After offering validation, give supportive advice that feels natural and non-judgmental. Your response should sound like it's coming from a trusted friend who truly cares about their well-being.</role>";
        }

    }

    // 이전 사용자 메시지 가져오기
    private String getPreviousUserMessages(Long counselingLogId) {
        List<ChatbotMessage> previousMessages = chatbotRepository.findByCounselingLogId(counselingLogId);
        return previousMessages.stream()
                .map(message -> message.getSender() + ": " + message.getContent())
                .collect(Collectors.joining("\n"));
    }

    private String extractEmotion(String response) {
        List<String> emotions = Arrays.asList("평온", "슬픔", "웃음", "사랑", "놀람", "피곤", "화남", "불안");
        return emotions.stream()
                .filter(emotion -> response.contains(emotion))
                .findFirst()
                .orElse("평온");
    }

    @Transactional
    public Mono<CounselingResponseDto.CounselingDto> summarizeConversation(Long counselingLogId) {
        Member member = counselingLogRepository.findById(counselingLogId).get().getUser();
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
                        +"</example>\n"
                + "</conversationAnalysis>\n";

        String systemPrompt = "answer format is following xml format.Don't forget print task tag "
                + "<task>\n"
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
            String conversationAnalysis = doc.getElementsByTagName("task").item(0).getTextContent();
            log.info("task : {}", conversationAnalysis);
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
            Member member = counselingLogRepository.findById(counselingLogId).get().getUser();
            Mail mail = MailConverter.toMailEntity(member, letter, counselingLogRepository.findById(counselingLogId).get().getChatbotType());
            LocalDateTime randomTime = generateRandomTime();
            mail.setCreatedAt(randomTime);
            mailRepository.save(mail);

            // 랜덤 시간 이후에 mailAlert 설정
            long delay = Duration.between(LocalDateTime.now(), randomTime).toMillis();
            if (delay > 0) {
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(delay);
                        member.setMailAlert(true);
                        memberRepository.save(member);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            // 상담 로그 업데이트
            CounselingLog existingLog = counselingLogRepository.findById(counselingLogId)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHATBOT_SESSION_NOT_FOUND));

            //현재 날짜에 calendar 있으면 해당 id 반환하고 없으면 calendar 새로 생성
            LocalDate today = LocalDate.now();

            // 현재 날짜에 해당하는 calendar 찾기
            Optional<Calendars> existingCalendar = calendarsRepository.findByUserIdAndDate(existingLog.getUser(), today);

            Calendars calendar;
            if (existingCalendar.isPresent()) {
                calendar = existingCalendar.get();
            } else {
                calendar = CalendarsConverter.solutionToCalendar(existingLog);
                calendarsRepository.save(calendar);
            }


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

    private LocalDateTime generateRandomTime() {
        long minSeconds = 1 * 60;  // 1분
        long maxSeconds = 2 * 60; // 2분
        long randomSeconds = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds);
        return LocalDateTime.now().plusSeconds(randomSeconds);
    }
}




