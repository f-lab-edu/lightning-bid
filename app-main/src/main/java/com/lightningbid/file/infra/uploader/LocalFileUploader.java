package com.lightningbid.file.infra.uploader;

import com.lightningbid.file.domain.enums.FileDomain;
import com.lightningbid.file.exception.FileEmptyException;
import com.lightningbid.file.exception.FileSaveFailedException;
import com.lightningbid.file.service.FileUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Profile("!prd")
@Primary
@Component
public class LocalFileUploader implements FileUploader {

    @Value("${file.image.upload-dir}")
    private String fileDir;

    @Override
    public String upload(MultipartFile image, FileDomain fileDomain) {
        if (image.isEmpty())
            throw new FileEmptyException();

        String ext = extractExtension(image.getOriginalFilename());
        String storedFileName = ext.isEmpty()
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + ext;

        try {
            Path saveDir = Paths.get(fileDir, fileDomain.getPath());
            Path savePath = saveDir.resolve(storedFileName);

            // 디렉토리 없으면 생성, 있으면 통과
            Files.createDirectories(saveDir);

            // 저장
            image.transferTo(savePath);

        } catch (IOException e) {
            throw new FileSaveFailedException();
        }

        return fileDomain.getPath() + storedFileName;
    }

    // ex) temp.png -> png return
    private String extractExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank())
            return "";

        originalFilename = originalFilename.strip();
        int dotIndex = originalFilename.lastIndexOf(".");

        // 점이 없거나, 맨 앞의 점이 마지막 점일 경우
        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1)
            return "";

        return originalFilename.substring(dotIndex + 1);
    }
}
