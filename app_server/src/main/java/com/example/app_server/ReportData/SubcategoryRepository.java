package com.example.app_server.ReportData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    Optional<Subcategory> findByNameAndCategory(String name, Category category);
    @Modifying
    @Query("DELETE FROM Subcategory s WHERE s.category.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Long reportId);
}
