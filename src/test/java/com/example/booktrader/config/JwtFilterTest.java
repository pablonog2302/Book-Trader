package com.example.booktrader.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtFilterTest {

    private JwtFilter jwtFilter;

    @BeforeEach
    void prepararFiltro() {
        jwtFilter = new JwtFilter();
        ReflectionTestUtils.setField(jwtFilter, "secret", "segredo-de-teste");
        ReflectionTestUtils.setField(jwtFilter, "emissor", "emissor-de-teste");
    }

    @Test
    void disponibilizaCpfDoTokenValidadoNaRequisicao() throws Exception {
        String token = JWT.create()
                .withIssuer("emissor-de-teste")
                .withSubject("12345678900")
                .sign(Algorithm.HMAC256("segredo-de-teste"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/livro");
        request.addHeader("Authorization", "Bearer " + token);

        jwtFilter.doFilterInternal(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain());

        assertEquals("12345678900", request.getAttribute("usuarioCpf"));
    }

    @Test
    void rejeitaTokenInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/livro");
        request.addHeader("Authorization", "Bearer token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
    }
}
