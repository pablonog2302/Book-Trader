package com.example.booktrader.controller;

import com.example.booktrader.DTO.LoginRequest;
import com.example.booktrader.DTO.LoginResponse;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")

public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest){

        //Usuario usuarioBanco = usuarioRepository

        if (usuarioRepository.existsUsuarioByCpfAndSenha(loginRequest.getLogin(), loginRequest.getSenha()) ){

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMensagem("Bem vindo ao sistema");



            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
