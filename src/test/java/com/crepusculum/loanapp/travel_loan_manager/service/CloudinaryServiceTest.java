package com.crepusculum.loanapp.travel_loan_manager.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.crepusculum.loanapp.travel_loan_manager.config.CloudinaryConfig;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.ImageUploadResponse;
import com.crepusculum.loanapp.travel_loan_manager.exception.InvalidImageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private CloudinaryConfig config;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @Test
    void uploadProfilePicture_Success() throws IOException {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getSize()).thenReturn(1024L);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        
        when(config.getMaxFileSize()).thenReturn(5 * 1024 * 1024L);
        when(config.getProfilePicturesFolder()).thenReturn("profiles");

        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://secure.url");
        uploadResult.put("public_id", "pid");
        uploadResult.put("format", "jpg");
        uploadResult.put("bytes", 1024);

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);


        ImageUploadResponse response = cloudinaryService.uploadProfilePicture(file);

        assertNotNull(response);
        assertEquals("https://secure.url", response.secureUrl());
        assertEquals("pid", response.publicId());
    }

    @Test
    void uploadProfilePicture_InvalidExtension_ThrowsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.pdf");

        assertThrows(InvalidImageException.class, () -> cloudinaryService.uploadProfilePicture(file));
    }

    @Test
    void uploadProfilePicture_TooLarge_ThrowsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getSize()).thenReturn(10 * 1024 * 1024L);
        when(config.getMaxFileSize()).thenReturn(5 * 1024 * 1024L);

        assertThrows(InvalidImageException.class, () -> cloudinaryService.uploadProfilePicture(file));
    }
}
