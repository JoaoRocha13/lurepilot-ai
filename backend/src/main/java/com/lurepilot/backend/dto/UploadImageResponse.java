package com.lurepilot.backend.dto;

public record UploadImageResponse(
        String url,
        String fileName,
        String contentType,
        long size
) {
}
