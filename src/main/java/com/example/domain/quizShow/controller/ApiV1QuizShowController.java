package com.example.domain.quizShow.controller;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.response.QuizShowResponse;
import com.example.domain.quizShow.service.QuizShowService;
import com.example.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizshow")
public class ApiV1QuizShowController {
    private final QuizShowService quizShowService;

    @GetMapping("")
    public RsData<QuizShowListResponse> list(@PageableDefault(size = 10) Pageable pageable) {
        QuizShowListResponse quizShowListResponse = this.quizShowService.getList(pageable);

        return RsData.of("200", "게시글 다건 조회", quizShowListResponse);
    }

    @GetMapping("/{id}")
    public RsData<QuizShowResponse> getQuizShow(@PathVariable("id") Long id) {
        QuizShowDTO quizShow = quizShowService.getQuizShow(id);

        return RsData.of("200", "게시글 단건 조회", new QuizShowResponse(quizShow));
    }

    @PostMapping("")
    public RsData<QuizShowResponse> create(@Valid @RequestBody QuizShowCreateRequest quizShowCR) {
        QuizShowDTO quizShowDTO = quizShowService.create(quizShowCR);

        return RsData.of("200", "게시글 생성 완료", new QuizShowResponse(quizShowDTO));
    }

    @PatchMapping("/{id}")
    public RsData<QuizShowResponse> modify(@PathVariable("id") Long id,
                                           @Valid @RequestBody QuizShowModifyRequest quizShowMR) {
        QuizShowDTO modifiedQuizShow = this.quizShowService.modify(id, quizShowMR);
        return RsData.of("200", "게시글 수정 완료", new QuizShowResponse(modifiedQuizShow));
    }

    @DeleteMapping("{id}")
    public RsData<QuizShowResponse> delete(@PathVariable("id") Long id) {
        QuizShowDTO deletedQuizShow = this.quizShowService.delete(id);
        return RsData.of("200", "게시글 삭제 완료", new QuizShowResponse(deletedQuizShow));
    }
}
