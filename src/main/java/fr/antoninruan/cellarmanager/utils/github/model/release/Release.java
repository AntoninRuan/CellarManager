package fr.antoninruan.cellarmanager.utils.github.model.release;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.model.Authenticated;
import fr.antoninruan.cellarmanager.utils.github.model.User;
import fr.antoninruan.cellarmanager.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Release extends Authenticated {

    private String url;
    private String assetUrl;
    private String uploadUrl;
    private String htmlUrl;
    private int id;
    private String nodeId;
    private String tagName;
    private String targetCommitish;
    private String name;
    private boolean draft;
    private JsonObject author;
    private boolean prerelease;
    private Date createdAt;
    private Date publishedAt;
    private List<Asset> assets;
    private String tarballUrl;
    private String zipballUrl;

    Release(String url, String assetUrl, String uploadUrl, String html_url, int id, String nodeId, String tagName, String targetCommitish,
                   String name, boolean draft, JsonObject author, boolean prerelease, Date createdAt, Date publishedAt, List<Asset> assets,
                   String tarballUrl, String zipballUrl) {
        this.url = url;
        this.assetUrl = assetUrl;
        this.uploadUrl = uploadUrl;
        this.htmlUrl = html_url;
        this.id = id;
        this.nodeId = nodeId;
        this.tagName = tagName;
        this.targetCommitish = targetCommitish;
        this.name = name;
        this.draft = draft;
        this.author = author;
        this.prerelease = prerelease;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.assets = assets;
        this.tarballUrl = tarballUrl;
        this.zipballUrl = zipballUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getAssetUrl() {
        return assetUrl;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public int getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTargetCommitish() {
        return targetCommitish;
    }

    public String getName() {
        return name;
    }

    public boolean isDraft() {
        return draft;
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

    public boolean isPrerelease() {
        return prerelease;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public String getTarballUrl() {
        return tarballUrl;
    }

    public String getZipballUrl() {
        return zipballUrl;
    }

    @Override
    public void setAuthenticateUsername(String authenticateUsername) {
        super.setAuthenticateUsername(authenticateUsername);
        for(Asset asset : assets) {
            asset.setAuthenticateUsername(authenticateUsername);
        }
    }

    @Override
    public void setAuthenticateToken(String authenticateToken) {
        super.setAuthenticateToken(authenticateToken);
        for(Asset asset : assets) {
            asset.setAuthenticateToken(authenticateToken);
        }
    }

    public static Release fromJson(JsonObject object) throws ParseException {
        String url = JsonUtils.getAsString(object.get("url"));
        String assetsUrl = JsonUtils.getAsString(object.get("assets_url"));
        String uploadUrl = JsonUtils.getAsString(object.get("upload_url"));
        String htmlUrl = object.get("html_url").getAsString();
        int id = object.get("id").getAsInt();
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String tagName = JsonUtils.getAsString(object.get("tag_name"));
        String targetCommitish = JsonUtils.getAsString(object.get("target_commitish"));
        String name = JsonUtils.getAsString(object.get("name"));
        boolean draft = object.get("draft").getAsBoolean();
        JsonObject author = object.get("author").getAsJsonObject();
        boolean prerelease = object.get("prerelease").getAsBoolean();
        Date createdAt = MainApp.GITHUB_DATE_FORMAT.parse(object.get("created_at").getAsString());
        Date publishedAt = MainApp.GITHUB_DATE_FORMAT.parse(object.get("published_at").getAsString());
        List<Asset> assets = new ArrayList<>();
        JsonArray assetsJson = object.get("assets").getAsJsonArray();
        for(JsonElement element : assetsJson) {
            assets.add(Asset.fromJson(element.getAsJsonObject()));
        }
        String tarballUrl = JsonUtils.getAsString(object.get("tarball_url"));
        String zipballUrl = JsonUtils.getAsString(object.get("zipball_url"));
        return new Release(url, assetsUrl, uploadUrl, htmlUrl, id, nodeId, tagName, targetCommitish, name, draft, author, prerelease,
                createdAt, publishedAt, assets, tarballUrl, zipballUrl);
    }

}
