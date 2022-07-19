package fr.antoninruan.cellarmanager.utils.github.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.antoninruan.cellarmanager.utils.JsonUtils;

public class License {

    private String key;
    private String name;
    private String spdxId;
    private String url;
    private String nodeId;
    private String htmlUrl;
    private String description;
    private String implementation;
    private JsonArray permissions;
    private JsonArray conditions;
    private JsonArray limitations;
    private String body;
    private boolean featured;

    private License(String key, String name, String spdxId, String url, String nodeId, String htmlUrl, String description, String implementation,
                   JsonArray permissions, JsonArray conditions, JsonArray limitations, String body, boolean featured) {
        this.key = key;
        this.name = name;
        this.spdxId = spdxId;
        this.url = url;
        this.nodeId = nodeId;
        this.htmlUrl = htmlUrl;
        this.description = description;
        this.implementation = implementation;
        this.permissions = permissions;
        this.conditions = conditions;
        this.limitations = limitations;
        this.body = body;
        this.featured = featured;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSpdxId() {
        return spdxId;
    }

    public String getUrl() {
        return url;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getImplementation() {
        return implementation;
    }

    public JsonArray getPermissions() {
        return permissions;
    }

    public JsonArray getConditions() {
        return conditions;
    }

    public JsonArray getLimitations() {
        return limitations;
    }

    public String getBody() {
        return body;
    }

    public boolean isFeatured() {
        return featured;
    }

    public static License fromJson(JsonObject object) {
        String key = JsonUtils.getAsString(object.get("key"));
        String name = JsonUtils.getAsString(object.get("name"));
        String spdxId = JsonUtils.getAsString(object.get("spdx_id"));
        String url = JsonUtils.getAsString(object.get("url"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String htmlUrl = JsonUtils.getAsString(object.get("html_url"));
        String description = JsonUtils.getAsString(object.get("description"));
        String implementation = JsonUtils.getAsString(object.get("implementation"));
        JsonArray permissions = object.get("permissions").getAsJsonArray();
        JsonArray conditions = object.get("conditions").getAsJsonArray();
        JsonArray limitations = object.get("limitations").getAsJsonArray();
        String body = JsonUtils.getAsString(object.get("body"));
        boolean featured = JsonUtils.getAsBoolean(object.get("featured"));
        return new License(key, name, spdxId, url, nodeId, htmlUrl, description, implementation, permissions, conditions, limitations, body, featured);
    }

}
