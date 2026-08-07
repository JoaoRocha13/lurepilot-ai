package com.lurepilot.backend.service;

import com.lurepilot.backend.dto.UploadImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path storageDirectory;

    public ImageStorageService(@Value("${lurepilot.uploads.directory:uploads}") String directory) {
        this.storageDirectory = Paths.get(directory).toAbsolutePath().normalize();
    }

    public UploadImageResponse store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are supported");
        }

        String extension = extensionFor(file.getOriginalFilename(), contentType);
        String fileName = UUID.randomUUID() + extension;
        Path target = storageDirectory.resolve(fileName).normalize();

        if (!target.startsWith(storageDirectory)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image file name");
        }

        try {
            Files.createDirectories(storageDirectory);
            Files.copy(file.getInputStream(), target);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image", ex);
        }

        return new UploadImageResponse("/uploads/" + fileName, fileName, contentType, file.getSize());
    }

    public Path getStorageDirectory() {
        return storageDirectory;
    }

    private String extensionFor(String originalFilename, String contentType) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (extension != null && extension.matches("[A-Za-z0-9]{1,5}")) {
            return "." + extension.toLowerCase(Locale.ROOT);
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
