package com.example.app_server.BookingDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/counsellor-bookings")
public class CounsellorBookingController {

    @Autowired
    private CounsellorBookingRepository counsellorBookingRepository;

    @Autowired
    private BookingService bookingService;

    // CREATE a new Counsellor booking
    @PostMapping
    public ResponseEntity<ResponseEntity<String>> createBooking(@RequestBody CounsellorBookingRequest request) {
        ResponseEntity<String> booking = bookingService.bookCounsellor(request);
        return ResponseEntity.ok(booking);
    }

    // READ all bookings for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CounsellorBooking>> getBookingsByUserId(@PathVariable String dnlId) {
        List<CounsellorBooking> bookings = counsellorBookingRepository.findByDnlId(dnlId);
        return ResponseEntity.ok(bookings);
    }

    // READ the latest booking for a specific user
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<LocalDateTime> getLastBookingTimeByUserId(@PathVariable String dnlId) {
        LocalDateTime lastBookingTime = counsellorBookingRepository.findLastBookingTimeByDnlId(dnlId);
        return lastBookingTime != null ? ResponseEntity.ok(lastBookingTime) : ResponseEntity.notFound().build();
    }

    // UPDATE a Counsellor booking for a specific user
    @PutMapping("/user/{userId}")
    public ResponseEntity<CounsellorBooking> updateBookingByUserId(@PathVariable String dnlId,
                                                                   @RequestBody CounsellorBooking updatedBooking) {
        return counsellorBookingRepository.findFirstByDnlIdOrderByBookingTimeDesc(dnlId)
                .map(existingBooking -> {
                    existingBooking.setBookingType(updatedBooking.getBookingType());
                    existingBooking.setBookingTime(updatedBooking.getBookingTime());
                    existingBooking.setLocation(updatedBooking.getLocation());
                    existingBooking.setGoogleMeetLink(updatedBooking.getGoogleMeetLink());
                    counsellorBookingRepository.save(existingBooking);
                    return ResponseEntity.ok(existingBooking);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE the latest booking for a specific user
    @DeleteMapping("/user/{dnlId}/latest")
    public ResponseEntity<Object> deleteLatestBookingByUserId(@PathVariable String dnlId) {
        return counsellorBookingRepository.findFirstByDnlIdOrderByBookingTimeDesc(dnlId)
                .map(booking -> {
                    counsellorBookingRepository.delete(booking);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
