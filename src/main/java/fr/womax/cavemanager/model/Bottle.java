package fr.womax.cavemanager.model;

import com.google.gson.JsonObject;
import fr.womax.cavemanager.MainApp;
import javafx.beans.property.*;

import java.util.Objects;

/**
 * @author Antonin Ruan
 */
public class Bottle {

    private int id;

    private StringProperty name;
    private StringProperty region;
    private StringProperty edition;
    private StringProperty domain;
    private String comment;
    private StringProperty year;
    private ObjectProperty <WineType> type;

    public Bottle(int id, String name, String region, String edition, String domain, String comment, int year, WineType type) {
        this.id = id;
        this.id = MainApp.nextBottleId();
        this.name = new SimpleStringProperty(name);
        this.region = new SimpleStringProperty(region);
        this.edition = new SimpleStringProperty(edition);
        this.domain = new SimpleStringProperty(domain);
        this.comment = comment;
        this.year = new SimpleStringProperty(String.valueOf(year));
        this.type = new SimpleObjectProperty <>(type);
    }


    public Bottle(String name, String region, String edition, String domain, String comment, int year, WineType type) {
        this.id = MainApp.nextBottleId();
        this.name = new SimpleStringProperty(name);
        this.region = new SimpleStringProperty(region);
        this.edition = new SimpleStringProperty(edition);
        this.domain = new SimpleStringProperty(domain);
        this.comment = comment;
        this.year = new SimpleStringProperty(String.valueOf(year));
        this.type = new SimpleObjectProperty <>(type);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getRegion() {
        return region.get();
    }

    public StringProperty regionProperty() {
        return region;
    }

    public String getEdition() {
        return edition.get();
    }

    public StringProperty editionProperty() {
        return edition;
    }

    public String getDomain() {
        return domain.get();
    }

    public StringProperty domainProperty() {
        return domain;
    }

    public String getComment() {
        return comment;
    }

    public int getYear() {
        return Integer.parseInt(year.get());
    }

    public StringProperty yearProperty() {
        return year;
    }

    public WineType getType() {
        return type.get();
    }

    public ObjectProperty <WineType> typeProperty() {
        return type;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setRegion(String region) {
        this.region.set(region);
    }

    public void setEdition(String edition) {
        this.edition.set(edition);
    }

    public void setDomain(String domain) {
        this.domain.set(domain);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public void setType(WineType type) {
        this.type.set(type);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name.getValue());
        object.addProperty("region", region.getValue());
        object.addProperty("edition", edition.getValue());
        object.addProperty("domain", domain.getValue());
        object.addProperty("comment", comment);
        object.addProperty("year", year.getValue());
        object.addProperty("type", type.getValue().toString());
        return object;
    }

    public static Bottle fromJson(int id, JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        String region = jsonObject.get("region").getAsString();
        String edition = jsonObject.get("edition").getAsString();
        String domain = jsonObject.get("domain").getAsString();
        String comment = jsonObject.get("comment").getAsString();
        int year = jsonObject.get("year").getAsInt();
        WineType type = WineType.valueOf(jsonObject.get("type").getAsString());
        return new Bottle(id, name, region, edition, domain, comment, year, type);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bottle bottle = (Bottle) o;
        return id == bottle.id &&
                name.equals(bottle.name) &&
                Objects.equals(region, bottle.region) &&
                Objects.equals(edition, bottle.edition) &&
                Objects.equals(domain, bottle.domain) &&
                Objects.equals(comment, bottle.comment) &&
                year.equals(bottle.year) &&
                type.equals(bottle.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, region, edition, domain, comment, year, type);
    }
}
