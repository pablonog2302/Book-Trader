package com.example.booktrader.utils;

public class OnlyNumber {
    public static String apenasNumeros(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        return valor.replaceAll("\\D", "");
    }
}