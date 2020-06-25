package fr.womax.cavemanager.model;

import com.google.gson.JsonObject;
import fr.womax.cavemanager.MainApp;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Antonin Ruan
 */
public class Spot {

    private ObjectProperty<Bottle> bottle;
    private int row, column;
    private BooleanProperty highlighted;

    protected Spot(int row, int column) {
        this(null , row, column);
    }

    protected Spot(Bottle bottle, int row, int column) {
        this.bottle = new SimpleObjectProperty <>(bottle);
        this.row = row;
        this.column = column;
        highlighted = new SimpleBooleanProperty(false);
        MainApp.getSpots().add(this);
    }

    public int getId() {
        return row * 100 + column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Bottle getBottle() {
        return bottle.getValue();
    }

    public void setBottle(Bottle bottle) {
        this.bottle.setValue(bottle);
    }

    public ObjectProperty<Bottle> bottleProperty() {
        return bottle;
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
        return bottle.getValue() == null;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", (row * 100) + column);
        object.addProperty("empty", bottle.getValue() == null);
        if(bottle.getValue() != null)
            object.addProperty("bottle", bottle.getValue().getId());
        return object;
    }

    public static Spot fromJson(JsonObject object) {
        Bottle bottle;
        if(!object.get("empty").getAsBoolean()) {
            bottle = MainApp.getBottles().get(object.get("bottle").getAsInt());
        } else
            bottle = null;
        int id = object.get("id").getAsInt();
        int row = id / 100;
        int column = id - (row * 100);
        return new Spot(bottle, row, column);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

}
