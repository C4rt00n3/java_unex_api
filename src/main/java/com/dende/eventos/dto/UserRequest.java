package com.dende.eventos.dto;

import java.time.LocalDate;

public record UserRequest(String name, LocalDate birthDate, String gender, String email, String password) {
}
