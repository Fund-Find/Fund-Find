package com.example.domain.user.dto;

import com.example.domain.user.entity.SiteUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
public class UserDTO {
    private final Long id;                // 사용자 ID (응답용)
    private final String username;        // 사용자 이름 (요청/응답 공통)
    private final String email;           // 이메일 (요청/응답 공통)
    private final String nickname;        // 닉네임 (요청/응답 공통)
    private final String intro;           // 자기소개 (요청/응답 공통)
    private final String thumbnailImg;    // 프로필 이미지 (응답용)
    private final LocalDateTime createdDate;  // 생성일 (응답용)
    private final LocalDateTime modifiedDate; // 수정일 (응답용)

    public UserDTO(SiteUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.createdDate = user.getCreatedDate();
        this.modifiedDate = user.getModifiedDate();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.intro = user.getIntro();
        this.thumbnailImg = user.getThumbnailImg();
    }
}
