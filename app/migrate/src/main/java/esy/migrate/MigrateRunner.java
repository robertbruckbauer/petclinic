package esy.migrate;

import java.sql.DriverManager;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public final class MigrateRunner {

    private MigrateRunner() {
    }

    static void main(final String[] args) {
        try {
            final var schema = requireEnv("DATABASE_SCHEMA");
            final var jdbcUrl = requireEnv("SPRING_DATASOURCE_URL");
            final var username = requireEnv("SPRING_DATASOURCE_USERNAME");
            final var password = requireEnv("SPRING_DATASOURCE_PASSWORD");
            final var changeLog = requireEnv("SPRING_LIQUIBASE_CHANGE_LOG");
            final var accessor = new ClassLoaderResourceAccessor(MigrateRunner.class.getClassLoader());
            try (final var connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                try (final var statement = connection.createStatement()) {
                    statement.execute(String.format("CREATE SCHEMA IF NOT EXISTS \"%s\"", schema));
                }
                final var database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
                try (final var liquibase = new Liquibase(changeLog, accessor, database)) {
                    liquibase.update(new Contexts(), new LabelExpression());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String requireEnv(final String name) {
        final var value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing environment variable " + name + '.');
        }
        return value;
    }
}
