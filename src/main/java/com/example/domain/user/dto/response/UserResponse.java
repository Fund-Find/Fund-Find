package com.example.domain.user.dto.response;

import com.example.domain.user.entity.SiteUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;                // 사용자 ID
    private String username;        // 사용자 이름
    private String email;           // 이메일
    private String nickname;        // 닉네임
    private String intro;           // 자기소개
    private String thumbnailImg;    // 프로필 이미지
    private LocalDateTime createdDate;  // 생성일
    private LocalDateTime modifiedDate; // 수정일


    // User 엔티티 기반 생성자
    public UserResponse(SiteUser user) {
        this.id = user.getId();  // User 엔티티의 ID
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.intro = user.getIntro();
        this.thumbnailImg = user.getThumbnailImg();
        this.createdDate = user.getCreatedDate();
        this.modifiedDate = user.getModifiedDate();
    }
}