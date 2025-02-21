package com.uaa.labelmaker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uaa.labelmaker.model.ParamsDto;
import com.uaa.labelmaker.service.EpsToPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/eps")
public class EspToPdfController {

    private static final int IMAGE_WIDTH = 47;
    private static final int IMAGE_HEIGHT = 47;

    private final EpsToPdfService epsToPdfService;

    public EspToPdfController(EpsToPdfService epsToPdfService) {
        this.epsToPdfService = epsToPdfService;
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertEspToPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jsonFile") MultipartFile jsonFile,
            @RequestParam("importer") String importer,
            @RequestParam("producent") String producent,
            @RequestParam("country") String country,
            @RequestParam("tp") String tp,
            @RequestParam("gost") String gost,
            @RequestParam("garant") String garant,
            @RequestParam("sex") String sex,
            @RequestParam("color") String color
    ) {
        try {
            var params = new ParamsDto(importer, producent, country, tp, gost, garant, sex, color);

            // Convert EPS to PNG
            byte[] productDataForExport = epsToPdfService.createShoeStickers(file.getInputStream(), jsonFile, params);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", sex+ ".pdf");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(productDataForExport);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping("/convert2")
    public ResponseEntity<byte[]> convertEspToPdf2(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jsonFile") MultipartFile jsonFile,
            @RequestParam("importer") String importer,
            @RequestParam("producent") String producent,
            @RequestParam("country") String country,
            @RequestParam("tp") String tp,
            @RequestParam("gost") String gost,
            @RequestParam("garant") String garant,
            @RequestParam("sex") String sex,
            @RequestParam("color") String color
    ) {
        try {
            var params = new ParamsDto(importer, producent, country, tp, gost, garant, sex, color);

            // Convert EPS to PNG
            byte[] productDataForExport = epsToPdfService.createShoeStickers2(file.getInputStream(), jsonFile, params);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", sex+ ".pdf");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(productDataForExport);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/convert3")
    public ResponseEntity<byte[]> convertEspToPdf3(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jsonFile") MultipartFile jsonFile,
            @RequestParam("importer") String importer,
            @RequestParam("producent") String producent,
            @RequestParam("country") String country,
            @RequestParam("tp") String tp,
            @RequestParam("gost") String gost,
            @RequestParam("garant") String garant,
            @RequestParam("sex") String sex,
            @RequestParam("color") String color
    ) {
        try {
            var params = new ParamsDto(importer, producent, country, tp, gost, garant, sex, color);

            // Convert EPS to PNG
            byte[] productDataForExport = epsToPdfService.createShoeStickers3(file.getInputStream(), jsonFile, params);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", sex+ ".pdf");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(productDataForExport);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") List<MultipartFile> files) {
        StringBuilder response = new StringBuilder();

        for (MultipartFile file : files) {
            try {
                // Читаем содержимое файла
                String data = new String(file.getBytes());

                // Генерируем изображение
                BufferedImage image = generateImageFromData(extractRelevantData(data));


                // Получаем оригинальное имя файла и заменяем расширение на .png
                String originalFileName = file.getOriginalFilename();
                if (originalFileName != null) {
                    originalFileName = originalFileName.replaceAll("\\.[^.]+$", "") + ".png";
                } else {
                    originalFileName = UUID.randomUUID() + ".png"; // На случай, если имя файла отсутствует
                }

                Path outputPath = Paths.get("output_images", originalFileName);
                Files.createDirectories(outputPath.getParent());
                ImageIO.write(image, "png", outputPath.toFile());


                response.append("Сохранено: ").append(originalFileName).append("\n");
            } catch (IOException e) {
                response.append("Ошибка при обработке файла: ").append(file.getOriginalFilename()).append("\n");
                e.printStackTrace();
            }
        }

        return response.toString();
    }

    private BufferedImage generateImageFromData(String data) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Инвертируем ось Y
        AffineTransform transform = new AffineTransform();
        transform.translate(0, IMAGE_HEIGHT);
        transform.scale(1, -1);
        g2d.setTransform(transform);

        // Рисуем фон белого цвета
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // Рисуем черные прямоугольники из данных
        g2d.setColor(Color.BLACK);
        String[] lines = data.split("\n");
        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length >= 4) {
                int x = (int) Double.parseDouble(parts[0]);
                int y = (int) Double.parseDouble(parts[1]);
                int width = (int) Double.parseDouble(parts[2]);
                int height = (int) Double.parseDouble(parts[3]);

                g2d.fillRect(x, y, width, height);
            }
        }

        g2d.dispose();
        return image;
    }

    private String extractRelevantData(String data) {
        // Определяем начальный и конечный индексы для блока
        int startIndex = data.indexOf("%%EndProlog");
        int endIndex = data.indexOf("%%EOF");

        if (startIndex != -1 && endIndex != -1) {
            // Извлекаем данные между %%EndProlog и %%EOF
            return data.substring(startIndex + "%%EndProlog".length(), endIndex).trim();
        }

        // Если блок не найден, возвращаем пустую строку
        return "";
    }
}
