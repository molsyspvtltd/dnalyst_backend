package com.example.app_server.BookingDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/phlebotomist-bookings")
public class PhlebotomistBookingController {

    @Autowired
    private PhlebotomistBookingRepository phlebotomistBookingRepository;

    @Autowired
    private BookingService bookingService;

    // CREATE a new Phlebotomist booking
    @PostMapping
    public ResponseEntity<ResponseEntity<String>> createBooking(@RequestBody PhlebotomistBookingRequest request) {
        ResponseEntity<String> booking = bookingService.bookPhlebotomist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    // READ all bookings for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PhlebotomistBooking>> getBookingsByUserId(@PathVariable String dnlId) {
        List<PhlebotomistBooking> bookings = phlebotomistBookingRepository.findByDnlId(dnlId);
        return ResponseEntity.ok(bookings);
    }

    // READ the latest booking time for a specific user
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<LocalDateTime> getLastBookingTimeByUserId(@PathVariable String dnlId) {
        LocalDateTime lastBookingTime = phlebotomistBookingRepository.findLastBookingTimeByDnlId(dnlId);
        return lastBookingTime != null ? ResponseEntity.ok(lastBookingTime) : ResponseEntity.notFound().build();
    }

    // UPDATE the latest Phlebotomist booking for a specific user
    @PutMapping("/user/{userId}")
    public ResponseEntity<PhlebotomistBooking> updateBookingByUserId(@PathVariable String dnlId,
                                                                     @RequestBody PhlebotomistBooking updatedBooking) {
        return phlebotomistBookingRepository.findFirstByDnlIdOrderByBookingTimeDesc(dnlId)
                .map(existingBooking -> {
                    existingBooking.setBookingTime(updatedBooking.getBookingTime());
                    existingBooking.setLocation(updatedBooking.getLocation());
                    phlebotomistBookingRepository.save(existingBooking);
                    return ResponseEntity.ok(existingBooking);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE the latest Phlebotomist booking for a specific user
    @DeleteMapping("/user/{userId}/latest")
    public ResponseEntity<Object> deleteLatestBookingByUserId(@PathVariable String dnlId) {
        return phlebotomistBookingRepository.findFirstByDnlIdOrderByBookingTimeDesc(dnlId)
                .map(booking -> {
                    phlebotomistBookingRepository.delete(booking);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
