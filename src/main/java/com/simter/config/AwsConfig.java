package com.simter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.comprehend.ComprehendClient;

@Configuration
public class AwsConfig {

    //AWS Comprehend 사용(감성 분석)
    @Bean
    public ComprehendClient comprehendClient() {
        return ComprehendClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                System.getProperty("AWS_ACCESS_KEY_ID"),
                                System.getProperty("AWS_SECRET_ACCESS_KEY")
                        )
                ))
                .region(Region.AP_NORTHEAST_2)  //서울 리전
                .build();
    }
}