package com.riffevents.controller;

import com.riffevents.model.Event;
import com.riffevents.service.ArtistService;
import com.riffevents.service.EventService;
import com.riffevents.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;


@Controller
public class HomeController {

    private final EventService eventService;
    private final VenueService venueService;
    private final ArtistService artistService;

    public HomeController(EventService eventService, VenueService venueService, ArtistService artistService) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.artistService = artistService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Event> upcomingEvents = eventService.getPublishedEvents()
                .stream()
                .filter(e -> e.getDate().isAfter(LocalDateTime.now()) &&
                        e.getDate().isBefore(LocalDateTime.now().plusMonths(3)))
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .limit(6)
                .toList();

        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("totalEvents", eventService.getFutureEvents());
        model.addAttribute("totalArtists", artistService.getAllArtists().size());
        model.addAttribute("totalVenues", venueService.getAllVenues().size());

        return "index";
    }

}