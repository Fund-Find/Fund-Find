package com.example.domain.quizShow.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final String uploadDir = "uploads";

    // 생성자에서 업로드 디렉토리 초기화
    public FileService() {
        try {
            // 디렉토리가 없으면 생성
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("업로드 디렉토리가 생성되었습니다: {}", uploadPath);
            }
        } catch (IOException e) {
            log.error("업로드 디렉토리 생성 실패", e);
            throw new RuntimeException("업로드 디렉토리 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // 파일 저장 메서드
    public String storeFile(MultipartFile file) {
        try {
            // 원본 파일명에서 안전한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
            }
            String storedFilename = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

            // uploadDir 로그 출력
            log.info("Upload Directory: {}", uploadDir);

            // 저장 경로 설정
            Path filePath = Paths.get(uploadDir).resolve(storedFilename).toAbsolutePath();

            // filePath 로그 출력
            log.info("Resolved File Path: {}", filePath);

            // 파일 저장
            file.transferTo(filePath.toFile());
            log.info("파일 저장 완료: {}", filePath);

            // HTTP URL 반환
            return "http://localhost:8080/uploads/" + storedFilename;
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 파일 삭제 메서드
    public void deleteFile(String filePath) {
        try {
            // URL 형식인지 확인하고 로컬 경로로 변환
            if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                filePath = Paths.get(uploadDir).resolve(fileName).toAbsolutePath().toString();
            }

            // 파일 삭제
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("파일 삭제 완료: {}", path);
            } else {
                log.warn("삭제하려는 파일이 존재하지 않습니다: {}", path);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패", e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 기존의 saveImage 메서드 리팩토링
    public String saveImage(MultipartFile file) {
        return storeFile(file); // storeFile 메서드를 재사용
    }
}
