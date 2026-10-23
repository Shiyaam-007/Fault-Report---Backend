package com.sprint.backend.controller;

import com.sprint.backend.dto.CreateTicketRequest;
import com.sprint.backend.dto.TicketSummaryDto;
import com.sprint.backend.dto.UpdateStatusRequest;
import com.sprint.backend.entity.Photo;
import com.sprint.backend.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * POST /api/tickets
     * Accepts multipart/form-data: fields (title, description, reporterId,
     * latitude, longitude) + optional "image" file part.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketSummaryDto> createTicket(
            @Valid @ModelAttribute CreateTicketRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        TicketSummaryDto dto = ticketService.createTicket(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * GET /api/tickets?reporterId={id}
     * Returns lightweight ticket summaries — optionally filtered by reporterId.
     */
    @GetMapping
    public ResponseEntity<List<TicketSummaryDto>> getTickets(@RequestParam(required = false) Long reporterId) {
        return ResponseEntity.ok(ticketService.getTickets(reporterId));
    }


    /**
     * GET /api/tickets/{id}/photo
     * Streams the raw BYTEA image back with the stored content-type,
     * allowing the frontend to use this URL directly in <img src="..."/>.
     */
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Photo photo = ticketService.getPhotoForTicket(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, photo.getContentType())
                .body(photo.getImageData());
    }

    /**
     * PATCH /api/tickets/{id}/status
     * Body: { "status": "IN_PROGRESS" }
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketSummaryDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(ticketService.updateStatus(id, request));
    }
}
