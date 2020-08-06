package fr.antoninruan.cellarmanager.utils.github;

import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.utils.github.model.Repository;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.exception.RepositoryNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.exception.UserNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.model.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;

public class GitHubAPIService {

    private static final String GITHUB_API_URL_BASE = "https://api.github.com/";

    public static String username = "";
    public static String token = "";

    private static boolean authenticated = false;

    public static void authenticateForGuestUser() {
        username = "antonin";
        token = "4a75255538b4d65581b4ef2340c3ba282d076965";
        authenticated = true;
    }

    public static void setAuthentication(String username, String token) {
        GitHubAPIService.username = username;
        GitHubAPIService.token = token;
        GitHubAPIService.authenticated = true;
    }

    public static void removeAuthentication() {
        username = "";
        token = "";
        authenticated = false;
    }

    public static boolean isAuthenticated() {
        return authenticated;
    }

    protected static void authenticateHttpConnection(HttpURLConnection connection) {
        String auth = username + ":" + token;
        byte[] encoded = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + new String(encoded));
    }

    public static User getUser(String name) throws IOException, UserNotFoundException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(GITHUB_API_URL_BASE + "users/" + name);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);
        if(connection.getResponseCode() > 299) {
            if(connection.getResponseCode() == 404) {
                throw new UserNotFoundException(name);
            }
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        } else {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            return User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        }
    }

    public static Repository getRepository(String username, String repoName) throws IOException, ParseException, GitHubAPIConnectionException, RepositoryNotFoundException {
        URL url = new URL(GITHUB_API_URL_BASE + "repos/" + username + "/" + repoName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);
        if(connection.getResponseCode() > 299) {
            if(connection.getResponseCode() == 404) {
                throw new RepositoryNotFoundException(username, repoName);
            }
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        } else {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Repository repository = Repository.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
            repository.setAuthenticateUsername(GitHubAPIService.username);
            repository.setAuthenticateToken(GitHubAPIService.token);
            return repository;
        }
    }

}
