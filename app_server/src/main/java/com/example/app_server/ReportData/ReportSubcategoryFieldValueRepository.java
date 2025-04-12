package com.example.app_server.ReportData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportSubcategoryFieldValueRepository extends JpaRepository<ReportSubcategoryFieldValue, Long> {
    @Modifying
    @Query("DELETE FROM ReportSubcategoryFieldValue f WHERE f.subcategoryValue.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Long reportId);
}
