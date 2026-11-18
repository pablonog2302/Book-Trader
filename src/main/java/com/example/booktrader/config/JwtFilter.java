package com.example.booktrader.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Value("${spring.secret}")
    private String secret;

    @Value("${spring.emissor}")
    private String emissor;




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String caminho = request.getRequestURI();

        if(caminho.equals("/login") ||
                // permite o cadastro publico sem liberar as demais rotas de usuarios
                (caminho.equals("/usuarios") && request.getMethod().equals("POST")) ||
                caminho.startsWith("/swagger-ui") ||
                caminho.startsWith("/webjars") ||
                caminho.startsWith("/swagger-resources") ||
                caminho.startsWith("/v3/api-docs")||
                request.getMethod().startsWith("OPTIONS")
        ){
            filterChain.doFilter(request,response);
            return;
        }

        String headerToken = request.getHeader("Authorization");

        if (headerToken == null || !headerToken.startsWith("Bearer ")) {
            responderNaoAutorizado(response);
            return;
        }

        try {
            String token = headerToken.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(emissor)
                    .build();
            var resultado = verifier.verify(token);
            // disponibiliza o cpf validado para os controllers identificarem o usuario da requisicao.
            request.setAttribute("usuarioCpf", resultado.getSubject());
        } catch (JWTVerificationException erro) {
            logger.warn("Falha na validação do token de acesso.", erro);
            responderNaoAutorizado(response);
            return;
        }

        filterChain.doFilter(request,response);




    }

    private void responderNaoAutorizado(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"mensagem\":\"Sua sessão expirou ou é inválida. Faça login novamente.\"}");
    }
}
