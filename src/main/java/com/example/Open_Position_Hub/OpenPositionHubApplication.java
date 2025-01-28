package com.example.Open_Position_Hub;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenPositionHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenPositionHubApplication.class, args);

		System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

		Scraper scraper = new Scraper();
		String urlCompanyD1_B_Greeting = "https://www.doodlin.co.kr/career#3276397a-a988-4ca5-ab47-9aa05e9cce30";
		String urlCompanyD2_A_Greeting = "https://teamdoeat.career.greetinghr.com/home#323ea93b-ce52-45c9-bbbf-0b85ad135508";

		Extractor extractor = new Extractor(new CssSelector(), new JobPostingMemoryRepository());

		try {
			Document doc = scraper.fetchHtml(urlCompanyD2_A_Greeting);
			extractor.extractGreeting(doc);
		} catch (IOException e) {
			System.err.println("fail: " + e.getMessage());
		}
	}

}
