package com.example.Open_Position_Hub.uploader;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
public class ExcelImportController {

    private final ExcelImportService excelImportService;

    public ExcelImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            excelImportService.importFromExcel(file);
            return ResponseEntity.ok("엑셀 업로드 및 DB 저장 완료");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("엑셀 처리 중 오류 발생: " + e.getMessage());
        }
    }
}

