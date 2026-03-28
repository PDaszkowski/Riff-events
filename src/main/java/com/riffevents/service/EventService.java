package com.riffevents.service;

import com.riffevents.model.*;
import com.riffevents.model.enums.EventStatus;
import com.riffevents.repository.ArtistRepository;
import com.riffevents.repository.EventArtistRepository;
import com.riffevents.repository.EventRepository;
import com.riffevents.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventArtistRepository eventArtistRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;

    public EventService(EventRepository eventRepository, EventArtistRepository eventArtistRepository,  ArtistRepository artistRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.eventArtistRepository = eventArtistRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
    }

    public List<Event> getPublishedEvents() {
        return eventRepository.findByStatus(EventStatus.PUBLISHED);
    }

    public Event getPublishedEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event with id " + id + " not found"));

        if(event.getStatus() != EventStatus.PUBLISHED) {
            throw new RuntimeException("Event with id " + id + " not available");
        }

        return event;
    }

    @Transactional
    public Event createEventWithLineup(
            String title,
            String description,
            LocalDateTime date,
            Long venueId,
            User organizer,
            String imageUrl,
            List<Long> artistIds) {


        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("There is no venue with ID: " + venueId));

        Event event = new Event(
                title,
                description,
                date,
                venue,
                organizer,
                imageUrl,
                EventStatus.PUBLISHED
        );

        Event savedEvent = eventRepository.save(event);


        if (artistIds != null && !artistIds.isEmpty()) {
            for (int i = 0; i < artistIds.size(); i++) {
                Long currentId = artistIds.get(i);

                Artist artist = artistRepository.findById(currentId)
                        .orElseThrow(() -> new RuntimeException("There is no artist with ID: " + currentId));

                EventArtist eventArtist = new EventArtist(savedEvent, artist, i + 1);
                eventArtistRepository.save(eventArtist);
            }
        }

        return savedEvent;
    }

    public long getFutureEvents()
    {
        return eventRepository.countByDateAfter(LocalDateTime.now());
    }
}

