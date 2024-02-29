// package com.Killian.crawler.service;

// import java.io.File;
// import java.io.FileOutputStream;
// import java.util.ArrayList;

// import org.apache.poi.hssf.usermodel.HSSFWorkbook;
// import org.apache.poi.ss.usermodel.Cell;
// import org.apache.poi.ss.usermodel.CellStyle;
// import org.apache.poi.ss.usermodel.Font;
// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.ss.usermodel.Sheet;
// import org.apache.poi.ss.usermodel.Workbook;
// import org.jsoup.Connection;
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;

// public class Crawler {

//     public ArrayList<String> visitedUrl;

//     public Crawler() {
//         this.visitedUrl = new ArrayList<>();
//     }

//     public void crawl(int level, String url) {
//         if (level > 5)
//             return;

//         Workbook workbook = new HSSFWorkbook();
//         Sheet sheet = workbook.createSheet("Sheet");
//         Font boldFont = workbook.createFont();
//         boldFont.setBold(true);
//         CellStyle writeInBold = workbook.createCellStyle();
//         writeInBold.setFont(boldFont);

//         Document doc = request(url);

//         if (doc != null) {

//             Elements allRowElm = doc.select("table.medias tr");
//             Element row0Elm = allRowElm.first();
//             Elements allCellInRow0 = row0Elm.select("th");
//             int columnCount = allCellInRow0.size();

//             // Write column name
//             Row row0 = sheet.createRow(0);
//             for (int i = 0; i < columnCount; i++) {
//                 Element cellElm = allCellInRow0.get(i);
//                 Cell cell = row0.createCell(i);
//                 cell.setCellValue(cellElm.text());
//                 cell.setCellStyle(writeInBold);
//             }

//             // Write data
//             for (int i = 1; i < allRowElm.size(); i++) {
//                 Element currRowElm = allRowElm.get(i);
//                 Elements allCellInCurrRow = currRowElm.select("td");
//                 // Write a single row of data
//                 Row nextRow = sheet.createRow(i);
//                 for (int j = 0; j < columnCount; j++) {
//                     Element cellElm = allCellInCurrRow.get(j);
//                     Cell nextCell = nextRow.createCell(j);
//                     nextCell.setCellValue(cellElm.text());
//                 }
//             }

//             // Fix size
//             // for (int columnIndex = 0; columnIndex <= columnCount; columnIndex++) {
//             //     sheet.autoSizeColumn(columnIndex);
//             // }

//         }

//         try {
//             File myFile = new File("C:/Users/FPTSHOP/Desktop/myFile.xls");
//             FileOutputStream outputStream = new FileOutputStream(myFile.getAbsolutePath());
//             workbook.write(outputStream);
//             workbook.close();
//             outputStream.close();
//             System.out.println("File exported");
//         } catch (Exception e) {
//             System.out.println("Error exporting .xls");
//         }
//     }

//     public Document request(String url) {
//         if (visitedUrl.contains(url)) {
//             System.out.println("Website visited: " + url);
//             return null;
//         } else {
//             try {
//                 Connection con = Jsoup.connect(url);
//                 Document doc = con.get();
//                 System.out.println("Visiting: " + url + " | Page title: " + doc.title());
//                 visitedUrl.add(url);
//                 return doc;
//             } catch (Exception e) {
//                 System.out.println("Error connecting: " + url);
//                 return null;
//             }
//         }
//     }
// }
