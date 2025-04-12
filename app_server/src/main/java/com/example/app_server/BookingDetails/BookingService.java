package com.example.app_server.BookingDetails;

import com.example.app_server.UserAccountCreation.User;
import com.example.app_server.UserAccountCreation.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.stream.Stream;

//@Service
//public class BookingService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CounsellorBookingRepository counsellorBookingRepository;
//
//    @Autowired
//    private PhlebotomistBookingRepository phlebotomistBookingRepository;
//
//    @Autowired
//    private DoctorBookingRepository doctorBookingRepository;
//
//    @Autowired
//    private DieticianBookingRepository dieticianBookingRepository;
//
//    @Autowired
//    private PhysiotherapistBookingRepository physiotherapistBookingRepository;
//
//    /**
//     * Book a Counsellor session.
//     */
//    public CounsellorBooking bookCounsellor(CounsellorBookingRequest request) {
//                User user = userRepository.findByMrnId(request.getMrnId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        if (!user.getSubscriptionActive()) {
//            throw new IllegalStateException("User does not have an active subscription.");
//        }
//
//        // Check if the user has any recent bookings
//        checkBookingGap(request.getMrnId(), request.getBookingTime());
//
//        CounsellorBooking booking = new CounsellorBooking();
//        booking.setUser(user);
//        booking.setBookingType(request.getBookingType());
//        booking.setBookingTime(request.getBookingTime());
//
//        if (request.getBookingType() == BookingType.OFFLINE) {
//            booking.setLocation(request.getLocation());
//        } else {
//            booking.setGoogleMeetLink("https://meet.google.com/your-link");
//        }
//
//        return counsellorBookingRepository.save(booking);
//    }
//
//    /**
//     * Book a Phlebotomist session (offline only).
//     */
//    public PhlebotomistBooking bookPhlebotomist(PhlebotomistBookingRequest request) {
//        if (!counsellorBookingRepository.existsByMrnId(request.getMrnId())) {
//            throw new IllegalStateException("User must complete Counsellor booking first.");
//        }
//
//        if (request.getBookingType() != BookingType.ONLINE) {
//            throw new IllegalStateException("Phlebotomist bookings are allowed only in ONLINE mode.");
//        }
//
//        User user = userRepository.findByMrnId(request.getMrnId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Check if the user has any recent bookings
//        checkBookingGap(request.getMrnId(), request.getBookingTime());
//
//        PhlebotomistBooking booking = new PhlebotomistBooking();
//        booking.setUser(user);
//        booking.setBookingType(BookingType.OFFLINE);
//        booking.setBookingTime(request.getBookingTime());
//        booking.setLocation(request.getLocation());
//
//        return phlebotomistBookingRepository.save(booking);
//    }
//
//    /**
//     * Book a Doctor session.
//     */
//    public DoctorBooking bookDoctor(DoctorBookingRequest request) {
//        if (!phlebotomistBookingRepository.existsByMrnId(request.getMrnId())) {
//            throw new IllegalStateException("User must complete Phlebotomist booking first.");
//        }
//
//        User user = userRepository.findByMrnId(request.getMrnId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Check if the user has any recent bookings
//        checkBookingGap(request.getMrnId(), request.getBookingTime());
//
//        DoctorBooking booking = new DoctorBooking();
//        booking.setUser(user);
//        booking.setBookingType(request.getBookingType());
//        booking.setBookingTime(request.getBookingTime());
//
//        if (request.getBookingType() == BookingType.OFFLINE) {
//            booking.setLocation(request.getLocation());
//        } else {
//            booking.setGoogleMeetLink("https://meet.google.com/your-link");
//        }
//
//        return doctorBookingRepository.save(booking);
//    }
//
//
//    /**
//     * Book a Dietician session.
//     */
//    public DieticianBooking bookDietician(DieticianBookingRequest request) {
//        if (!doctorBookingRepository.existsByMrnId(request.getMrnId())) {
//            throw new IllegalStateException("User must complete Doctor booking first.");
//        }
//
//        User user = userRepository.findByMrnId(request.getMrnId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Check if the user has any recent bookings
//        checkBookingGap(request.getMrnId(), request.getBookingTime());
//
//        DieticianBooking booking = new DieticianBooking();
//        booking.setUser(user);
//        booking.setBookingType(request.getBookingType());
//        booking.setBookingTime(request.getBookingTime());
//
//        if (request.getBookingType() == BookingType.OFFLINE) {
//            booking.setLocation(request.getLocation());
//        } else {
//            booking.setGoogleMeetLink("https://meet.google.com/your-link");
//        }
//
//        return dieticianBookingRepository.save(booking);
//    }
//
//    /**
//     * Book a Physiotherapist session.
//     */
//    public PhysiotherapistBooking bookPhysiotherapist(PhysiotherapistBookingRequest request) {
//        if (!dieticianBookingRepository.existsByMrnId(request.getMrnId())) {
//            throw new IllegalStateException("User must complete Dietician booking first.");
//        }
//
//        User user = userRepository.findByMrnId(request.getMrnId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Check if the user has any recent bookings
//        checkBookingGap(request.getMrnId(), request.getBookingTime());
//
//        PhysiotherapistBooking booking = new PhysiotherapistBooking();
//        booking.setUser(user);
//        booking.setBookingType(request.getBookingType());
//        booking.setBookingTime(request.getBookingTime());
//
//        if (request.getBookingType() == BookingType.OFFLINE) {
//            booking.setLocation(request.getLocation());
//        } else {
//            booking.setGoogleMeetLink("https://meet.google.com/your-link");
//        }
//
//        return physiotherapistBookingRepository.save(booking);
//    }
//
//    private void checkBookingGap(String mrnId, LocalDateTime requestedBookingTime) {
//        // Get the most recent Counsellor, Phlebotomist, Doctor, Dietician, and Physiotherapist booking time.
//        LocalDateTime lastCounsellorBooking = counsellorBookingRepository.findLastBookingTimeByMrnId(mrnId);
//        LocalDateTime lastPhlebotomistBooking = phlebotomistBookingRepository.findLastBookingTimeByMrnId(mrnId);
//        LocalDateTime lastDoctorBooking = doctorBookingRepository.findLastBookingTimeByMrnId(mrnId);
//        LocalDateTime lastDieticianBooking = dieticianBookingRepository.findLastBookingTimeByMrnId(mrnId);
//        LocalDateTime lastPhysiotherapistBooking = physiotherapistBookingRepository.findLastBookingTimeByMrnId(mrnId);
//
//        // Find the most recent booking time
//        LocalDateTime lastBookingTime = Stream.of(lastCounsellorBooking, lastPhlebotomistBooking, lastDoctorBooking,
//                        lastDieticianBooking, lastPhysiotherapistBooking)
//                .filter(Objects::nonNull)
//                .max(LocalDateTime::compareTo)
//                .orElse(null);
//
//        // If there's a recent booking, check if the gap is less than 1 hour
//        if (lastBookingTime != null && lastBookingTime.plusHours(1).isAfter(requestedBookingTime)) {
//            throw new IllegalStateException("You must wait at least 1 hour before making another booking.");
//        }
//    }
//}
@Service
public class BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CounsellorBookingRepository counsellorBookingRepository;

    @Autowired
    private PhlebotomistBookingRepository phlebotomistBookingRepository;

    @Autowired
    private DoctorBookingRepository doctorBookingRepository;

    @Autowired
    private DieticianBookingRepository dieticianBookingRepository;

    @Autowired
    private PhysiotherapistBookingRepository physiotherapistBookingRepository;


    private String generateBookingCode(String prefix, JpaRepository<? extends BaseBooking, String> repo) {
        long count = repo.count() + 1;
        return String.format("BK%s%03d", prefix.toUpperCase(), count);
    }
    /**
     * Book a Counsellor session.
     */
    public ResponseEntity<String> bookCounsellor(CounsellorBookingRequest request) {
        try {
            User user = userRepository.findByMrnId(request.getMrnId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (!user.getSubscriptionActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have an active subscription.");
            }

            if (checkBookingConflict(request.getMrnId(), request.getBookingTime(), "COUNSELLOR")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You already have a Counsellor session booked for this day.");
            }
            checkBookingGap(request.getMrnId(), request.getBookingTime());

            // Generate booking code using the helper method
            String bookingCode = generateBookingCode("COUN", counsellorBookingRepository);

            // Check if the user has any recent bookings
            checkBookingGap(request.getMrnId(), request.getBookingTime());

            CounsellorBooking booking = new CounsellorBooking();
            booking.setBookingCode(bookingCode);
            booking.setUser(user);
            booking.setBookingType(request.getBookingType());
            booking.setBookingTime(request.getBookingTime());

            if (request.getBookingType() == BookingType.OFFLINE) {
                booking.setLocation(request.getLocation());
            } else {
                booking.setGoogleMeetLink("https://meet.google.com/your-link");
            }

            counsellorBookingRepository.save(booking);

            return ResponseEntity.ok("Counsellor session booked successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking Counsellor session: " + e.getMessage());
        }
    }

    /**
     * Book a Phlebotomist session (offline only).
     */
    public ResponseEntity<String> bookPhlebotomist(PhlebotomistBookingRequest request) {
        try {

            if (!counsellorBookingRepository.existsByMrnId(request.getMrnId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must complete Counsellor booking first.");
            }
            if (request.getBookingType() != BookingType.OFFLINE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phlebotomist bookings are allowed only in OFFLINE mode.");
            }
            if (checkBookingConflict(request.getMrnId(), request.getBookingTime(), "PHLEBOTOMIST")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You already have a Phlebotomist session booked for this day.");
            }



            User user = userRepository.findByMrnId(request.getMrnId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String bookingCode = generateBookingCode("BKPHL", phlebotomistBookingRepository);
            // Check if the user has any recent bookings
            checkBookingGap(request.getMrnId(), request.getBookingTime());

            PhlebotomistBooking booking = new PhlebotomistBooking();
            booking.setBookingCode(bookingCode);
            booking.setUser(user);
            booking.setBookingType(BookingType.OFFLINE);
            booking.setBookingTime(request.getBookingTime());
            booking.setLocation(request.getLocation());

            phlebotomistBookingRepository.save(booking);

            return ResponseEntity.ok("Phlebotomist session booked successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking Phlebotomist session: " + e.getMessage());
        }
    }

    /**
     * Book a Doctor session.
     */
    public ResponseEntity<String> bookDoctor(DoctorBookingRequest request) {
        try {
            if (!phlebotomistBookingRepository.existsByMrnId(request.getMrnId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must complete Phlebotomist booking first.");
            }

            User user = userRepository.findByMrnId(request.getMrnId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (checkBookingConflict(request.getMrnId(), request.getBookingTime(), "DOCTOR")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You already have a Doctor session booked for this day.");
            }


            String bookingCode = generateBookingCode("BKDOC", doctorBookingRepository);
            // Check if the user has any recent bookings
            checkBookingGap(request.getMrnId(), request.getBookingTime());

            DoctorBooking booking = new DoctorBooking();
            booking.setBookingCode(bookingCode);
            booking.setUser(user);
            booking.setBookingType(request.getBookingType());
            booking.setBookingTime(request.getBookingTime());

            if (request.getBookingType() == BookingType.OFFLINE) {
                booking.setLocation(request.getLocation());
            } else {
                booking.setGoogleMeetLink("https://meet.google.com/your-link");
            }

            doctorBookingRepository.save(booking);

            return ResponseEntity.ok("Doctor session booked successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking Doctor session: " + e.getMessage());
        }
    }

    /**
     * Book a Dietician session.
     */
    public ResponseEntity<String> bookDietician(DieticianBookingRequest request) {
        try {
            if (!doctorBookingRepository.existsByMrnId(request.getMrnId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must complete Doctor booking first.");
            }

            User user = userRepository.findByMrnId(request.getMrnId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (checkBookingConflict(request.getMrnId(), request.getBookingTime(), "DIETICIAN")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You already have a Dietician session booked for this day.");
            }

            String bookingCode = generateBookingCode("BKDIT", dieticianBookingRepository);
            // Check if the user has any recent bookings
            checkBookingGap(request.getMrnId(), request.getBookingTime());

            DieticianBooking booking = new DieticianBooking();
            booking.setBookingCode(bookingCode);
            booking.setUser(user);
            booking.setBookingType(request.getBookingType());
            booking.setBookingTime(request.getBookingTime());

            if (request.getBookingType() == BookingType.OFFLINE) {
                booking.setLocation(request.getLocation());
            } else {
                booking.setGoogleMeetLink("https://meet.google.com/your-link");
            }

            dieticianBookingRepository.save(booking);

            return ResponseEntity.ok("Dietician session booked successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking Dietician session: " + e.getMessage());
        }
    }

    /**
     * Book a Physiotherapist session.
     */
    public ResponseEntity<String> bookPhysiotherapist(PhysiotherapistBookingRequest request) {
        try {
            if (!dieticianBookingRepository.existsByMrnId(request.getMrnId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must complete Dietician booking first.");
            }

            User user = userRepository.findByMrnId(request.getMrnId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (checkBookingConflict(request.getMrnId(), request.getBookingTime(), "PHYSIOTHERAPIST")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("You already have a Physiotherapist session booked for this day.");
            }

            String bookingCode = generateBookingCode("BKPHY", physiotherapistBookingRepository);

            // Check if the user has any recent bookings
            checkBookingGap(request.getMrnId(), request.getBookingTime());

            PhysiotherapistBooking booking = new PhysiotherapistBooking();
            booking.setBookingCode(bookingCode);
            booking.setUser(user);
            booking.setBookingType(request.getBookingType());
            booking.setBookingTime(request.getBookingTime());

            if (request.getBookingType() == BookingType.OFFLINE) {
                booking.setLocation(request.getLocation());
            } else {
                booking.setGoogleMeetLink("https://meet.google.com/your-link");
            }

            physiotherapistBookingRepository.save(booking);

            return ResponseEntity.ok("Physiotherapist session booked successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error booking Physiotherapist session: " + e.getMessage());
        }
    }

    private void checkBookingGap(String mrnId, LocalDateTime requestedBookingTime) {
        // Get the most recent Counsellor, Phlebotomist, Doctor, Dietician, and Physiotherapist booking time.
        LocalDateTime lastCounsellorBooking = counsellorBookingRepository.findLastBookingTimeByMrnId(mrnId);
        LocalDateTime lastPhlebotomistBooking = phlebotomistBookingRepository.findLastBookingTimeByMrnId(mrnId);
        LocalDateTime lastDoctorBooking = doctorBookingRepository.findLastBookingTimeByMrnId(mrnId);
        LocalDateTime lastDieticianBooking = dieticianBookingRepository.findLastBookingTimeByMrnId(mrnId);
        LocalDateTime lastPhysiotherapistBooking = physiotherapistBookingRepository.findLastBookingTimeByMrnId(mrnId);

        // Find the most recent booking time
        LocalDateTime lastBookingTime = Stream.of(lastCounsellorBooking, lastPhlebotomistBooking, lastDoctorBooking,
                        lastDieticianBooking, lastPhysiotherapistBooking)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // If there's a recent booking, check if the gap is less than 1 hour
        if (lastBookingTime != null && lastBookingTime.plusHours(1).isAfter(requestedBookingTime)) {
            throw new IllegalStateException("You must wait at least 1 hour before making another booking.");
        }
    }

    private boolean checkBookingConflict(String mrnId, LocalDateTime requestedBookingTime, String sessionType) {
        LocalDate bookingDate = requestedBookingTime.toLocalDate();
        LocalDateTime start = bookingDate.atStartOfDay();
        LocalDateTime end = bookingDate.atTime(LocalTime.MAX);

        switch (sessionType.toUpperCase()) {
            case "COUNSELLOR":
                return counsellorBookingRepository.existsByMrnIdAndBookingTimeBetween(mrnId, start, end);
            case "PHLEBOTOMIST":
                return phlebotomistBookingRepository.existsByMrnIdAndBookingTimeBetween(mrnId, start, end);
            case "DOCTOR":
                return doctorBookingRepository.existsByMrnIdAndBookingTimeBetween(mrnId, start, end);
            case "DIETICIAN":
                return dieticianBookingRepository.existsByMrnIdAndBookingTimeBetween(mrnId, start, end);
            case "PHYSIOTHERAPIST":
                return physiotherapistBookingRepository.existsByMrnIdAndBookingTimeBetween(mrnId, start, end);
            default:
                throw new IllegalArgumentException("Invalid session type");
        }

    }



}

