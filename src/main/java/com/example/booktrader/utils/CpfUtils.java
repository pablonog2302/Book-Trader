package com.example.booktrader.utils;

public class CpfUtils {

    public static boolean possuiFormatoNumericoValido(String cpf) {
        return cpf != null && cpf.matches("[0-9]{11}");
    }

    public static String mensagemDeErro(String cpf) {
        if (!possuiFormatoNumericoValido(cpf)) {
            return "O CPF deve conter exatamente 11 números, sem letras ou símbolos.";
        }
        if (!isValidCpf(cpf)) {
            return "O CPF informado é inválido.";
        }
        return null;
    }

    /**
     * Valida um CPF composto por exatamente 11 algarismos.
     *
     * @param cpf String contendo o CPF
     * @return true se o CPF for válido, false caso contrário
     */
    public static boolean isValidCpf(String cpf) {
        if (!possuiFormatoNumericoValido(cpf)) {
            return false;
        }

        String numeros = cpf;

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
