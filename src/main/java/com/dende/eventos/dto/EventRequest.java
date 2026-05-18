package com.dende.eventos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventRequest(
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
        Boolean refundTicket,
        BigDecimal refundFee,
        Boolean active) {
}
