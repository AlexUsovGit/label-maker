package com.uaa.labelmaker.service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.uaa.labelmaker.model.ParamsDto;
import com.uaa.labelmaker.model.ProductData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class StickerFactory {
    private static final Logger logger = LoggerFactory.getLogger(StickerFactory.class);
    public static final String FONT = "arial.ttf";
    public static final int NAME_SYMBOLS_COUNT = 30;
    public static final int BARCODE_FIXED_HEIGHT = 54;
    public static final int BARCODE_SIZE = 26;
    public static final int BARCODE_FIXED_HEIGHT_COMMON = 22;
    public static final String SHOE = "shoe";
    public static final String WEAR = "wear";


    public static byte[] createPDF(PDDocument qrSourceDocument, ProductData productData, ParamsDto params) {


        PDFRenderer pdfRenderer = new PDFRenderer(qrSourceDocument);
        int pageCount = qrSourceDocument.getPages().getCount();
//        int pageCount = 1;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Rectangle one = new Rectangle(205.0F, 240.0F);
        Document document = new Document(one);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.setMargins(3.0F, 1.0F, 1.5F, 1.0F);
            document.open();
            BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            float[] columnWidths = {2F, 5F};
            // Координаты области, где находится QR-код (например, в центре страницы)
            // Определите вручную или вычислите автоматически.
            int x = 45; // Начальная координата X (в пикселях)
            int y = 120; // Начальная координата Y (в пикселях)
            int width = 325; // Ширина области (в пикселях)
            int height = 340; // Высота области (в пикселях)

//            for (int i = 0; i < pageCount; i++) {
            for (int i = 0; i < pageCount; i++) {
                BufferedImage fullPageImage = pdfRenderer.renderImageWithDPI(i, 300);
                // Обрезаем изображение до заданной области (QR-код)
                BufferedImage qrCodeImage = fullPageImage.getSubimage(x, y, width, height);
                generateShoeStickers(baseFont, writer, document, qrCodeImage, productData, params);
            }

            // Рендерим первую страницу (индексация с 0)
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // 300 DPI для высокого качества


//            // Координаты области, где находится QR-код (например, в центре страницы)
//            // Определите вручную или вычислите автоматически.
//            int x = 550; // Начальная координата X (в пикселях)
//            int y = 840; // Начальная координата Y (в пикселях)
//            int width = 1430; // Ширина области (в пикселях)
//            int height = 1340; // Высота области (в пикселях)


            document.close();
            logger.info("Save stickers for product list");
            return outputStream.toByteArray();

        } catch (Exception e) {
            String errorMessage = String.format("Create stickers error. Message = %s", e.getMessage());
            logger.warn(errorMessage);
            document.close();
            return new byte[0];
        }

    }

    public static byte[] createPDF3(ProductData productData, ParamsDto params) throws DocumentException, IOException, InterruptedException {
        // Настройки папки с EPS-файлами
        String folderPath = "/Users/aleksejusov/Downloads/WB_1_1/размер 37/fe835af5-b1af-4dbf-a689-9438398c46bf_begin_offset_0_number_of_codes_240";
        File folder = new File(folderPath);
        File[] epsFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".eps"));

        if (epsFiles == null || epsFiles.length == 0) {
            System.out.println("Нет EPS-файлов в указанной папке.");
            return new byte[0];
        }

        // Подготовка PDF-документа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = createDocument(outputStream);
        BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Обработка каждого EPS-файла
        for (File epsFile : Arrays.stream(epsFiles).limit(1).toList()) {
            try {
                BufferedImage image = convertEPSToImage(epsFile);

                if (image != null) {
                    System.out.println("Изображение успешно сгенерировано. Размер: " +
                            image.getWidth() + "x" + image.getHeight());
                    generateShoeStickers(baseFont, PdfWriter.getInstance(document, outputStream), document, image, productData, params);
                } else {
                    System.err.println("Не удалось декодировать изображение из файла: " + epsFile.getName());
                }
            } catch (Exception e) {
                System.err.println("Ошибка при обработке файла: " + epsFile.getName());
                e.printStackTrace();
            }
        }

        document.close();
        logger.info("Сохранение наклеек для списка продуктов завершено.");
        return outputStream.toByteArray();
    }

    private static Document createDocument(OutputStream outputStream) throws DocumentException {
        Rectangle pageSize = new Rectangle(205.0F, 240.0F);
        Document document = new Document(pageSize);
        PdfWriter.getInstance(document, outputStream);
        document.setMargins(3.0F, 1.0F, 1.5F, 1.0F);
        document.open();
        return document;
    }

    private static BufferedImage convertEPSToImage(File epsFile) throws IOException, InterruptedException {
        // Путь к Ghostscript
        String gsPath = "gs"; // Укажите полный путь, если не в PATH

        List<String> command = Arrays.asList(
                gsPath,
                "-dNOPAUSE",
                "-dBATCH",
                "-dSAFER",
                "-dEPSCrop",
                "-sDEVICE=pngalpha",
                "-r300",
                "-sOutputFile=%stdout",
                epsFile.getAbsolutePath()
        );

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        try (InputStream inputStream = new BufferedInputStream(process.getInputStream())) {
            BufferedImage image = ImageIO.read(inputStream);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("Ошибка при конвертации EPS: " + epsFile.getName() + ". Код завершения: " + exitCode);
            }

            return image;
        }
    }


    private static void generateShoeStickers(

            BaseFont bf,
            PdfWriter writer,
            Document document,
            BufferedImage bufferedImage,
            ProductData productData,
            ParamsDto params
    ) throws DocumentException, IOException {
        float[] columnWidths = {1.5F, 3F};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        Font font = new Font(bf, 6f, Font.NORMAL);
        Font sizeFont = new Font(bf, 22.0f, Font.NORMAL);


        Map<String, ProductData.ProductAttributes> gtinProductAttributes = productData.getGtinProductAttributes();
        Set<String> strings = gtinProductAttributes.keySet();
        Optional<String> first = strings.stream().findFirst();
        String barcode = "";
        if (first.isPresent()) {
            barcode = first.get();
            ProductData.ProductAttributes productAttributes = gtinProductAttributes.get(first.get());
            String nameValue = productAttributes.getFullName();
            int nameRowHeight = (nameValue != null && nameValue.length() > NAME_SYMBOLS_COUNT) ? 18 : 10;
            addDataCell("Наименование", font, table, nameRowHeight);
            addDataCell(nameValue, font, table, nameRowHeight);

            addDataCell("Артикул", font, table);
            addDataCell(params.getSex(), font, table);

            addDataCell("Торговая марка", font, table);
            String code = productAttributes.getBrand();
            addDataCell(code, font, table);
            addDataCell("Цвет", font, table);
            addDataCell(params.getColor(), font, table);
            addDataCell("Верх", font, table);
            addDataCell(productAttributes.getMaterialUpper(), font, table);
            addDataCell("Подкладка", font, table);
            addDataCell(productAttributes.getMaterialLining(), font, table);
            addDataCell("Подошва", font, table);
            addDataCell(productAttributes.getMaterialDown(), font, table);


//            addDataCell(productAttributes.getProductSize(), font, table, 20);

            addDataMatrixCell( table, bufferedImage);


            addBarcodeCellData(writer, table, barcode.substring(1), 1, BARCODE_FIXED_HEIGHT);


            createAndAddNewPage(1, document, table);
            PdfPTable tableText = new PdfPTable(new float[]{5F});
            tableText.setWidthPercentage(100);
            addDataCellWithoutBorderAndHeight("Импортер в РФ: " + params.getImporter(), font, tableText);
            addDataCellWithoutBorderAndHeight("Изготовитель: " + params.getProducent(), font, tableText);
            document.add(tableText);

            PdfPTable tableAfter = new PdfPTable(new float[]{3F, 2F});
            tableAfter.setWidthPercentage(100);




            addDataCellWithoutBorder("Страна производства: " + params.getCountry(), font, tableAfter);
            addSizeDataCell(productAttributes.getProductSize(), sizeFont, tableAfter);
            addDataCellWithoutBorder(params.getTp(), font, tableAfter);
            addDataCellWithoutBorder("ГОСТ " + params.getGost(), font, tableAfter);
            addDataCellWithoutBorder("Гарантийный срок носки: " + params.getGarant(), font, tableAfter);
            PdfPCell shoeV = createImageCell("eac.jpg", 16F);
            shoeV.setBorder(Rectangle.NO_BORDER);
            tableAfter.addCell(shoeV);
            addDataCellWithoutBorder("", font, tableAfter);
            document.add(tableAfter);
//            document.add(new Paragraph("Страна производства: " + params.getCountry(), font));
//            document.add(new Paragraph(params.getTp(), font));
//            document.add(new Paragraph("ГОСТ " + params.getGost(), font));
//            document.add(new Paragraph("Гарантийный срок носки: " + params.getGarant(), font));

        }


    }

    private static PdfPCell createImageCell(String path, float height) throws DocumentException, IOException {
        Image img = Image.getInstance(path);
        PdfPCell cell = new PdfPCell(img, true);
        cell.setVerticalAlignment(Rectangle.TOP);
        cell.setHorizontalAlignment(Rectangle.RIGHT);
        cell.setPaddingLeft(2);
        cell.setFixedHeight(height);
        return cell;
    }


    private static void addDataMatrixCell( PdfPTable table, BufferedImage bufferedImage) throws IOException, BadElementException {
        PdfPCell cell;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        Image image = Image.getInstance(baos.toByteArray());
        cell = new PdfPCell(image, true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(1F);
        cell.setFixedHeight(BARCODE_FIXED_HEIGHT);
        table.addCell(cell);
    }

    private static void addSizeDataCell(String size, Font sizeFont, PdfPTable table) {
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(size, sizeFont));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingLeft(1F);
//        cell.setBorder(Rectangle.NO_BORDER);
        cell.setRowspan(4);
        table.addCell(cell);
    }

    private static void addDataCell(String data, Font font, PdfPTable table) {
        addDataCell(data, font, table, 10);
    }

    private static void addDataCellWithoutBorderAndHeight(String data, Font font, PdfPTable table) {
        addDataCellWithoutBorder(data, font, table, 0);
    }
    private static void addDataCellWithoutBorder(String data, Font font, PdfPTable table) {
        addDataCellWithoutBorder(data, font, table, 10);
    }

    private static void addDataCell(String data, Font font, PdfPTable table, int fixedHeight) {
        PdfPCell cell = new PdfPCell(new Phrase(data, font));
        cell.setFixedHeight(fixedHeight);
        table.addCell(cell);
    }
    private static void addDataCellWithoutBorder(String data, Font font, PdfPTable table, int fixedHeight) {
        PdfPCell cell = new PdfPCell(new Phrase(data, font));
        if(fixedHeight> 0){
            cell.setFixedHeight(fixedHeight);
        }

        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private static void createAndAddNewPage(Integer pageCount, Document document, PdfPTable table) throws DocumentException {
        for (int i = 0; i < pageCount; i++) {
            document.newPage();
            document.add(table);
        }
    }

    private static void addBarcodeCellData(PdfWriter writer, PdfPTable table, String barcodeValue, int colspan, int fixedHeight) {
        PdfPCell cell = createBarcode(writer, barcodeValue, BARCODE_SIZE);
        cell.setColspan(colspan);
        cell.setFixedHeight(fixedHeight);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }


    public static PdfPCell createBarcode(PdfWriter writer, String code, int barcodeHeight) {
        BarcodeEAN barcode = new BarcodeEAN();
        barcode.setCodeType(Barcode.EAN13);
        barcode.setCode(code);
        barcode.setBarHeight(barcodeHeight);
        // PdfPCell cell = new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.GRAY), true);
        return new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK), false);
    }


}
