package com.crepusculum.loanapp.travel_loan_manager.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUploadUtil {

    private static final String UPLOAD_DIR = "uploads/profile-pictures/";

    public static String saveFile(MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Paths.get(UPLOAD_DIR + filename);

        Files.copy(file.getInputStream(), filePath);
        return "/uploads/profile-pictures/" + filename;
    }
}