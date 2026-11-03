package com.example.ozora.senda.statistics;

public final class EnvironmentalAssessment {
    public static String aviso(int ppb, int threshold) {
        if (ppb < threshold) return "";
        return "Aviso orientativo · La última lectura alcanza el umbral configurado de " + threshold + " ppb.";
    }
}
