package com.crepusculum.loanapp.travel_loan_manager.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.crepusculum.loanapp.travel_loan_manager.config.CloudinaryConfig;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.ImageUploadResponse;
import com.crepusculum.loanapp.travel_loan_manager.exception.ImageUploadException;
import com.crepusculum.loanapp.travel_loan_manager.exception.InvalidImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final CloudinaryConfig config;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    public ImageUploadResponse uploadProfilePicture(MultipartFile file) {
        validateImage(file);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", config.getProfilePicturesFolder(),
                            "transformation", String.format("c_%s,w_%d,h_%d,g_%s",
                                    config.getTransformationCrop(),
                                    config.getTransformationWidth(),
                                    config.getTransformationHeight(),
                                    config.getTransformationGravity())
                    )
            );

            return new ImageUploadResponse(
                    (String) uploadResult.get("secure_url"),
                    (String) uploadResult.get("public_id"),
                    (String) uploadResult.get("format"),
                    ((Number) uploadResult.get("bytes")).longValue()
            );
        } catch (IOException e) {
            log.error("Failed to upload profile picture", e);
            throw new ImageUploadException("Failed to upload profile picture", e);
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Successfully deleted image with publicId: {}", publicId);
        } catch (IOException e) {
            log.error("Failed to delete image with publicId: {}", publicId, e);
            throw new ImageUploadException("Failed to delete image", e);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Profile picture is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageException("Only JPEG, PNG, and WebP images are allowed");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !hasValidExtension(filename)) {
            throw new InvalidImageException("Invalid file extension");
        }

        if (file.getSize() > config.getMaxFileSize()) {
            throw new InvalidImageException(String.format("Image size must be less than %d MB",
                    config.getMaxFileSize() / (1024 * 1024)));
        }
    }

    private boolean hasValidExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}