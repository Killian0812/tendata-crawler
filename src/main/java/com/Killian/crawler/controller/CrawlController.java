package com.Killian.crawler.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
public class CrawlController {

    @PostMapping("/getXls")
    public void getXls(@RequestParam("name") String name, @RequestParam("order") int order,
            @RequestParam("dataCnt") int dataCnt, @RequestBody String src, HttpServletResponse response) {

        String filePath = "src/main/resources/templates/clonepage.html";

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(src);
            String html = jsonNode.get("src").asText();
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(html);
                System.out.println("Write successful.");

                // workbook here
                Workbook workbook = crawl("http://localhost:8080/clonepage", dataCnt);

                try {
                    int fileOrder = order;
                    File myFile = new File(
                            "C:/Users/Administrator/Desktop/New folder - Copy/file" + fileOrder + ".xls");
                    FileOutputStream outputStream = new FileOutputStream(myFile.getAbsolutePath());
                    workbook.write(outputStream);
                    workbook.close();
                    outputStream.close();
                    System.out.println("File exported: " + fileOrder);
                } catch (Exception e) {
                    System.out.println("Error exporting .xls");
                }

                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment; filename=" + name + "_" + order + ".xls");

                try (OutputStream outputStream = response.getOutputStream()) {
                    workbook.write(outputStream);
                    outputStream.flush();
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Workbook crawl(String url, int dataCnt) {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet");
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        CellStyle writeInBold = workbook.createCellStyle();
        writeInBold.setFont(boldFont);

        Document doc = request(url);

        if (doc != null) {

            // Write headers
            int columnCount = 11;
            // List<String> columnNames = Arrays.asList("Date", "Exporter(VN)",
            // "Exporter(EN)", "Importer", "HS Code",
            // "Product(EN)", "Destination Country", "Gross Weight(Kg)", "Quantity",
            // "Quantity Unit",
            // "Total Price(USD)");
            // Row row0 = sheet.createRow(0);
            // for (int i = 0; i < columnCount; i++) {
            // Cell cell = row0.createCell(i);
            // cell.setCellValue(columnNames.get(i));
            // cell.setCellStyle(writeInBold);
            // }

            Elements allRowElm = doc.select("tbody.ant-table-tbody tr");

            for (int i = 1; i <= dataCnt; i++) {
                Element currDataRowElm = allRowElm.get(i);
                Elements cellDataElms = currDataRowElm.select("td");

                // Write data in each row
                Row nextRow = sheet.createRow(i - 1);
                for (int j = 1; j < columnCount + 1; j++) {
                    Cell cell = nextRow.createCell(j - 1);
                    cell.setCellValue(cellDataElms.get(j).text());
                }
            }

            // Fix size
            // for (int columnIndex = 0; columnIndex <= columnCount; columnIndex++) {
            // sheet.autoSizeColumn(columnIndex);
            // }

        }

        return workbook;
    }

    public Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            System.out.println("Visiting: " + url + " | Page title: " + doc.title());
            return doc;
        } catch (Exception e) {
            System.out.println("Error connecting: " + url);
            return null;
        }
    }
}
