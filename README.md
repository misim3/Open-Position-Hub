# [openposition](https://www.openposition.site/)
산업기능요원 채용 정보를 한눈에 볼 수 있는 채용 공고 수집 플랫폼

### 1. 프로젝트 개요
- **프로젝트명**: Open Position Hub
- **목표**: IT 산업기능요원 대상 채용 공고를 통합 조회할 수 있는 플랫폼 구축
- **목적**: 서비스 운영 경험
- **대상**: IT 산업기능요원
- **핵심 기능**: 공고 수집 기능, 공고 검색 기능

### 🔧 기술 스택

- Back-end: Java, Spring Boot, MySQL
- Crawling: Jsoup, Selenium
- Infra: AWS EC2, RDS, Nginx, Ubuntu
- 테스트: JUnit, k6
- 기타: GitHub Actions (CI), Linux Shell Script

### 🎯 주요 기능

- 기업 채용 페이지 크롤링 (40여 개 기업)
- 정적(Jsoup) + 동적(Selenium) 페이지 모두 대응
- 공고 중복 방지 로직 구현
- 매일 새벽 3시 자동 수집 (Spring Scheduler 기반)
- 에러 발생 시 로깅 및 예외 대응
- 배포 자동화 스크립트 포함

### ⚙️ 인프라 구성

- EC2 (Ubuntu 22.04, t2.micro)
- AWS RDS (MYSQL)
- Nginx + HTTPS 설정
- 프리티어 내에서 서버 자원 운영 최적화 (SWAP 메모리 추가)

![oph drawio (1)](https://github.com/user-attachments/assets/d2a2ab5d-2fff-443f-a2e5-2959d79f31d2)
