package com.riffevents.controller;

import com.riffevents.model.Event;
import com.riffevents.repository.EventRepository;
import com.riffevents.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {


    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public  AdminController(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;


    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<Event> allEvents = eventRepository.findAll();


        long totalEvents = allEvents.size();
        long totalUsers = userRepository.count();
        int totalTicketsSold = allEvents.stream()
                .mapToInt(Event::getSoldTickets)
                .sum();

        BigDecimal totalRevenue = allEvents.stream()
                .map(Event::getGeneratedRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalTicketsSold", totalTicketsSold);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("events", allEvents);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}