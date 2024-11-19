package com.example.domain.discussion.service;

import com.example.domain.discussion.entity.Discussion;
import com.example.domain.discussion.form.DiscussionForm;
import com.example.domain.discussion.repository.DiscussionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscussionService {

    private final DiscussionRepository discussionRepository;

    public void create (DiscussionForm discussionForm) {
        Discussion discussion = Discussion.builder()
                .title(discussionForm.getTitle())
                .content(discussionForm.getContent())
                .build();
        this.discussionRepository.save(discussion);
    }
}
