package com.lurepilot.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageStorageServiceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void storesImageWithGeneratedSafeNameAndUrl() throws Exception {
        ImageStorageService service = new ImageStorageService(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "catch photo.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        var response = service.store(file);

        assertThat(response.url()).startsWith("/uploads/");
        assertThat(response.fileName()).endsWith(".png");
        assertThat(response.contentType()).isEqualTo("image/png");
        assertThat(response.size()).isEqualTo(3);
        assertThat(Files.readAllBytes(tempDirectory.resolve(response.fileName()))).containsExactly(1, 2, 3);
    }

    @Test
    void rejectsNonImageFiles() {
        ImageStorageService service = new ImageStorageService(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "notes".getBytes());

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only image files are supported");
    }
}
