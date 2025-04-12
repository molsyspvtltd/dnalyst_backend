package com.example.app_server.ReportData;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportTypeSeeder {

    @Autowired
    private ReportTypeRepository reportTypeRepository;

    @PostConstruct
    public void init() {
        List<String> types = List.of("Normal", "Child", "Sports");

        for (String type : types) {
            if (reportTypeRepository.findByNameIgnoreCase(type).isEmpty()) {
                ReportType rt = new ReportType();
                rt.setName(type);
                reportTypeRepository.save(rt);
            }
        }
    }
}
