package com.example.app_server.SubscriptionDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @GetMapping("/{dnlId}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable String dnlId) {
        return subscriptionService.getSubscriptionById(dnlId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createSubscription(@RequestBody Subscription subscription) {
        try {
            subscriptionService.createSubscription(subscription);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Subscription created successfully. Awaiting team verification.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }


    @PutMapping("/{dnlId}")
    public ResponseEntity<?> updateSubscription(@PathVariable String dnlId, @RequestBody Subscription subscription) {
        try {
            // Ensure that update is only performed if the subscription exists
            Subscription updatedSubscription = subscriptionService.updateSubscription(dnlId, subscription);
            return ResponseEntity.ok(updatedSubscription);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/{dnlId}/status")
    public ResponseEntity<?> updateSubscriptionStatus(
            @PathVariable String dnlId,
            @RequestParam String status
    ) {
        try {
            subscriptionService.updateSubscriptionStatus(dnlId, status);
            String message = status.equalsIgnoreCase("APPROVED") ?
                    "Subscription approved successfully." :
                    "Subscription rejected.";
            return ResponseEntity.ok(message);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


    @DeleteMapping("/{dnlId}")
    public ResponseEntity<?> deleteSubscription(@PathVariable String dnlId) {
        try {
            subscriptionService.deleteSubscription(dnlId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}

