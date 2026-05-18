package com.dende.eventos.controllers;

public final class PathUtil {
    private PathUtil() {
    }

    public static Long idFromPath(String path, int position) {
        String[] parts = path.split("/");
        if (parts.length <= position || parts[position].isBlank()) {
            throw new IllegalArgumentException("Id nao informado");
        }
        return Long.parseLong(parts[position]);
    }
}
