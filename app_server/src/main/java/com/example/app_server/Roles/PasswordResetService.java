package com.example.app_server.Roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetRequestRepository resetRepository;

    @Autowired
    private RoleUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createRequest(ForgotPasswordRequest request) {
        RoleUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setEmail(user.getEmail());
        resetRequest.setMessage(request.getMessage());
        resetRequest.setRequestedBy(user);
        resetRepository.save(resetRequest);
    }

    public List<PasswordResetRequest> getPendingRequests() {
        return resetRepository.findByResolvedFalse();
    }

    public void resetPassword(AdminResetPasswordRequest request) {
        RoleUser user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        List<PasswordResetRequest> unresolvedRequests = resetRepository.findByResolvedFalse()
                .stream()
                .filter(r -> r.getEmail().equals(request.getUserEmail()))
                .toList();

        for (PasswordResetRequest r : unresolvedRequests) {
            r.setResolved(true);
        }
        resetRepository.saveAll(unresolvedRequests);
    }
}
