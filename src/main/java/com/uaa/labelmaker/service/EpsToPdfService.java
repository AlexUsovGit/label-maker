package com.uaa.labelmaker.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.uaa.labelmaker.model.ParamsDto;
import com.uaa.labelmaker.model.ProductData;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
public class EpsToPdfService {

    private static final int IMAGE_WIDTH = 47;
    private static final int IMAGE_HEIGHT = 47;
    private final ObjectMapper objectMapper;

    public EpsToPdfService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void processExcelFile(InputStream inputStream) {
        try {
             Workbook workbook = new XSSFWorkbook(inputStream);

                Sheet sheet = workbook.getSheetAt(0); // Берём первый лист
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        System.out.println("Ячейка: " + cell.toString());
                    }
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage processAllFilesInDirectory(MultipartFile file) throws IOException {
        try {
            // Читаем содержимое файла
            String data = new String(file.getBytes());

            // Генерируем изображение
            BufferedImage image = generateImageFromData(extractRelevantData(data), 4);

//
//            // Получаем оригинальное имя файла и заменяем расширение на .png
//            String originalFileName = file.getOriginalFilename();
//            if (originalFileName != null) {
//                originalFileName = originalFileName.replaceAll("\\.[^.]+$", "") + ".png";
//            } else {
//                originalFileName = UUID.randomUUID() + ".png"; // На случай, если имя файла отсутствует
//            }
//
//
//
//            Path outputPath = Paths.get("output_images", originalFileName);
//            Files.createDirectories(outputPath.getParent());
//            ImageIO.write(image, "png", outputPath.toFile());
//            System.out.println("Сохранено: " +originalFileName);

            return image;

        } catch (IOException e) {
            System.out.println("Ошибка при обработке файла: " +file.getOriginalFilename());

            e.printStackTrace();
            return null;
        }
    }


    public BufferedImage generateImageFromData(String data, int scaleFactor) {
        int scaledWidth = IMAGE_WIDTH * scaleFactor;
        int scaledHeight = IMAGE_HEIGHT * scaleFactor;

        BufferedImage image = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Включаем сглаживание (антиалиасинг)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Инвертируем ось Y
        AffineTransform transform = new AffineTransform();
        transform.translate(0, scaledHeight);
        transform.scale(1, -1);
        g2d.setTransform(transform);

        // Рисуем фон белого цвета
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, scaledWidth, scaledHeight);

        // Рисуем черные прямоугольники из данных
        g2d.setColor(Color.BLACK);
        String[] lines = data.split("\n");
        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length >= 4) {
                int x = (int) (Double.parseDouble(parts[0]) * scaleFactor);
                int y = (int) (Double.parseDouble(parts[1]) * scaleFactor);
                int width = (int) (Double.parseDouble(parts[2]) * scaleFactor);
                int height = (int) (Double.parseDouble(parts[3]) * scaleFactor);

                g2d.fillRect(x, y, width, height);
            }
        }

        g2d.dispose();
        return image;
    }


    public String extractRelevantData(String data) {
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
            return StickerFactory.createWbPDF(document, productData, params);

        } catch (IOException e) {
            System.err.println("Ошибка при обработке PDF-файла: " + e.getMessage());
            return null;
        }
    }
    public byte[] createShoeStickers3(InputStream epsInputStream, MultipartFile jsonFile, ParamsDto params) throws IOException, DocumentException, InterruptedException {

        ProductData productData = objectMapper.readValue(jsonFile.getInputStream(), ProductData.class);

//        try (PDDocument document = PDDocument.load(epsInputStream)) {
            // Создаем объект рендера для PDF-документа
            return StickerFactory.createPDF3( productData, params);

//        } catch (IOException | DocumentException | InterruptedException e) {
//            System.err.println("Ошибка при обработке PDF-файла: " + e.getMessage());
//            return null;
//        }
    }

//    public byte[] createPdf(InputStream epsInputStream, MultipartFile jsonFile, ParamsDto params) throws IOException {
//
//        // Укажите путь к папке с EPS-файлами
//        String folderPath = "/Users/aleksejusov/Downloads/WB_1_1/размер 37/fe835af5-b1af-4dbf-a689-9438398c46bf_begin_offset_0_number_of_codes_240";
//
//        // Путь к Ghostscript
//        String gsPath = "gs"; // Укажите полный путь, если не в PATH
//
//        // Получение списка файлов из папки
//        File folder = new File(folderPath);
//        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".eps"));
//
//        if (files == null || files.length == 0) {
//            System.out.println("Нет EPS-файлов в указанной папке.");
//            return new byte[0];
//        }
//        for (int i = 0; i < files.length; i++) {
//
//        }
//
//        // Обработка каждого EPS-файла
//        for (File epsFile : Arrays.stream(files).limit(1).toList()) {
//            try {
//
//                // Получение имени файла без расширения
//                String fileName = epsFile.getName().substring(0, epsFile.getName().lastIndexOf('.'));
//                String outputFilePath = folderPath + File.separator + fileName + ".png";
//
//                List<String> command = new ArrayList<>();
//                command.add(gsPath);
//                command.add("-dNOPAUSE");
//                command.add("-dBATCH");
//                command.add("-dSAFER");
//                command.add("-dEPSCrop"); // Использовать BoundingBox из EPS
//                command.add("-sDEVICE=pngalpha"); // Устройство для PNG
//                command.add("-r300"); // Установка разрешения (DPI)
//                command.add("-sOutputFile=%stdout"); //
//                command.add(epsFile.getAbsolutePath());
//
//                // Запуск процесса
//                ProcessBuilder pb = new ProcessBuilder(command);
//                Process process = pb.start();
//
//                // Чтение выходного потока и преобразование в BufferedImage
//                try (InputStream inputStream = process.getInputStream()) {
//                    // Декодируем поток данных PNG в BufferedImage
//                    BufferedImage image = ImageIO.read(inputStream);
//
//                    if (image != null) {
//                        System.out.println("Изображение успешно сгенерировано. Размер: " +
//                                image.getWidth() + "x" + image.getHeight());
//                    } else {
//                        System.err.println("Не удалось декодировать изображение.");
//                    }
//                }
//
//
//                // Проверяем завершение процесса
//                int exitCode = process.waitFor();
//                if (exitCode != 0) {
//                    System.err.println("Ошибка при конвертации EPS. Код завершения: " + exitCode);
//                }
//
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//                return new byte[0];
//            }
//        }
//
////    }
//
//    }
    public byte[] createShoeStickers2(InputStream epsInputStream, MultipartFile jsonFile, ParamsDto params) throws IOException {

        // Укажите путь к папке с EPS-файлами
        String folderPath = "/Users/aleksejusov/Downloads/WB_1_1/размер 37/fe835af5-b1af-4dbf-a689-9438398c46bf_begin_offset_0_number_of_codes_240";

        // Путь к Ghostscript
        String gsPath = "gs"; // Укажите полный путь, если не в PATH

        // Получение списка файлов из папки
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".eps"));

        if (files == null || files.length == 0) {
            System.out.println("Нет EPS-файлов в указанной папке.");
            return new byte[0];
        }

        // Обработка каждого EPS-файла
        for (File epsFile : files) {
            try {
                // Получение имени файла без расширения
                String fileName = epsFile.getName().substring(0, epsFile.getName().lastIndexOf('.'));
                String outputFilePath = folderPath + File.separator + fileName + ".png";

                // Формирование команды Ghostscript
                ProcessBuilder pb = new ProcessBuilder(
                        gsPath,
                        "-dNOPAUSE",
                        "-dBATCH",
                        "-dSAFER",
                        "-dEPSCrop",
                        "-sDEVICE=pngalpha", // Устройство для вывода PNG
                        "-r300",             // Разрешение (DPI)
                        "-sOutputFile=" + outputFilePath,
                        epsFile.getAbsolutePath()
                );

                // Запуск процесса
                Process process = pb.start();

                // Ожидание завершения процесса
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("Файл успешно конвертирован: " + outputFilePath);
                } else {
                    System.err.println("Ошибка при конвертации файла: " + epsFile.getName() + ". Код завершения: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Ошибка при обработке файла: " + epsFile.getName());
                e.printStackTrace();
            }
        }
        return new byte[0];
    }



}
