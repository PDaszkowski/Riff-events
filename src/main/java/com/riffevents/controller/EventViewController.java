package com.riffevents.controller;

import com.riffevents.model.Artist;
import com.riffevents.model.Event;
import com.riffevents.model.Favorite;
import com.riffevents.model.User;
import com.riffevents.model.enums.EventStatus;
import com.riffevents.repository.EventRepository;
import com.riffevents.repository.FavoriteRepository;
import com.riffevents.repository.VenueRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
public class EventViewController {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final FavoriteRepository favoriteRepository;

    public EventViewController(EventRepository eventRepository, VenueRepository venueRepository, FavoriteRepository favoriteRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @GetMapping("/events")
    public String listEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> city,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<Long> artistIds,
            @RequestParam(defaultValue = "false") boolean showPast,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        List<Artist> myFavoriteArtists = List.of();
        if (currentUser != null) {
            myFavoriteArtists = favoriteRepository.findByUserId(currentUser.getId())
                    .stream()
                    .map(Favorite::getArtist)
                    .toList();
        }

        List<EventStatus> eventStatuses = null;
        if (status != null && !status.isEmpty()) {
            eventStatuses = status.stream()
                    .map(s -> EventStatus.valueOf(s.toUpperCase()))
                    .toList();
        }

        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<String> cleanCities = (city != null && !city.isEmpty()) ? city : null;
        List<Long> cleanArtistIds = (artistIds != null && !artistIds.isEmpty()) ? artistIds : null;

        List<Event> filteredEvents = eventRepository.findByFilters(
                cleanSearch,
                cleanCities,
                eventStatuses,
                cleanArtistIds
        );

        LocalDateTime now = LocalDateTime.now();
        List<Event> processedEvents = filteredEvents.stream()
                .filter(e -> showPast || e.getDate().isAfter(now))
                .sorted(Comparator.comparing(Event::getDate))
                .toList();

        Long featuredEventId = 2L;
        Event featuredEvent = eventRepository.findById(featuredEventId).orElse(null);
        if (featuredEvent == null && !processedEvents.isEmpty()) {
            featuredEvent = processedEvents.get(0);
        }

        model.addAttribute("events", processedEvents);
        model.addAttribute("venues", venueRepository.findAll());
        model.addAttribute("myFavorites", myFavoriteArtists);
        model.addAttribute("featuredEvent", featuredEvent);
        model.addAttribute("now", now);
        model.addAttribute("showPast", showPast);

        model.addAttribute("selectedSearch", search);
        model.addAttribute("selectedCities", city != null ? city : List.of());
        model.addAttribute("selectedStatuses", status != null ? status : List.of());
        model.addAttribute("selectedArtists", artistIds != null ? artistIds : List.of());

        return "event-list";
    }

    @GetMapping("/events/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String showCreateForm() {
        return "create-event";
    }
}