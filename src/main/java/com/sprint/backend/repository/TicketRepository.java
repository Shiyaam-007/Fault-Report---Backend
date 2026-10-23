package com.sprint.backend.repository;

import com.sprint.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Fetch all tickets with their reporter but without joining photos.
     * This keeps the list query lightweight (no BYTEA loaded).
     */
    @Query("SELECT t FROM Ticket t ORDER BY t.id ASC")
    List<Ticket> findAllWithReporter();
}
