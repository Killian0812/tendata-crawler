package com.Killian.crawler.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

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

public class CustomCrawler {

    public ArrayList<String> visitedUrl;

    public CustomCrawler() {
        this.visitedUrl = new ArrayList<>();
    }

    public void crawl(int level, String url) {
        if (level > 5)
            return;

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

            int dataCnt = 15;

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

        try {
            int fileOrder = 26;
            File myFile = new File("C:/Users/duong/Desktop/Cuong/2024/file" + fileOrder + ".xls");
            FileOutputStream outputStream = new FileOutputStream(myFile.getAbsolutePath());
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            System.out.println("File exported: " + fileOrder);
        } catch (Exception e) {
            System.out.println("Error exporting .xls");
        }
    }

    public Document request(String url) {
        if (visitedUrl.contains(url)) {
            System.out.println("Website visited: " + url);
            return null;
        } else {
            try {
                Connection con = Jsoup.connect(url);
                Document doc = con.get();
                System.out.println("Visiting: " + url + " | Page title: " + doc.title());
                visitedUrl.add(url);
                return doc;
            } catch (Exception e) {
                System.out.println("Error connecting: " + url);
                return null;
            }
        }
    }
}
