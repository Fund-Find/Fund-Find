package com.example.domain.quizShow.service;

import com.example.domain.quizShow.constant.QuizTypeEnum;
import com.example.domain.quizShow.dto.*;
import com.example.domain.quizShow.entity.*;
import com.example.domain.quizShow.repository.*;
import com.example.domain.quizShow.request.QuizRequest;
import com.example.domain.quizShow.request.QuizShowModifyRequest;
import com.example.domain.quizShow.request.QuizSubmitRequest;
import com.example.domain.quizShow.response.QuizShowListResponse;
import com.example.domain.quizShow.response.QuizSubmitResponse;
import com.example.domain.quizShow.validator.QuizValidator;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.entity.UserQuizResult;
import com.example.domain.user.repository.UserQuizResultRepository;
import com.example.domain.user.repository.UserRepository;
import com.example.domain.user.service.UserService;
import com.example.global.security.SecurityUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private static final int MAX_ANSWERS_PER_USER = 10;

    @Value("${custom.upload.dir}")
    private String uploadDir;

    public QuizShowListResponse getList(Pageable pageable) {
        Page<QuizShow> quizShowPage = this.quizShowRepository.findAllWithQuizzesAndChoices(pageable);

        List<QuizShowDTO> quizShows = quizShowPage.getContent().stream()
                .map(QuizShowDTO::new)
                .collect(Collectors.toList());

        // 퀴즈 타입 정보 조회
        List<QuizTypeDTO> quizTypes = quizTypeRepository.findAll().stream()
                .map(type -> new QuizTypeDTO(type.getId(), type.getTypeName()))
                .collect(Collectors.toList());

        // 카테고리 조회 - Enum 기반으로 DTO 생성
        List<QuizShowCategoryDTO> categories = Arrays.stream(QuizShowCategoryEnum.values())
                .map(QuizShowCategoryDTO::fromEnum)
                .collect(Collectors.toList());

        return new QuizShowListResponse(
                quizShows,
                quizTypes,
                categories,
                quizShowPage.getTotalElements(),
                quizShowPage.getTotalPages(),
                quizShowPage.getNumber()
        );
    }

    @Transactional
    public QuizShowDTO getQuizShow(Long id) {
        try {
            // 1. 퀴즈쇼와 퀴즈 목록을 가져옵니다
            QuizShow quizShow = quizShowRepository.findByIdWithQuizzes(id)
                    .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈쇼를 찾을 수 없습니다."));

            // 2. 현재 로그인한 사용자의 추천 여부 확인 (이 부분 추가)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
                SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                boolean hasVoted = quizShow.checkUserVoted(securityUser.getId());
                quizShow = quizShow.toBuilder()
                        .hasVoted(hasVoted)
                        .build();
            }

            // 3. 마지막 조회 시간 체크 및 조회수 증가 로직
            LocalDateTime now = LocalDateTime.now();
            if (quizShow.getLastViewedAt() == null ||
                    Duration.between(quizShow.getLastViewedAt(), now).getSeconds() > 5) {

                quizShow = quizShowRepository.save(
                        quizShow.toBuilder()
                                .view(quizShow.getView() + 1)
                                .lastViewedAt(now)
                                .build()
                );
            }

            // 4. 해당 퀴즈쇼의 모든 퀴즈와 선택지를 가져옵니다
            List<Quiz> quizzesWithChoices = quizRepository.findQuizzesWithChoicesByQuizShowId(id);

            // 5. 선택지 순서 랜덤화
            if (quizShow.getQuizzes() != null) {
                quizShow.getQuizzes().forEach(quiz -> {
                    if (quiz.getChoices() != null) {
                        Collections.shuffle(quiz.getChoices());
                    }
                });
            }

            // 6. 랜덤화된 퀴즈 데이터를 퀴즈쇼에 설정
            quizShow = quizShow.toBuilder()
                    .quizzes(quizzesWithChoices)
                    .build();

            return new QuizShowDTO(quizShow);
        } catch (Exception e) {
            log.error("퀴즈쇼 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("퀴즈쇼 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public QuizSubmitResponse submitAndSaveResult(Long quizShowId, List<QuizSubmitRequest.QuizAnswer> answers, Long userId) {
        try {
            QuizShow quizShow = quizShowRepository.findByIdWithQuizzes(quizShowId)
                    .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

            List<Quiz> quizzesWithChoices = quizRepository.findQuizzesWithChoicesByQuizShowId(quizShowId);
            Map<Long, Quiz> quizMap = quizzesWithChoices.stream()
                    .collect(Collectors.toMap(Quiz::getId, quiz -> quiz));

            SiteUser user = userService.getUser(userId);

            Map<Long, Boolean> results = new HashMap<>();
            Map<Long, Integer> correctAnswers = new HashMap<>();
            int totalScore = 0;

            // 각 답안 처리
            for (QuizSubmitRequest.QuizAnswer answer : answers) {
                Quiz quiz = quizMap.get(answer.getQuizId());
                if (quiz == null) {
                    throw new EntityNotFoundException("퀴즈를 찾을 수 없습니다.");
                }

                QuizTypeEnum quizType = quiz.getQuizType().getType();
                boolean isCorrect;

                switch (quizType) {
                    case MULTIPLE_CHOICE:
                    case TRUE_FALSE:
                        // ID로 선택지를 찾아 정답 여부 확인
                        isCorrect = quiz.getChoices().stream()
                                .filter(choice -> choice.getId() != null &&
                                        choice.getId() == answer.getChoiceId().longValue())
                                .anyMatch(QuizChoice::getIsCorrect);
                        break;
                    case SUBJECTIVE:
                    case SHORT_ANSWER:
                        // 기존 로직 유지
                        String userAnswer = answer.getTextAnswer();
                        isCorrect = quiz.getChoices().stream()
                                .anyMatch(choice -> choice.getChoiceContent()
                                        .trim()
                                        .equalsIgnoreCase(userAnswer.trim()));
                        break;
                    default:
                        throw new IllegalArgumentException("지원하지 않는 퀴즈 타입입니다.");
                }

                results.put(quiz.getId(), isCorrect);

                // 새로운 답안 저장
                QuizAnswer quizAnswer;

                if (quizType == QuizTypeEnum.MULTIPLE_CHOICE || quizType == QuizTypeEnum.TRUE_FALSE) {
                    if (answer.getChoiceId() == null) {
                        throw new IllegalArgumentException("선택형 퀴즈는 choiceId가 필수입니다.");
                    }
                    quizAnswer = QuizAnswer.builder()
                            .quiz(quiz)
                            .user(user)
                            .userAnswer(answer.getChoiceId().toString()) // choiceId를 문자열로 저장
                            .isCorrect(isCorrect)
                            .answeredAt(LocalDateTime.now())
                            .build();
                } else if (quizType == QuizTypeEnum.SUBJECTIVE || quizType == QuizTypeEnum.SHORT_ANSWER) {
                    if (answer.getTextAnswer() == null || answer.getTextAnswer().trim().isEmpty()) {
                        throw new IllegalArgumentException("주관식 퀴즈는 텍스트 답변이 필수입니다.");
                    }
                    quizAnswer = QuizAnswer.builder()
                            .quiz(quiz)
                            .user(user)
                            .userAnswer(answer.getTextAnswer().trim()) // ChoiceContent에 텍스트 답변 저장
                            .isCorrect(isCorrect)
                            .answeredAt(LocalDateTime.now())
                            .build();
                } else {
                    throw new IllegalArgumentException("지원하지 않는 퀴즈 타입입니다.");
                }

// 답안 저장
                quizAnswerRepository.save(quizAnswer);


                if (isCorrect) {
                    totalScore += quiz.getQuizScore();
                }
            }

            // 오래된 답안 삭제 (선택적)
            quizAnswerRepository.deleteOldAnswers(userId, MAX_ANSWERS_PER_USER);

            // 이전 결과가 있는지 확인
            Optional<UserQuizResult> existingResult = userQuizResultRepository
                    .findByUserIdAndQuizShowId(userId, quizShowId);

            if (existingResult.isPresent()) {
                // 이전 결과가 있으면 점수만 업데이트
                UserQuizResult updatedResult = existingResult.get().toBuilder()
                        .score(totalScore)
                        .build();
                userQuizResultRepository.save(updatedResult);
            } else {
                // 이전 결과가 없으면 새로 생성
                UserQuizResult newResult = UserQuizResult.builder()
                        .user(user)
                        .quizShow(quizShow)
                        .score(totalScore)
                        .build();
                userQuizResultRepository.save(newResult);
            }

            return QuizSubmitResponse.builder()
                    .score(totalScore)
                    .results(results)
                    .correctAnswers(correctAnswers)
                    .build();

        } catch (Exception e) {
            log.error("퀴즈 제출 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("퀴즈 제출 처리 중 오류가 발생했습니다.", e);
        }
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

    private final FileService fileService;

    @Transactional
    public QuizShowResponseDTO create(@Valid QuizShowCreateDTO createDTO, Long userId) {
        SiteUser creator = userService.getUser(userId);
        String imagePath = createDTO.getCustomImagePath(); // 컨트롤러에서 설정한 값 사용

        log.info("Service 진입 - customImagePath: {}, useCustomImage: {}",
                imagePath, createDTO.isUseCustomImage());

        QuizShow quizShow = QuizShow.builder()
                .showName(createDTO.getShowName())
                .category(createDTO.getCategory())
                .showDescription(createDTO.getShowDescription())
                .totalQuizCount(createDTO.getTotalQuizCount())
                .totalScore(createDTO.getTotalScore())
                .view(0)
                .votes(new HashSet<>())
                .customImagePath(imagePath)  // 컨트롤러에서 이미 저장된 경로 사용
                .useCustomImage(createDTO.isUseCustomImage())
                .creator(creator)
                .build();

        log.info("Entity 생성 - customImagePath: {}, useCustomImage: {}",
                quizShow.getCustomImagePath(), quizShow.isUseCustomImage());

        QuizShow savedQuizShow = quizShowRepository.save(quizShow);
        log.info("Entity 저장 완료 - customImagePath: {}, useCustomImage: {}",
                savedQuizShow.getCustomImagePath(), savedQuizShow.isUseCustomImage());

        if (createDTO.getQuizzes() != null) {
            createQuizzes(savedQuizShow, createDTO.getQuizzes());
        }

        return new QuizShowResponseDTO(savedQuizShow);
    }

    @Transactional
    public QuizShowDTO modify(Long id, QuizShowModifyRequest request) {
        // 1. 퀴즈쇼와 퀴즈 목록 조회
        QuizShow quizShow = quizShowRepository.findByIdWithQuizzes(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        // 2. 이미지 처리 로직
        String imagePath = quizShow.getCustomImagePath();
        if (request.isUseCustomImage()) {
            if (request.getImageFile() != null) {
                if (imagePath != null) {
                    fileService.deleteFile(imagePath);
                }
                imagePath = fileService.saveImage(request.getImageFile());
            }
        } else {
            if (imagePath != null) {
                fileService.deleteFile(imagePath);
                imagePath = null;
            }
        }

        // 3. 퀴즈쇼 업데이트
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

        // 4. 퀴즈 업데이트
        if (request.getQuizzes() != null) {
            // 기존 퀴즈와 선택지 정보 로드
            List<Quiz> existingQuizzes = quizRepository.findQuizzesWithChoicesByQuizShowId(id);
            Map<Long, Quiz> existingQuizMap = existingQuizzes.stream()
                    .collect(Collectors.toMap(Quiz::getId, quiz -> quiz));

            updateQuizzes(updatedQuizShow, request.getQuizzes(), existingQuizMap);
        }

        return new QuizShowDTO(updatedQuizShow);
    }

    // updateQuizzes 메소드도 수정
    private void updateQuizzes(QuizShow quizShow, List<QuizRequest> quizRequests, Map<Long, Quiz> existingQuizMap) {
        for (QuizRequest quizRequest : quizRequests) {
            if (quizRequest.getId() == null) {
                createNewQuiz(quizShow, quizRequest);
            } else {
                Quiz existingQuiz = existingQuizMap.get(quizRequest.getId());
                if (existingQuiz == null) {
                    throw new EntityNotFoundException("퀴즈를 찾을 수 없습니다.");
                }

                if (Boolean.TRUE.equals(quizRequest.getIsDeleted())) {
                    quizRepository.delete(existingQuiz);
                } else {
                    updateExistingQuiz(existingQuiz, quizRequest);
                }
            }
        }
    }

    // 권한 체크 메소드 추가
    @Transactional
    public boolean canDeleteQuizShow(Long quizShowId, Long userId) {
        QuizShow quizShow = quizShowRepository.findById(quizShowId)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        return quizShow.getCreator().getId().equals(userId);
    }

    // 삭제 메소드에서 권한 체크 활용
    @Transactional
    public QuizShowDTO delete(Long id, Long userId) {
        // 권한 체크
        if (!canDeleteQuizShow(id, userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        QuizShow quizShow = quizShowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        if (quizShow.isUseCustomImage() && quizShow.getCustomImagePath() != null) {
            fileService.deleteFile(quizShow.getCustomImagePath());
        }

        QuizShowDTO quizShowDTO = new QuizShowDTO(quizShow);
        quizShowRepository.delete(quizShow);

        return quizShowDTO;
    }

    private void createQuizzes(QuizShow quizShow, List<QuizCreateDTO> quizCreateDTOS) {
        for (QuizCreateDTO quizCreateDTO : quizCreateDTOS) {
            QuizType quizType = quizTypeRepository.findById(quizCreateDTO.getQuizTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("퀴즈 타입을 찾을 수 없습니다."));

            Quiz quiz = Quiz.builder()
                    .quizShow(quizShow)
                    .quizContent(quizCreateDTO.getQuizContent())
                    .quizScore(quizCreateDTO.getQuizScore())
                    .quizType(quizType)  // 퀴즈 타입 설정
                    .choices(new ArrayList<>())
                    .build();

            createChoices(quiz, quizCreateDTO.getChoices());
            quizValidator.validateQuiz(quiz, quizCreateDTO.getQuizTypeId());
            quizRepository.save(quiz);
        }
    }

    private void createChoices(Quiz quiz, List<QuizChoiceCreateDTO> choices) {
        if (choices != null) {
            for (QuizChoiceCreateDTO choice : choices) {
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

    @Transactional
    public QuizShowDTO toggleVote(Long quizShowId, Long userId) {
        QuizShow quizShow = quizShowRepository.findById(quizShowId)
                .orElseThrow(() -> new EntityNotFoundException("퀴즈쇼를 찾을 수 없습니다."));

        SiteUser user = userService.getUser(userId);
        QuizShow updatedQuizShow = quizShow.updateVoteStatus(user);

        return new QuizShowDTO(quizShowRepository.save(updatedQuizShow));
    }

//    public QuizShowDTO convertToDto(QuizShow quizShow) {
//        return new QuizShowDTO(
//                quizShow.getId(),
//                quizShow.getShowName(),
//                quizShow.getCategory(),
//                quizShow.getShowDescription(),
//                quizShow.getTotalQuizCount(),
//                quizShow.getTotalScore(),
//                Integer.valueOf(quizShow.getVotes().size()) // votes의 크기만 포함
//        );
//    }

//    @Transactional
//    public QuizShowResponseDTO create(QuizShowCreateDTO dto, Long userId) {
//        try {
//            QuizShow quizShow = mapToQuizShowEntity(dto, userId);
//            QuizShow savedQuizShow = quizShowRepository.save(quizShow);
//            return mapToQuizShowResponseDTO(savedQuizShow);
//        } catch (Exception e) {
//            log.error("퀴즈쇼 생성 중 오류 발생", e);
//            throw new RuntimeException("퀴즈쇼 생성에 실패했습니다.", e);
//        }
//    }

    private final UserRepository userRepository;

    private QuizShow mapToQuizShowEntity(QuizShowCreateDTO dto, Long userId) {
        SiteUser creator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        QuizShow quizShow = QuizShow.builder()
                .showName(dto.getShowName())
                .category(dto.getCategory())
                .showDescription(dto.getShowDescription())
                .totalQuizCount(dto.getTotalQuizCount())
                .totalScore(dto.getTotalScore())
                .useCustomImage(dto.isUseCustomImage())
                .creator(creator)
                .view(0)
                .build();

        List<Quiz> quizzes = mapToQuizEntities(dto.getQuizzes(), quizShow);
        quizShow.setQuizzes(quizzes);
        return quizShow;
    }

    private List<Quiz> mapToQuizEntities(List<QuizCreateDTO> dtos, QuizShow quizShow) {
        return dtos.stream()
                .map(dto -> createQuizEntity(dto, quizShow))
                .collect(Collectors.toList());
    }

    private Quiz createQuizEntity(QuizCreateDTO dto, QuizShow quizShow) {
        QuizType quizType = quizTypeRepository.findById(dto.getQuizType().getId())
                .orElseThrow(() -> new EntityNotFoundException("Quiz type not found"));

        Quiz quiz = Quiz.builder()
                .quizShow(quizShow)
                .quizContent(dto.getQuizContent())
                .quizScore(dto.getQuizScore())
                .quizType(quizType)
                .build();

        // choices 설정 추가
        List<QuizChoice> choices = mapToQuizChoiceEntities(dto.getChoices(), quiz);
        quiz.setChoices(choices);

        return quiz;
    }

    private QuizChoice createQuizChoiceEntity(QuizChoiceCreateDTO dto, Quiz quiz) {
        return QuizChoice.builder()
                .quiz(quiz)
                .choiceContent(dto.getChoiceContent())
                .isCorrect(dto.getIsCorrect())
                .build();
    }

    private List<Quiz> mapQuizzes(List<QuizCreateDTO> dtos, QuizShow quizShow) {
        return dtos.stream()
                .map(dto -> mapToQuizEntity(dto, quizShow))
                .collect(Collectors.toList());
    }

    private Quiz mapToQuizEntity(QuizCreateDTO dto, QuizShow quizShow) {
        try {
            QuizType quizType = quizTypeRepository.findById(dto.getQuizType().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Quiz type not found for type: %s", dto.getQuizType())
                    ));

            Quiz quiz = Quiz.builder()
                    .quizShow(quizShow)
                    .quizContent(dto.getQuizContent())
                    .quizScore(dto.getQuizScore())
                    .quizType(quizType)
                    .build();

            List<QuizChoice> choices = mapToQuizChoiceEntities(dto.getChoices(), quiz);
            quiz.addChoices(choices);
            return quiz;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid quiz type: " + dto.getQuizType(), e);
        }
    }

    // Entity → DTO 매핑
    private QuizShowResponseDTO mapToQuizShowResponseDTO(QuizShow quizShow) {
        return QuizShowResponseDTO.builder()
                .id(quizShow.getId())
                .showName(quizShow.getShowName())
                .category(quizShow.getCategory())
                .showDescription(quizShow.getShowDescription())
                .totalQuizCount(quizShow.getTotalQuizCount())
                .totalScore(quizShow.getTotalScore())
                .effectiveImagePath(quizShow.getEffectiveImagePath())
                .quizzes(mapToQuizResponseDTOs(quizShow.getQuizzes()))
                .hasVoted(getCurrentUserVoteStatus(quizShow))
                .voteCount(quizShow.getVotes().size())
                .view(quizShow.getView())
                .build();
    }

    private List<QuizResponseDTO> mapToQuizResponseDTOs(List<Quiz> quizzes) {
        return quizzes.stream()
                .map(this::mapToQuizResponseDTO)
                .collect(Collectors.toList());
    }

    private QuizResponseDTO mapToQuizResponseDTO(Quiz quiz) {
        return QuizResponseDTO.builder()
                .id(quiz.getId())
                .quizTypeId(quiz.getQuizType().getId())
                .quizContent(quiz.getQuizContent())
                .quizScore(quiz.getQuizScore())
                .choices(mapToQuizChoiceResponseDTOs(quiz.getChoices()))
                .build();
    }

    private List<QuizChoiceDTO> mapToQuizChoiceResponseDTOs(List<QuizChoice> choices) {
        return choices.stream()
                .map(QuizChoiceDTO::new)
                .collect(Collectors.toList());
    }

    private QuizChoiceResponseDTO mapToQuizChoiceResponseDTO(QuizChoice choice) {
        return QuizChoiceResponseDTO.builder()
                .id(choice.getId())
                .quizId(choice.getQuiz().getId())
                .choiceContent(choice.getChoiceContent())
                .build();
    }

    private List<QuizChoice> mapToQuizChoiceEntities(List<QuizChoiceCreateDTO> dtos, Quiz quiz) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(dto -> mapToQuizChoiceEntity(dto, quiz))
                .collect(Collectors.toList());
    }

    private QuizChoice mapToQuizChoiceEntity(QuizChoiceCreateDTO dto, Quiz quiz) {
        return QuizChoice.builder()
                .quiz(quiz)
                .choiceContent(dto.getChoiceContent())
                .isCorrect(dto.getIsCorrect())
                .build();
    }

    // 보안 관련 유틸리티 메서드
    private boolean getCurrentUserVoteStatus(QuizShow quizShow) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser)) {
            return false;
        }
        SecurityUser user = (SecurityUser) auth.getPrincipal();
        return quizShow.checkUserVoted(user.getId());
    }

}
