package com.riffevents.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_artists")
public class EventArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false)
    private int position;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    public EventArtist() {}

    public EventArtist(Event event, Artist artist, int position) {
        this.event = event;
        this.artist = artist;
        this.position = position;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "EventArtist{" +
                "id=" + id +
                ", event=" + (event != null ? event.getTitle() : "null") +
                ", artist=" + (artist != null ? artist.getName() : "null") +
                ", position=" + position +
                ", createdAt=" + createdAt +
                '}';
    }

}
