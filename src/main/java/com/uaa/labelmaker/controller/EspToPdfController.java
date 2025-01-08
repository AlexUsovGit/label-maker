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

@RestController
@RequestMapping("/eps")
public class EspToPdfController {


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
}
