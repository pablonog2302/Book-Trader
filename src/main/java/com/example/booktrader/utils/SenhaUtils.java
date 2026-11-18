package com.example.booktrader.utils;

public final class SenhaUtils {

    private SenhaUtils() {
    }

    public static String mensagemDeErro(String senha) {
        if (senha == null || senha.isEmpty()) {
            return "Informe sua senha.";
        }
        if (!senha.matches("[0-9]+")) {
            return "A senha deve conter somente números.";
        }
        if (senha.length() > 8) {
            return "A senha deve ter no máximo 8 dígitos.";
        }
        return null;
    }

}
