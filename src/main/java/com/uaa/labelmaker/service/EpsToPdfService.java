package com.uaa.labelmaker.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.uaa.labelmaker.model.ParamsDto;
import com.uaa.labelmaker.model.ProductData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


@Service
public class EpsToPdfService {

    private final ObjectMapper objectMapper;

    public EpsToPdfService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void renderEpsToPng(InputStream epsInputStream, String outputFilePath) throws Exception {
        String outputImagePath = "output.png";

        try (PDDocument document = PDDocument.load(epsInputStream)) {
            // Создаем объект рендера для PDF-документа
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Рендерим первую страницу (индексация с 0)
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // 300 DPI для высокого качества
            BufferedImage fullPageImage = pdfRenderer.renderImageWithDPI(0, 300);

//            // Координаты области, где находится QR-код (например, в центре страницы)
//            // Определите вручную или вычислите автоматически.
//            int x = 550; // Начальная координата X (в пикселях)
//            int y = 840; // Начальная координата Y (в пикселях)
//            int width = 1430; // Ширина области (в пикселях)
//            int height = 1340; // Высота области (в пикселях)
            // Координаты области, где находится QR-код (например, в центре страницы)
            // Определите вручную или вычислите автоматически.
            int x = 45; // Начальная координата X (в пикселях)
            int y = 120; // Начальная координата Y (в пикселях)
            int width = 325; // Ширина области (в пикселях)
            int height = 350; // Высота области (в пикселях)

            // Обрезаем изображение до заданной области (QR-код)
            BufferedImage qrCodeImage = fullPageImage.getSubimage(x, y, width, height);

            // Сохраняем обрезанное изображение как PNG
            ImageIO.write(qrCodeImage, "PNG", new File(outputImagePath));

            System.out.println("QR-код успешно извлечен и сохранен: " + outputImagePath);

//
//            // Сохраняем изображение как PNG
//            ImageIO.write(image, "PNG", new File(outputImagePath));

            System.out.println("Изображение успешно извлечено и сохранено: " + outputImagePath);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке PDF-файла: " + e.getMessage());
        }
    }

    public byte[] createShoeStickers(InputStream epsInputStream, MultipartFile jsonFile, ParamsDto params) throws IOException {

        ProductData productData = objectMapper.readValue(jsonFile.getInputStream(), ProductData.class);

        try (PDDocument document = PDDocument.load(epsInputStream)) {
            // Создаем объект рендера для PDF-документа
            return StickerFactory.createPDF(document, productData, params);

        } catch (IOException e) {
            System.err.println("Ошибка при обработке PDF-файла: " + e.getMessage());
            return null;
        }
    }


}
