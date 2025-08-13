package com.lightningbid.file.service;

import com.lightningbid.file.domain.enums.FileDomain;
import com.lightningbid.file.domain.model.File;
import com.lightningbid.file.domain.repository.FileRepository;
import com.lightningbid.file.exception.FileNotFoundException;
import com.lightningbid.file.exception.FileNotImageException;
import com.lightningbid.file.web.dto.FileUploadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileUploader fileUploader;

    private final FileRepository fileRepository;

    public List<FileUploadResponseDto> uploadAndSaveFiles(List<MultipartFile> files, FileDomain fileDomain) {

        List<FileUploadResponseDto> responseDto = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            validateFile(file, fileDomain);

            String storedFileUrl = fileUploader.upload(file, fileDomain);

            File savedFile = saveFileMetadata(file, storedFileUrl, fileDomain);

            responseDto.add(FileUploadResponseDto.builder()
                    .fileUuid(savedFile.getUuid())
                    .fileUrl(savedFile.getFileUrl())
                    .build());
        }

        return responseDto;
    }

    public File findFileByUuid(String uuid) {

        return fileRepository.findByUuid(uuid).orElseThrow(FileNotFoundException::new);
    }

    public void softDeleteFileByExcludingFileIdAndUserId(Long fileUuid, Long userId) {

        fileRepository.softDeleteByExcludingIdAndUserId(fileUuid, userId);
    }

    private File saveFileMetadata(MultipartFile multipartFile, String storedFileUrl, FileDomain fileDomain) {
        File file = File.builder()
                .originalFileName(multipartFile.getOriginalFilename())
                .uuid(extractStoredFileName(storedFileUrl).split("\\.")[0])
                .domain(fileDomain)
                .storedFileName(extractStoredFileName(storedFileUrl))
                .fileUrl(storedFileUrl)
                .mimeType(multipartFile.getContentType())
                .fileSize(multipartFile.getSize())
                .build();

        return fileRepository.save(file);
    }

    private void validateFile(MultipartFile file, FileDomain domain) {
        // ITEM(상품) 또는 PROFILE(프로필) 도메인인 경우, 이미지 파일만 허용
        if (domain == FileDomain.ITEM || domain == FileDomain.PROFILE) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image")) {
                log.error("이미지가 아닌 파일 업로드 시도: contentType={}, domain={}", contentType, domain);
                throw new FileNotImageException();
            }
        }

        // 다른 형식 추가할 시 로직 추가
    }

    // 파일명만 추출 ex) item/uuid_123.png -> uuid_123.png
    private String extractStoredFileName(String fileUrl) {
        int slashIndex = fileUrl.indexOf("/");
        if (slashIndex != -1) {
            return fileUrl.substring(slashIndex + 1);
        }
        return fileUrl;
    }
}
