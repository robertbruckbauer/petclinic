package esy.app;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class DatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleaner(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Truncates all entity tables in the correct foreign-key order.
     */
    @Transactional
    public void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM visit");
        jdbcTemplate.execute("DELETE FROM vet_skill");
        jdbcTemplate.execute("DELETE FROM vet_species");
        jdbcTemplate.execute("DELETE FROM vet");
        jdbcTemplate.execute("DELETE FROM pet");
        jdbcTemplate.execute("DELETE FROM owner");
        jdbcTemplate.execute("DELETE FROM enum");
        jdbcTemplate.execute("DELETE FROM ping");
    }
}
