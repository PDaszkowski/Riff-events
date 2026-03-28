package com.riffevents.controller;

import com.google.zxing.WriterException;
import com.riffevents.model.Ticket;
import com.riffevents.model.User;
import com.riffevents.repository.EventRepository;
import com.riffevents.repository.TicketRepository;
import com.riffevents.repository.UserRepository;
import com.riffevents.service.EmailService;
import com.riffevents.service.QRCodeService;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.signing-secret}")
    private String signingSecret;

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public StripeWebhookController(EventRepository eventRepository,
                                   TicketRepository ticketRepository,
                                   UserRepository userRepository,
                                   EmailService emailService) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) throws Exception {

        com.stripe.model.Event stripeEvent = Webhook.constructEvent(
                payload, sigHeader, signingSecret
        );

        if ("checkout.session.completed".equals(stripeEvent.getType())) {
            com.stripe.model.EventDataObjectDeserializer dataObjectDeserializer = stripeEvent.getDataObjectDeserializer();
            Session session = null;

            if (dataObjectDeserializer.getObject().isPresent()) {
                session = (Session) dataObjectDeserializer.getObject().get();
            } else {

                session = (Session) dataObjectDeserializer.deserializeUnsafe();
            }

            if (session != null) {
                handleSuccessfulPayment(session);
            } else {
                System.out.println("ERROR: Deserialization session object error.");
            }
        }

        return ResponseEntity.ok("OK");
    }

    private void handleSuccessfulPayment(Session session) throws Exception {
        Long eventId = Long.valueOf(session.getMetadata().get("eventId"));
        Long userId = Long.valueOf(session.getMetadata().get("userId"));
        int quantity = Integer.parseInt(session.getMetadata().get("quantity"));

        System.out.println("Metadata: " + session.getMetadata());

        com.riffevents.model.Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (int i = 0; i < quantity; i++) {

            String qrCodeContent = UUID.randomUUID().toString();

            Ticket ticket = new Ticket(
                    event,
                    user,
                    null,
                    event.getTicketPrice(),
                    qrCodeContent,
                    "PAID"
            );
            ticketRepository.save(ticket);

            System.out.println("Received webhook for userId=" + userId);
            System.out.println("User email: " + user.getEmail());
            System.out.println("Ticket QR: " + qrCodeContent);

            emailService.sendTicketEmail(
                    user.getEmail(),
                    "Welcome " + user.getName() + ", Congrats on buying your ticket to: " + event.getTitle(),
                    "Thank you for purchase! Your ticket is in attachment.",
                    qrCodeContent
            );
        }

        event.setSoldTickets(event.getSoldTickets() + quantity);
        eventRepository.save(event);
    }

}
