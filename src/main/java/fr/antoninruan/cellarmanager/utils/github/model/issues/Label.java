package fr.antoninruan.cellarmanager.utils.github.model.issues;

import com.google.gson.JsonObject;
import fr.antoninruan.cellarmanager.utils.JsonUtils;

public class Label {

    private int id;
    private String nodeId;
    private String url;
    private String name;
    private String description;
    private String color;
    private boolean isDefault;

    public Label(int id, String nodeId, String url, String name, String description, String color, boolean isDefault) {
        this.id = id;
        this.nodeId = nodeId;
        this.url = url;
        this.name = name;
        this.description = description;
        this.color = color;
        this.isDefault = isDefault;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public static Label fromJson(JsonObject object) {
        int id = JsonUtils.getAsInt(object.get("id"));
        String nodeId = JsonUtils.getAsString(object.get("node_id"));
        String url = JsonUtils.getAsString(object.get("url"));
        String name = JsonUtils.getAsString(object.get("name"));
        String descritpion = JsonUtils.getAsString(object.get("description"));
        String color = JsonUtils.getAsString(object.get("color"));
        boolean isDefault = JsonUtils.getAsBoolean(object.get("default"));
        return new Label(id, nodeId, url, name, descritpion, color, isDefault);
    }

}
