package com.example.booktrader.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SenhaUtilsTest {

    @Test
    void aceitaSenhaNumericaComAteOitoDigitos() {
        assertNull(SenhaUtils.mensagemDeErro("12345678"));
    }

    @Test
    void informaQuandoSenhaUltrapassaOitoDigitos() {
        assertEquals(
                "A senha deve ter no máximo 8 dígitos.",
                SenhaUtils.mensagemDeErro("123456789"));
    }

    @Test
    void informaQuandoSenhaContemCaracteresNaoNumericos() {
        assertEquals(
                "A senha deve conter somente números.",
                SenhaUtils.mensagemDeErro("senha123"));
    }
}
