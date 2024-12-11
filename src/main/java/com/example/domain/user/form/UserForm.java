package com.example.domain.user.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    @NotEmpty(message = "아이디가 비어있습니다.")
    @Size(min = 7, max = 30, message = "아이디 길이는 7에서 30 사이어야 합니다.")
    private String username;

    @NotEmpty(message = "비밀번호가 비어있습니다.")
    @Size(min = 7, max = 30, message = "비밀번호 길이는 7에서 30 사이어야 합니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인란이 비어있습니다.")
    private String passwordCheck;

    @NotEmpty(message = "이메일이 비어있습니다.")
    @Email
    private String email;

    @NotEmpty(message = "닉네임이 비어있습니다.")
    private String nickname;
}
