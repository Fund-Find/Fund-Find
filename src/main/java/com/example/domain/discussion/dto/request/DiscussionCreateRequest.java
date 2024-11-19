package com.example.domain.discussion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiscussionCreateRequest {
    @NotBlank
    private String subject;
    @NotBlank
    private String content;
}
