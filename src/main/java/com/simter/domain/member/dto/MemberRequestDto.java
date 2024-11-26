package com.simter.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 포함하여 8~16자로 입력해주세요.")
        private String password;

        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z]{1,10}$", message = "닉네임은 한글과 영문자만 사용하여 1~10자로 입력해주세요.")
        private String nickname;

        @NotNull
        private String loginType;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SocialRegisterDto {
        private String email;

        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z]{1,10}$", message = "닉네임은 한글과 영문자만 사용하여 1~10자로 입력해주세요.")
        private String nickname;

        @NotNull
        private String loginType;

        @NotNull
        private JwtTokenDto token;

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
        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z]{1,10}$", message = "닉네임은 한글과 영문자만 사용하여 1~10자로 입력해주세요.")
        String nickname;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PasswordChangeDto {
        @NotNull String oldPassword;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 포함하여 8~16자로 입력해주세요.")
        String newPassword;
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
