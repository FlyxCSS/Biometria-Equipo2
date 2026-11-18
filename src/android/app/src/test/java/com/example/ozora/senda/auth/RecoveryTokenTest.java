package com.example.ozora.senda.auth;
import org.junit.Test;
import static org.junit.Assert.*;
public class RecoveryTokenTest {
    private final String token = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    @Test public void aceptaTokenCompletoYEnlaceDelServidor() {
        assertEquals(token, RecoveryToken.extract("  " + token + " "));
        assertEquals(token, RecoveryToken.extract("https://parque.example/senda/acceso#token=" + token));
    }
    @Test public void rechazaCodigosCortosYFormatosAjenos() {
        for (String value : new String[]{null, "", "1234", token.substring(1), token + "a",
                token.toUpperCase(), "javascript:alert(1)", "https://parque.example/?token=" + token,
                "http://parque.example/acceso#token=" + token})
            assertNull(RecoveryToken.extract(value));
    }
    @Test public void rechazaTokensAmbiguosYFragmentosMalformados() {
        assertNull(RecoveryToken.extract("https://parque.example/acceso#token=" + token + "&token=" + token));
        assertNull(RecoveryToken.extract("https://parque.example/acceso#token=%ZZ"));
        assertNull(RecoveryToken.extract("https://parque.example/acceso#otro=" + token));
    }
    @Test public void extraerNoDependeDeContactarConElHost() {
        assertEquals(token, RecoveryToken.extract("https://sin-red.invalid/acceso#token=" + token));
    }
}
