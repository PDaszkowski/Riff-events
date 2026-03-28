package com.riffevents.controller;

import com.riffevents.model.Event;
import com.riffevents.model.User;
import com.riffevents.repository.EventRepository;
import com.riffevents.service.StripePaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final EventRepository eventRepository;
    private final StripePaymentService stripePaymentService;

    public PaymentController(EventRepository eventRepository,
                             StripePaymentService stripePaymentService) {
        this.eventRepository = eventRepository;
        this.stripePaymentService = stripePaymentService;
    }

    @PostMapping("/checkout/{eventId}")
    public ResponseEntity<?> checkout(
            @PathVariable Long eventId,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal User user) throws Exception {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: User not logged in.");
        }

        int quantity = body.getOrDefault("quantity", 1);

        if (quantity < 1 || quantity > 10) {
            return ResponseEntity.badRequest()
                    .body("Invalid ticket quantity");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getSoldTickets() + quantity > event.getTotalTickets()) {
            return ResponseEntity.badRequest().body("Not enough tickets");
        }

        String checkoutUrl = stripePaymentService.createCheckoutSession(event, user, quantity);

        return ResponseEntity.ok(Map.of("url", checkoutUrl));
    }

    @GetMapping("/debug/session")
    public ResponseEntity<String> debugSession(HttpSession session,
                                               @AuthenticationPrincipal User user) {
        System.out.println("=== DEBUG SESSION ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("User in session: " + (user != null ? user.getEmail() : "null"));
        System.out.println("=====================");
        return ResponseEntity.ok("Check console logs for session info");
    }

}
