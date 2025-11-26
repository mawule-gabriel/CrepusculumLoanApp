package com.crepusculum.loanapp.travel_loan_manager.config;

import com.cloudinary.Cloudinary;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Value("${cloudinary.max-file-size}")
    private long maxFileSize;

    @Value("${cloudinary.folder.profile-pictures}")
    private String profilePicturesFolder;

    @Value("${cloudinary.transformation.width}")
    private int transformationWidth;

    @Value("${cloudinary.transformation.height}")
    private int transformationHeight;

    @Value("${cloudinary.transformation.crop}")
    private String transformationCrop;

    @Value("${cloudinary.transformation.gravity}")
    private String transformationGravity;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", "true");

        return new Cloudinary(config);
    }
}