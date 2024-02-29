package com.Killian.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
		startCrawling();
	}

	public static void startCrawling() {
		// CustomCrawler crawler = new CustomCrawler();
		// String url = "https://en.tutiempo.net/climate/ws-64580.html";
		// String url = "http://localhost:8080/";
	}
}
