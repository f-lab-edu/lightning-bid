package com.lightningbid.file.web.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponseDto {
    private String fileUuid;
    private String fileUrl;
}
