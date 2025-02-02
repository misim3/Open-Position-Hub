package com.example.Open_Position_Hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenPositionHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenPositionHubApplication.class, args);

		/*
		큰 구조는 큰 컨테이너가 존재해서 각 모듈 호출
		각 모듈은 스크래퍼, 셀렉터, 추출기, 변환기, 레포지토리.
		 */
	}

}
