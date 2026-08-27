package com.example.booktrader.controller;

import com.example.booktrader.DTO.LoginRequest;
import com.example.booktrader.DTO.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")

public class LoginController {

    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest){

        if (loginRequest.getLogin().equals("string")&&
                loginRequest.getSenha().equals("string")){
            LoginResponse loginResponse = new LoginResponse();

            loginResponse.setMensagem("Bem vindo ao sistema");



            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
