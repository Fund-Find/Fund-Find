package com.example.domain.user.entity;

import com.example.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SiteUser extends BaseEntity {
    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @Column(unique = true)
    private String socialProvider;

    @Column(columnDefinition = "text")
    private String intro;

    private String nickname;

    private String thumbnailImg;

}
