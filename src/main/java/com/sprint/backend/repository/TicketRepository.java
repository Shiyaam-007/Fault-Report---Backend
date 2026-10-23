package com.sprint.backend.repository;

import com.sprint.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Fetch all tickets with their reporter but without joining photos.
     * This keeps the list query lightweight (no BYTEA loaded).
     */
    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.reporter ORDER BY t.id DESC")
    List<Ticket> findAllWithReporter();

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.reporter WHERE t.reporter.id = :reporterId ORDER BY t.id DESC")
    List<Ticket> findByReporterId(@Param("reporterId") Long reporterId);
}

