package com.example.domain.quizShow.controller;

import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.response.QuizShowCreateResponse;
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
        QuizShow quizShow = quizShowService.getQuizShow(id);

        return RsData.of("200", "게시글 단건 조회", new QuizShowResponse(quizShow));
    }

    @PostMapping("")
    public RsData<QuizShowCreateResponse> create(@Valid @RequestBody QuizShowCreateRequest quizShow_CR_DTO) {
        QuizShowResponse quizShowR_DTO = quizShowService.create(quizShow_CR_DTO);

        return RsData.of("200", "게시글 생성 완료", new QuizShowCreateResponse(quizShowR_DTO));
    }

    @PatchMapping("/{id}")
    public RsData<QuizShowResponse> modify(@PathVariable("id") Long id,
                                           @Valid @RequestBody QuizShowModifyRequest quizShowMR_DTO) {
        QuizShow quizShow = this.quizShowService.getQuizShow(id);

        if (quizShow == null) return RsData.of(
                "500",
                "%d 번 게시물은 존재하지 않습니다.".formatted(id),
                null
        );

        QuizShowResponse quizShowR_DTO = this.quizShowService.modify(id, quizShowMR_DTO);

        return RsData.of("200", "게시글 수정 완료", quizShowR_DTO);
    }

    @DeleteMapping("{id}")
    public RsData<QuizShowResponse> delete(@PathVariable("id") Long id) {
        QuizShow quizShow = this.quizShowService.getQuizShow(id);

        if (quizShow == null) return RsData.of(
                "500",
                "%d 번 게시물은 존재하지 않습니다.".formatted(id),
                null
        );

        QuizShowResponse quizShowResponse = this.quizShowService.delete(quizShow);

        return RsData.of("200", "게시글 삭제 완료", quizShowResponse);
    }
}
