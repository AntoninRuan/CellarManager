package fr.antoninruan.cellarmanager.utils.github.model.release;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.model.User;
import fr.antoninruan.cellarmanager.utils.github.model.Authenticated;
import fr.antoninruan.cellarmanager.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

public class Asset extends Authenticated {

    private String url;
    private int id;
    private String nodeId;
    private String name;
    private String label;
    private JsonObject uploader;
    private String contentType;
    private String state;
    private long size;
    private int downloadCount;
    private Date createdAt;
    private Date updatedAt;
    private String browserDownloadUrl;

    Asset(String url, int id, String nodeId, String name, String label, JsonObject uploader, String contentType, String state,
          long size, int downloadCount, Date createdAt, Date updatedAt, String browserDownloadUrl) {
        this.url = url;
        this.id = id;
        this.nodeId = nodeId;
        this.name = name;
        this.label = label;
        this.uploader = uploader;
        this.contentType = contentType;
        this.state = state;
        this.size = size;
        this.downloadCount = downloadCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.browserDownloadUrl = browserDownloadUrl;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public User getUploader() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(uploader.get("url").getAsString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        User uploader = User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
        uploader.setAuthenticateUsername(super.getAuthenticateUsername());
        uploader.setAuthenticateToken(super.getAuthenticateToken());
        return uploader;
    }

    public String getContentType() {
        return contentType;
    }

    public String getState() {
        return state;
    }

    public long getSize() {
        return size;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getBrowserDownloadUrl() {
        return browserDownloadUrl;
    }

    protected static Asset fromJson(JsonObject object) throws ParseException {
        String url = JsonUtils.getAsString(object.get("url"));
        int id = JsonUtils.getAsInt(object.get("id"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String name = JsonUtils.getAsString(object.get("name"));
        String label = JsonUtils.getAsString(object.get("label"));
        JsonObject uploader = object.get("uploader").getAsJsonObject();
        String contentType = JsonUtils.getAsString(object.get("content_type"));
        String state = JsonUtils.getAsString(object.get("state"));
        long size = object.get("size").getAsLong();
        int downloadCount = object.get("download_count").getAsInt();
        Date createdAt = MainApp.GITHUB_DATE_FORMAT.parse(JsonUtils.getAsString(object.get("created_at")));
        Date updatedAt = MainApp.GITHUB_DATE_FORMAT.parse(JsonUtils.getAsString(object.get("updated_at")));
        String browserDownloadUrl = JsonUtils.getAsString(object.get("browser_download_url"));
        return new Asset(url, id, nodeId, name, label, uploader, contentType, state, size, downloadCount, createdAt, updatedAt, browserDownloadUrl);
    }

}
