package com.dende.eventos.mappers;

import com.dende.eventos.entities.User;
import com.dende.eventos.entities.UserRole;
import java.time.LocalDate;
import repositories.util.RowMapper;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(String[] row) {
        return new User(
                Long.valueOf(row[0]),
                row[1],
                LocalDate.parse(row[2]),
                row[3],
                row[4],
                row[5],
                UserRole.valueOf(row[6]),
                "1".equals(row[7]) || "true".equalsIgnoreCase(row[7]),
                null,
                null);
    }
}
