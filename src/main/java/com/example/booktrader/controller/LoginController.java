package com.example.booktrader.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.booktrader.DTO.LoginRequest;
import com.example.booktrader.DTO.LoginResponse;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/login")

public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${spring.secret}")
    private String secret;

    @Value("${spring.emissor}")
    private String emissor;

    @Value("${spring.tempo}")
    private Long tempo;

    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest){




        //Usuario usuarioBanco = usuarioRepository

        if (usuarioRepository.existsUsuarioByCpfAndSenha(loginRequest.getLogin(), loginRequest.getSenha()) ){
            //JWT Metodo (AULA Q NAO VIM)
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer(emissor)
                    .withSubject(loginRequest.getLogin())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);


            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMensagem("Bem vindo ao sistema");
            loginResponse.setToken(token);


            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private Instant gerarDataExpiracao(){

        LocalDateTime dataAtual = LocalDateTime.now();

        LocalDateTime dataFuturoSoma = dataAtual.plusMinutes(tempo);

        return dataFuturoSoma.toInstant(ZoneOffset.of("-03:00"));


    }

}
