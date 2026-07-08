package com.ncsmap.common.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storageProfileImage(MultipartFile file);
}
