package com.simter;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimterApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();

        // 환경 변수를 시스템 속성으로 설정
        System.setProperty("DB_ROOTPW", dotenv.get("DB_ROOTPW"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PW", dotenv.get("DB_PW"));
        System.setProperty("OAUTH_ID", dotenv.get("OAUTH_ID"));
        System.setProperty("OAUTH_PW", dotenv.get("OAUTH_PW"));
        System.setProperty("KAKAO_ID", dotenv.get("KAKAO_ID"));
        System.setProperty("KAKAO_URI", dotenv.get("KAKAO_URI"));
        System.setProperty("MAIL_NAME", dotenv.get("MAIL_NAME"));
        System.setProperty("MAIL_PW", dotenv.get("MAIL_PW"));
        System.setProperty("AWS_ACCESS_KEY_ID", dotenv.get("AWS_ACCESS_KEY_ID"));
        System.setProperty("AWS_SECRET_ACCESS_KEY", dotenv.get("AWS_SECRET_ACCESS_KEY"));
        System.setProperty("AWS_REGION", dotenv.get("AWS_REGION"));
        System.setProperty("CLAUDE_API_KEY", dotenv.get("CLAUDE_API_KEY"));



        SpringApplication.run(SimterApplication.class, args);
    }
}