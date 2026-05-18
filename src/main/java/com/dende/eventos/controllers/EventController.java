package com.dende.eventos.controllers;

import com.dende.eventos.dto.EventRequest;
import com.dende.eventos.services.EventService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EventController implements HttpHandler {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if ("POST".equals(method) && "/events".equals(path)) {
                create(exchange);
                return;
            }
            if ("GET".equals(method) && path.matches("/events/\\d+")) {
                findById(exchange);
                return;
            }
            if ("PUT".equals(method) && path.matches("/events/\\d+")) {
                update(exchange);
                return;
            }
            if ("DELETE".equals(method) && path.matches("/events/\\d+")) {
                delete(exchange);
                return;
            }
            if ("GET".equals(method) && path.matches("/organizers/\\d+/events")) {
                findByOrganizer(exchange);
                return;
            }
            HttpResponseWriter.json(exchange, 404, JsonUtil.message("Rota nao encontrada"));
        } catch (Exception exception) {
            HttpResponseWriter.error(exchange, exception);
        }
    }

    private void create(HttpExchange exchange) throws IOException {
        EventRequest request = JsonUtil.toEventRequest(body(exchange));
        HttpResponseWriter.json(exchange, 201, JsonUtil.eventToJson(eventService.create(request)));
    }

    private void findById(HttpExchange exchange) throws IOException {
        Long id = PathUtil.idFromPath(exchange.getRequestURI().getPath(), 2);
        HttpResponseWriter.json(exchange, 200, JsonUtil.eventToJson(eventService.findById(id)));
    }

    private void update(HttpExchange exchange) throws IOException {
        Long id = PathUtil.idFromPath(exchange.getRequestURI().getPath(), 2);
        EventRequest request = JsonUtil.toEventRequest(body(exchange));
        HttpResponseWriter.json(exchange, 200, JsonUtil.eventToJson(eventService.update(id, request)));
    }

    private void delete(HttpExchange exchange) throws IOException {
        Long id = PathUtil.idFromPath(exchange.getRequestURI().getPath(), 2);
        eventService.delete(id);
        HttpResponseWriter.noContent(exchange);
    }

    private void findByOrganizer(HttpExchange exchange) throws IOException {
        Long organizerId = PathUtil.idFromPath(exchange.getRequestURI().getPath(), 2);
        HttpResponseWriter.json(exchange, 200, JsonUtil.eventsToJson(eventService.findByOrganizerId(organizerId)));
    }

    private String body(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
