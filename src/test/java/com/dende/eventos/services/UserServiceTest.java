package com.dende.eventos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dende.eventos.dto.UserRequest;
import com.dende.eventos.dto.UserResponse;
import com.dende.eventos.entities.User;
import com.dende.eventos.entities.UserRole;
import com.dende.eventos.exceptions.BusinessException;
import com.dende.eventos.repositories.UserRepository;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserServiceTest {
    @Test
    void shouldCreateCommonUser() {
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserService(repository);

        UserResponse response = service.createCommonUser(
                new UserRequest("Ana Souza", LocalDate.of(2000, 1, 10), "F", "ANA@EMAIL.COM", "123456"));

        assertEquals(1L, response.id());
        assertEquals("Ana Souza", response.name());
        assertEquals("ana@email.com", response.email());
        assertEquals(UserRole.COMUM, response.role());
        assertTrue(response.active());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserService(repository);

        service.createCommonUser(new UserRequest("Ana Souza", LocalDate.of(2000, 1, 10), "F", "ana@email.com", "123456"));

        assertThrows(BusinessException.class, () ->
                service.createCommonUser(new UserRequest("Outra Ana", LocalDate.of(1999, 5, 20), "F", "ana@email.com", "123456")));
    }

    private static class FakeUserRepository extends UserRepository {
        private final Map<Long, User> users = new HashMap<>();
        private long nextId = 1L;

        @Override
        public User save(User user) {
            user.setId(nextId++);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            users.put(user.getId(), user);
            return user;
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public List<User> findAll() {
            return List.copyOf(users.values());
        }

        @Override
        public User update(User user) {
            user.setUpdatedAt(LocalDateTime.now());
            users.put(user.getId(), user);
            return user;
        }

        @Override
        public void deleteById(Long id) {
            users.remove(id);
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return users.values()
                    .stream()
                    .filter(user -> user.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }
    }
}
