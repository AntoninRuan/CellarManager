package fr.antoninruan.cellarmanager.utils.github.model.issues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.utils.github.model.Repository;
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
import java.util.Date;
import java.util.List;

public class Issue extends Authenticated {

    private int id;
    private String nodeId;
    private String url;
    private String repositoryUrl;
    private String labelsUrl;
    private String commentsUrl;
    private String eventsUrl;
    private String htmlUrl;
    private int number;
    private String state;
    private String title;
    private String body;
    private JsonObject user;
    private List<Label> labels;
    private JsonElement assignee;
    private JsonArray assignees;
    private Milestone milestone;
    private boolean locked;
    private String activeLockReason;
    private int comments;
    private JsonObject pullRequest;
    private Date closedAt;
    private Date createdAt;
    private Date updatedAt;

    public Issue(int id, String nodeId, String url, String repositoryUrl, String labelsUrl, String commentsUrl, String eventsUrl,
                 String htmlUrl, int number, String state, String title, String body, JsonObject user, List<Label> labels,
                 JsonElement assignee, JsonArray assignees, Milestone milestone, boolean locked, String activeLockReason, int comments,
                 JsonObject pullRequest, Date closedAt, Date createdAt, Date updatedAt) {
        this.id = id;
        this.nodeId = nodeId;
        this.url = url;
        this.repositoryUrl = repositoryUrl;
        this.labelsUrl = labelsUrl;
        this.commentsUrl = commentsUrl;
        this.eventsUrl = eventsUrl;
        this.htmlUrl = htmlUrl;
        this.number = number;
        this.state = state;
        this.title = title;
        this.body = body;
        this.user = user;
        this.labels = labels;
        this.assignee = assignee;
        this.assignees = assignees;
        this.milestone = milestone;
        this.locked = locked;
        this.activeLockReason = activeLockReason;
        this.comments = comments;
        this.pullRequest = pullRequest;
        this.closedAt = closedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getUrl() {
        return url;
    }

    public Repository getRepository() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(repositoryUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        Repository repository = Repository.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        repository.setAuthenticateUsername(super.getAuthenticateUsername());
        repository.setAuthenticateToken(super.getAuthenticateToken());
        return repository;
    }

    public String getLabelsUrl() {
        return labelsUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public int getNumber() {
        return number;
    }

    public String getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public User getUser() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(user.get("url").getAsString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        User user = User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        user.setAuthenticateUsername(super.getAuthenticateUsername());
        user.setAuthenticateToken(super.getAuthenticateToken());
        return user;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public User getAssignee() throws IOException, ParseException {
        if(assignee.isJsonNull())
            return null;
        URL url = new URL(assignee.getAsJsonObject().get("url").getAsString());
        InputStreamReader reader = new InputStreamReader(url.openStream());
        return User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public List<User> getAssignees() throws IOException, ParseException, GitHubAPIConnectionException {
        List<User> assignees = new ArrayList<>();
        for(JsonElement element : this.assignees) {
            URL url = new URL(element.getAsJsonObject().get("url").getAsString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            authenticateHttpConnection(connection);

            if(connection.getResponseCode() > 299) {
                throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
            }

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            User assignee = User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
            assignee.setAuthenticateUsername(super.getAuthenticateUsername());
            assignee.setAuthenticateToken(super.getAuthenticateToken());
            assignees.add(assignee);
        }
        return assignees;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getActiveLockReason() {
        return activeLockReason;
    }

    public int getComments() {
        return comments;
    }

    public JsonObject getPullRequest() {
        return pullRequest;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public static Issue fromJson(JsonObject object) throws ParseException {
        int id = JsonUtils.getAsInt(object.get("id"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String url = JsonUtils.getAsString(object.get("url"));
        String repositoryUrl = JsonUtils.getAsString(object.get("repository_url"));
        String labelsUrl = JsonUtils.getAsString(object.get("labels_url"));
        String commentsUrl = JsonUtils.getAsString(object.get("comments_url"));
        String eventsUrl = JsonUtils.getAsString(object.get("events_url"));
        String htmlUrl = JsonUtils.getAsString(object.get("html_url"));
        int number = JsonUtils.getAsInt(object.get("number"));
        String state = JsonUtils.getAsString(object.get("state"));
        String title = JsonUtils.getAsString(object.get("title"));
        String body = JsonUtils.getAsString(object.get("body"));
        JsonObject user = object.get("user").getAsJsonObject();
        List<Label> labels = new ArrayList<>();
        for(JsonElement element : object.get("labels").getAsJsonArray()) {
            labels.add(Label.fromJson(element.getAsJsonObject()));
        }
        JsonElement assignee = object.get("assignee");
        JsonArray assignees = object.get("assignees").getAsJsonArray();
        Milestone milestone = object.get("milestone").isJsonNull() ? null : Milestone.fromJson(object.get("milestone").getAsJsonObject());
        boolean locked = JsonUtils.getAsBoolean(object.get("locked"));
        String activeLockReason = JsonUtils.getAsString(object.get("active_lock_reason"));
        int comments = JsonUtils.getAsInt(object.get("comments"));
        Date closedAt = JsonUtils.getAsDate(object.get("closed_at"));
        Date createdAt = JsonUtils.getAsDate(object.get("created_at"));
        Date updatedAt = JsonUtils.getAsDate(object.get("updated_at"));
        return new Issue(id, nodeId, url, repositoryUrl, labelsUrl, commentsUrl, eventsUrl, htmlUrl, number, state, title, body,
                user, labels, assignee, assignees, milestone, locked, activeLockReason, comments, null, closedAt, createdAt, updatedAt);
    }

}
