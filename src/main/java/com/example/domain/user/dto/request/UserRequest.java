package com.example.domain.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    @NotEmpty(message = "아이디가 비어있습니다.")
    @Size(min = 7, max = 15, message = "아이디 길이는 7에서 15 사이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문과 숫자만 허용됩니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=\\-{};:'\",.<>/?]).{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password1;

    private String password2;

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "이메일에는 반드시 .(dot)이 포함되어야 합니다."
    )
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @Size(max = 12, message = "닉네임은 최대 12자이어야 합니다.")
    private String nickname;

    @Size(max = 500, message = "자기소개는 최대 500자이어야 합니다.")
    private String intro;

    private MultipartFile thumbnailImg;

    @AssertTrue(message = "이용약관에 동의해주세요.")
    private boolean agreement;
}
