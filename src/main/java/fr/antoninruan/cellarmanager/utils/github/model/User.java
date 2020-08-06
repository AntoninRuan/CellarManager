package fr.antoninruan.cellarmanager.utils.github.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.utils.github.Test;
import fr.antoninruan.cellarmanager.utils.github.utils.Authenticated;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User extends Authenticated {

    private String login;
    private int id;
    private String nodeId;
    private String avatarUrl;
    private String url;
    private String htmlUrl;
    private String followersUrl;
    private String followingUrl;
    private String gistsUrl;
    private String starredUrl;
    private String subscriptionsUrl;
    private String organizationsUrl;
    private String reposUrl;
    private String eventsUrl;
    private String receiveEventsUrl;
    private String type;
    private boolean siteAdmin;
    private String name;
    private String company;
    private String blog;
    private String location;
    private String email;
    private boolean hireable;
    private String bio;
    private String twitterUsername;
    private int publicRepos;
    private int publicGist;
    private int followers;
    private int following;
    private Date createdAt;
    private Date updatedAt;

    public User(String login, int id, String nodeId, String avatarUrl, String url, String htmlUrl, String followersUrl, String followingUrl,
                String gistsUrl, String starredUrl, String subscriptionsUrl, String organizationsUrl, String reposUrl, String eventsUrl, String receiveEventsUrl,
                String type, boolean siteAdmin, String name, String company, String blog, String location, String email, boolean hireable, String bio,
                String twitterUsername, int publicRepos, int publicGist, int followers, int following, Date createdAt, Date updatedAt) {
        this.login = login;
        this.id = id;
        this.nodeId = nodeId;
        this.avatarUrl = avatarUrl;
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.followersUrl = followersUrl;
        this.followingUrl = followingUrl;
        this.gistsUrl = gistsUrl;
        this.starredUrl = starredUrl;
        this.subscriptionsUrl = subscriptionsUrl;
        this.organizationsUrl = organizationsUrl;
        this.reposUrl = reposUrl;
        this.eventsUrl = eventsUrl;
        this.receiveEventsUrl = receiveEventsUrl;
        this.type = type;
        this.siteAdmin = siteAdmin;
        this.name = name;
        this.company = company;
        this.blog = blog;
        this.location = location;
        this.email = email;
        this.hireable = hireable;
        this.bio = bio;
        this.twitterUsername = twitterUsername;
        this.publicRepos = publicRepos;
        this.publicGist = publicGist;
        this.followers = followers;
        this.following = following;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getLogin() {
        return login;
    }

    public int getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public List<User> getFollowers() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(followersUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        List<User> followers = new ArrayList<>();
        for(JsonElement element : array) {
            InputStreamReader reader1 = new InputStreamReader(new URL(element.getAsJsonObject().get("url").getAsString()).openStream());
            User follower = User.fromJson(JsonParser.parseReader(reader1).getAsJsonObject());
            follower.setAuthenticateUsername(super.getAuthenticateUsername());
            follower.setAuthenticateToken(super.getAuthenticateToken());
            followers.add(follower);
        }
        return followers;
    }

    public List<User> getFollowing() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(followingUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        List<User> followings = new ArrayList<>();
        for(JsonElement element : array) {
            InputStreamReader reader1 = new InputStreamReader(new URL(element.getAsJsonObject().get("url").getAsString()).openStream());
            User following = User.fromJson(JsonParser.parseReader(reader1).getAsJsonObject());
            following.setAuthenticateUsername(super.getAuthenticateUsername());
            following.setAuthenticateToken(super.getAuthenticateToken());
            followings.add(following);
        }
        return followings;
    }

    public List<Repository> getRepositories() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(reposUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(url.openStream());
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        List<Repository> repos = new ArrayList<>();
        for(JsonElement element : array) {
            Repository repository = Repository.fromJson(element.getAsJsonObject());
            repository.setAuthenticateUsername(super.getAuthenticateUsername());
            repository.setAuthenticateToken(super.getAuthenticateToken());
            repos.add(repository);
        }
        return repos;
    }

    public String getType() {
        return type;
    }

    public boolean isSiteAdmin() {
        return siteAdmin;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getBlog() {
        return blog;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public boolean isHireable() {
        return hireable;
    }

    public String getBio() {
        return bio;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public int getPublicRepos() {
        return publicRepos;
    }

    public int getPublicGist() {
        return publicGist;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public static User fromJson(JsonObject object) throws ParseException {
        String login = JsonUtils.getAsString(object.get("login"));
        int id = JsonUtils.getAsInt(object.get("id"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String avatarUrl = JsonUtils.getAsString(object.get("avatar_url"));
        String url = JsonUtils.getAsString(object.get("url"));
        String htmlUrl = JsonUtils.getAsString(object.get("html_url"));
        String followersUrl = JsonUtils.getAsString(object.get("followers_url"));
        String followingUrl = JsonUtils.getAsString(object.get("following_url"));
        String gistsUrl = JsonUtils.getAsString(object.get("gists_url"));
        String starredUrl = JsonUtils.getAsString(object.get("starred_url"));
        String subscriptionsUrl = JsonUtils.getAsString(object.get("subscriptions_url"));
        String organizationsUrl = JsonUtils.getAsString(object.get("organizations_url"));
        String reposUrl = JsonUtils.getAsString(object.get("repos_url"));
        String eventsUrl = JsonUtils.getAsString(object.get("events_url"));
        String receiveEventsUrl = JsonUtils.getAsString(object.get("received_events_url"));
        String type = JsonUtils.getAsString(object.get("type"));
        boolean siteAdmin = object.get("site_admin").getAsBoolean();
        String name = JsonUtils.getAsString(object.get("name"));
        String compagny = JsonUtils.getAsString(object.get("company"));
        String blog = JsonUtils.getAsString(object.get("blog"));
        String location = JsonUtils.getAsString(object.get("location"));
        String email = JsonUtils.getAsString(object.get("email"));
        boolean hireable = JsonUtils.getAsBoolean(object.get("hireable"));
        String bio = JsonUtils.getAsString(object.get("bio"));
        String twitterUsername = JsonUtils.getAsString(object.get("twitter_username"));
        int publicRepos = JsonUtils.getAsInt(object.get("public_repos"));
        int publicGist = JsonUtils.getAsInt(object.get("public_gists"));
        int followers = JsonUtils.getAsInt(object.get("followers"));
        int following = JsonUtils.getAsInt(object.get("following"));
        Date createdAt = Test.DATE_FORMAT.parse(JsonUtils.getAsString(object.get("created_at")));
        Date updatedAt = Test.DATE_FORMAT.parse(JsonUtils.getAsString(object.get("updated_at")));
        return new User(login, id, nodeId, avatarUrl, url, htmlUrl, followersUrl, followingUrl, gistsUrl, starredUrl, subscriptionsUrl, organizationsUrl, reposUrl,
                eventsUrl, receiveEventsUrl, type, siteAdmin, name, compagny, blog, location, email, hireable, bio, twitterUsername, publicRepos, publicGist,
                followers, following, createdAt, updatedAt);
    }

}
