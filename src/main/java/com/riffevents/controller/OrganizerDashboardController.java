package com.riffevents.controller;

import com.riffevents.model.Event;
import com.riffevents.model.User;
import com.riffevents.repository.EventRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@PreAuthorize("hasRole('ORGANIZER')")
public class OrganizerDashboardController {

    private final EventRepository eventRepository;

    public OrganizerDashboardController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/organizer/dashboard")
    public String showDashboard(@AuthenticationPrincipal User organizer, Model model) {

        List<Event> myEvents = eventRepository.findByOrganizerId(organizer.getId());

        int totalSold = myEvents.stream().mapToInt(Event::getSoldTickets).sum();

        BigDecimal totalRevenue = myEvents.stream()
                .map(Event::getGeneratedRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double averageAttendance = myEvents.isEmpty() ? 0 :
                (double) totalSold / myEvents.stream().mapToInt(Event::getTotalTickets).sum() * 100;

        model.addAttribute("events", myEvents);
        model.addAttribute("totalSold", totalSold);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("averageAttendance", Math.round(averageAttendance));
        model.addAttribute("organizerName", organizer.getName());

        return "organizer/dashboard";
    }
}