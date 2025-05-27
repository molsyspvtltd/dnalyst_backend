package com.example.app_server.BookingDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/physiotherapist-bookings")
public class PhysiotherapistBookingController {

    @Autowired
    private PhysiotherapistBookingRepository physiotherapistBookingRepository;

    @Autowired
    private BookingService bookingService;

    // CREATE a new Physiotherapist booking
    @PostMapping
    public ResponseEntity<ResponseEntity<String>> createBooking(@RequestBody PhysiotherapistBookingRequest request) {
        ResponseEntity<String> booking = bookingService.bookPhysiotherapist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    // READ all bookings for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PhysiotherapistBooking>> getBookingsByUserId(@PathVariable String dnlId) {
        List<PhysiotherapistBooking> bookings = physiotherapistBookingRepository.findByDnlId(dnlId);
        return ResponseEntity.ok(bookings);
    }

    // READ the latest booking time for a specific user
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<LocalDateTime> getLastBookingTimeByUserId(@PathVariable String dnlId) {
        LocalDateTime lastBookingTime = physiotherapistBookingRepository.findLastBookingTimeByDnlId(dnlId);
        return lastBookingTime != null ? ResponseEntity.ok(lastBookingTime) : ResponseEntity.notFound().build();
    }

    // UPDATE the latest Physiotherapist booking for a specific user
    @PutMapping("/user/{userId}")
    public ResponseEntity<PhysiotherapistBooking> updateBookingByUserId(@PathVariable String dnlId,
                                                                        @RequestBody PhysiotherapistBooking updatedBooking) {
        return physiotherapistBookingRepository.findFirstByDnlIdOrderByBookingTimeDesc(dnlId)
                .map(existingBooking -> {
                    existingBooking.setBookingTime(updatedBooking.getBookingTime());
                    existingBooking.setLocation(updatedBooking.getLocation());
                    physiotherapistBookingRepository.save(existingBooking);
                    return ResponseEntity.ok(existingBooking);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE the latest Physiotherapist booking for a specific user
    @DeleteMapping("/user/{userId}/latest")
    public ResponseEntity<Object> deleteLatestBookingByUserId(@PathVariable String dnlId) {
        return physiotherapistBookingRepository.findFirstByDnlIdOrderByBookingTimeDesc(dnlId)
                .map(booking -> {
                    physiotherapistBookingRepository.delete(booking);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
