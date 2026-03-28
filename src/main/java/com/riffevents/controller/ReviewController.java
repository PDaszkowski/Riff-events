package com.riffevents.controller;

import com.riffevents.model.Event;
import com.riffevents.model.Review;
import com.riffevents.model.User;
import com.riffevents.repository.EventRepository;
import com.riffevents.repository.ReviewRepository;
import com.riffevents.repository.TicketRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/events")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public ReviewController(ReviewRepository reviewRepository, EventRepository eventRepository, TicketRepository ticketRepository) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/{eventId}/reviews")
    public String showEventReviews(@PathVariable Long eventId, @AuthenticationPrincipal User currentUser, Model model) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        List<Review> reviews = reviewRepository.findByEventId(eventId);

        boolean canReview = false;
        if (currentUser != null) {
            canReview = canUserReview(currentUser, event);
        }

        model.addAttribute("event", event);
        model.addAttribute("reviews", reviews);
        model.addAttribute("canReview", canReview);
        return "event-reviews";
    }

    @PostMapping("/{eventId}/reviews")
    public String addReview(@PathVariable Long eventId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            @AuthenticationPrincipal User currentUser) {

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (currentUser != null && canUserReview(currentUser, event)) {
            Review review = new Review();
            review.setEvent(event);
            review.setUser(currentUser);
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedAt(LocalDateTime.now());
            reviewRepository.save(review);
        }

        return "redirect:/events/" + eventId + "/reviews";
    }

    private boolean canUserReview(User user, Event event) {
        boolean isPast = event.getDate().isBefore(LocalDateTime.now());
        boolean hasTicket = ticketRepository.existsByUserIdAndEventId(user.getId(), event.getId());
        boolean alreadyReviewed = reviewRepository.existsByUserIdAndEventId(user.getId(), event.getId());

        return isPast && hasTicket && !alreadyReviewed;
    }
}