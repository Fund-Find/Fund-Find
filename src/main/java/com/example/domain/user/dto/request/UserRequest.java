package com.example.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotEmpty(message = "아이디가 비어있습니다.")
    @Size(min = 7, max = 30, message = "아이디 길이는 7에서 30 사이어야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @Size(max = 100, message = "닉네임은 최대 100자이어야 합니다.")
    private String nickname;

    @Size(max = 500, message = "자기소개는 최대 500자이어야 합니다.")
    private String intro;

    private String thumbnailImg;
}
