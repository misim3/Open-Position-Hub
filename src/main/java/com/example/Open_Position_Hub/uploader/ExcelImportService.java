package com.example.Open_Position_Hub.uploader;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelImportService {

    private final CompanyRepository companyRepository;

    public ExcelImportService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
            Map<String, CompanyEntity> existingMap = new HashMap<>();

            // DB에 이미 있는 업체 목록 조회
            List<CompanyEntity> existingCompanies = companyRepository.findAll();
            for (CompanyEntity entity : existingCompanies) {
                existingMap.put(entity.getName(), entity);
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 업체명 정제
                Cell nameCell = row.getCell(1);
                if (nameCell == null || nameCell.getCellType() != CellType.STRING) continue;
                String rawName = nameCell.getStringCellValue();
                String cleanName = extractCleanName(rawName);

                // 플랫폼
                Cell platformCell = row.getCell(3);
                String platform = (platformCell != null && platformCell.getCellType() == CellType.STRING)
                    ? platformCell.getStringCellValue()
                    : null;

                // URL 추출 (하이퍼링크 우선)
                Cell urlCell = row.getCell(2);
                String url = null;
                if (urlCell != null) {
                    Hyperlink link = urlCell.getHyperlink();
                    if (link != null) {
                        url = link.getAddress();
                    } else if (urlCell.getCellType() == CellType.STRING) {
                        url = urlCell.getStringCellValue();
                    }
                }

                CompanyEntity existing = existingMap.get(cleanName);

                if (existing == null) {
                    // 신규 → 저장
                    companyRepository.save(new CompanyEntity(cleanName, platform, url));
                } else {
                    // 기존 → 변경된 경우만 업데이트
                    boolean isModified =
                        !Objects.equals(existing.getRecruitmentPlatform(), platform) ||
                            !Objects.equals(existing.getRecruitmentUrl(), url);

                    if (isModified) {
                        existing.setRecruitmentPlatform(platform);
                        existing.setRecruitmentUrl(url);
                        companyRepository.save(existing);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("엑셀 처리 중 오류 발생", e);
        }
    }

    private String extractCleanName(String rawName) {
        if (rawName == null) return "";

        // 괄호 제거
        String cleaned = rawName.replaceAll("\\(.*?\\)", "");

        // 앞/뒤에 (주), (유), 주식회사 제거
        cleaned = cleaned.replaceAll("(?i)(^\\s*(주식회사|[(]?(주|유)[)]?)\\s*|\\s*(주식회사|[(]?(주|유)[)]?)\\s*$)", "");

        return cleaned.trim();
    }
}
