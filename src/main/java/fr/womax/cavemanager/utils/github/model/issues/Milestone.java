package fr.womax.cavemanager.utils.github.model.issues;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.womax.cavemanager.utils.github.exception.GitHubAPIConnectionException;
import fr.womax.cavemanager.utils.github.model.User;
import fr.womax.cavemanager.utils.github.utils.Authenticated;
import fr.womax.cavemanager.utils.github.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

public class Milestone extends Authenticated {

    private String url;
    private String htmlUrl;
    private String labelsUrl;
    private int id;
    private String nodeId;
    private int number;
    private String state;
    private String title;
    private String description;
    private JsonObject creator;
    private int openIssues;
    private int closedIssues;
    private Date createdAt;
    private Date updatedAt;
    private Date closedAt;
    private Date dueOn;

    public Milestone(String url, String htmlUrl, String labelsUrl, int id, String nodeId, int number, String state, String title, String description, JsonObject creator, int openIssues, int closedIssues, Date createdAt, Date updatedAt, Date closedAt, Date dueOn) {
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.labelsUrl = labelsUrl;
        this.id = id;
        this.nodeId = nodeId;
        this.number = number;
        this.state = state;
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.openIssues = openIssues;
        this.closedIssues = closedIssues;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
        this.dueOn = dueOn;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getLabelsUrl() {
        return labelsUrl;
    }

    public int getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
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

    public String getDescription() {
        return description;
    }

    public User getCreator() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(creator.get("url").getAsString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        User creator = User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        creator.setAuthenticateUsername(super.getAuthenticateUsername());
        creator.setAuthenticateToken(super.getAuthenticateToken());
        return creator;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public int getClosedIssues() {
        return closedIssues;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public Date getDueOn() {
        return dueOn;
    }

    public static Milestone fromJson(JsonObject object) throws ParseException {
        String url = JsonUtils.getAsString(object.get("url"));
        String htmlUrl = JsonUtils.getAsString(object.get("html_url"));
        String labelsUrl = JsonUtils.getAsString(object.get("labels_url"));
        int id = JsonUtils.getAsInt(object.get("id"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        int number = JsonUtils.getAsInt(object.get("number"));
        String state = JsonUtils.getAsString(object.get("state"));
        String title = JsonUtils.getAsString(object.get("title"));
        String description = JsonUtils.getAsString(object.get("description"));
        JsonObject creator = object.get("creator").getAsJsonObject();
        int openIssues = JsonUtils.getAsInt(object.get("open_issues"));
        int closedIsses = JsonUtils.getAsInt(object.get("closed_issues"));
        Date createdAt = JsonUtils.getAsDate(object.get("created_at"));
        Date updatedAt = JsonUtils.getAsDate(object.get("updated_at"));
        Date closedAt = JsonUtils.getAsDate(object.get("closed_at"));
        Date dueOn = JsonUtils.getAsDate(object.get("due_on"));
        return new Milestone(url, htmlUrl, labelsUrl, id, nodeId, number, state, title, description,
                creator, openIssues, closedIsses, createdAt, updatedAt, closedAt, dueOn);
    }

}
