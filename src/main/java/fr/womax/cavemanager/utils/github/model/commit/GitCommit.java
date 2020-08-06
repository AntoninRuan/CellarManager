package fr.womax.cavemanager.utils.github.model.commit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.womax.cavemanager.utils.github.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GitCommit {

    private String sha;
    private String nodeId;
    private String url;
    private String htmlUrl;
    private JsonObject author;
    private JsonObject commiter;
    private JsonObject tree;
    private String messages;
    private JsonArray parents;
    private JsonObject verification;

    GitCommit(String sha, String nodeId, String url, String htmlUrl, JsonObject author, JsonObject commiter,
                     JsonObject tree, String messages, JsonArray parents, JsonObject verification) {
        this.sha = sha;
        this.nodeId = nodeId;
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.author = author;
        this.commiter = commiter;
        this.tree = tree;
        this.messages = messages;
        this.parents = parents;
        this.verification = verification;
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

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public JsonObject getAuthor() {
        return author;
    }

    public JsonObject getCommiter() {
        return commiter;
    }

    public Tree getTree() throws IOException {
        URL url = new URL(tree.get("url").getAsString());
        InputStreamReader reader = new InputStreamReader(url.openStream());
        return Tree.fromJson(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public String getMessages() {
        return messages;
    }

    public List<GitCommit> getParents() {
        List<GitCommit> parents = new ArrayList<>();
        for(JsonElement element : this.parents) {
            parents.add(GitCommit.fromJson(element.getAsJsonObject()));
        }
        return parents;
    }

    public JsonObject getVerification() {
        return verification;
    }

    public static GitCommit fromJson(JsonObject object) {
        String sha = JsonUtils.getAsString(object.get("sha"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String url = JsonUtils.getAsString(object.get("url"));
        String htmlUrl = JsonUtils.getAsString(object.get("html_url"));
        JsonObject author = object.get("author").getAsJsonObject();
        JsonObject committer = object.get("committer").getAsJsonObject();
        JsonObject tree = object.get("tree").getAsJsonObject();
        String message = JsonUtils.getAsString(object.get("message"));
        JsonArray parents = object.get("parents").getAsJsonArray();
        JsonObject verification = object.get("verification").getAsJsonObject();
        return new GitCommit(sha, nodeId, url, htmlUrl, author, committer, tree, message, parents, verification);
    }
}
