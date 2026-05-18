package com.dende.eventos.controllers;

import com.dende.eventos.exceptions.BusinessException;
import com.dende.eventos.exceptions.NotFoundException;
import com.dende.eventos.exceptions.RepositoryException;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class HttpResponseWriter {
    private HttpResponseWriter() {
    }

    public static void json(HttpExchange exchange, int status, String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    public static void noContent(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    public static void error(HttpExchange exchange, Exception exception) throws IOException {
        if (exception instanceof NotFoundException) {
            json(exchange, 404, JsonUtil.message(exception.getMessage()));
            return;
        }
        if (exception instanceof BusinessException) {
            json(exchange, 400, JsonUtil.message(exception.getMessage()));
            return;
        }
        if (exception instanceof RepositoryException) {
            json(exchange, 500, JsonUtil.message("Erro de persistencia"));
            return;
        }
        json(exchange, 500, JsonUtil.message("Erro interno"));
    }
}
