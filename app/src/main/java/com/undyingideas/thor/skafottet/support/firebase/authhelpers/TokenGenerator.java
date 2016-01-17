package com.undyingideas.thor.skafottet.support.firebase.authhelpers;

/**
 * Created on 17-01-2016, 22:52.
 * Project : skafottet
 * From https://github.com/firebase/firebase-token-generator-java/blob/master/src/main/java/com/firebase/security/token/TokenGenerator.java <br>
 * - Hand optimized for Android
 * @author rudz
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Firebase JWT token generator.
 *
 * @author vikrum
 */
public class TokenGenerator {

    private static final int TOKEN_VERSION = 0;

    private final String firebaseSecret;

    /**
     * Default constructor given a Firebase secret.
     *
     * @param firebaseSecret
     */
    public TokenGenerator(String firebaseSecret) {
        super();
        this.firebaseSecret = firebaseSecret;
    }

    /**
     * Create a token for the given object.
     *
     * @param data
     * @return
     */
    public String createToken(final Map<String, Object> data) { return createToken(data, new TokenOptions()); }

    /**
     * Create a token for the given object and options.
     *
     * @param data
     * @param options
     * @return
     */
    public String createToken(final Map<String, Object> data, final TokenOptions options) {
        if (isaBoolean(data, options)) {
            throw new IllegalArgumentException("TokenGenerator.createToken: data is empty and no options are set.  This token will have no effect on Firebase.");
        }

        final JSONObject claims = new JSONObject();

        try {
            claims.put("v", TOKEN_VERSION);
            claims.put("iat", new Date().getTime() / 1000);

            final boolean isAdminToken = options != null && options.isAdmin();
            validateToken("TokenGenerator.createToken", data, isAdminToken);

            if (data != null && !data.isEmpty()) {
                claims.put("d", new JSONObject(data));
            }

            // Handle options
            if (options != null) {
                if (options.getExpires() != null) {
                    claims.put("exp", options.getExpires().getTime() / 1000);
                }

                if (options.getNotBefore() != null) {
                    claims.put("nbf", options.getNotBefore().getTime() / 1000);
                }

                // Only add these claims if they're true to avoid sending them over the wire when false.
                if (options.isAdmin()) {
                    claims.put("admin", options.isAdmin());
                }

                if (options.isDebug()) {
                    claims.put("debug", options.isDebug());
                }
            }
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

        final String token = computeToken(claims);
        if (token.length() > 1024) {
            throw new IllegalArgumentException("TokenGenerator.createToken: Generated token is too long. The token cannot be longer than 1024 bytes.");
        }
        return token;
    }

    private static boolean isaBoolean(final Map<String, Object> data, final TokenOptions options) {
        return (data == null || data.size() == 0) && (options == null || !options.isAdmin() && !options.isDebug());
    }

    private String computeToken(final JSONObject claims) {
        return JWTEncoder.encode(claims, firebaseSecret);
    }

    private static void validateToken(final String functionName, final Map<String, Object> data, final boolean isAdminToken) {
        final boolean containsUid = data != null && data.containsKey("uid");
        if (!containsUid && !isAdminToken || containsUid && !(data.get("uid") instanceof String)) {
            throw new IllegalArgumentException(functionName + ": Data payload must contain a \"uid\" key that must be a string.");
        } else if (containsUid && data.get("uid").toString().length() > 256) {
            throw new IllegalArgumentException(functionName + ": Data payload must contain a \"uid\" key that must not be longer than 256 characters.");
        }
    }



}
