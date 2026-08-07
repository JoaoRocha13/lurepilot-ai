package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.UploadImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.Locale;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final long MAX_IMAGE_SIZE = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final Path storageDirectory;

    @Value("${lurepilot.uploads.public-base-url:/uploads}")
    private String publicBaseUrl;

    public ImageStorageService(@Value("${lurepilot.uploads.directory:uploads}") String directory) {
        this.storageDirectory = Paths.get(directory).toAbsolutePath().normalize();
    }

    public UploadImageResponse store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }

        String contentType = file.getContentType();
        String normalizedContentType = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        if (!ALLOWED_CONTENT_TYPES.contains(normalizedContentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG, PNG, WEBP and GIF images are supported");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image must be 10 MB or smaller");
        }

        String extension = extensionFor(normalizedContentType);
        String fileName = UUID.randomUUID() + extension;
        Path target = storageDirectory.resolve(fileName).normalize();

        if (!target.startsWith(storageDirectory)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image file name");
        }

        try {
            Files.createDirectories(storageDirectory);
            Path temporaryTarget = Files.createTempFile(storageDirectory, ".upload-", ".tmp");
            try {
                Files.copy(file.getInputStream(), temporaryTarget, StandardCopyOption.REPLACE_EXISTING);
                try {
                    Files.move(temporaryTarget, target, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(temporaryTarget, target);
                }
            } finally {
                Files.deleteIfExists(temporaryTarget);
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image", ex);
        }

        return new UploadImageResponse(publicUrl(fileName), fileName, normalizedContentType, file.getSize());
    }

    public Path getStorageDirectory() {
        return storageDirectory;
    }

    private String publicUrl(String fileName) {
        String baseUrl = publicBaseUrl == null || publicBaseUrl.isBlank() ? "/uploads" : publicBaseUrl.trim();
        return baseUrl.replaceAll("/+$", "") + "/" + fileName;
    }

    private String extensionFor(String contentType) {
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
