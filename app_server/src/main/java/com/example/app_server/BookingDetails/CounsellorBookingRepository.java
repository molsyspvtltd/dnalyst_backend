package com.example.app_server.BookingDetails;

import com.example.app_server.Roles.RoleUser;
import com.example.app_server.UserAccountCreation.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//@Repository
//public interface CounsellorBookingRepository extends JpaRepository<CounsellorBooking, String> {
//
//    // Find all bookings for a specific user
//    @Query("SELECT b FROM CounsellorBooking b WHERE b.user.mrnId = :mrnId")
//    List<CounsellorBooking> findByMrnId(@Param("mrnId") String mrnId);
//
//
//    // Find the last booking time for a specific user
//    @Query("SELECT b.bookingTime FROM CounsellorBooking b WHERE b.user.mrnId = :mrnId ORDER BY b.bookingTime DESC")
//    LocalDateTime findLastBookingTimeByMrnId(@Param("mrnId") String mrnId);
//
//    // Find the latest booking for a specific user
//    Optional<CounsellorBooking> findFirstByMrnIdOrderByBookingTimeDesc(String mrnId);
//
//    // Check if a user has any bookings
//    boolean existsByMrnId(String mrnId);



//}


@Repository
public interface CounsellorBookingRepository extends JpaRepository<CounsellorBooking, String> {

    // Bookings that are not yet assigned to a counsellor
    List<CounsellorBooking> findByAssignedToAndBookingTimeBetween(RoleUser assignedTo, LocalDateTime start, LocalDateTime end);

    List<CounsellorBooking> findByAssignedTo(RoleUser assignedTo);

    List<CounsellorBooking> findByAssignedToIsNull();

    List<CounsellorBooking> findByAssignedToAndStatus(RoleUser assignedTo, BookingStatus status);
    List<CounsellorBooking> findByAssignedToAndStatusAndBookingTimeBetween(RoleUser assignedTo,
                                                                           BookingStatus status,
                                                                           LocalDateTime start,
                                                                           LocalDateTime end);


    // Find all bookings for a specific user's MRN ID
    @Query("SELECT b FROM CounsellorBooking b WHERE b.subscription.dnlId = :dnlId")
    List<CounsellorBooking> findByDnlId(@Param("dnlId") String dnlId);


    // ✅ Find the last booking time for a specific user's MRN ID using MAX
    @Query("SELECT MAX(b.bookingTime) FROM CounsellorBooking b WHERE b.subscription.dnlId = :dnlId")
    LocalDateTime findLastBookingTimeByDnlId(@Param("dnlId") String dnlId);


    // Optionally: also keep this for full booking record retrieval (not needed in booking gap logic)
    @Query("SELECT b FROM CounsellorBooking b WHERE b.subscription.dnlId = :dnlId ORDER BY b.bookingTime DESC")
    Optional<CounsellorBooking> findFirstByDnlIdOrderByBookingTimeDesc(@Param("dnlId") String dnlId);


    // Check if a user has any bookings by their MRN ID
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM CounsellorBooking b WHERE b.subscription.dnlId = :dnlId")
    boolean existsByDnlId(@Param("dnlId") String dnlId);


    // Check for booking conflict on the same day
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END " +
            "FROM CounsellorBooking b " +
            "WHERE b.subscription.dnlId = :dnlId AND b.bookingTime BETWEEN :start AND :end")
    boolean existsByDnlIdAndBookingTimeBetween(@Param("dnlId") String dnlId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

}

