package com.lightningbid.file.web.controller;

import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.file.domain.enums.FileDomain;
import com.lightningbid.file.domain.model.File;
import com.lightningbid.file.service.FileService;
import com.lightningbid.file.web.dto.FileUploadResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/files")
@RestController
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<CommonResponseDto<List<FileUploadResponseDto>>> uploadFiles(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("domain") FileDomain domain) {

        List<FileUploadResponseDto> responseDto = fileService.uploadAndSaveFiles(files, domain);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponseDto.success(
                        HttpStatus.OK.value(),
                        "이미지가 성공적으로 업로드되었습니다.",
                        responseDto
                ));
    }

}
