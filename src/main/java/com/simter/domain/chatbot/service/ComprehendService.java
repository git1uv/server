package com.simter.domain.chatbot.service;

import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.LanguageCode;
import org.springframework.stereotype.Service;

@Service
public class ComprehendService {

    private final ComprehendClient comprehendClient;

    public ComprehendService(ComprehendClient comprehendClient) {
        this.comprehendClient = comprehendClient;
    }

    public String analyzeSentiment(String text) {
        DetectSentimentRequest detectSentimentRequest = DetectSentimentRequest.builder()
                .text(text)
                .languageCode(LanguageCode.EN)
                .build();

        DetectSentimentResponse detectSentimentResponse = comprehendClient.detectSentiment(detectSentimentRequest);
        return detectSentimentResponse.sentiment().toString();
    }
}
