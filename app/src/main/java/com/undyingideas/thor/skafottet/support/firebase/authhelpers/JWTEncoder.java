package com.undyingideas.thor.skafottet.support.firebase.authhelpers;

/**
 * Created on 17-01-2016, 22:48.
 * Project : skafottet
 * Fra : https://github.com/firebase/firebase-token-generator-java/blob/master/src/main/java/com/firebase/security/token/JWTEncoder.java
 * Altered for android use.
 * @author rudz
 */

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * JWT encoder.
 *
 * @author vikrum
 */
public final class JWTEncoder {

    private static final String TOKEN_SEP = ".";
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private static final String HMAC_256 = "HmacSHA256";

    /**
     * Encode and sign a set of claims.
     *
     * @param claims
     * @param secret
     * @return
     */
    public static String encode(JSONObject claims, String secret) {
        final String encodedHeader = getCommonHeader();
        final String encodedClaims = encodeJson(claims);

        final String secureBits = encodedHeader + TOKEN_SEP + encodedClaims;

        final String sig = sign(secret, secureBits);

        return secureBits + TOKEN_SEP + sig;
    }

    private static String sign(final String secret, final String secureBits) {
        try {
            final Mac sha256_HMAC = Mac.getInstance(HMAC_256);
            final SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(UTF8_CHARSET), HMAC_256);
            sha256_HMAC.init(secret_key);
            final byte[] sig = sha256_HMAC.doFinal(secureBits.getBytes(UTF8_CHARSET));
            return Base64.encodeToString(sig, Base64.DEFAULT);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCommonHeader() {
        try {
            final JSONObject headerJson = new JSONObject();
            headerJson.put("typ", "JWT");
            headerJson.put("alg", "HS256");
            return encodeJson(headerJson);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeJson(final JSONObject jsonData) {
        return Base64.encodeToString(jsonData.toString().getBytes(UTF8_CHARSET), Base64.DEFAULT);
    }
}