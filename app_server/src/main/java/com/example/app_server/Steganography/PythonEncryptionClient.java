package com.example.app_server.Steganography;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class PythonEncryptionClient {

    private final String pythonApi = "http://localhost:5000";

    public byte[] encrypt(byte[] plainData) throws IOException {
        return postBytes(pythonApi + "/encrypt", plainData);
    }

    public byte[] decrypt(byte[] encryptedData) throws IOException {
        return postBytes(pythonApi + "/decrypt", encryptedData);
    }

    private byte[] postBytes(String url, byte[] data) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(data);
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Python API error: " + conn.getResponseCode());
        }

        try (InputStream in = conn.getInputStream()) {
            return in.readAllBytes();
        }
    }
}

