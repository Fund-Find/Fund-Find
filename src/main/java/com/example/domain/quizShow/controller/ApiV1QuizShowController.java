package com.example.domain.quizShow.controller;

import com.example.domain.quizShow.dto.QuizCreateDTO;
import com.example.domain.quizShow.dto.QuizShowCreateDTO;
import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.dto.QuizShowResponseDTO;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.request.QuizSubmitRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.response.QuizShowResponse;
import com.example.domain.quizShow.response.QuizSubmitResponse;
import com.example.domain.quizShow.service.FileService;
import com.example.domain.quizShow.service.QuizShowService;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import com.example.global.rsData.RsData;
import com.example.global.security.SecurityUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizshow")
@Slf4j
public class ApiV1QuizShowController {
    private final QuizShowService quizShowService;

    @GetMapping("")
    public RsData<QuizShowListResponse> list(@PageableDefault(size = 9) Pageable pageable) {
        QuizShowListResponse quizShowListResponse = this.quizShowService.getList(pageable);
        return RsData.of("200", "게시글 다건 조회", quizShowListResponse);
    }

    @GetMapping("/{id}")
    public RsData<QuizShowResponse> getQuizShow(@PathVariable("id") Long id) {
        QuizShowDTO quizShowDTO = quizShowService.getQuizShow(id); // DTO 변환
        return RsData.of("200", "퀴즈쇼 조회 성공", new QuizShowResponse(quizShowDTO));
    }

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public RsData<QuizShowResponse> create(
//            @RequestPart("data") String data,  // JSON 데이터를 String으로 먼저 받음
//            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
//            @AuthenticationPrincipal SecurityUser securityUser) {
//        if (securityUser == null) {
//            return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
//        }
//
//        try {
//            // JSON 데이터를 역직렬화하여 객체로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//            QuizShowCreateRequest quizShowCR = objectMapper.readValue(data, QuizShowCreateRequest.class);
//
//            log.info("===== quizShowCR 내용 확인 =====");
//            log.info("showName: {}", quizShowCR.getShowName());
//            log.info("category: {}", quizShowCR.getCategory());
//            log.info("showDescription: {}", quizShowCR.getShowDescription());
//            log.info("useCustomImage: {}", quizShowCR.isUseCustomImage());
//            log.info("quizzes.size: {}", quizShowCR.getQuizzes().size());
//
//            if (imageFile != null) {
//                quizShowCR.setImageFile(imageFile);
//            }
//
//            // Service 호출
//            log.info("로그 확인" + data);
//            QuizShowDTO quizShowDTO = quizShowService.create(quizShowCR, securityUser.getId());
//            return RsData.of("200", "게시글 생성 완료", new QuizShowResponse(quizShowDTO));
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("로그로그로그로그로그로그로그로그로그로그로그로그");
//            return RsData.of("500", "게시글 생성 중 오류가 발생했습니다: " + e.getMessage(), null);
//        }
//    }

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
    public RsData<QuizShowResponse> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null) {
            return RsData.of("401", "로그인이 필요한 서비스입니다.", null);
        }

        try {
            QuizShowDTO deletedQuizShow = this.quizShowService.delete(id, securityUser.getId());
            return RsData.of("200", "게시글 삭제 완료", new QuizShowResponse(deletedQuizShow));
        } catch (IllegalStateException e) {
            return RsData.of("403", e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            return RsData.of("404", e.getMessage(), null);
        }
    }

    private final FileService fileService;

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public RsData<QuizShowResponseDTO> create(
//            @Valid @RequestPart("data") QuizShowCreateDTO createDTO,
//            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
//            @AuthenticationPrincipal SecurityUser securityUser) {
//
//        if (imageFile != null) {
//            // 이미지 처리 로직
//            String imagePath = fileService.saveImage(imageFile);
//            createDTO.setSelectedImagePath(imagePath);
//        }
//
//        QuizShowResponseDTO response = quizShowService.create(createDTO, securityUser.getId());
//        return RsData.of("200", "퀴즈쇼가 생성되었습니다.", response);
//    }

    private final UserService userService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RsData<QuizShowResponseDTO>> create(
            @Valid @RequestPart("data") QuizShowCreateDTO createDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        log.info("--------------------------------1");
        try {
            // 1. 인증 처리
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        RsData.of("403", "엑세스 토큰이 없습니다.", null)
                );
            }

            log.info("--------------------------------2");
            SiteUser user = userService.getSiteUserFromAccessToken(accessToken);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        RsData.of("403", "유효하지 않은 사용자입니다.", null)
                );
            }
            log.info("--------------------------------3");

            // 2. 이미지 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imagePath = fileService.saveImage(imageFile);
                    createDTO.setSelectedImagePath(imagePath);
                    createDTO.setUseCustomImage(true);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            RsData.of("400", "이미지 처리 중 오류가 발생했습니다.", null)
                    );
                }log.info("--------------------------------4");
            } else {
                createDTO.setSelectedImagePath(createDTO.getEffectiveImagePath());
            }

            // 3. 퀴즈 타입 검증
            try {
                validateQuizTypes(createDTO.getQuizzes());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        RsData.of("400", "퀴즈 타입 검증 실패: " + e.getMessage(), null)
                );
            }
            log.info("--------------------------------5");

            // 4. 퀴즈쇼 생성
            QuizShowResponseDTO responseDTO = quizShowService.create(createDTO, user.getId());

            log.info("--------------------------------6");
            return ResponseEntity.ok(
                    RsData.of("200", "퀴즈쇼가 성공적으로 생성되었습니다.", responseDTO)
            );

        } catch (Exception e) {
            log.error("퀴즈쇼 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    RsData.of("500", "서버 오류: " + e.getMessage(), null)
            );
        }
    }

    private void validateQuizTypes(List<QuizCreateDTO> quizzes) {
        if (quizzes == null || quizzes.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 퀴즈가 필요합니다.");
        }

        quizzes.forEach(quiz -> {
            if (quiz.getQuizType() == null) {
                throw new IllegalArgumentException("퀴즈 타입은 필수입니다.");
            }
        });
    }
}