package com.ncsmap.common.file;

import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-url}")
    private String accessUrl;

    @Override
    public String storageProfileImage(MultipartFile file) {

        log.info("프로필 이미지 업로드 시작 originalFilename={}, contentType={}, size={}bytes",
                file != null ? file.getOriginalFilename() : null,
                file != null ? file.getContentType() : null,
                file != null ? file.getSize() : null);

        validateFile(file);

        try (InputStream inputStream = file.getInputStream()) {

            Path uploadPath = Path.of(uploadDir)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            log.info("업로드 디렉토리 확인 path={}", uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String storedFileName = UUID.randomUUID() + extension;

            Path filePath = uploadPath.resolve(storedFileName)
                    .normalize();

            log.debug("프로필 이미지 저장 경로 path={}", filePath);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = accessUrl + "/" + storedFileName;

            log.info("프로필 이미지 저장 성공 originalFilename={}, storedFileName={}, imageUrl={}",
                    originalFilename,
                    storedFileName,
                    imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("프로필 이미지 저장 실패 originalFilename={}, reason={}",
                    file != null ? file.getOriginalFilename() : null,
                    e.getMessage(),
                    e);

            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null) {
            log.warn("파일 검증 실패 reason=file is null");
            throw new BusinessException(ErrorCode.EMPTY_FILE);
        }

        if (file.isEmpty()) {
            log.warn("파일 검증 실패 reason=file is empty");
            throw new BusinessException(ErrorCode.EMPTY_FILE);
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {

            log.warn(
                    "파일 검증 실패 originalFilename={}, contentType={}",
                    file.getOriginalFilename(),
                    file.getContentType()
            );

            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }

        log.debug(
                "파일 검증 성공 originalFilename={}, contentType={}, size={}bytes",
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
        );
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }
}
