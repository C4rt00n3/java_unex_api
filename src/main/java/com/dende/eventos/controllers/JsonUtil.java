package com.dende.eventos.controllers;

import com.dende.eventos.dto.EventRequest;
import com.dende.eventos.dto.EventResponse;
import com.dende.eventos.dto.UpdateUserRequest;
import com.dende.eventos.dto.UserRequest;
import com.dende.eventos.dto.UserResponse;
import com.dende.eventos.dto.UserStatusRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static UserRequest toUserRequest(String body) {
        Map<String, String> json = parse(body);
        return new UserRequest(
                json.get("name"),
                parseDate(json.get("birthDate")),
                json.get("gender"),
                json.get("email"),
                json.get("password"));
    }

    public static UpdateUserRequest toUpdateUserRequest(String body) {
        Map<String, String> json = parse(body);
        return new UpdateUserRequest(json.get("name"), json.get("email"));
    }

    public static UserStatusRequest toUserStatusRequest(String body) {
        Map<String, String> json = parse(body);
        return new UserStatusRequest(Boolean.parseBoolean(json.getOrDefault("active", "false")));
    }

    public static EventRequest toEventRequest(String body) {
        Map<String, String> json = parse(body);
        return new EventRequest(
                parseLong(json.get("organizerId")),
                parseLong(json.get("parentEventId")),
                json.get("title"),
                json.get("description"),
                json.get("webPage"),
                json.get("eventType"),
                json.get("modality"),
                json.get("location"),
                parseDateTime(first(json.get("startDate"), json.get("eventDate"))),
                parseDateTime(json.get("endDate")),
                parseInteger(json.get("maximumCapacity")),
                parseBigDecimal(json.get("ticketPrice")),
                parseBoolean(json.get("refundTicket")),
                parseBigDecimal(json.get("refundFee")),
                parseBoolean(json.get("active")));
    }

    public static String userToJson(UserResponse user) {
        return "{"
                + field("id", user.id()) + ","
                + field("name", user.name()) + ","
                + field("birthDate", user.birthDate()) + ","
                + field("gender", user.gender()) + ","
                + field("email", user.email()) + ","
                + field("role", user.role().name()) + ","
                + field("active", user.active()) + ","
                + field("createdAt", user.createdAt()) + ","
                + field("updatedAt", user.updatedAt())
                + "}";
    }

    public static String eventToJson(EventResponse event) {
        return "{"
                + field("id", event.id()) + ","
                + field("organizerId", event.organizerId()) + ","
                + field("parentEventId", event.parentEventId()) + ","
                + field("title", event.title()) + ","
                + field("description", event.description()) + ","
                + field("webPage", event.webPage()) + ","
                + field("eventType", event.eventType()) + ","
                + field("modality", event.modality()) + ","
                + field("location", event.location()) + ","
                + field("startDate", event.startDate()) + ","
                + field("endDate", event.endDate()) + ","
                + field("maximumCapacity", event.maximumCapacity()) + ","
                + field("ticketPrice", event.ticketPrice()) + ","
                + field("refundTicket", event.refundTicket()) + ","
                + field("refundFee", event.refundFee()) + ","
                + field("active", event.active()) + ","
                + field("createdAt", event.createdAt()) + ","
                + field("updatedAt", null)
                + "}";
    }

    public static String eventsToJson(List<EventResponse> events) {
        return events.stream()
                .map(JsonUtil::eventToJson)
                .collect(Collectors.joining(",", "[", "]"));
    }

    public static String message(String message) {
        return "{" + field("message", message) + "}";
    }

    private static Map<String, String> parse(String body) {
        String cleanBody = body == null ? "" : body.trim();
        if (cleanBody.startsWith("{")) {
            cleanBody = cleanBody.substring(1);
        }
        if (cleanBody.endsWith("}")) {
            cleanBody = cleanBody.substring(0, cleanBody.length() - 1);
        }
        return List.of(cleanBody.split(","))
                .stream()
                .map(String::trim)
                .filter(entry -> !entry.isBlank() && entry.contains(":"))
                .map(entry -> entry.split(":", 2))
                .collect(Collectors.toMap(
                        parts -> unquote(parts[0].trim()),
                        parts -> unquote(parts[1].trim()),
                        (current, ignored) -> current));
    }

    private static Long parseLong(String value) {
        return value == null || value.isBlank() ? null : Long.parseLong(value);
    }

    private static Integer parseInteger(String value) {
        return value == null || value.isBlank() ? null : Integer.parseInt(value);
    }

    private static BigDecimal parseBigDecimal(String value) {
        return value == null || value.isBlank() ? null : new BigDecimal(value);
    }

    private static Boolean parseBoolean(String value) {
        return value == null || value.isBlank() ? null : Boolean.parseBoolean(value);
    }

    private static LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private static LocalDateTime parseDateTime(String value) {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
    }

    private static String first(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }

    private static String field(String name, Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return quote(name) + ":" + value;
        }
        return quote(name) + ":" + quote(value == null ? null : value.toString());
    }

    private static String quote(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String unquote(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            return trimmed.substring(1, trimmed.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        return trimmed;
    }
}
