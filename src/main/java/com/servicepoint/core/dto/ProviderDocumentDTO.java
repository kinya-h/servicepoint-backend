package com.servicepoint.core.dto;

import com.servicepoint.core.model.ProviderDocument;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class ProviderDocumentDTO {
    private Integer documentId;
    private String documentType;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private Timestamp uploadedAt;

    public static ProviderDocumentDTO fromEntity(ProviderDocument document) {
        ProviderDocumentDTO dto = new ProviderDocumentDTO();
        dto.setDocumentId(document.getDocumentId());
        dto.setDocumentType(document.getDocumentType());
        dto.setFileName(document.getFileName());
        dto.setFileUrl(document.getFileUrl());
        dto.setFileSize(document.getFileSize());
        dto.setUploadedAt(document.getUploadedAt());
        return dto;
    }
}