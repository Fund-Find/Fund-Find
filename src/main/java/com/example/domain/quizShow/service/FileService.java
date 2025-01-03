package com.example.domain.quizShow.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String saveImage(MultipartFile file) {
        log.info("파일 저장 시작: {}", file.getOriginalFilename());
        if (file == null || file.isEmpty()) {
            log.warn("빈 파일이 전달됨");
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        try {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 전체 경로 생성 (uploads/filename)
            String fullPath = uploadDir + "/" + filename;

            // 파일 저장
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            log.info("파일 저장 완료: {}", filename);
            return fullPath;

        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
