package repositories.util;

@FunctionalInterface
public interface RowMapper<T> extends br.com.dende.softhouse.repositorry.RowMapper<T> {
    @Override
    T mapRow(String[] row);
}
