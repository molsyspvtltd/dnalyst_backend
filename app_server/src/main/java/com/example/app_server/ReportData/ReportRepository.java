package com.example.app_server.ReportData;

import com.example.app_server.UserAccountCreation.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByUser(User user);

    Optional<Report> findTopByOrderByIdDesc();

    @Query("SELECT r.id FROM Report r WHERE r.user = :user")
    Optional<Long> findReportIdByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM Report r WHERE r.id = :id")
    void deleteReportById(@Param("id") Long id);
}
