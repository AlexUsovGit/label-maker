package com.uaa.labelmaker.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.uaa.labelmaker.model.LabelDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class XlsxDataHandler implements DataHandler
{
    @Autowired
    StorageService storageService;

    String pattern = "MM.yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    public static final String FONT = "Arial.ttf";

    @Override
    public List<LabelDto> readBook(InputStream inputStream)
    {

        List<LabelDto> labels = new ArrayList<>();
        Workbook workbook;
        try
        {
            workbook = new XSSFWorkbook(inputStream);
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet)
        {

            if (row.getRowNum() == 0)
            {
                System.out.println(
                        row.getCell(0) + "\t" +
                        row.getCell(1) + "\t" +
                        row.getCell(2) + "\t" +
                        row.getCell(3) + "\t" +
                        row.getCell(4) + "\t" +
                        row.getCell(5) + "\t" +
                        row.getCell(6) + "\t" +
                        row.getCell(7) + "\t"
                );
            }
            else
            {
                String sizes = row.getCell(1).getStringCellValue();
                for (String currentSize : Arrays.stream(sizes.split(";")).toList())
                {
                    LabelDto labelDto = new LabelDto();
                    try
                    {
                        labelDto.setModel(row.getCell(0).getStringCellValue());
                    }
                    catch (Exception e)
                    {
                        System.out.println("Try to get number value of model");
                        labelDto.setModel(String.valueOf((int) row.getCell(0).getNumericCellValue()));
                    }

                    labelDto.setRazmer(currentSize);
                    double poln = row.getCell(2).getNumericCellValue();
                    labelDto.setPolnota(String.valueOf((int) poln));
                    String date = simpleDateFormat.format(row.getCell(3).getDateCellValue());
                    labelDto.setDataVypuska(date);
                    labelDto.setMaterVerh(row.getCell(4).getStringCellValue());
                    labelDto.setMaterPodkladka(row.getCell(5).getStringCellValue());
                    labelDto.setMaterNiz(row.getCell(6).getStringCellValue());
                    labels.add(labelDto);
                }

            }
            System.out.println("Row number " + row.getRowNum() + " complete");
        }

        return labels;
    }

    @Override
    public ResponseEntity<InputStreamResource> handle(MultipartFile file)
    {
        return ResponseEntity
                .ok()
                .body(new InputStreamResource(create(readBook(storageService.upload(file)))));
    }

    @Override
    public ByteArrayInputStream create(List<LabelDto> labels)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy_ HH:mm:ss");
        String fileName = formatter.format(LocalDateTime.now()) + " labels.pdf";
        System.out.println("Create file");
        Rectangle one = new Rectangle(50.0F, 50.0F);
        Document document = new Document(one);
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            PdfWriter.getInstance(document, out);
            document.setMargins(1, 1, 1, 1);
            document.open();
            BaseFont bf = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            // Font font = FontFactory.getFont(String.valueOf(Font.FontFamily.TIMES_ROMAN), "Cp1250", true);
            Font font = new Font(bf, 4.5F, Font.NORMAL);

            float[] headWith = {4F};
            float[] columnWidths = {2F, 3F};
            float[] materialsWith = {2F};

            for (LabelDto label : labels)
            {
                PdfPTable headTable = new PdfPTable(headWith);
                headTable.setWidthPercentage(100);
                headTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                PdfPCell modelHead = new PdfPCell(new Phrase(label.getModel(), font));
                modelHead.setPaddingBottom(0F);
                modelHead.setPaddingTop(0F);
                modelHead.setBorderColor(BaseColor.WHITE);
                headTable.addCell(modelHead);
                PdfPCell infoHead = new PdfPCell(new Phrase(label.getRazmer() + " " + label.getPolnota() + " " + label.getDataVypuska(), font));
                infoHead.setBorderColor(BaseColor.WHITE);
                infoHead.setPaddingTop(0F);
                headTable.addCell(infoHead);
                document.add(headTable);

                PdfPTable table = new PdfPTable(columnWidths);
                table.setWidthPercentage(100);
                table.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell shoeV = createImageCell("v.png", 12F);
//                shoeV.setPaddingLeft(4F);
                PdfPCell shoeP = createImageCell("p.png", 12F);
//                shoeP.setPaddingLeft(4F);
                PdfPCell shoeN = createImageCell("n.png", 12F);
//                shoeN.setPaddingLeft(4F);

                PdfPCell imageV = createMaterialCell(label.getMaterVerh(), 12F);
                PdfPCell imageP = createMaterialCell(label.getMaterPodkladka(), 12F);
                PdfPCell imageN = createMaterialCell(label.getMaterNiz(), 12F);
//
//                imageV.setBorderColor(BaseColor.WHITE);
//                imageP.setBorderColor(BaseColor.WHITE);
//                imageN.setBorderColor(BaseColor.WHITE);

//                imageAll.setBorderColor(BaseColor.WHITE);
                table.addCell(shoeV);
                table.addCell(imageV);
                table.addCell(shoeP);
                table.addCell(imageP);
                table.addCell(shoeN);
                table.addCell(imageN);

                PdfPTable materials = new PdfPTable(materialsWith);
//                table.setWidthPercentage(100);
//                table.setHorizontalAlignment(Element.ALIGN_LEFT);

//
//                materials.addCell(imageV);
//                materials.addCell(imageP);
//                materials.addCell(imageN);
//                PdfPCell matCell = new PdfPCell(materials);
//                matCell.setBorderColor(BaseColor.WHITE);
//                table.addCell(matCell);
//
//                PdfPCell imageMat = createImageCell("mat.png");
//                imageMat.setBorderColor(BaseColor.WHITE);
//                table.addCell(imageMat);
//
//                PdfPCell emp = new PdfPCell();
//                emp.setBorderColor(BaseColor.WHITE);
//                table.addCell(emp);
//                document.add(materials);
                document.add(table);
            }
            document.close();
            System.out.println("Creating file is complete");
            return new ByteArrayInputStream(out.toByteArray());

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    private PdfPCell createImageCell(String path, float height) throws DocumentException, IOException
    {
        Image img = Image.getInstance(path);
        PdfPCell cell = new PdfPCell(img, true);
        cell.setFixedHeight(height);
        return cell;
    }

    private PdfPCell createMaterialCell(String path, float height) throws DocumentException, IOException
    {
        if (path.contains("/"))
        {

            List<Float> matTWithList = new ArrayList<>();
            matTWithList.add(3F);
            matTWithList.add(3F);
            if (path.split("/").length == 3)
            {
                matTWithList.add(3F);
            }

            float[] matTWith = new float[matTWithList.size()];

            int i = 0;

            for (Float f : matTWithList)
            {
                matTWith[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
            }
            PdfPTable matT = new PdfPTable(matTWith);
            matT.setHorizontalAlignment(Element.ALIGN_CENTER);
            Image img = Image.getInstance(path.split("/")[0] + ".png");
            PdfPCell cell = new PdfPCell(img, true);
//            cell.setBorderColor(BaseColor.WHITE);
            cell.setFixedHeight(height);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            matT.addCell(cell);

//            img = Image.getInstance("sl.png");
//            cell = new PdfPCell(img, true);
//            cell.setBorderColor(BaseColor.WHITE);
//            cell.setFixedHeight(height);
//            matT.addCell(cell);

            img = Image.getInstance(path.split("/")[1] + ".png");
            cell = new PdfPCell(img, true);
            cell.setFixedHeight(height);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorderColor(BaseColor.WHITE);
            matT.addCell(cell);

            if (path.split("/").length == 3)
            {
                img = Image.getInstance(path.split("/")[2] + ".png");
                cell = new PdfPCell(img, true);
                cell.setFixedHeight(height);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorderColor(BaseColor.WHITE);
                matT.addCell(cell);
            }

            return new PdfPCell(matT);

        }
        else
        {
            Image img = Image.getInstance(path + ".png");
            PdfPCell cell = new PdfPCell(img, true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setFixedHeight(height);
            return cell;
        }

    }
}
