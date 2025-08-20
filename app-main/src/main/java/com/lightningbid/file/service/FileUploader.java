package com.lightningbid.file.service;


import com.lightningbid.file.domain.enums.FileDomain;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String upload(MultipartFile multipartFile, FileDomain fileDomain);
}
