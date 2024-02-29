package com.Killian.crawler.controller;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class CrawlController {

    @PostMapping("/getXls")
    public ResponseEntity<String> getXls(@RequestParam String name, @RequestParam int order, @RequestBody String src) {

        String filePath = "src/main/resources/templates/clonepage.html";

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(src);
            String html = jsonNode.get("src").asText();
            // System.out.println(html);
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(html);
                System.out.println("Write successful.");
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("STR");
    }
}
