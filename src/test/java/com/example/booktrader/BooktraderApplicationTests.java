package com.example.booktrader;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BooktraderApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void cpfPossuiRestricaoDeUnicidadeNoBanco() {
        Integer indices = jdbcTemplate.queryForObject(
                "select count(*) from pg_indexes "
                        + "where schemaname = 'public' "
                        + "and tablename = 'usuario' "
                        + "and indexname = 'uk_usuario_cpf'",
                Integer.class);

        assertNotNull(indices);
        assertTrue(indices > 0);
    }

}
