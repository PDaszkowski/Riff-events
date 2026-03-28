package com.riffevents.controller;

import com.riffevents.model.Event;
import com.riffevents.model.User;
import com.riffevents.service.EventService;
import com.riffevents.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;

    }

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal User organizer) {

        String title = (String) payload.get("title");
        String description = (String) payload.get("description");
        LocalDateTime date = LocalDateTime.parse((String) payload.get("date"));
        Long venueId = Long.valueOf(payload.get("venueId").toString());
        String imageUrl = (String) payload.get("imageUrl");

        @SuppressWarnings("unchecked")
        List<Integer> artistIdsInt = (List<Integer>) payload.get("artistIds");
        List<Long> artistIds = artistIdsInt.stream().map(Long::valueOf).toList();

        User currentOrganizer = (organizer != null) ? organizer : userService.findById(1L).get();

        Event newEvent = eventService.createEventWithLineup(
                title, description, date, venueId, currentOrganizer, imageUrl, artistIds
        );

        return ResponseEntity.ok(newEvent);
    }

}