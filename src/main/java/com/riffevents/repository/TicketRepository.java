package com.riffevents.repository;

import com.riffevents.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByUserId(Long userId);
    Optional<Ticket> findByQrCode(String qrCode);
    List<Ticket> findByStatus(String status);
    List<Ticket> findByEventIdAndStatus(Long eventId, String status);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
