package com.financeHub.backend;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BackendApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Flyway flyway;

    @Test
    void conectaAoMySql84() {
        String version = jdbcTemplate.queryForObject(
                "SELECT VERSION()",
                String.class
        );

        assertThat(version).startsWith("8.4.");
    }

    @Test
    void executaMigrationAoIniciar() {
        String description = jdbcTemplate.queryForObject(
                "SELECT description FROM integration_probe WHERE id = 1",
                String.class
        );

        assertThat(description).isEqualTo("CRT");
        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    void preservaMigrationsAplicadasAoExecutarNovamente() {
        int appliedBefore = flyway.info().applied().length;

        flyway.validate();
        flyway.migrate();

        assertThat(flyway.info().applied()).hasSize(appliedBefore);

        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM integration_probe",
                Long.class
        );

        assertThat(total).isEqualTo(1L);
    }
}