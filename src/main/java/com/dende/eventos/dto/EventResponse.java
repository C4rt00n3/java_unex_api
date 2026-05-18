package com.dende.eventos.dto;

import com.dende.eventos.entities.Event;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        Long organizerId,
        Long parentEventId,
        String title,
        String description,
        String webPage,
        String eventType,
        String modality,
        String location,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer maximumCapacity,
        BigDecimal ticketPrice,
        boolean refundTicket,
        BigDecimal refundFee,
        boolean active,
        LocalDateTime createdAt) {
    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getOrganizerId(),
                event.getParentEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getWebPage(),
                event.getEventType(),
                event.getModality(),
                event.getLocation(),
                event.getStartDate(),
                event.getEndDate(),
                event.getMaximumCapacity(),
                event.getTicketPrice(),
                event.isRefundTicket(),
                event.getRefundFee(),
                event.isActive(),
                event.getCreatedAt());
    }
}
