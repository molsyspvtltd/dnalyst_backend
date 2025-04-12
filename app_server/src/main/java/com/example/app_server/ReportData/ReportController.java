package com.example.app_server.ReportData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private ReportService reportService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadReport(
            @RequestParam String mrnId,
            @RequestParam("file") MultipartFile file) {
        try {
            reportService.uploadReportFromJson(mrnId, file);
            return ResponseEntity.ok("Report uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteReport(@RequestParam String mrnId) {
        try {
            reportService.deleteReportByMrnId(mrnId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Report and all related data deleted successfully",
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Failed to delete report: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/get-report")
    public ResponseEntity<String> getReportJson(@RequestParam String mrnId) {
        try {
            String json = reportService.getReportAsJson(mrnId);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}