package com.dende.eventos.services;

import com.dende.eventos.dto.EventRequest;
import com.dende.eventos.dto.EventResponse;
import com.dende.eventos.entities.Event;
import com.dende.eventos.entities.User;
import com.dende.eventos.entities.UserRole;
import com.dende.eventos.exceptions.BusinessException;
import com.dende.eventos.exceptions.NotFoundException;
import com.dende.eventos.repositories.EventRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;

    public EventService(EventRepository eventRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    public EventResponse create(EventRequest request) {
        validate(request);
        ensureOrganizer(request.organizerId());
        Event event = buildEvent(request);
        return EventResponse.from(eventRepository.save(event));
    }

    public EventResponse findById(Long id) {
        return EventResponse.from(findEntityById(id));
    }

    public EventResponse update(Long id, EventRequest request) {
        validate(request);
        ensureOrganizer(request.organizerId());
        Event event = findEntityById(id);
        Event updated = buildEvent(request);
        updated.setId(event.getId());
        return EventResponse.from(eventRepository.update(updated));
    }

    public void delete(Long id) {
        findEntityById(id);
        eventRepository.deleteById(id);
    }

    public List<EventResponse> findByOrganizerId(Long organizerId) {
        ensureOrganizer(organizerId);
        return eventRepository.findByOrganizerId(organizerId)
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    private Event findEntityById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento nao encontrado"));
    }

    private void ensureOrganizer(Long organizerId) {
        if (organizerId == null) {
            throw new BusinessException("Organizador e obrigatorio");
        }
        User organizer = userService.findEntityById(organizerId);
        if (organizer.getRole() != UserRole.ORGANIZADOR) {
            throw new BusinessException("Usuario informado nao e organizador");
        }
        if (!organizer.isActive()) {
            throw new BusinessException("Organizador inativo");
        }
    }

    private void validate(EventRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new BusinessException("Titulo e obrigatorio");
        }
        if (request.description() == null || request.description().isBlank()) {
            throw new BusinessException("Descricao e obrigatoria");
        }
        if (request.location() == null || request.location().isBlank()) {
            throw new BusinessException("Local e obrigatorio");
        }
        if (request.startDate() == null || request.startDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Data de inicio do evento deve ser futura");
        }
        if (request.endDate() == null || !request.endDate().isAfter(request.startDate())) {
            throw new BusinessException("Data de fim deve ser maior que a data de inicio");
        }
        if (request.maximumCapacity() == null || request.maximumCapacity() <= 0) {
            throw new BusinessException("Capacidade maxima deve ser maior que zero");
        }
    }

    private Event buildEvent(EventRequest request) {
        Event event = new Event();
        event.setOrganizerId(request.organizerId());
        event.setParentEventId(request.parentEventId());
        event.setTitle(request.title().trim());
        event.setDescription(request.description() == null ? null : request.description().trim());
        event.setWebPage(request.webPage());
        event.setEventType(defaultString(request.eventType(), "SHOW"));
        event.setModality(defaultString(request.modality(), "PRESENCIAL"));
        event.setLocation(request.location().trim());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        event.setMaximumCapacity(request.maximumCapacity());
        event.setTicketPrice(request.ticketPrice() == null ? BigDecimal.ZERO : request.ticketPrice());
        event.setRefundTicket(Boolean.TRUE.equals(request.refundTicket()));
        event.setRefundFee(request.refundFee() == null ? BigDecimal.ZERO : request.refundFee());
        event.setActive(request.active() == null || request.active());
        return event;
    }

    private String defaultString(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim().toUpperCase();
    }
}
