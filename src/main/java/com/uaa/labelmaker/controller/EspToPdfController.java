package com.uaa.labelmaker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uaa.labelmaker.model.ParamsDto;
import com.uaa.labelmaker.model.ProductData;
import com.uaa.labelmaker.service.EpsToPdfService;
import com.uaa.labelmaker.service.StickerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/eps")
public class EspToPdfController {

    private static final String UPLOAD_DIR = "uploads";


    private final EpsToPdfService epsToPdfService;
    private final ObjectMapper objectMapper;

    public EspToPdfController(EpsToPdfService epsToPdfService, ObjectMapper objectMapper) {
        this.epsToPdfService = epsToPdfService;
        this.objectMapper = objectMapper;
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
            headers.setContentDispositionFormData("attachment", sex + ".pdf");
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
            headers.setContentDispositionFormData("attachment", sex + ".pdf");
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
            headers.setContentDispositionFormData("attachment", sex + ".pdf");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(productDataForExport);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping("/upload/data")
    public ResponseEntity<byte[]> handleFilesUpload(@RequestParam("files") List<MultipartFile> files,
                                                    @RequestParam("jsonFile") MultipartFile jsonFile,
                                                    @RequestParam("importer") String importer,
                                                    @RequestParam("producent") String producent,
                                                    @RequestParam("country") String country,
                                                    @RequestParam("tp") String tp,
                                                    @RequestParam("gost") String gost,
                                                    @RequestParam("garant") String garant,
                                                    @RequestParam("sex") String sex,
                                                    @RequestParam("color") String color) {
        Map<String, BufferedImage> localStorage = new HashMap<>(files.size());

        // Создаём папку для загрузки, если её нет
        try {
            for (MultipartFile file : files) {
                if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".xlsx")) {
                    // Читаем Excel-файл
                    epsToPdfService.processExcelFile(file.getInputStream());
                }

                if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".eps")) {
                    // Читаем Excel-файл
                    BufferedImage bufferedImage = epsToPdfService.processAllFilesInDirectory(file);
                    if(bufferedImage != null){
                        localStorage.put(file.getOriginalFilename().replaceAll("\\.[^.]+$", "") ,bufferedImage);
                    }

                }

            }
            System.out.println(localStorage.size());
            var params = new ParamsDto(importer, producent, country, tp, gost, garant, sex, color);

            ProductData productData = objectMapper.readValue(jsonFile.getInputStream(), ProductData.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", sex + ".pdf");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(StickerFactory.createWbPDF(localStorage, productData,params));


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
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
                BufferedImage image = epsToPdfService.generateImageFromData(epsToPdfService.extractRelevantData(data), 2);


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


}
