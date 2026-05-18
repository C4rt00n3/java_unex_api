package com.dende.eventos.controllers;

import com.dende.eventos.dto.UpdateUserRequest;
import com.dende.eventos.dto.UserRequest;
import com.dende.eventos.dto.UserStatusRequest;
import com.dende.eventos.services.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserController implements HttpHandler {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if ("POST".equals(method) && "/users".equals(path)) {
                createCommonUser(exchange);
                return;
            }
            if ("POST".equals(method) && "/organizers".equals(path)) {
                createOrganizer(exchange);
                return;
            }
            if ("GET".equals(method) && path.matches("/users/\\d+")) {
                findUser(exchange);
                return;
            }
            if ("PUT".equals(method) && path.matches("/users/\\d+")) {
                updateProfile(exchange);
                return;
            }
            if ("PATCH".equals(method) && path.matches("/users/\\d+/status")) {
                changeStatus(exchange);
                return;
            }
            HttpResponseWriter.json(exchange, 404, JsonUtil.message("Rota nao encontrada"));
        } catch (Exception exception) {
            HttpResponseWriter.error(exchange, exception);
        }
    }

    private void createCommonUser(HttpExchange exchange) throws IOException {
        UserRequest request = JsonUtil.toUserRequest(body(exchange));
        HttpResponseWriter.json(exchange, 201, JsonUtil.userToJson(userService.createCommonUser(request)));
    }

    private void createOrganizer(HttpExchange exchange) throws IOException {
        UserRequest request = JsonUtil.toUserRequest(body(exchange));
        HttpResponseWriter.json(exchange, 201, JsonUtil.userToJson(userService.createOrganizer(request)));
    }

    private void findUser(HttpExchange exchange) throws IOException {
        Long id = PathUtil.idFromPath(exchange.getRequestURI().getPath(), 2);
        HttpResponseWriter.json(exchange, 200, JsonUtil.userToJson(userService.findById(id)));
    }

    private void updateProfile(HttpExchange exchange) throws IOException {
        Long id = PathUtil.idFromPath(exchange.getRequestURI().getPath(), 2);
        UpdateUserRequest request = JsonUtil.toUpdateUserRequest(body(exchange));
        HttpResponseWriter.json(exchange, 200, JsonUtil.userToJson(userService.updateProfile(id, request)));
    }

    private void changeStatus(HttpExchange exchange) throws IOException {
        Long id = PathUtil.idFromPath(exchange.getRequestURI().getPath(), 2);
        UserStatusRequest request = JsonUtil.toUserStatusRequest(body(exchange));
        HttpResponseWriter.json(exchange, 200, JsonUtil.userToJson(userService.changeStatus(id, request.active())));
    }

    private String body(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
