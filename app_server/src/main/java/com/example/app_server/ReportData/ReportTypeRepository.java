package com.example.app_server.ReportData;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportTypeRepository extends JpaRepository<ReportType, Long> {
    Optional<ReportType> findByNameIgnoreCase(String name);

}

