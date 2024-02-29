package com.Killian.crawler.controller;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/api")
public class CrawlController {

    private byte[] writeWorkbookToByteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @PostMapping("/getXls")
    public ResponseEntity<byte[]> getXls(@RequestParam("name") String name, @RequestParam("order") int order,
            @RequestParam("dataCnt") int dataCnt, @RequestBody String src) {

        String filePath = "src/main/resources/templates/clonepage.html";

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(src);
            String html = jsonNode.get("src").asText();
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
                writer.write(html);
                System.out.println("Clone successful.");

                try {
                    // workbook here
                    Workbook workbook = crawl("http://localhost:8080/clonepage", dataCnt);

                    try {
                        File myFile = new File("./exported/" + name + '_' + order + ".xls");
                        FileOutputStream outputStream = new FileOutputStream(myFile.getAbsolutePath());
                        workbook.write(outputStream);
                        workbook.close();
                        outputStream.close();
                        System.out.println("File exported: " + order);
                    } catch (Exception e) {
                        System.out.println("Error exporting .xls");
                    }

                    byte[] excelBytes = writeWorkbookToByteArray(workbook);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Disposition", "attachment; filename=exported_data.xlsx");

                    return ResponseEntity
                            .ok()
                            .headers(headers)
                            .body(excelBytes);
                } catch (Exception e) {
                    // Handle exceptions appropriately
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

            } catch (IOException e) {
                System.err.println("Error cloning to file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
