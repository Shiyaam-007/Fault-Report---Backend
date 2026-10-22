package com.sprint.backend.dto;

import com.sprint.backend.entity.Ticket;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Lightweight ticket summary used in the GET /api/tickets list.
 * Does NOT include image binary data.
 */
@Data
@Builder
public class TicketSummaryDto {

    private Long   id;
    private String title;
    private String description;
    private String status;
    private String reporterName;
    private Long   reporterId;
    private boolean hasPhoto;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static TicketSummaryDto from(Ticket t, boolean hasPhoto) {
        return TicketSummaryDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus().name())
                .reporterName(t.getReporter().getName())
                .reporterId(t.getReporter().getId())
                .hasPhoto(hasPhoto)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
