package com.example.booktrader.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfUtilsTest {

    @Test
    void aceitaCpfValidoComOnzeNumeros() {
        assertTrue(CpfUtils.isValidCpf("52998224725"));
    }

    @Test
    void rejeitaCpfComMenosDeOnzeNumeros() {
        assertFalse(CpfUtils.isValidCpf("5299822472"));
    }

    @Test
    void rejeitaCpfComMaisDeOnzeNumeros() {
        assertFalse(CpfUtils.isValidCpf("529982247251"));
    }

    @Test
    void rejeitaCpfComLetrasOuPontuacao() {
        assertFalse(CpfUtils.isValidCpf("529.982.247-25"));
        assertFalse(CpfUtils.isValidCpf("5299822472a"));
    }

    @Test
    void rejeitaDigitosVerificadoresInvalidos() {
        assertFalse(CpfUtils.isValidCpf("52998224724"));
    }
}
