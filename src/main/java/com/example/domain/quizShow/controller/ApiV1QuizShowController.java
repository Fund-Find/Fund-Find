package com.example.domain.quizShow.controller;

import com.example.domain.global.rsData.RsData;
import com.example.domain.quizShow.dto.QuizShowCreateRequestDTO;
import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.dto.QuizShowModifyRequestDTO;
import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.response.QuizShowResponse;
import com.example.domain.quizShow.service.QuizShowService;
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
        QuizShowListResponseDTO quizShowDTO = this.quizShowService.getList(pageable);

        return RsData.of("200", "게시글 다건 조회", new QuizShowListResponse(quizShowDTO));
    }

    @GetMapping("/{id}")
    public RsData<QuizShowResponse> getQuizShow(@PathVariable("id") Long id) {
        QuizShow quizShow = quizShowService.getQuizShow(id);
        QuizShowResponseDTO quizShowResponseDTO = QuizShowResponseDTO.from(quizShow);

        return RsData.of("200", "게시글 단건 조회", new QuizShowResponse(quizShowResponseDTO));
    }

    @PostMapping("")
    public RsData<QuizShowCreateRequestDTO> create(@RequestBody QuizShowCreateRequestDTO newQuizShow) {
        QuizShow quizShow = quizShowService.create(newQuizShow);
        return RsData.of("200", "게시글 생성 완료", QuizShowCreateRequestDTO.form(quizShow));
    }

    @PatchMapping("/{id}")
    public RsData<QuizShowModifyRequestDTO> modify(@PathVariable("id") Long id,
                                                   @RequestBody QuizShowModifyRequestDTO quizShowModifyRequestDTO) {
        QuizShow modifyQuizShow = quizShowService.modify(id, quizShowModifyRequestDTO);

        return RsData.of("200", "게시글 수정 완료", QuizShowModifyRequestDTO.form(modifyQuizShow));
    }

    @DeleteMapping("{id}")
    public RsData<QuizShowResponseDTO> delete(@PathVariable("id") Long id) {
        QuizShow quizShow = this.quizShowService.getQuizShow(id);

        if (quizShow == null) return RsData.of(
                "500",
                "%d 번 게시물은 존재하지 않습니다.".formatted(id),
                null
        );

        quizShowService.delete(quizShow);
        QuizShowResponseDTO quizShowResponseDTO = new QuizShowResponseDTO(quizShow);

        return RsData.of("200", "게시글 삭제 완료", quizShowResponseDTO);
    }
}
