package com.ngokaf.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.regex.Pattern;

@Component
public class CodeRetraitHmac {

    private static final String PREFIX = "hmac-sha256$";
    private static final int SHA256_LENGTH_BYTES = 32;
    private static final Pattern VERIFICATEUR_PATTERN =
            Pattern.compile("hmac-sha256\\$[0-9a-f]{64}");

    private final String secret;

    public CodeRetraitHmac(@Value("${NGOKAF_HMAC_SECRET:}") String secret) {
        this.secret = secret;
    }

    public String genererVerificateur(String code) {
        byte[] key = obtenirCleConfiguree();
        verifierCodeEntree(code);
        return PREFIX + HexFormat.of().formatHex(calculerHmac(code, key));
    }

    public boolean verifier(String code, String verificateurStocke) {
        byte[] key = obtenirCleConfiguree();
        if (code == null || code.isBlank()
                || verificateurStocke == null
                || !VERIFICATEUR_PATTERN.matcher(verificateurStocke).matches()) {
            return false;
        }

        byte[] valeurStockee = HexFormat.of().parseHex(verificateurStocke.substring(PREFIX.length()));
        byte[] valeurCalculee = calculerHmac(code, key);
        return MessageDigest.isEqual(valeurCalculee, valeurStockee);
    }

    private byte[] obtenirCleConfiguree() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "La vérification du code de retrait est désactivée : "
                            + "la variable d'environnement NGOKAF_HMAC_SECRET n'est pas configurée.");
        }

        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < SHA256_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "La variable d'environnement NGOKAF_HMAC_SECRET doit contenir au moins 32 octets UTF-8.");
        }
        return key;
    }

    private byte[] calculerHmac(String code, byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(code.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Impossible de calculer le vérificateur HMAC-SHA-256.", e);
        }
    }

    private void verifierCodeEntree(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code de retrait ne peut pas être vide.");
        }
    }
}
