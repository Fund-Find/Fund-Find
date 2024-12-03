package com.example.domain.discussion.entity;

import com.example.domain.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Discussion extends BaseEntity {

    @Column
    private String title;

    @Column(columnDefinition = "text")
    private String content;
}
