package com.example.booktrader.utils;

public class CpfUtils {

    /**
     * Valida um número de CPF (com ou sem formatação).
     *
     * @param cpf String contendo o CPF
     * @return true se o CPF for válido, false caso contrário
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        // Remove tudo o que não for dígito
        String numeros = OnlyNumber.apenasNumeros(cpf);

        // CPF deve ter exatamente 11 dígitos
        if (numeros.length() != 11) {
            return false;
        }

        // Descarta CPFs com todos os dígitos iguais (ex: 000.000.000-00)
        if (numeros.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Cálculo do 1º dígito verificador
            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += Character.getNumericValue(numeros.charAt(i)) * (10 - i);
            }
            int digito1 = 11 - (soma1 % 11);
            if (digito1 > 9) {
                digito1 = 0;
            }

            // Valida o 1º dígito
            if (digito1 != Character.getNumericValue(numeros.charAt(9))) {
                return false;
            }

            // Cálculo do 2º dígito verificador
            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += Character.getNumericValue(numeros.charAt(i)) * (11 - i);
            }
            int digito2 = 11 - (soma2 % 11);
            if (digito2 > 9) {
                digito2 = 0;
            }

            // Valida o 2º dígito
            return digito2 == Character.getNumericValue(numeros.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }
}
