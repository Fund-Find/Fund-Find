package com.example.domain.quizShow.controller;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.request.QuizSubmitRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.response.QuizShowResponse;
import com.example.domain.quizShow.response.QuizSubmitResponse;
import com.example.domain.quizShow.service.QuizShowService;
import com.example.global.rsData.RsData;
import com.example.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizshow")
public class ApiV1QuizShowController {
    private final QuizShowService quizShowService;

    @GetMapping("")
    public RsData<QuizShowListResponse> list(@PageableDefault(size = 9) Pageable pageable) {
        QuizShowListResponse quizShowListResponse = this.quizShowService.getList(pageable);
        return RsData.of("200", "게시글 다건 조회", quizShowListResponse);
    }

    @GetMapping("/{id}")
    public RsData<QuizShowResponse> getQuizShow(@PathVariable("id") Long id) {
        QuizShowDTO quizShow = quizShowService.getQuizShow(id);
        return RsData.of("200", "게시글 단건 조회", new QuizShowResponse(quizShow));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RsData<QuizShowResponse> create(
            @Valid @RequestPart("data") QuizShowCreateRequest quizShowCR,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        if (imageFile != null) {
            quizShowCR.setImageFile(imageFile);
        }
        QuizShowDTO quizShowDTO = quizShowService.create(quizShowCR);
        return RsData.of("200", "게시글 생성 완료", new QuizShowResponse(quizShowDTO));
    }

    @PostMapping("/{id}/submit")
    public RsData<QuizSubmitResponse> submitQuiz(
            @PathVariable("id") Long quizShowId,
            @Valid @RequestBody QuizSubmitRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {

        // 로그인 체크 추가
        if (securityUser == null) {
            return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
        }

        QuizSubmitResponse result = quizShowService.submitAndSaveResult(
                quizShowId,
                request.getAnswers(),
                securityUser.getId()
        );

        return RsData.of("200", "퀴즈 제출이 완료되었습니다.", result);
    }

    @PostMapping("/{id}/vote")
    public RsData<QuizShowDTO> toggleVote(
            @PathVariable("id") Long quizShowId,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (securityUser == null) {
            return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
        }

        QuizShowDTO updatedQuizShow = quizShowService.toggleVote(quizShowId, securityUser.getId());
        return RsData.of(
                "200",
                updatedQuizShow.isHasVoted() ? "추천이 완료되었습니다." : "추천이 취소되었습니다.",
                updatedQuizShow
        );
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RsData<QuizShowResponse> modify(
            @PathVariable("id") Long id,
            @Valid @RequestPart("data") QuizShowModifyRequest quizShowMR,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        if (imageFile != null) {
            quizShowMR.setImageFile(imageFile);
        }
        QuizShowDTO modifiedQuizShow = this.quizShowService.modify(id, quizShowMR);
        return RsData.of("200", "게시글 수정 완료", new QuizShowResponse(modifiedQuizShow));
    }

    @DeleteMapping("/{id}")
    public RsData<QuizShowResponse> delete(@PathVariable("id") Long id) {
        QuizShowDTO deletedQuizShow = this.quizShowService.delete(id);
        return RsData.of("200", "게시글 삭제 완료", new QuizShowResponse(deletedQuizShow));
    }
}