package com.example.booktrader.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiExceptionHandlerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ControladorDeTeste())
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void retornaMensagemDeNegocioNoFormatoPadronizado() throws Exception {
        mockMvc.perform(get("/erro-de-negocio"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem")
                        .value("Já existe uma conta cadastrada com este CPF."));
    }

    @Test
    void naoExpõeDetalhesDeErroInterno() throws Exception {
        mockMvc.perform(get("/erro-interno"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem")
                        .value("Não foi possível concluir a operação. Tente novamente mais tarde."));
    }

    @RestController
    static class ControladorDeTeste {

        @GetMapping("/erro-de-negocio")
        void erroDeNegocio() {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma conta cadastrada com este CPF.");
        }

        @GetMapping("/erro-interno")
        void erroInterno() {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha SQL: senha secreta");
        }
    }
}
