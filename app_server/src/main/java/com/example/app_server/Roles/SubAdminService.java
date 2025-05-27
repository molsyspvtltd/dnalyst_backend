package com.example.app_server.Roles;

import com.example.app_server.BookingDetails.*;
import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.UserAccountCreation.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubAdminService {

    private final RoleUserRepository roleuserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhysiotherapistBookingRepository physiotherapistBookingRepository;
    private final DoctorBookingRepository doctorBookingRepository;
    private final PhlebotomistBookingRepository phlebotomistBookingRepository;
    private final CounsellorBookingRepository counsellorBookingRepository;
    private final DieticianBookingRepository dieticianBookingRepository;
    private final FileStorageService fileStorageService;


    public SubAdminService(RoleUserRepository roleuserRepository, CounsellorBookingRepository counsellorBookingRepository, PasswordEncoder passwordEncoder, PhysiotherapistBookingRepository physiotherapistBookingRepository, DieticianBookingRepository dieticianBookingRepository, PhlebotomistBookingRepository phlebotomistBookingRepository, DoctorBookingRepository doctorBookingRepository, FileStorageService fileStorageService) {
        this.roleuserRepository = roleuserRepository;
        this.passwordEncoder = passwordEncoder;
        this.physiotherapistBookingRepository = physiotherapistBookingRepository;
        this.dieticianBookingRepository = dieticianBookingRepository;
        this.doctorBookingRepository = doctorBookingRepository;
        this.phlebotomistBookingRepository = phlebotomistBookingRepository;
        this.counsellorBookingRepository = counsellorBookingRepository;
        this.fileStorageService = fileStorageService;
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            throw new RuntimeException("User not authenticated: " + principal);
        }
    }

    public List<RoleUser> getAllStaff() {
        String subAdminEmail = getCurrentUserEmail();
        RoleUser subAdmin = roleuserRepository.findByEmail(subAdminEmail)
                .orElseThrow(() -> new RuntimeException("Sub-Admin not found"));

        System.out.println("Sub-Admin ID: " + subAdmin.getId());
        System.out.println("Sub-Admin Email: " + subAdmin.getEmail());

        List<RoleUser> staffList = roleuserRepository.findByManagedBy(subAdmin);
        System.out.println("Staff Count: " + staffList.size());

        return staffList;
    }


    public RoleUser createStaff(RoleUser roleuser) {
        String subAdminEmail = getCurrentUserEmail();
        RoleUser subAdmin = roleuserRepository.findByEmail(subAdminEmail)
                .orElseThrow(() -> new RuntimeException("Sub-Admin not found"));

        // Generate unique Staff ID
        String nextStaffId = generateNextStaffId(subAdmin.getId());

        roleuser.setId(nextStaffId); // Set generated staff ID
        roleuser.setManagedBy(subAdmin);
        roleuser.setRole(Role.STAFF);
        roleuser.setPassword(passwordEncoder.encode(roleuser.getPassword()));

        return roleuserRepository.save(roleuser);
    }

    // Helper function to generate next Staff ID
    private String generateNextStaffId(String subAdminId) {
        String lastStaffId = roleuserRepository.findLastIdByPrefix(subAdminId + "ST%"); // Fetch latest staff ID under sub-admin
        int nextNumber = (lastStaffId != null) ? Integer.parseInt(lastStaffId.substring(subAdminId.length() + 2)) + 1 : 1;
        return String.format("%sST%03d", subAdminId, nextNumber);
    }

    public String deleteStaff(String staffId) {
        String subAdminEmail = getCurrentUserEmail();
        RoleUser subAdmin = roleuserRepository.findByEmail(subAdminEmail)
                .orElseThrow(() -> new RuntimeException("Sub-Admin not found"));

        RoleUser staff = roleuserRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Ensure the staff belongs to the current sub-admin
        if (!staff.getManagedBy().equals(subAdmin)) {
            return "Error: You can only remove your own staff!";
        }

        roleuserRepository.delete(staff);
        return "Staff removed successfully!";
    }

    @Transactional
    public List<SubscriptionDTO> getSubscriptionsAssignedToSubAdmin() {
        RoleUser subAdmin = getLoggedInSubAdmin();

        List<Subscription> subscriptions = switch (subAdmin.getRole()) {
            case DOCTOR -> doctorBookingRepository.findByAssignedTo(subAdmin)
                    .stream()
                    .map(DoctorBooking::getSubscription)
                    .distinct()
                    .toList();

            case DIETICIAN -> dieticianBookingRepository.findByAssignedTo(subAdmin)
                    .stream()
                    .map(DieticianBooking::getSubscription)
                    .distinct()
                    .toList();

            case PHYSIO -> physiotherapistBookingRepository.findByAssignedTo(subAdmin)
                    .stream()
                    .map(PhysiotherapistBooking::getSubscription)
                    .distinct()
                    .toList();

            case COUNSELLOR -> counsellorBookingRepository.findByAssignedTo(subAdmin)
                    .stream()
                    .map(CounsellorBooking::getSubscription)
                    .distinct()
                    .toList();

            case PHLEBOTOMIST -> phlebotomistBookingRepository.findByAssignedTo(subAdmin)
                    .stream()
                    .map(PhlebotomistBooking::getSubscription)
                    .distinct()
                    .toList();

            default -> throw new RuntimeException("Invalid sub-admin role");
        };

        // Map Subscription entities to SubscriptionDTOs with user summary info
        return subscriptions.stream().map(sub -> {
            var user = sub.getUser();
            UserSummaryDTO userSummary = new UserSummaryDTO(
                    user.getMrnId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getOccupation(),
                    user.getHeight(),
                    user.getWeight()
            );

            String productName = sub.getProduct().getProductName();
            return new SubscriptionDTO(
                    sub.getDnlId(),
                    productName,
                    userSummary
            );
        }).collect(Collectors.toList());
    }

    public List<SubscriptionDTO> getTodaySubscriptionsAssignedToSubAdmin() {
        RoleUser subAdmin = getLoggedInSubAdmin();

        // Today range
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Subscription> subscriptions = switch (subAdmin.getRole()) {
            case DOCTOR -> doctorBookingRepository
                    .findByAssignedToAndBookingTimeBetween(subAdmin, startOfDay, endOfDay)
                    .stream().map(DoctorBooking::getSubscription).distinct().toList();

            case DIETICIAN -> dieticianBookingRepository
                    .findByAssignedToAndBookingTimeBetween(subAdmin, startOfDay, endOfDay)
                    .stream().map(DieticianBooking::getSubscription).distinct().toList();

            case PHYSIO -> physiotherapistBookingRepository
                    .findByAssignedToAndBookingTimeBetween(subAdmin, startOfDay, endOfDay)
                    .stream().map(PhysiotherapistBooking::getSubscription).distinct().toList();

            case COUNSELLOR -> counsellorBookingRepository
                    .findByAssignedToAndBookingTimeBetween(subAdmin, startOfDay, endOfDay)
                    .stream().map(CounsellorBooking::getSubscription).distinct().toList();

            case PHLEBOTOMIST -> phlebotomistBookingRepository
                    .findByAssignedToAndBookingTimeBetween(subAdmin, startOfDay, endOfDay)
                    .stream().map(PhlebotomistBooking::getSubscription).distinct().toList();

            default -> throw new RuntimeException("Invalid sub-admin role");
        };

        if (subscriptions.isEmpty()) {
            return Collections.emptyList();
        }

        return subscriptions.stream().map(sub -> {
            var user = sub.getUser();
            UserSummaryDTO userSummary = new UserSummaryDTO(
                    user.getMrnId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getOccupation(),
                    user.getHeight(),
                    user.getWeight()
            );
            return new SubscriptionDTO(sub.getDnlId(), sub.getProduct().getProductName(), userSummary);
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<SubscriptionDTO> getTodayPendingSubscriptionsAssignedToSubAdmin() {
        RoleUser subAdmin = getLoggedInSubAdmin();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Subscription> subscriptions = switch (subAdmin.getRole()) {
            case DOCTOR -> doctorBookingRepository
                    .findByAssignedToAndStatusAndBookingTimeBetween(subAdmin, BookingStatus.PENDING, startOfDay, endOfDay)
                    .stream().map(DoctorBooking::getSubscription).distinct().toList();

            case DIETICIAN -> dieticianBookingRepository
                    .findByAssignedToAndStatusAndBookingTimeBetween(subAdmin, BookingStatus.PENDING, startOfDay, endOfDay)
                    .stream().map(DieticianBooking::getSubscription).distinct().toList();

            case PHYSIO -> physiotherapistBookingRepository
                    .findByAssignedToAndStatusAndBookingTimeBetween(subAdmin, BookingStatus.PENDING, startOfDay, endOfDay)
                    .stream().map(PhysiotherapistBooking::getSubscription).distinct().toList();

            case COUNSELLOR -> counsellorBookingRepository
                    .findByAssignedToAndStatusAndBookingTimeBetween(subAdmin, BookingStatus.PENDING, startOfDay, endOfDay)
                    .stream().map(CounsellorBooking::getSubscription).distinct().toList();

            case PHLEBOTOMIST -> phlebotomistBookingRepository
                    .findByAssignedToAndStatusAndBookingTimeBetween(subAdmin, BookingStatus.PENDING, startOfDay, endOfDay)
                    .stream().map(PhlebotomistBooking::getSubscription).distinct().toList();

            default -> throw new RuntimeException("Invalid sub-admin role");
        };

        return subscriptions.stream().map(sub -> {
            var user = sub.getUser();
            UserSummaryDTO userSummary = new UserSummaryDTO(
                    user.getMrnId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getOccupation(),
                    user.getHeight(),
                    user.getWeight()
            );
            return new SubscriptionDTO(sub.getDnlId(), sub.getProduct().getProductName(), userSummary);
        }).collect(Collectors.toList());
    }

    @Transactional
    public String uploadResultAndMarkCompleted(String bookingCode, MultipartFile file) {
        RoleUser subAdmin = getLoggedInSubAdmin();
        Role role = subAdmin.getRole();
        String filePath = fileStorageService.storeFile(file);

        switch (role) {
            case DOCTOR -> {
                DoctorBooking booking = doctorBookingRepository.findById(bookingCode)
                        .orElseThrow(() -> new RuntimeException("Doctor booking not found"));
                validateAndSaveBooking(booking, subAdmin, filePath);
            }
            case DIETICIAN -> {
                DieticianBooking booking = dieticianBookingRepository.findById(bookingCode)
                        .orElseThrow(() -> new RuntimeException("Dietician booking not found"));
                validateAndSaveBooking(booking, subAdmin, filePath);
            }
            case PHYSIO -> {
                PhysiotherapistBooking booking = physiotherapistBookingRepository.findById(bookingCode)
                        .orElseThrow(() -> new RuntimeException("Physiotherapist booking not found"));
                validateAndSaveBooking(booking, subAdmin, filePath);
            }
            case COUNSELLOR -> {
                CounsellorBooking booking = counsellorBookingRepository.findById(bookingCode)
                        .orElseThrow(() -> new RuntimeException("Counsellor booking not found"));
                validateAndSaveBooking(booking, subAdmin, filePath);
            }
            case PHLEBOTOMIST -> {
                PhlebotomistBooking booking = phlebotomistBookingRepository.findById(bookingCode)
                        .orElseThrow(() -> new RuntimeException("Phlebotomist booking not found"));
                validateAndSaveBooking(booking, subAdmin, filePath);
            }
            default -> throw new RuntimeException("Invalid sub-admin role");
        }

        return "File uploaded and booking marked as COMPLETED";
    }

    private void validateAndSaveBooking(BaseBooking booking, RoleUser subAdmin, String filePath) {
        if (!booking.getAssignedTo().getId().equals(subAdmin.getId())) {
            throw new RuntimeException("Unauthorized to update this booking");
        }

        booking.setFilePath(filePath);
        booking.setStatus(BookingStatus.COMPLETED);

        if (booking instanceof DoctorBooking d) doctorBookingRepository.save(d);
        else if (booking instanceof DieticianBooking d) dieticianBookingRepository.save(d);
        else if (booking instanceof PhysiotherapistBooking p) physiotherapistBookingRepository.save(p);
        else if (booking instanceof CounsellorBooking c) counsellorBookingRepository.save(c);
        else if (booking instanceof PhlebotomistBooking p) phlebotomistBookingRepository.save(p);
        else throw new RuntimeException("Unknown booking type");
    }


    private RoleUser getLoggedInSubAdmin() {
        String subAdminEmail = getCurrentUserEmail();
        return roleuserRepository.findByEmail(subAdminEmail)
                .orElseThrow(() -> new RuntimeException("Sub-Admin not found"));
    }

}
