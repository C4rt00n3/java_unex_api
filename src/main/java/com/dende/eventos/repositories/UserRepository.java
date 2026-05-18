package com.dende.eventos.repositories;

import br.com.dende.softhouse.annotations.Repository;
import br.com.dende.softhouse.repositorry.ResultSetMapper;
import com.dende.eventos.entities.User;
import com.dende.eventos.exceptions.RepositoryException;
import com.dende.eventos.mappers.UserRowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import repositories.util.ConnectionPool;
import repositories.util.CrudRepository;
import repositories.util.RowMapper;

@Repository
public class UserRepository implements CrudRepository<User, Long> {
    private final RowMapper<User> mapper = new UserRowMapper();
    private static final String USER_COLUMNS = "id, nome, data_nascimento, sexo, email, senha, tipo_usuario, ativo";

    @Override
    public <S extends User> S save(S user) {
        String sql = """
                INSERT INTO usuario (nome, data_nascimento, sexo, email, senha, tipo_usuario, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setDate(2, java.sql.Date.valueOf(user.getBirthDate()));
            statement.setString(3, user.getGender());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().name());
            statement.setBoolean(7, user.isActive());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return (S) findById(keys.getLong(1))
                            .orElseThrow(() -> new RepositoryException("Usuario criado nao encontrado", null));
                }
                throw new RepositoryException("Nenhum id gerado ao criar usuario", null);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao salvar usuario", exception);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT " + USER_COLUMNS + " FROM usuario WHERE id = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(ResultSetMapper.mapOne(resultSet, mapper));
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao buscar usuario por id", exception);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT " + USER_COLUMNS + " FROM usuario ORDER BY id";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return new ArrayList<>(ResultSetMapper.map(resultSet, mapper));
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao listar usuarios", exception);
        }
    }

    @Override
    public User update(User user) {
        String sql = """
                UPDATE usuario
                   SET nome = ?, data_nascimento = ?, sexo = ?, email = ?, senha = ?, tipo_usuario = ?, ativo = ?
                 WHERE id = ?
                """;
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setDate(2, java.sql.Date.valueOf(user.getBirthDate()));
            statement.setString(3, user.getGender());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().name());
            statement.setBoolean(7, user.isActive());
            statement.setLong(8, user.getId());
            statement.executeUpdate();
            return findById(user.getId())
                    .orElseThrow(() -> new RepositoryException("Usuario atualizado nao encontrado", null));
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao atualizar usuario", exception);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao remover usuario", exception);
        }
    }

    @Override
    public void delete(User user) {
        deleteById(user.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends User> users) {
        for (User user : users) {
            delete(user);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM usuario";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao remover todos os usuarios", exception);
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT " + USER_COLUMNS + " FROM usuario WHERE email = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(ResultSetMapper.mapOne(resultSet, mapper));
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao buscar usuario por email", exception);
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM usuario WHERE email = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao verificar email", exception);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao contar usuarios", exception);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> ids) {
        List<User> users = new ArrayList<>();
        for (Long id : ids) {
            findById(id).ifPresent(users::add);
        }
        return users;
    }

    @Override
    public <V> Optional<User> findByField(String field, V value) {
        if (!List.of("id", "nome", "data_nascimento", "sexo", "email", "tipo_usuario", "ativo").contains(field)) {
            throw new RepositoryException("Campo de usuario nao permitido: " + field, null);
        }
        String sql = "SELECT " + USER_COLUMNS + " FROM usuario WHERE " + field + " = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(ResultSetMapper.mapOne(resultSet, mapper));
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao buscar usuario por campo", exception);
        }
    }
}
