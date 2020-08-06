package fr.womax.cavemanager.utils.github.model;

import com.google.gson.JsonObject;

public class Tag {

    private String name;
    private String zipballUrl;
    private String tarballUrl;
    private String commitUrl;
    private String nodeId;

    Tag(String name, String zipballUrl, String tarballUrl, String commitUrl, String nodeId) {
        this.name = name;
        this.zipballUrl = zipballUrl;
        this.tarballUrl = tarballUrl;
        this.commitUrl = commitUrl;
        this.nodeId = nodeId;
    }

    public static Tag fromJson(JsonObject object) {
        String name = object.get("name").getAsString();
        String zipballUrl = object.get("zipball_url").getAsString();
        String tarballUrl = object.get("tarballUrl").getAsString();
        String commitSha = object.get("commit").getAsJsonObject().get("sha").getAsString();
        String commitUrl = object.get("commit").getAsJsonObject().get("url").getAsString();
        String nodeId = object.get("node_id").getAsString();
        return new Tag(name, zipballUrl, tarballUrl, commitUrl, nodeId);
    }

}
