package fr.antoninruan.cellarmanager.utils.github.utils;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Authenticated {

    private String authenticateUsername;
    private String authenticateToken;

    public Authenticated() {
        this.authenticateUsername = "";
        this.authenticateToken = "";
    }

    protected String getAuthenticateUsername() {
        return authenticateUsername;
    }

    protected String getAuthenticateToken() {
        return authenticateToken;
    }

    public void setAuthenticateUsername(String authenticateUsername) {
        this.authenticateUsername = authenticateUsername;
    }

    public void setAuthenticateToken(String authenticateToken) {
        this.authenticateToken = authenticateToken;
    }

    protected void authenticateHttpConnection(HttpURLConnection connection) {
        String auth = authenticateUsername + ":" + authenticateToken;
        byte[] encoded = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + new String(encoded));
    }

}
