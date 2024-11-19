package com.example.domain.discussion.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscussionForm {
    @NotEmpty(message = "제목을 입력해 주세요.")
    private String title;

    @NotEmpty(message = "내용을 입력해 주세요.")
    private String content;
}
