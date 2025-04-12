package com.example.app_server.ReportData;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Replace findByIdWithSubcategories with this:
    @EntityGraph(attributePaths = {"subcategories"})
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdWithSubcategories(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Long reportId);
}


