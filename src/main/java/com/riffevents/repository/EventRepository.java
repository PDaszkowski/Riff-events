package com.riffevents.repository;

import com.riffevents.model.Event;
import com.riffevents.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findByVenueId(Long venueId);

    @Query("SELECT DISTINCT e FROM Event e " +
            "LEFT JOIN e.eventArtists ea " +
            "LEFT JOIN ea.artist a " +
            "WHERE (:search IS NULL OR LOWER(CAST(e.title AS string)) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) AND " +
            "(:cities IS NULL OR e.venue.city IN :cities) AND " +
            "(:statuses IS NULL OR e.status IN :statuses) AND " +
            "(:artistIds IS NULL OR a.id IN :artistIds)")
    List<Event> findByFilters(
            @Param("search") String search,
            @Param("cities") List<String> cities,
            @Param("statuses") List<EventStatus> statuses,
            @Param("artistIds") List<Long> artistIds
    );

    long countByDateAfter(LocalDateTime date);



}
