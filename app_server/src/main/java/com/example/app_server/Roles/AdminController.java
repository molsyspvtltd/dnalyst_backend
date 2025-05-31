package com.example.app_server.Roles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-subadmin")
    public RoleUser createSubAdmin(@RequestBody RoleUser user, @RequestParam Role subAdminRole) {
        return adminService.createSubAdmin(user, subAdminRole);
    }

    @PostMapping("/create-staff")
    public RoleUser createStaff(@RequestBody RoleUser user, @RequestParam String subAdminId) {
        return adminService.createStaff(user, subAdminId);
    }

    @GetMapping("/all-users")
    public List<RoleUser> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/master")
    public ResponseEntity<RoleUser> getMasterAdmin() {
        RoleUser masterAdmin = adminService.getMasterAdmin();
        if (masterAdmin != null) {
            return ResponseEntity.ok(masterAdmin);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/remove-staff")
    public ResponseEntity<String> removeStaff(@RequestParam String staffId) {
        adminService.removeStaff(staffId);
        return ResponseEntity.ok("Staff removed successfully.");
    }

    @DeleteMapping("/remove-subadmin")
    public ResponseEntity<String> removeSubAdmin(@RequestParam String subAdminId) {
        adminService.removeSubAdmin(subAdminId);
        return ResponseEntity.ok("Sub-Admin and associated staff removed successfully.");
    }
    @PutMapping("/assign-subadmin")
    public ResponseEntity<String> assignSubAdminToBooking(@RequestParam String bookingType,
                                                          @RequestParam String bookingId,
                                                          @RequestParam String subAdminId) {
        try {
            adminService.assignSubAdminToBooking(bookingType, bookingId, subAdminId);
            return ResponseEntity.ok("Sub-Admin assigned successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error assigning Sub-Admin: " + e.getMessage());
        }
    }

    @GetMapping("/unassigned-bookings")
    public ResponseEntity<?> getUnassignedBookings(@RequestParam String bookingType) {
        try {
            List<?> bookings = adminService.getUnassignedBookings(bookingType);
            return ResponseEntity.ok(bookings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/clients")
    public ResponseEntity<List<SubscriptionDTO>> getClientsBySubAdminAndRole(
            @PathVariable("id") String subAdminId,
            @RequestParam("role") Role role) {
        try {
            List<SubscriptionDTO> clients = adminService.getClientsAssignedToSubAdmin(subAdminId, role);
            return ResponseEntity.ok(clients);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/sub-admin")
    public ResponseEntity<List<RoleUser>> getUsersByRole(@RequestParam Role role) {
        List<RoleUser> users = adminService.getUsersByRole(role);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }


}
