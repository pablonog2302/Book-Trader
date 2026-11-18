package com.example.booktrader.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.booktrader.DTO.LoginRequest;
import com.example.booktrader.DTO.LoginResponse;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.UsuarioRepository;
import com.example.booktrader.utils.CpfUtils;
import com.example.booktrader.utils.SenhaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        if (loginRequest == null || loginRequest.getLogin() == null
                || loginRequest.getLogin().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o CPF.");
        }

        String erroCpf = CpfUtils.mensagemDeErro(loginRequest.getLogin());
        if (erroCpf != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroCpf);
        }

        String erroSenha = SenhaUtils.mensagemDeErro(loginRequest.getSenha());
        if (erroSenha != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroSenha);
        }

        String cpf = loginRequest.getLogin();
        if (usuarioRepository.existsUsuarioByCpfAndSenha(cpf, loginRequest.getSenha()) ){

            Usuario usuarioBanco = usuarioRepository.getUsuarioByCpf(cpf).orElse(null);

            //JWT Metodo (AULA Q NAO VIM)
            // o cpf no subject identifica o usuario nas requisicoes autenticadas; a expiracao usa o tempo configurado.
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer(emissor)
                    .withSubject(cpf)
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);


            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMensagem("Bem vindo ao sistema");
            loginResponse.setToken(token);
            loginResponse.setUsuarioId(usuarioBanco.getId());
            loginResponse.setUsuarioNome(usuarioBanco.getNome());


            return ResponseEntity.ok(loginResponse);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "CPF ou senha inválidos.");
    }

    private Instant gerarDataExpiracao(){

        LocalDateTime dataAtual = LocalDateTime.now();

        LocalDateTime dataFuturoSoma = dataAtual.plusMinutes(tempo);

        return dataFuturoSoma.toInstant(ZoneOffset.of("-03:00"));


    }

}
