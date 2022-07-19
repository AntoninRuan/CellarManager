package fr.antoninruan.cellarmanager.utils.github.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.exception.LabelNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.model.commit.Commit;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Issue;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Label;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Milestone;
import fr.antoninruan.cellarmanager.utils.github.model.release.Release;
import fr.antoninruan.cellarmanager.utils.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class Repository extends Authenticated {

    private final int id;
    private final String nodeId;
    private final String name;
    private final String fullName;
    private final boolean isPrivate;
    private final JsonObject owner;
    private final String htmlUrl;
    private final String description;
    private final boolean fork;
    private final String url;
    private final String forksUrl;
    private final String keysUrl;
    private final String collaboratorsUrl;
    private final String teamsUrl;
    private final String hooksUrl;
    private final String issueEventsUrl;
    private final String eventsUrl;
    private final String assigneesUrl;
    private final String branchesUrl;
    private final String tagsUrl;
    private final String blobsUrl;
    private final String gitTagsUrl;
    private final String gitRefsUrl;
    private final String treesUrl;
    private final String statusesUrl;
    private final String languagesUrl;
    private final String stargazersUrl;
    private final String contributorsUrl;
    private final String subscribersUrl;
    private final String subscriptionUrl;
    private final String commitsUrl;
    private final String gitCommitsUrl;
    private final String commentsUrl;
    private final String issueCommentsUrl;
    private final String contentsUrl;
    private final String compareUrl;
    private final String mergesUrl;
    private final String archiveUrl;
    private final String downloadUrl;
    private final String issuesUrl;
    private final String pullsUrl;
    private final String milestonesUrl;
    private final String notificationsUrl;
    private final String labelsUrl;
    private final String releasesUrl;
    private final String deployementsUrl;
    private final Date createdAt;
    private final Date publishedAt;
    private final Date pushedAt;
    private final String gitUrl;
    private final String sshUrl;
    private final String cloneUrl;
    private final String svnurl;
    private final String homepage;
    private final long size;
    private final int stargazersCount;
    private final int watchersCount;
    private final String language;
    private final boolean issues;
    private final boolean projects;
    private final boolean download;
    private final boolean wiki;
    private final boolean pages;
    private final int forksCount;
    private final String mirrorUrl;
    private final boolean archived;
    private final boolean disabled;
    private final int openIssuesCount;
    private final JsonElement license;
    private final int forks;
    private final int openIssues;
    private final int watchers;
    private final String defaultBranch;

    public Repository(int id, String nodeId, String name, String fullName, boolean isPrivate, JsonObject owner, String htmlUrl, String description,
                      boolean fork, String url, String forksUrl, String keysUrl, String collaboratorsUrl, String teamsUrl, String hooksUrl,
                      String issueEventsUrl, String eventsUrl, String assigneesUrl, String branchesUrl, String tagsUrl, String blobsUrl,
                      String gitTagsUrl, String gitRefsUrl, String treesUrl, String statusesUrl, String languagesUrl, String stargazersUrl,
                      String contributorsUrl, String subscribersUrl, String subscriptionUrl, String commitsUrl, String gitCommitsUrl, String commentsUrl,
                      String issueCommentsUrl, String contentsUrl, String compareUrl, String mergesUrl, String archiveUrl, String downloadUrl,
                      String issuesUrl, String pullsUrl, String milestonesUrl, String notificationsUrl, String labelsUrl, String releasesUrl,
                      String deployementsUrl, Date createdAt, Date publishedAt, Date pushedAt, String gitUrl, String sshUrl, String cloneUrl,
                      String svnurl, String homepage, long size, int stargazersCount, int watchersCount, String language, boolean issues,
                      boolean projects, boolean download, boolean wiki, boolean pages, int forksCount, String mirrorUrl, boolean archived, boolean disabled,
                      int openIssuesCount, JsonElement license, int forks, int openIssues, int watchers, String defaultBranch) {
        this.id = id;
        this.nodeId = nodeId;
        this.name = name;
        this.fullName = fullName;
        this.isPrivate = isPrivate;
        this.owner = owner;
        this.htmlUrl = htmlUrl;
        this.description = description;
        this.fork = fork;
        this.url = url;
        this.forksUrl = forksUrl;
        this.keysUrl = keysUrl;
        this.collaboratorsUrl = collaboratorsUrl;
        this.teamsUrl = teamsUrl;
        this.hooksUrl = hooksUrl;
        this.issueEventsUrl = issueEventsUrl;
        this.eventsUrl = eventsUrl;
        this.assigneesUrl = assigneesUrl;
        this.branchesUrl = branchesUrl;
        this.tagsUrl = tagsUrl;
        this.blobsUrl = blobsUrl;
        this.gitTagsUrl = gitTagsUrl;
        this.gitRefsUrl = gitRefsUrl;
        this.treesUrl = treesUrl;
        this.statusesUrl = statusesUrl;
        this.languagesUrl = languagesUrl;
        this.stargazersUrl = stargazersUrl;
        this.contributorsUrl = contributorsUrl;
        this.subscribersUrl = subscribersUrl;
        this.subscriptionUrl = subscriptionUrl;
        this.commitsUrl = commitsUrl;
        this.gitCommitsUrl = gitCommitsUrl;
        this.commentsUrl = commentsUrl;
        this.issueCommentsUrl = issueCommentsUrl;
        this.contentsUrl = contentsUrl;
        this.compareUrl = compareUrl;
        this.mergesUrl = mergesUrl;
        this.archiveUrl = archiveUrl;
        this.downloadUrl = downloadUrl;
        this.issuesUrl = issuesUrl;
        this.pullsUrl = pullsUrl;
        this.milestonesUrl = milestonesUrl;
        this.notificationsUrl = notificationsUrl;
        this.labelsUrl = labelsUrl;
        this.releasesUrl = releasesUrl;
        this.deployementsUrl = deployementsUrl;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.pushedAt = pushedAt;
        this.gitUrl = gitUrl;
        this.sshUrl = sshUrl;
        this.cloneUrl = cloneUrl;
        this.svnurl = svnurl;
        this.homepage = homepage;
        this.size = size;
        this.stargazersCount = stargazersCount;
        this.watchersCount = watchersCount;
        this.language = language;
        this.issues = issues;
        this.projects = projects;
        this.download = download;
        this.wiki = wiki;
        this.pages = pages;
        this.forksCount = forksCount;
        this.mirrorUrl = mirrorUrl;
        this.archived = archived;
        this.disabled = disabled;
        this.openIssuesCount = openIssuesCount;
        this.license = license;
        this.forks = forks;
        this.openIssues = openIssues;
        this.watchers = watchers;
        this.defaultBranch = defaultBranch;
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

    public String getFullName() {
        return fullName;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public User getOwner() throws IOException, ParseException {
        URL url = new URL(owner.get("url").getAsString());
        InputStreamReader reader = new InputStreamReader(url.openStream());
        return User.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFork() {
        return fork;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public Date getPushedAt() {
        return pushedAt;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public String getSvnurl() {
        return svnurl;
    }

    public String getHomepage() {
        return homepage;
    }

    public long getSize() {
        return size;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public String getLanguage() {
        return language;
    }

    public boolean hasIssues() {
        return issues;
    }

    public boolean hasProjects() {
        return projects;
    }

    public boolean hasDownload() {
        return download;
    }

    public boolean hasWiki() {
        return wiki;
    }

    public boolean hasPages() {
        return pages;
    }

    public int getForksCount() {
        return forksCount;
    }

    public String getMirrorUrl() {
        return mirrorUrl;
    }

    public boolean isArchived() {
        return archived;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getOpenIssuesCount() {
        return openIssuesCount;
    }

    public License getLicense() throws IOException, GitHubAPIConnectionException{
        if(license.isJsonNull()) {
            return null;
        }
        URL url = new URL(license.getAsJsonObject().get("url").getAsString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject licenseJson = JsonParser.parseReader(reader).getAsJsonObject();

        return License.fromJson(licenseJson);
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public int getWatchers() {
        return watchers;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public List<Repository> getForks() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(forksUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray forksJson = JsonParser.parseReader(reader).getAsJsonArray();
        List<Repository> forks = new ArrayList<>();
        for(JsonElement element : forksJson) {
            Repository repository = Repository.fromJson(element.getAsJsonObject());
            repository.setAuthenticateUsername(super.getAuthenticateUsername());
            repository.setAuthenticateToken(super.getAuthenticateToken());
            forks.add(repository);
        }
        return forks;
    }

    public List<User> getCollaborators() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(collaboratorsUrl.replace("{/collaborator}", ""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        JsonArray collaboratorsJson = JsonParser.parseReader(reader).getAsJsonArray();
        List<User> collaborators = new ArrayList<>();
        for(JsonElement element : collaboratorsJson) {
            User collaborator = User.fromJson(element.getAsJsonObject());
            collaborator.setAuthenticateUsername(super.getAuthenticateUsername());
            collaborator.setAuthenticateToken(super.getAuthenticateToken());
            collaborators.add(collaborator);
        }
        return collaborators;
    }

    public List<Tag> getTags() throws IOException, GitHubAPIConnectionException {
        URL url = new URL(tagsUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray tagsJson = JsonParser.parseReader(reader).getAsJsonArray();
        List<Tag> tags = new ArrayList<>();
        for(JsonElement element : tagsJson) {
            tags.add(Tag.fromJson(element.getAsJsonObject()));
        }
        return tags;
    }

    private Map<String, Integer> getLanguages() throws IOException, GitHubAPIConnectionException {
        URL url = new URL(languagesUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject languagesJson = JsonParser.parseReader(reader).getAsJsonObject();
        Map<String, Integer> languages = new HashMap<>();
        for(Map.Entry<String, JsonElement> entry : languagesJson.entrySet()) {
            languages.put(entry.getKey(), entry.getValue().getAsInt());
        }
        return languages;
    }

    public List<User> getStargazers() throws IOException, ParseException, GitHubAPIConnectionException {
        return getUsers(stargazersUrl);
    }

    public List<User> getContributors() throws IOException, ParseException, GitHubAPIConnectionException {
        return getUsers(contributorsUrl);
    }

    public List<User> getSubscribers() throws IOException, ParseException, GitHubAPIConnectionException {
        return getUsers(subscribersUrl);
    }

    public List<User> getSubscriptions() throws IOException, ParseException, GitHubAPIConnectionException {
        return getUsers(subscriptionUrl);
    }

    public List<Commit> getCommits() throws IOException, GitHubAPIConnectionException {
        URL url = new URL(commitsUrl.replace("{/sha}", ""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray commitsJson = JsonParser.parseReader(reader).getAsJsonArray();
        List<Commit> commits = new ArrayList<>();
        for(JsonElement element : commitsJson) {
            Commit commit = Commit.fromJson(element.getAsJsonObject());
            commit.setAuthenticateUsername(super.getAuthenticateUsername());
            commit.setAuthenticateToken(super.getAuthenticateToken());
            commits.add(commit);
        }
        return commits;
    }

    public List<Release> getReleases() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(releasesUrl.replace("{/id}", ""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray releasesJson = JsonParser.parseReader(reader).getAsJsonArray();
        List<Release> releases = new ArrayList<>();
        for(JsonElement element : releasesJson) {
            Release release = Release.fromJson(element.getAsJsonObject());
            release.setAuthenticateUsername(super.getAuthenticateUsername());
            release.setAuthenticateToken(super.getAuthenticateToken());
            releases.add(release);
        }
        return releases;
    }

    public Release getLatestRelease() throws IOException, ParseException {
        URL url = new URL(releasesUrl.replace("{/id}", "/latest"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        return Release.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public List<Milestone> getMilestones() throws IOException, ParseException, GitHubAPIConnectionException {
        URL url = new URL(milestonesUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        List<Milestone> milestones = new ArrayList<>();
        for(JsonElement element : array) {
            Milestone milestone = Milestone.fromJson(element.getAsJsonObject());
            milestone.setAuthenticateUsername(super.getAuthenticateUsername());
            milestone.setAuthenticateToken(super.getAuthenticateToken());
            milestones.add(milestone);
        }
        return milestones;
    }

    public List<Label> getLabels() throws IOException, GitHubAPIConnectionException {
        URL url = new URL(labelsUrl.replace("{/name}", ""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        List<Label> labels = new ArrayList<>();
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        for(JsonElement element : array) {
            labels.add(Label.fromJson(element.getAsJsonObject()));
        }
        return labels;
    }

    public Label getLabel(String name) throws IOException, LabelNotFoundException, GitHubAPIConnectionException {
        URL url = new URL(labelsUrl.replace("{/name}", "/" + name));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            if(connection.getResponseCode() == 404) {
                throw new LabelNotFoundException(name, this.getName());
            }
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        return Label.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public Issue createIssue(String title, String body, Label[] labels) throws IOException, GitHubAPIConnectionException, ParseException {
        URL url = new URL(issuesUrl.replace("{/number}", ""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        authenticateHttpConnection(connection);

        JsonObject out = new JsonObject();
        out.addProperty("title", title);
        out.addProperty("body", body);
        JsonArray labelsJson = new JsonArray();
        for(Label label : labels) {
            labelsJson.add(label.getName());
        }
        out.add("labels", labelsJson);

        byte[] output = out.toString().getBytes(StandardCharsets.UTF_8);

        connection.setFixedLengthStreamingMode(output.length);
        connection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
        connection.connect();
        try (OutputStream os = connection.getOutputStream()){
            os.write(output);
        }

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        return Issue.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
    }

    private List<User> getUsers(String usersUrl) throws IOException, GitHubAPIConnectionException, ParseException {
        URL url = new URL(contributorsUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        authenticateHttpConnection(connection);

        if(connection.getResponseCode() > 299) {
            throw new GitHubAPIConnectionException(connection.getResponseCode(), connection.getResponseMessage());
        }

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonArray contributorsJson = JsonParser.parseReader(reader).getAsJsonArray();
        List<User> contributors = new ArrayList<>();
        for(JsonElement element : contributorsJson) {
            URL url1 = new URL(element.getAsJsonObject().get("url").getAsString());
            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
            authenticateHttpConnection(connection1);

            if(connection1.getResponseCode() > 299) {
                throw new GitHubAPIConnectionException(connection1.getResponseCode(), connection1.getResponseMessage());
            }

            InputStreamReader reader1 = new InputStreamReader(connection1.getInputStream());
            User contributor = User.fromJson(JsonParser.parseReader(reader1).getAsJsonObject());
            contributor.setAuthenticateUsername(super.getAuthenticateUsername());
            contributor.setAuthenticateToken(super.getAuthenticateToken());
            contributors.add(contributor);
        }
        return contributors;
    }

    public static Repository fromJson(JsonObject object) throws ParseException {
        int id = JsonUtils.getAsInt(object.get("id"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String name = JsonUtils.getAsString(object.get("name"));
        String fullName = JsonUtils.getAsString(object.get("full_name"));
        boolean isPrivate = object.get("private").getAsBoolean();
        JsonObject owner = object.get("owner").getAsJsonObject();
        String hmtlUrl = JsonUtils.getAsString(object.get("html_url"));
        String description = JsonUtils.getAsString(object.get("description"));
        boolean fork = object.get("fork").getAsBoolean();
        String url = JsonUtils.getAsString(object.get("url"));
        String forkUrl = JsonUtils.getAsString(object.get("forks_url"));
        String keysUrl = JsonUtils.getAsString(object.get("keys_url"));
        String collaboratorsUrl = JsonUtils.getAsString(object.get("collaborators_url"));
        String teamsUrl = JsonUtils.getAsString(object.get("teams_url"));
        String hooksUrl = JsonUtils.getAsString(object.get("hooks_url"));
        String issueEventUrl = JsonUtils.getAsString(object.get("issue_events_url"));
        String eventsUrl = JsonUtils.getAsString(object.get("events_url"));
        String assigneesUrl = JsonUtils.getAsString(object.get("assignees_url"));
        String branchesUrl = JsonUtils.getAsString(object.get("branches_url"));
        String tagsUrl = JsonUtils.getAsString(object.get("tags_url"));
        String blobsUrl = JsonUtils.getAsString(object.get("blobs_url"));
        String gitTagsUrl = JsonUtils.getAsString(object.get("git_tags_url"));
        String gitRefsUrl = JsonUtils.getAsString(object.get("git_refs_url"));
        String treesUrl = JsonUtils.getAsString(object.get("trees_url"));
        String statusesUrl = JsonUtils.getAsString(object.get("statuses_url"));
        String languagesUrl = JsonUtils.getAsString(object.get("languages_url"));
        String stargazersUrl = JsonUtils.getAsString(object.get("stargazers_url"));
        String contributorsUrl = JsonUtils.getAsString(object.get("contributors_url"));
        String subscribersUrl = JsonUtils.getAsString(object.get("subscribers_url"));
        String subscriptionUrl = JsonUtils.getAsString(object.get("subscription_url"));
        String commitsUrl = JsonUtils.getAsString(object.get("commits_url"));
        String gitCommitsUrl =  JsonUtils.getAsString(object.get("git_commits_url"));
        String commentsUrl = JsonUtils.getAsString(object.get("comments_url"));
        String issueCommentUrl = JsonUtils.getAsString(object.get("issue_comment_url"));
        String contentsUrl = JsonUtils.getAsString(object.get("contents_url"));
        String compareUrl = JsonUtils.getAsString(object.get("compare_url"));
        String mergesUrl = JsonUtils.getAsString(object.get("merges_url"));
        String archiveUrl = JsonUtils.getAsString(object.get("archive_url"));
        String downloadUrl = JsonUtils.getAsString(object.get("downloads_url"));
        String issuesUrl = JsonUtils.getAsString(object.get("issues_url"));
        String pullsUrl = JsonUtils.getAsString(object.get("pulls_url"));
        String milestoneUrl = JsonUtils.getAsString(object.get("milestones_url"));
        String notificationsUrl = JsonUtils.getAsString(object.get("notifications_url"));
        String labelsUrl = JsonUtils.getAsString(object.get("labels_url"));
        String releasesUrl = JsonUtils.getAsString(object.get("releases_url"));
        String deploymentsUrl = JsonUtils.getAsString(object.get("deployments_url"));
        Date createdAt = MainApp.GITHUB_DATE_FORMAT.parse(object.get("created_at").getAsString());
        Date publishedAt = MainApp.GITHUB_DATE_FORMAT.parse(object.get("updated_at").getAsString());
        Date pushedAt = MainApp.GITHUB_DATE_FORMAT.parse(object.get("pushed_at").getAsString());
        String gitUrl = JsonUtils.getAsString(object.get("git_url"));
        String sshUrl = JsonUtils.getAsString(object.get("ssh_url"));
        String cloneUrl = JsonUtils.getAsString(object.get("clone_url"));
        String svnUrl = JsonUtils.getAsString(object.get("svn_url"));
        String homepage = JsonUtils.getAsString(object.get("homepage"));
        long size = object.get("size").getAsLong();
        int stargazersCount = JsonUtils.getAsInt(object.get("stargazers_count"));
        int watchersCount = JsonUtils.getAsInt(object.get("watchers_count"));
        String language = JsonUtils.getAsString(object.get("language"));
        boolean issues = JsonUtils.getAsBoolean(object.get("has_issues"));
        boolean projects = JsonUtils.getAsBoolean(object.get("has_projects"));
        boolean downloads = JsonUtils.getAsBoolean(object.get("has_downloads"));
        boolean wiki = JsonUtils.getAsBoolean(object.get("has_wiki"));
        boolean pages = JsonUtils.getAsBoolean(object.get("has_pages"));
        int forksCount = JsonUtils.getAsInt(object.get("forks_count"));
        String mirrorUrl = JsonUtils.getAsString(object.get("mirror_url"));
        boolean archived = JsonUtils.getAsBoolean(object.get("archived"));
        boolean disabled = JsonUtils.getAsBoolean(object.get("disabled"));
        int openIssuesCount = JsonUtils.getAsInt(object.get("open_issues_count"));
        JsonElement license = object.get("license");
        int forks = JsonUtils.getAsInt(object.get("forks"));
        int openIssues = JsonUtils.getAsInt(object.get("open_issues"));
        int watchers = JsonUtils.getAsInt(object.get("watchers"));
        String defaultBranch = JsonUtils.getAsString(object.get("default_branch"));
        return new Repository(id, nodeId, name, fullName, isPrivate, owner, hmtlUrl, description, fork, url, forkUrl, keysUrl,
                collaboratorsUrl, teamsUrl, hooksUrl, issueEventUrl, eventsUrl, assigneesUrl, branchesUrl, tagsUrl, blobsUrl, gitTagsUrl,
                gitRefsUrl, treesUrl, statusesUrl, languagesUrl, stargazersUrl, contributorsUrl, subscribersUrl, subscriptionUrl,
                commitsUrl, gitCommitsUrl, commentsUrl, commentsUrl, contentsUrl, compareUrl, mergesUrl, archiveUrl, downloadUrl, issuesUrl,
                pullsUrl, milestoneUrl, notificationsUrl, labelsUrl, releasesUrl, deploymentsUrl, createdAt, publishedAt, pushedAt, gitUrl, sshUrl,
                cloneUrl, svnUrl, homepage, size, stargazersCount, watchersCount, language, issues, projects, downloads, wiki, pages, forksCount,
                mirrorUrl, archived, disabled, openIssuesCount, license, forks, openIssues, watchers, defaultBranch);
    }

}
