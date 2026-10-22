package com.sprint.backend.service;

import com.sprint.backend.dto.CreateTicketRequest;
import com.sprint.backend.dto.TicketSummaryDto;
import com.sprint.backend.dto.UpdateStatusRequest;
import com.sprint.backend.entity.Photo;
import com.sprint.backend.entity.Ticket;
import com.sprint.backend.entity.User;
import com.sprint.backend.repository.PhotoRepository;
import com.sprint.backend.repository.TicketRepository;
import com.sprint.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository   userRepository;
    private final PhotoRepository  photoRepository;

    // ------------------------------------------------------------------
    // Create ticket + optional photo
    // ------------------------------------------------------------------
    @Transactional
    public TicketSummaryDto createTicket(CreateTicketRequest req, MultipartFile image)
            throws IOException {

        User reporter = userRepository.findById(req.getReporterId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Reporter not found: " + req.getReporterId()));

        Ticket ticket = Ticket.builder()
                .reporter(reporter)
                .title(req.getTitle())
                .description(req.getDescription())
                .status(Ticket.Status.OPEN)
                .build();

        ticketRepository.save(ticket);

        boolean hasPhoto = false;
        if (image != null && !image.isEmpty()) {
            Photo photo = Photo.builder()
                    .ticket(ticket)
                    .imageData(image.getBytes())
                    .contentType(image.getContentType() != null
                            ? image.getContentType() : "image/jpeg")
                    .build();
            photoRepository.save(photo);
            hasPhoto = true;
        }

        return TicketSummaryDto.from(ticket, hasPhoto);
    }

    // ------------------------------------------------------------------
    // List all tickets (lightweight — no BYTEA)
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getAllTickets() {
        return ticketRepository.findAllWithReporter()
                .stream()
                .map(t -> TicketSummaryDto.from(t, photoRepository.existsByTicketId(t.getId())))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Get raw photo bytes for a ticket
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public Photo getPhotoForTicket(Long ticketId) {
        return photoRepository.findFirstByTicketId(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No photo found for ticket " + ticketId));
    }

    // ------------------------------------------------------------------
    // Update ticket status
    // ------------------------------------------------------------------
    @Transactional
    public TicketSummaryDto updateStatus(Long ticketId, UpdateStatusRequest req) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found: " + ticketId));

        Ticket.Status newStatus;
        try {
            newStatus = Ticket.Status.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status: " + req.getStatus());
        }

        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);

        return TicketSummaryDto.from(ticket,
                photoRepository.existsByTicketId(ticketId));
    }
}
