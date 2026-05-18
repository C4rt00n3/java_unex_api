package com.dende.eventos.repositories;

import br.com.dende.softhouse.annotations.Repository;
import br.com.dende.softhouse.repositorry.ResultSetMapper;
import com.dende.eventos.entities.Event;
import com.dende.eventos.exceptions.RepositoryException;
import com.dende.eventos.mappers.EventRowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import repositories.util.ConnectionPool;
import repositories.util.CrudRepository;
import repositories.util.RowMapper;

@Repository
public class EventRepository implements CrudRepository<Event, Long> {
    private final RowMapper<Event> mapper = new EventRowMapper();
    private static final String EVENT_COLUMNS = """
            id, organizador_id, evento_principal_id, nome, descricao, pagina_web,
            tipo_evento, modalidade, local_evento, data_inicio, data_fim,
            capacidade_maxima, preco_ingresso, estorna_ingresso, taxa_estorno, ativo, data_cadastro
            """;

    @Override
    public <S extends Event> S save(S event) {
        String sql = """
                INSERT INTO evento (
                    organizador_id, evento_principal_id, nome, descricao, pagina_web,
                    tipo_evento, modalidade, local_evento, data_inicio, data_fim,
                    capacidade_maxima, preco_ingresso, estorna_ingresso, taxa_estorno, ativo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(statement, event);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return (S) findById(keys.getLong(1))
                            .orElseThrow(() -> new RepositoryException("Evento criado nao encontrado", null));
                }
                throw new RepositoryException("Nenhum id gerado ao criar evento", null);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao salvar evento", exception);
        }
    }

    @Override
    public Optional<Event> findById(Long id) {
        String sql = "SELECT " + EVENT_COLUMNS + " FROM evento WHERE id = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(ResultSetMapper.mapOne(resultSet, mapper));
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao buscar evento por id", exception);
        }
    }

    @Override
    public List<Event> findAll() {
        String sql = "SELECT " + EVENT_COLUMNS + " FROM evento ORDER BY data_inicio";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return new ArrayList<>(ResultSetMapper.map(resultSet, mapper));
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao listar eventos", exception);
        }
    }

    @Override
    public Event update(Event event) {
        String sql = """
                UPDATE evento
                   SET organizador_id = ?, evento_principal_id = ?, nome = ?, descricao = ?,
                       pagina_web = ?, tipo_evento = ?, modalidade = ?, local_evento = ?,
                       data_inicio = ?, data_fim = ?, capacidade_maxima = ?, preco_ingresso = ?,
                       estorna_ingresso = ?, taxa_estorno = ?, ativo = ?
                 WHERE id = ?
                """;
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, event);
            statement.setLong(16, event.getId());
            statement.executeUpdate();
            return findById(event.getId())
                    .orElseThrow(() -> new RepositoryException("Evento atualizado nao encontrado", null));
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao atualizar evento", exception);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM evento WHERE id = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao remover evento", exception);
        }
    }

    @Override
    public void delete(Event event) {
        deleteById(event.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends Event> events) {
        for (Event event : events) {
            delete(event);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM evento";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao remover todos os eventos", exception);
        }
    }

    public List<Event> findByOrganizerId(Long organizerId) {
        String sql = "SELECT " + EVENT_COLUMNS + " FROM evento WHERE organizador_id = ? ORDER BY data_inicio";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, organizerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return new ArrayList<>(ResultSetMapper.map(resultSet, mapper));
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao listar eventos do organizador", exception);
        }
    }

    private void fillStatement(PreparedStatement statement, Event event) throws SQLException {
        statement.setLong(1, event.getOrganizerId());
        if (event.getParentEventId() == null) {
            statement.setNull(2, java.sql.Types.BIGINT);
        } else {
            statement.setLong(2, event.getParentEventId());
        }
        statement.setString(3, event.getTitle());
        statement.setString(4, event.getDescription());
        statement.setString(5, event.getWebPage());
        statement.setString(6, event.getEventType());
        statement.setString(7, event.getModality());
        statement.setString(8, event.getLocation());
        statement.setTimestamp(9, Timestamp.valueOf(event.getStartDate()));
        statement.setTimestamp(10, Timestamp.valueOf(event.getEndDate()));
        statement.setInt(11, event.getMaximumCapacity());
        statement.setBigDecimal(12, event.getTicketPrice());
        statement.setBoolean(13, event.isRefundTicket());
        statement.setBigDecimal(14, event.getRefundFee());
        statement.setBoolean(15, event.isActive());
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM evento";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao contar eventos", exception);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<Event> findAllById(Iterable<Long> ids) {
        List<Event> events = new ArrayList<>();
        for (Long id : ids) {
            findById(id).ifPresent(events::add);
        }
        return events;
    }

    @Override
    public <V> Optional<Event> findByField(String field, V value) {
        if (!List.of("id", "organizador_id", "evento_principal_id", "nome", "tipo_evento", "modalidade", "ativo")
                .contains(field)) {
            throw new RepositoryException("Campo de evento nao permitido: " + field, null);
        }
        String sql = "SELECT " + EVENT_COLUMNS + " FROM evento WHERE " + field + " = ?";
        try (Connection connection = ConnectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(ResultSetMapper.mapOne(resultSet, mapper));
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Erro ao buscar evento por campo", exception);
        }
    }
}
