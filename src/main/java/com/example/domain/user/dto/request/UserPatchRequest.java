package com.example.domain.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserPatchRequest {

    @Size(max = 12, message = "닉네임은 최대 12자이어야 합니다.")
    private String nickname;

    @Size(max = 500, message = "자기소개는 최대 500자이어야 합니다.")
    private String intro;

    private MultipartFile thumbnailImg;
}
