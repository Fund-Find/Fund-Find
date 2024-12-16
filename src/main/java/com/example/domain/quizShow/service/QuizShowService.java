package com.example.domain.quizShow.service;

import com.example.domain.quizShow.dto.QuizShowDTO;
import com.example.domain.quizShow.entity.*;
import com.example.domain.quizShow.repository.QuizRepository;
import com.example.domain.quizShow.repository.QuizShowCategoryRepository;
import com.example.domain.quizShow.repository.QuizShowRepository;
import com.example.domain.quizShow.repository.QuizTypeRepository;
import com.example.domain.quizShow.request.QuizRequest;
import com.example.domain.quizShow.request.QuizShowCreateRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.validator.QuizValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizShowService {
    private final QuizShowRepository quizShowRepository;
    private final QuizShowCategoryRepository quizCategoryRepository;
    private final QuizRepository quizRepository;
    private final QuizValidator quizValidator;
    private final QuizTypeRepository quizTypeRepository;

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
        QuizShow quizShow = quizShowRepository.findByIdWithQuizzesAndChoices(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈쇼를 찾을 수 없습니다."));
        return new QuizShowDTO(quizShow);
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
            QuizShowCategory quizQuizShowCategory = quizCategoryRepository.findById(quizReq.getQuizTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("퀴즈 카테고리를 찾을 수 없습니다."));

            Quiz quiz = Quiz.builder()
                    .quizShow(quizShow)
                    .quizContent(quizReq.getQuizContent())
                    .quizScore(quizReq.getQuizScore())
                    .choices(new ArrayList<>())
                    .build();

            createChoices(quiz, quizReq.getChoices());
            quizValidator.validateQuiz(quiz);
            quizRepository.save(quiz);
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
        QuizShowCategory category = quizCategoryRepository.findById(request.getQuizTypeId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 카테고리를 찾을 수 없습니다."));

        Quiz quiz = Quiz.builder()
                .quizShow(quizShow)
                .quizContent(request.getQuizContent())
                .quizScore(request.getQuizScore())
                .choices(new ArrayList<>())
                .build();

        createChoices(quiz, request.getChoices());
        quizValidator.validateQuiz(quiz);
        return quizRepository.save(quiz);
    }

    private void updateExistingQuiz(Quiz quiz, QuizRequest request) {
        QuizShowCategory category = quizCategoryRepository.findById(request.getQuizTypeId())
                .orElseThrow(() -> new EntityNotFoundException("퀴즈 카테고리를 찾을 수 없습니다."));

        quiz.getChoices().clear();
        Quiz updatedQuiz = quiz.toBuilder()
                .quizContent(request.getQuizContent())
                .quizScore(request.getQuizScore())
                .build();

        createChoices(updatedQuiz, request.getChoices());
        quizValidator.validateQuiz(updatedQuiz);
        quizRepository.save(updatedQuiz);
    }

    private void createChoices(Quiz quiz, List<String> choiceContents) {
        if (choiceContents != null) {
            for (String choiceContent : choiceContents) {
                QuizChoice choice = QuizChoice.builder()
                        .quiz(quiz)
                        .choiceContent(choiceContent)
                        .build();
                quiz.getChoices().add(choice);
            }
        }
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