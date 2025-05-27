package com.example.app_server.Steganography;

import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.SubscriptionDetails.SubscriptionRepository;
import com.example.app_server.UserAccountCreation.User;
import com.example.app_server.UserAccountCreation.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SteganographyService {

    private final Path fileStorageLocation;

    @Autowired
    private PythonEncryptionClient encryptionClient;

    @Autowired
    private StegoImageRepository stegoImageRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    public SteganographyService(FileStorageProperties properties) throws IOException {
        this.fileStorageLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    public File embedData(MultipartFile selfieImage, MultipartFile dataFile, String dnlId) throws Exception {
        BufferedImage originalImage = ImageUtils.readImage(selfieImage);
        byte[] encryptedData = encryptionClient.encrypt(dataFile.getBytes());

        BufferedImage stegoImage = ImageUtils.embedLSB(originalImage, encryptedData);

        String filename = "stego_output_" + System.currentTimeMillis() + ".png";
        File outputFile = fileStorageLocation.resolve(filename).toFile();
        ImageIO.write(stegoImage, "png", outputFile);

        Subscription subscription = subscriptionRepository.findByDnlId(dnlId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with DNL ID: " + dnlId));

        StegoImage stego = new StegoImage(
                selfieImage.getOriginalFilename(),
                dataFile.getOriginalFilename(),
                outputFile.getAbsolutePath(),
                subscription
        );
        stegoImageRepository.save(stego);

        return outputFile;
    }

    public byte[] extractData(MultipartFile stegoImageFile) throws Exception {
        BufferedImage stegoImage = ImageUtils.readImage(stegoImageFile);
        byte[] encryptedData = ImageUtils.extractLSB(stegoImage);
        return encryptionClient.decrypt(encryptedData);
    }
}