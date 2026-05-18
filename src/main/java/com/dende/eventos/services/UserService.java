package com.dende.eventos.services;

import com.dende.eventos.dto.UpdateUserRequest;
import com.dende.eventos.dto.UserRequest;
import com.dende.eventos.dto.UserResponse;
import com.dende.eventos.entities.User;
import com.dende.eventos.entities.UserRole;
import com.dende.eventos.exceptions.BusinessException;
import com.dende.eventos.exceptions.NotFoundException;
import com.dende.eventos.repositories.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponse createCommonUser(UserRequest request) {
        return createUser(request, UserRole.COMUM);
    }

    public UserResponse createOrganizer(UserRequest request) {
        return createUser(request, UserRole.ORGANIZADOR);
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(findEntityById(id));
    }

    public UserResponse updateProfile(Long id, UpdateUserRequest request) {
        validateName(request.name());
        validateEmail(request.email());
        User user = findEntityById(id);
        repository.findByEmail(request.email())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new BusinessException("Email ja cadastrado");
                });

        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        return UserResponse.from(repository.update(user));
    }

    public UserResponse changeStatus(Long id, boolean active) {
        User user = findEntityById(id);
        user.setActive(active);
        return UserResponse.from(repository.update(user));
    }

    public User findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));
    }

    private UserResponse createUser(UserRequest request, UserRole role) {
        validateName(request.name());
        validateBirthDate(request.birthDate());
        validateGender(request.gender());
        validateEmail(request.email());
        validatePassword(request.password());
        String email = request.email().trim().toLowerCase();
        if (repository.existsByEmail(email)) {
            throw new BusinessException("Email ja cadastrado");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setBirthDate(request.birthDate());
        user.setGender(request.gender().trim().toUpperCase());
        user.setEmail(email);
        user.setPassword(request.password());
        user.setRole(role);
        user.setActive(true);
        return UserResponse.from(repository.save(user));
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Nome e obrigatorio");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new BusinessException("Email invalido");
        }
    }

    private void validateBirthDate(java.time.LocalDate birthDate) {
        if (birthDate == null || birthDate.isAfter(java.time.LocalDate.now())) {
            throw new BusinessException("Data de nascimento invalida");
        }
    }

    private void validateGender(String gender) {
        if (gender == null || !List.of("M", "F", "O").contains(gender.trim().toUpperCase())) {
            throw new BusinessException("Sexo deve ser M, F ou O");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new BusinessException("Senha deve ter no minimo 6 caracteres");
        }
    }
}
