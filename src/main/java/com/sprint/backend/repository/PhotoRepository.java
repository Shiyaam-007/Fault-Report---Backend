package com.sprint.backend.repository;

import com.sprint.backend.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    /**
     * Fetch the first photo for a given ticket, including the BYTEA blob.
     * Only called by the dedicated photo endpoint, not the list view.
     */
    @Query("SELECT p FROM Photo p WHERE p.ticket.id = :ticketId ORDER BY p.uploadedAt ASC")
    Optional<Photo> findFirstByTicketId(Long ticketId);

    boolean existsByTicketId(Long ticketId);
}
