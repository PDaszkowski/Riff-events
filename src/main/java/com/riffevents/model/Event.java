package com.riffevents.model;

import com.riffevents.model.enums.EventStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private Integer soldTickets;

    @Column(nullable = false)
    private Integer totalTickets;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventArtist> eventArtists = new ArrayList<>();


    public Event() {

    }

    public Event(String title, String description, LocalDateTime date, Venue venue, User organizer, String imageUrl, EventStatus status)
    {
        this.title = title;
        this.description = description;
        this.date = date;
        this.venue = venue;
        this.organizer = organizer;
        this.imageUrl = imageUrl;
        this.status = status;
        this.soldTickets = 0;
        this.totalTickets = venue.getCapacity();
        this.ticketPrice = BigDecimal.valueOf(250);
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Venue getVenue()
    {
        return venue;
    }

    public void setVenue(Venue venue)
    {
        this.venue = venue;
    }

    public User getOrganizer()
    {
        return organizer;
    }

    public void setOrganizer(User organizer)
    {
        this.organizer = organizer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<EventArtist> getEventArtists() {return eventArtists;}

    public Integer getSoldTickets() {return soldTickets;}

    public void setSoldTickets(Integer soldTickets) {this.soldTickets = soldTickets;}

    public Integer getTotalTickets() {return totalTickets;}

    public void setTotalTickets(Integer totalTickets) {this.totalTickets = totalTickets;}

    public void setEventArtists(List<EventArtist> eventArtists) {this.eventArtists = eventArtists;}

    public BigDecimal getTicketPrice() {return ticketPrice;}

    public BigDecimal getGeneratedRevenue() {
        if (this.ticketPrice == null || this.soldTickets == null) {
            return BigDecimal.ZERO;
        }
        return this.ticketPrice.multiply(BigDecimal.valueOf(this.soldTickets.longValue()));
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", venue=" + (venue != null ? venue.getName() : "null") +
                ", organizer=" + (organizer != null ? organizer.getName() : "null") +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}
