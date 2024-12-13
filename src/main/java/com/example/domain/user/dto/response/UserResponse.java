package com.example.domain.user.dto.response;

import com.example.domain.user.entity.SiteUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Setter
public class UserResponse {
    private final Long id;                // 사용자 ID
    private final String username;        // 사용자 이름
    private final String email;           // 이메일
    private final String nickname;        // 닉네임
    private final String intro;           // 자기소개
    private final String thumbnailImg;    // 프로필 이미지
    private final LocalDateTime createdDate;  // 생성일
    private final LocalDateTime modifiedDate; // 수정일
    private Long expirationTime;


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

    public static UserResponse fromEntity(SiteUser user) {
        return new UserResponse(user);
    }
}