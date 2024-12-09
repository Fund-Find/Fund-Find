package com.example.domain.quizShow.controller;

import com.example.domain.quizShow.dto.QuizShowCreateRequestDTO;
import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.dto.QuizShowModifyRequestDTO;
import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.response.QuizShowCreateResponse;
import com.example.domain.quizShow.service.QuizShowService;
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
    public com.example.global.RsData.RsData<QuizShowListResponseDTO> list(@PageableDefault(size = 10) Pageable pageable) {
        QuizShowListResponseDTO quizShowDTO = this.quizShowService.getList(pageable);

        return com.example.global.RsData.RsData.of("200", "게시글 다건 조회", quizShowDTO);
    }

    @GetMapping("/{id}")
    public com.example.global.RsData.RsData<QuizShowResponseDTO> getQuizShow(@PathVariable("id") Long id) {
        QuizShow quizShow = quizShowService.getQuizShow(id);

        return com.example.global.RsData.RsData.of("200", "게시글 단건 조회", new QuizShowResponseDTO(quizShow));
    }

    @PostMapping("")
    public com.example.global.RsData.RsData<QuizShowCreateResponse> create(@Valid @RequestBody QuizShowCreateRequestDTO quizShow_CR_DTO) {
        QuizShowResponseDTO quizShowR_DTO = quizShowService.create(quizShow_CR_DTO);

        return com.example.global.RsData.RsData.of("200", "게시글 생성 완료", new QuizShowCreateResponse(quizShowR_DTO));
    }

    @PatchMapping("/{id}")
    public com.example.global.RsData.RsData<QuizShowResponseDTO> modify(@PathVariable("id") Long id,
                                                                        @Valid @RequestBody QuizShowModifyRequestDTO quizShowMR_DTO) {
        QuizShow quizShow = this.quizShowService.getQuizShow(id);

        if (quizShow == null) return com.example.global.RsData.RsData.of(
                "500",
                "%d 번 게시물은 존재하지 않습니다.".formatted(id),
                null
        );

        QuizShowResponseDTO quizShowR_DTO = this.quizShowService.modify(id, quizShowMR_DTO);

        return com.example.global.RsData.RsData.of("200", "게시글 수정 완료", quizShowR_DTO);
    }

    @DeleteMapping("{id}")
    public com.example.global.RsData.RsData<QuizShowResponseDTO> delete(@PathVariable("id") Long id) {
        QuizShow quizShow = this.quizShowService.getQuizShow(id);

        if (quizShow == null) return com.example.global.RsData.RsData.of(
                "500",
                "%d 번 게시물은 존재하지 않습니다.".formatted(id),
                null
        );

        QuizShowResponseDTO quizShowResponseDTO = this.quizShowService.delete(quizShow);

        return com.example.global.RsData.RsData.of("200", "게시글 삭제 완료", quizShowResponseDTO);
    }
}
