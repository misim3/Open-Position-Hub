# openposition
병역특례 채용 정보를 한눈에 볼 수 있는 채용 공고 수집 플랫폼
- [사이트 접속 링크](https://www.openposition.site/)

### 1. 프로젝트 개요
- **프로젝트명**: Open Position Hub
- **목표**: 병역특례 채용 공고를 통합 조회할 수 있는 플랫폼 구축
- **목적**: 서비스 운영 경험
- **대상**: 병역특례 희망자
- **핵심 기능**: 공고 수집 기능, 공고 검색 기능

### 🔧 기술 스택

- Back-end: Java, Spring Boot, MySQL
- Scraping: Jsoup, Selenium
- Infra: AWS EC2, RDS, Nginx, Ubuntu, Gitub Actions

### 🎯 주요 기능

- 기업 채용 페이지 스크래핑 (현재 기업 66곳에서 채용 공고 수집 중)
- 정적(Jsoup) + 동적(Selenium) 페이지 모두 대응
- 공고 중복 방지 로직 구현
- 매일 새벽 3시 자동 수집 (Spring Scheduler 기반)
- 에러 발생 시 로깅 및 예외 대응

### ⚙️ 인프라 구성

- EC2 (Ubuntu 24.04, t2.micro)
- AWS RDS (MYSQL)
- Nginx + HTTPS 설정
- GITHUB ACTIONS CI/CD

![oph drawio (1)](https://github.com/user-attachments/assets/d2a2ab5d-2fff-443f-a2e5-2959d79f31d2)
