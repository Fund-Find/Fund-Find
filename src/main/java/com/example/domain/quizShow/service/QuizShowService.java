package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.entity.*;
import com.example.domain.quizShow.repository.*;
import com.example.domain.quizShow.request.QuizRequest;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.request.QuizSubmitRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.response.QuizSubmitResponse;
import com.example.domain.quizShow.validator.QuizValidator;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.entity.UserQuizResult;
import com.example.domain.user.repository.UserQuizResultRepository;
import com.example.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuizShowService {
    private final QuizShowRepository quizShowRepository;
    private final QuizShowCategoryRepository quizCategoryRepository;
    private final QuizRepository quizRepository;
    private final QuizValidator quizValidator;
    private final QuizTypeRepository quizTypeRepository;
    private final UserService userService;
    private final QuizAnswerRepository quizAnswerRepository;
    private final UserQuizResultRepository userQuizResultRepository;

    @Value("${custom.upload.dir}")
    private String uploadDir;

    public QuizShowListResponse getList(Pageable pageable) {
        Page<QuizShow> quizShowPage = this.quizShowRepository.findAllWithQuizzesAndChoices(pageable);

        List<QuizShowDTO> quizShows = quizShowPage.getContent().stream()
                .map(QuizShowDTO::new)
                .collect(Collectors.toList());

        return new QuizShowListResponse(quizShows,
                quizShowPage.getTotalElements(),
                quizShowPage.getTotalPages(),
                quizShowPage.getNumber());
    }

    public QuizShowDTO getQuizShow(Long id) {
        try {
            QuizShow quizShow = quizShowRepository.findByIdWithQuizzesAndChoices(id)
                    .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈쇼를 찾을 수 없습니다."));

            // 선택지 순서 랜덤화
            for (Quiz quiz : quizShow.getQuizzes()) {
                Collections.shuffle(quiz.getChoices());
            }

            return new QuizShowDTO(quizShow);
        } catch (Exception e) {
            log.error("퀴즈쇼 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("퀴즈쇼 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public QuizSubmitResponse submitAndSaveResult(
            Long quizShowId,
            List<QuizSubmitRequest.QuizAnswer> answers,
            Long userId) {

        // 퀴즈쇼 조회
        QuizShow quizShow = quizShowRepository.findByIdWithQuizzesAndChoices(quizShowId)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        // 사용자 조회
        SiteUser user = userService.getUser(userId);

        // 답안 채점 및 결과 저장
        Map<Long, Boolean> results = new HashMap<>();
        Map<Long, Integer> correctAnswers = new HashMap<>();
        int totalScore = 0;

        // 각 답안 처리
        for (QuizSubmitRequest.QuizAnswer answer : answers) {
            Quiz quiz = quizShow.getQuizzes().stream()
                    .filter(q -> q.getId().equals(answer.getQuizId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("퀴즈를 찾을 수 없습니다."));

            // 정답 찾기
            int correctAnswerIndex = findCorrectAnswerIndex(quiz);
            correctAnswers.put(quiz.getId(), correctAnswerIndex);

            // 정답 확인
            boolean isCorrect = answer.getAnswer() == correctAnswerIndex;
            results.put(quiz.getId(), isCorrect);

            if (isCorrect) {
                totalScore += quiz.getQuizScore();
            }

            // 답안 기록 저장
            saveQuizAnswer(quiz, user, answer.getAnswer(), isCorrect);
        }

        // 최종 결과 저장
        saveUserQuizResult(user, quizShow, totalScore);

        return QuizSubmitResponse.builder()
                .score(totalScore)
                .results(results)
                .correctAnswers(correctAnswers)
                .build();
    }

    private int findCorrectAnswerIndex(Quiz quiz) {
        for (int i = 0; i < quiz.getChoices().size(); i++) {
            if (quiz.getChoices().get(i).getIsCorrect()) {
                return i;
            }
        }
        throw new IllegalStateException("정답이 설정되지 않은 퀴즈가 있습니다.");
    }

    private void saveQuizAnswer(Quiz quiz, SiteUser user, int userAnswer, boolean isCorrect) {
        QuizAnswer quizAnswer = QuizAnswer.builder()
                .quiz(quiz)
                .user(user)
                .userAnswer(String.valueOf(userAnswer))
                .isCorrect(isCorrect)
                .answeredAt(LocalDateTime.now())
                .build();

        quizAnswerRepository.save(quizAnswer);
    }

    private void saveUserQuizResult(SiteUser user, QuizShow quizShow, int totalScore) {
        UserQuizResult quizResult = UserQuizResult.builder()
                .user(user)
                .quizShow(quizShow)
                .score(totalScore)
                .build();

        userQuizResultRepository.save(quizResult);
    }

    @Transactional
    public QuizShowDTO create(@Valid QuizShowCreateRequest request) {
        String imagePath = null;
        if (request.isUseCustomImage() && request.getImageFile() != null) {
            imagePath = saveImage(request.getImageFile());
        }

        QuizShow quizShow = QuizShow.builder()
                .showName(request.getShowName())
                .category(request.getCategory())
                .showDescription(request.getShowDescription())
                .totalQuizCount(request.getTotalQuizCount())
                .totalScore(request.getTotalScore())
                .view(0)
                .votes(new HashSet<>())
                .customImagePath(imagePath)
                .useCustomImage(request.isUseCustomImage())
                .build();

        quizShowRepository.save(quizShow);

        if (request.getQuizzes() != null) {
            createQuizzes(quizShow, request.getQuizzes());
        }

        return new QuizShowDTO(quizShow);
    }

    @Transactional
    public QuizShowDTO modify(Long id, QuizShowModifyRequest request) {
        QuizShow quizShow = quizShowRepository.findByIdWithQuizzesAndChoices(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        String imagePath = quizShow.getCustomImagePath();
        if (request.isUseCustomImage()) {
            if (request.getImageFile() != null) {
                if (imagePath != null) {
                    deleteImage(imagePath);
                }
                imagePath = saveImage(request.getImageFile());
            }
        } else {
            if (imagePath != null) {
                deleteImage(imagePath);
                imagePath = null;
            }
        }

        QuizShow updatedQuizShow = quizShow.toBuilder()
                .showName(request.getShowName())
                .category(request.getCategory())
                .showDescription(request.getShowDescription())
                .totalQuizCount(request.getTotalQuizCount())
                .totalScore(request.getTotalScore())
                .customImagePath(imagePath)
                .useCustomImage(request.isUseCustomImage())
                .build();

        quizShowRepository.save(updatedQuizShow);

        if (request.getQuizzes() != null) {
            updateQuizzes(updatedQuizShow, request.getQuizzes());
        }

        return new QuizShowDTO(updatedQuizShow);
    }

    @Transactional
    public QuizShowDTO delete(Long id) {
        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        if (quizShow.isUseCustomImage() && quizShow.getCustomImagePath() != null) {
            deleteImage(quizShow.getCustomImagePath());
        }

        QuizShowDTO quizShowDTO = new QuizShowDTO(quizShow);
        quizShowRepository.delete(quizShow);

        return quizShowDTO;
    }

    private void createQuizzes(QuizShow quizShow, List<QuizRequest> quizRequests) {
        for (QuizRequest quizReq : quizRequests) {
            QuizType quizType = quizTypeRepository.findById(quizReq.getQuizTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

            Quiz quiz = Quiz.builder()
                    .quizShow(quizShow)
                    .quizContent(quizReq.getQuizContent())
                    .quizScore(quizReq.getQuizScore())
                    .quizType(quizType)  // 퀴즈 타입 설정
                    .choices(new ArrayList<>())
                    .build();

            createChoices(quiz, quizReq.getChoices());
            quizValidator.validateQuiz(quiz, quizReq.getQuizTypeId());
            quizRepository.save(quiz);
        }
    }

    private void createChoices(Quiz quiz, List<QuizChoice> choices) {
        if (choices != null) {
            for (QuizChoice choice : choices) {
                QuizChoice newChoice = QuizChoice.builder()
                        .quiz(quiz)
                        .choiceContent(choice.getChoiceContent())
                        .isCorrect(choice.getIsCorrect())
                        .build();
                quiz.getChoices().add(newChoice);
            }
        }
    }

    private void updateQuizzes(QuizShow quizShow, List<QuizRequest> quizRequests) {
        for (QuizRequest quizRequest : quizRequests) {
            if (quizRequest.getId() == null) {
                createNewQuiz(quizShow, quizRequest);
            } else {
                Quiz existingQuiz = quizRepository.findByIdWithChoices(quizRequest.getId())
                        .orElseThrow(() -> new EntityNotFoundException("퀴즈를 찾을 수 없습니다."));

                if (Boolean.TRUE.equals(quizRequest.getIsDeleted())) {
                    quizRepository.delete(existingQuiz);
                } else {
                    updateExistingQuiz(existingQuiz, quizRequest);
                }
            }
        }
    }

    private Quiz createNewQuiz(QuizShow quizShow, QuizRequest request) {
        QuizType quizType = quizTypeRepository.findById(request.getQuizTypeId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        Quiz quiz = Quiz.builder()
                .quizShow(quizShow)
                .quizContent(request.getQuizContent())
                .quizScore(request.getQuizScore())
                .quizType(quizType)
                .choices(new ArrayList<>())
                .build();

        createChoices(quiz, request.getChoices());
        quizValidator.validateQuiz(quiz, request.getQuizTypeId());
        return quizRepository.save(quiz);
    }

    private void updateExistingQuiz(Quiz quiz, QuizRequest request) {
        QuizType quizType = quizTypeRepository.findById(request.getQuizTypeId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

        quiz.getChoices().clear();
        Quiz updatedQuiz = quiz.toBuilder()
                .quizContent(request.getQuizContent())
                .quizScore(request.getQuizScore())
                .quizType(quizType)
                .build();

        createChoices(updatedQuiz, request.getChoices());
        quizValidator.validateQuiz(updatedQuiz, request.getQuizTypeId());
        quizRepository.save(updatedQuiz);
    }

    private String saveImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        }
    }

    private void deleteImage(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("이미지 삭제에 실패했습니다.", e);
        }
    }
}