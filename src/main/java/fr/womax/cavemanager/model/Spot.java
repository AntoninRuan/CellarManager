package fr.womax.cavemanager.model;

import com.google.gson.JsonObject;
import fr.womax.cavemanager.MainApp;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author Antonin Ruan
 */
public class Spot {

    private Bottle bottle;
    private int row, column;
    private BooleanProperty highlighted;

    protected Spot(int row, int column) {
        this(null , row, column);
    }

    protected Spot(Bottle bottle, int row, int column) {
        this.bottle = bottle;
        this.row = row;
        this.column = column;
        highlighted = new SimpleBooleanProperty(false);
        MainApp.getSpots().add(this);
    }

    protected int getId() {
        return row * 100 + column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Bottle getBottle() {
        return bottle;
    }

    public void setBottle(Bottle bottle) {
        this.bottle = bottle;
    }

    public boolean isHighlighted() {
        return highlighted.get();
    }

    public BooleanProperty highlightedProperty() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted.set(highlighted);
    }

    public boolean isEmpty() {
        return bottle == null;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", (row * 100) + column);
        object.addProperty("empty", bottle == null);
        if(bottle != null)
            object.add("bottle", bottle.toJson());
        return object;
    }

    public static Spot fromJson(JsonObject object) {
        Bottle bottle;
        if(!object.get("empty").getAsBoolean()) {
            bottle = Bottle.fromJson(object.get("bottle").getAsJsonObject());
        } else
            bottle = null;
        int id = object.get("id").getAsInt();
        int raw = id / 100;
        int column = id - (raw * 100);
        return new Spot(bottle, raw, column);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

}
