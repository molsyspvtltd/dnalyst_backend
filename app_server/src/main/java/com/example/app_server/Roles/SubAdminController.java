package com.example.app_server.Roles;


import com.example.app_server.SubscriptionDetails.Subscription;
import com.example.app_server.UserAccountCreation.User;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/subadmin")
public class SubAdminController {

    private final SubAdminService subAdminService;

    public SubAdminController(SubAdminService subAdminService) {
        this.subAdminService = subAdminService;
    }

    @PostMapping("/create-staff")
    public RoleUser createStaff(@RequestBody RoleUser roleuser) {
        return subAdminService.createStaff(roleuser);
    }

    @GetMapping("/all-staff")
    public List<RoleUser> getAllStaff() {
        return subAdminService.getAllStaff();
    }

    @DeleteMapping("/remove-staff/{staffId}")
    public String removeStaff(@PathVariable String staffId) {
        return subAdminService.deleteStaff(staffId);
    }

    @GetMapping("/assigned-clients")
    @Transactional
    public List<SubscriptionDTO> getMyClients() {
        return subAdminService.getSubscriptionsAssignedToSubAdmin();
    }
    @GetMapping("/assigned-today")
    public ResponseEntity<?> getTodayUsers() {
        List<SubscriptionDTO> subs = subAdminService.getTodaySubscriptionsAssignedToSubAdmin();
        if (subs.isEmpty()) {
            return ResponseEntity.ok("No patients available today");
        }
        return ResponseEntity.ok(subs);
    }
    @GetMapping("/assigned-today/pending")
    public ResponseEntity<List<SubscriptionDTO>> getTodayPendingAssignedClients() {
        return ResponseEntity.ok(subAdminService.getTodayPendingSubscriptionsAssignedToSubAdmin());
    }

    @PostMapping("/upload-result/{bookingCode}")
    public ResponseEntity<String> uploadBookingFile(@PathVariable String bookingCode,
                                                    @RequestParam("file") MultipartFile file) {
        String result = subAdminService.uploadResultAndMarkCompleted(bookingCode, file);
        return ResponseEntity.ok(result);
    }

}
