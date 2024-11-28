package com.example.domain.quizShow.controller;

import com.example.domain.global.rsData.RsData;
import com.example.domain.quizShow.dto.QuizShowCreateRequestDTO;
import com.example.domain.quizShow.dto.QuizShowListResponseDTO;
import com.example.domain.quizShow.dto.QuizShowModifyRequestDTO;
import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.response.QuizShowListResponse;
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
    public RsData<QuizShowResponseDTO> getQuizShow() {
        return RsData.of("200", "게시글 단건 조회");
    }

    @PostMapping("")
    public RsData<QuizShowCreateRequestDTO> create() {
        return RsData.of("200", "게시글 생성 완료");
    }

    @PatchMapping("/{id}")
    public RsData<QuizShowModifyRequestDTO> modify() {
        return RsData.of("200", "게시글 수정 완료");
    }

    @DeleteMapping("{id}")
    public RsData<QuizShowResponseDTO> delete() {
        return RsData.of("200", "게시글 삭제 완료");
    }
}
