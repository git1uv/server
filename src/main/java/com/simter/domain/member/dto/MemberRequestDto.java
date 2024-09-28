package com.simter.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

public class MemberRequestDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterDto {
        @NotNull
        private String email;

        @NotNull
        private String password;

        @NotNull
        private String nickname;

        @NotNull
        private String loginType;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SocialRegisterDto {
        @NotNull
        private String email;

        @NotNull
        private String nickname;

        @NotNull
        private String loginType;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PasswordReissueDto {
        @NotNull String email;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequestDto {
        @NotNull
        private String email;
        @NotNull
        private String password;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NicknameChangeDto {
        @NotNull String nickname;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PasswordChangeDto {
        @NotNull String oldPassword;
        @NotNull String newPassword;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SocialLoginDto {
        @NotNull String email;
        @NotNull JwtTokenDto token;
        @NotNull String loginType;
        @NotNull boolean isMember;
    }
}
