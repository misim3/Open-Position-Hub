package com.example.Open_Position_Hub.uploader;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelImportService {

    private static final Logger log = LoggerFactory.getLogger(ExcelImportService.class);
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;

    public ExcelImportService(CompanyRepository companyRepository,
        JobPostingRepository jobPostingRepository) {
        this.companyRepository = companyRepository;
        this.jobPostingRepository = jobPostingRepository;
    }

    @Transactional
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

            List<CompanyEntity> toInsert = new ArrayList<>();
            List<Long> toDeleteJobPostingEntityByCompanyId = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 업체명 정제
                Cell nameCell = row.getCell(1);
                if (nameCell == null || nameCell.getCellType() != CellType.STRING) {
                    continue;
                }
                String rawName = nameCell.getStringCellValue();
                String cleanName = extractCleanName(rawName);

                // 플랫폼
                Cell platformCell = row.getCell(3);
                String platform =
                    (platformCell != null && platformCell.getCellType() == CellType.STRING)
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
                    toInsert.add(new CompanyEntity(cleanName, platform, url));

                } else {
                    // 기존 → 변경된 경우만 업데이트 및 저장된 공고 삭제
                    boolean isModified =
                        !Objects.equals(existing.getRecruitmentPlatform(), platform) ||
                            !Objects.equals(existing.getRecruitmentUrl(), url);

                    if (isModified) {
                        existing.setRecruitmentPlatform(platform);
                        existing.setRecruitmentUrl(url);
                        toInsert.add(existing);
                        toDeleteJobPostingEntityByCompanyId.add(existing.getId());
                    }
                }
            }

            companyRepository.saveAll(toInsert);
            jobPostingRepository.deleteByCompanyIdIn(toDeleteJobPostingEntityByCompanyId);

        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException("엑셀 처리 중 오류 발생", e);
        }
    }

    private static String extractCleanName(String rawName) {

        if (rawName == null) {
            return "";
        }

        String cleaned = rawName.replaceAll("\\(.*?\\)", "");

        cleaned = cleaned.replaceAll(
            "(?i)(^\\s*(?:주식회사|유한회사)\\s*|\\s*(?:주식회사|유한회사)\\s*$)", "");

        return cleaned.trim();
    }
}
