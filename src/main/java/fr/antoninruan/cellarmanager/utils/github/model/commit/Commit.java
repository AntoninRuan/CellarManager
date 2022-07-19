package fr.antoninruan.cellarmanager.utils.github.model.commit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.model.User;
import fr.antoninruan.cellarmanager.utils.github.model.Authenticated;
import fr.antoninruan.cellarmanager.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Commit extends Authenticated {

    private String sha;
    private String nodeId;
    private String url;
    private GitCommit commit;
    private String htmlUrl;
    private String commentsUrl;
    private JsonObject author;
    private JsonObject committer;
    private JsonArray parentArray;
    private JsonObject stats;
    private List<File> files;

    Commit(String sha, String nodeId, GitCommit commit, String url, String htmlUrl, String commentsUrl,
                  JsonObject author, JsonObject committer, JsonArray parentArray, JsonObject stats, List<File> files) {
        this.sha = sha;
        this.nodeId = nodeId;
        this.commit = commit;
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.commentsUrl = commentsUrl;
        this.author = author;
        this.committer = committer;
        this.parentArray = parentArray;
        this.stats = stats;
        this.files = files;
    }

    public String getSha() {
        return sha;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getUrl() {
        return url;
    }

    public GitCommit getCommit() {
        return commit;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public User getAuthor() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(author.get("url").getAsString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        User author = User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        author.setAuthenticateUsername(super.getAuthenticateUsername());
        author.setAuthenticateToken(super.getAuthenticateToken());
        return author;
    }

    public User getCommitter() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(committer.get("url").getAsString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        User committer = User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        committer.setAuthenticateUsername(super.getAuthenticateUsername());
        committer.setAuthenticateToken(super.getAuthenticateToken());
        return committer;
    }

    public List<Commit> getParent() throws IOException {
        List<Commit> parents = new ArrayList<>();
        for(JsonElement element : parentArray) {
            Commit commit = Commit.fromJson(element.getAsJsonObject());
            commit.setAuthenticateUsername(super.getAuthenticateUsername());
            commit.setAuthenticateToken(super.getAuthenticateToken());
            parents.add(commit);
        }
        return parents;
    }

    public JsonObject getStats() {
        return stats;
    }

    public List<File> getFiles() {
        return files;
    }

    public static Commit fromJson(JsonObject object) throws IOException {
        String sha = JsonUtils.getAsString(object.get("sha"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        GitCommit commit = GitCommit.fromJson(object.get("commit").getAsJsonObject());
        String url = JsonUtils.getAsString(object.get("url"));
        String htmlUrl = JsonUtils.getAsString(object.get("html_url"));
        String commentUrl = JsonUtils.getAsString(object.get("comments_url"));
        JsonObject author = object.get("author").getAsJsonObject();
        JsonObject committer = object.get("committer").getAsJsonObject();
        JsonArray parentsArray = object.get("parents").getAsJsonArray();
        JsonObject stats = object.get("stats").getAsJsonObject();
        List<File> files = new ArrayList<>();
        JsonArray filesArray = object.get("files").getAsJsonArray();
        for(JsonElement element : filesArray) {
            files.add(File.fromJson(element.getAsJsonObject()));
        }
        return new Commit(sha, nodeId, commit, url, htmlUrl, commentUrl, author, committer, parentsArray, stats, files);
    }

}
