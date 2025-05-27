package com.example.app_server.ReportData;

import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.SubscriptionDetails.SubscriptionRepository;
import com.example.app_server.UserAccountCreation.User;
import com.example.app_server.UserAccountCreation.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class ReportService {

    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private ReportRepository reportRepository;
    @Autowired private ReportTypeRepository reportTypeRepository;
    @Autowired private SubcategoryRepository subcategoryRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ReportSubcategoryValueRepository reportSubcategoryValueRepository;
    @Autowired private ReportSubcategoryFieldValueRepository reportSubcategoryFieldValueRepository;


    @Transactional
    public void uploadReportFromJson(String dnlId, MultipartFile file) throws Exception {
        Subscription subscription = subscriptionRepository.findByDnlId(dnlId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (reportRepository.findBySubscription(subscription).isPresent()) {
            throw new RuntimeException("User already has a report");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file.getInputStream());

        String reportTypeName = root.get("reportType").asText();
        ReportType reportType = reportTypeRepository.findByNameIgnoreCase(reportTypeName)
                .orElseThrow(() -> new RuntimeException("Invalid report type"));


        Report report = new Report();
        report.setId(generateCustomId());
        report.setSubscription(subscription);
        report.setReportType(reportType);
        report.setCreatedAt(LocalDateTime.now());
        report = reportRepository.save(report);

        List<ReportSubcategoryValue> values = new ArrayList<>();

        for (JsonNode categoryNode : root.get("categories")) {
            String categoryName = categoryNode.get("name").asText();

            Category category = new Category();
            category.setName(categoryName);
            category.setReport(report);
            Category savedCategory = categoryRepository.save(category);

            for (JsonNode subcategoryNode : categoryNode.get("subcategories")) {
                String subcategoryName = subcategoryNode.get("name").asText();
                String advice = subcategoryNode.has("advice") ? subcategoryNode.get("advice").asText() : null;

                Optional<Subcategory> existingSubcategoryOpt = subcategoryRepository.findByNameAndCategory(subcategoryName, category);
                Subcategory subcategory = existingSubcategoryOpt.orElseGet(() -> {
                    Subcategory newSub = new Subcategory();
                    newSub.setName(subcategoryName);
                    newSub.setAdvice(advice);
                    newSub.setCategory(savedCategory);
                    return subcategoryRepository.save(newSub);
                });

                ReportSubcategoryValue value = new ReportSubcategoryValue();
                value.setReport(report);
                value.setSubcategory(subcategory);
                value = reportSubcategoryValueRepository.save(value); // Initial save

                List<ReportSubcategoryFieldValue> fieldValues = new ArrayList<>();

                JsonNode fieldsNode = subcategoryNode.get("fields");
                if (fieldsNode.isArray()) {
                    for (JsonNode fieldObj : fieldsNode) {
                        Iterator<String> fieldNames = fieldObj.fieldNames();
                        while (fieldNames.hasNext()) {
                            String fieldName = fieldNames.next();
                            String fieldValue = fieldObj.get(fieldName).asText();

                            ReportSubcategoryFieldValue fieldEntry = new ReportSubcategoryFieldValue();
                            fieldEntry.setFieldName(fieldName);
                            fieldEntry.setFieldValue(fieldValue);
                            fieldEntry.setSubcategoryValue(value);

                            reportSubcategoryFieldValueRepository.save(fieldEntry);
                            fieldValues.add(fieldEntry);
                        }
                    }
                }

                value.setFieldValues(fieldValues);
                values.add(value);

            }
        }


        report.setSubcategoryValues(values);
        reportRepository.save(report);
    }

    @Transactional
    public void deleteReportByDnlId(String dnlId) {
        // 1. Find user and get report ID
        Subscription subscription = subscriptionRepository.findByDnlId(dnlId)
                .orElseThrow(() -> new RuntimeException("User not found with DNL ID: " + dnlId));

        Long reportId = reportRepository.findReportIdBySubscription(subscription)
                .orElseThrow(() -> new RuntimeException("No report found for user: " + dnlId));

        // 2. Delete in proper order using JPQL
        try {
            // Delete field values first
            reportSubcategoryFieldValueRepository.deleteByReportId(reportId);

            // Then delete subcategory values
            reportSubcategoryValueRepository.deleteByReportId(reportId);

            // Delete subcategories
            subcategoryRepository.deleteByReportId(reportId);

            // Delete categories
            categoryRepository.deleteByReportId(reportId);

            // Finally delete the report
            reportRepository.deleteReportById(reportId);

            // Flush changes immediately
            reportRepository.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete report: " + e.getMessage(), e);
        }
    }

    public String getReportAsJson(String dnlId) throws Exception {
        Subscription subscription = subscriptionRepository.findByDnlId(dnlId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Report report = reportRepository.findBySubscription(subscription)
                .orElseThrow(() -> new RuntimeException("No report found for user"));

        // You can construct the JSON manually or using Jackson
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        response.put("dnlId", dnlId);
        response.put("reportType", report.getReportType().getName());
        response.put("createdAt", report.getCreatedAt().toString());

        List<Map<String, Object>> categoriesList = new ArrayList<>();
        for (Category category : report.getCategories()) {
            Map<String, Object> catMap = new HashMap<>();
            catMap.put("name", category.getName());

            List<Map<String, Object>> subcategoriesList = new ArrayList<>();
            for (Subcategory sub : category.getSubcategories()) {
                Map<String, Object> subMap = new HashMap<>();
                subMap.put("name", sub.getName());
                subMap.put("advice", sub.getAdvice());

                List<Map<String, String>> fields = new ArrayList<>();
                for (ReportSubcategoryValue value : report.getSubcategoryValues()) {
                    if (value.getSubcategory().equals(sub)) {
                        for (ReportSubcategoryFieldValue field : value.getFieldValues()) {
                            fields.add(Map.of(field.getFieldName(), field.getFieldValue()));
                        }
                    }
                }
                subMap.put("fields", fields);
                subcategoriesList.add(subMap);
            }

            catMap.put("subcategories", subcategoriesList);
            categoriesList.add(catMap);
        }

        response.put("categories", categoriesList);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
    }

    private String generateCustomId() {
        Optional<Report> lastReport = reportRepository.findTopByOrderByIdDesc();
        String lastId = lastReport.map(Report::getId).orElse("DNLRPT000");

        int num = Integer.parseInt(lastId.replace("DNLRPT", ""));
        return String.format("RPT%03d", num + 1);
    }

}
