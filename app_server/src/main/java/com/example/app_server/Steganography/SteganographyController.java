package com.example.app_server.Steganography;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
@RestController
@RequestMapping("/api/stego")
public class SteganographyController {

    @Autowired
    private SteganographyService stegoService;

    @PostMapping("/embed")
    public ResponseEntity<String> embedData(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data") MultipartFile data,
            @RequestParam("dnlId") String dnlId) {
        try {
            File stegoImage = stegoService.embedData(image, data, dnlId);
            return ResponseEntity.ok("Data embedded. Image saved at: " + stegoImage.getAbsolutePath());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Embedding failed: " + e.getMessage());
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<byte[]> extractData(@RequestParam("image") MultipartFile image) {
        try {
            byte[] extractedData = stegoService.extractData(image);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"decrypted_data\"")
                    .body(extractedData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}