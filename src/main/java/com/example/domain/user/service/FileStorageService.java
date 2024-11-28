package com.example.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads"; // 기본 업로드 디렉토리 설정

    public FileStorageService() {
        try {
            // 디렉토리가 없으면 생성
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            // 파일 이름 처리 및 저장 파일 이름 생성
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
            }
            String storedFilename = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

            // 저장 경로 설정
            Path filePath = Paths.get(uploadDir).resolve(storedFilename).toAbsolutePath();

            // 파일 저장
            file.transferTo(filePath.toFile());

            // 저장된 파일 경로 반환
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath();
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage(), e);
        }
    }
}
