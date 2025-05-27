package com.example.app_server.Steganography;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static BufferedImage readImage(MultipartFile file) throws IOException {
        return ImageIO.read(file.getInputStream());
    }

    public static BufferedImage embedLSB(BufferedImage image, byte[] data) {
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage stego = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        byte[] lengthPrefix = ByteBuffer.allocate(4).putInt(data.length).array();
        byte[] fullData = ByteBuffer.allocate(lengthPrefix.length + data.length)
                .put(lengthPrefix).put(data).array();

        int dataIndex = 0, bitIndex = 0;

        outer:
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = image.getRGB(x, y);
                int[] colors = { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };

                for (int i = 0; i < 3; i++) {
                    if (dataIndex >= fullData.length) break outer;
                    int bit = (fullData[dataIndex] >> (7 - bitIndex)) & 1;
                    colors[i] = (colors[i] & 0xFE) | bit;

                    bitIndex++;
                    if (bitIndex == 8) {
                        bitIndex = 0;
                        dataIndex++;
                    }
                }

                int newRgb = (colors[0] << 16) | (colors[1] << 8) | colors[2];
                stego.setRGB(x, y, newRgb);
            }
        }

        return stego;
    }

    public static byte[] extractLSB(BufferedImage image) {
        int w = image.getWidth(), h = image.getHeight();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int byteVal = 0, bitCount = 0, totalBytes = -1;
        List<Byte> output = new ArrayList<>();

        outer:
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = image.getRGB(x, y);
                int[] colors = { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };

                for (int i = 0; i < 3; i++) {
                    byteVal = (byteVal << 1) | (colors[i] & 1);
                    bitCount++;

                    if (bitCount == 8) {
                        output.add((byte) byteVal);
                        bitCount = 0;
                        byteVal = 0;

                        if (output.size() == 4 && totalBytes == -1) {
                            byte[] lenBytes = new byte[4];
                            for (int j = 0; j < 4; j++) lenBytes[j] = output.get(j);
                            totalBytes = ByteBuffer.wrap(lenBytes).getInt();
                        }

                        if (totalBytes != -1 && output.size() == totalBytes + 4) break outer;
                    }
                }
            }
        }

        byte[] result = new byte[totalBytes];
        for (int i = 0; i < totalBytes; i++) {
            result[i] = output.get(i + 4);
        }
        return result;
    }
}
