package com.example.app_server.BookingDetails;

import com.example.app_server.Roles.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhlebotomistBookingRepository extends JpaRepository<PhlebotomistBooking, String> {
    List<PhlebotomistBooking> findByAssignedToIsNull();
    List<PhlebotomistBooking> findByAssignedTo(RoleUser assignedTo);
    List<PhlebotomistBooking> findByAssignedToAndBookingTimeBetween(RoleUser assignedTo, LocalDateTime start, LocalDateTime end);

    List<PhlebotomistBooking> findByAssignedToAndStatus(RoleUser assignedTo, BookingStatus status);
    List<PhlebotomistBooking> findByAssignedToAndStatusAndBookingTimeBetween(RoleUser assignedTo,
                                                                             BookingStatus status,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    // Find all bookings for a specific user's MRN ID
    @Query("SELECT b FROM PhlebotomistBooking b WHERE b.subscription.dnlId = :dnlId")
    List<PhlebotomistBooking> findByDnlId(@Param("dnlId") String dnlId);

    // Find the last booking time for a specific user's MRN ID
    @Query("SELECT MAX(b.bookingTime) FROM PhlebotomistBooking b WHERE b.subscription.dnlId = :dnlId")
    LocalDateTime findLastBookingTimeByDnlId(@Param("dnlId") String dnlId);

    // Find the latest booking for a specific user's MRN ID
    @Query("SELECT b FROM PhlebotomistBooking b WHERE b.subscription.dnlId = :dnlId ORDER BY b.bookingTime DESC")
    Optional<PhlebotomistBooking> findFirstByDnlIdOrderByBookingTimeDesc(@Param("dnlId") String dnlId);

    // Check if a user has any bookings by their MRN ID
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM PhlebotomistBooking b WHERE b.subscription.dnlId = :dnlId")
    boolean existsByDnlId(@Param("dnlId") String dnlId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END " +
            "FROM PhlebotomistBooking b " +
            "WHERE b.subscription.dnlId = :dnlId AND b.bookingTime BETWEEN :start AND :end")
    boolean existsByDnlIdAndBookingTimeBetween(@Param("dnlId") String dnlId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}

