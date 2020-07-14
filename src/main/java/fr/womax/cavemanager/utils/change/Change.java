package fr.womax.cavemanager.utils.change;

import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.utils.Saver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Antonin Ruan
 */
public class Change {

    private static final ObservableList <Change> changeHistory = FXCollections.observableArrayList();

    private ChangeType type;
    private Spot src;
    private Spot dest;
    private Bottle oldBottle;

    public Change(ChangeType type, Spot src, Spot dest, Bottle oldBottle) {
        this.type = type;
        this.src = src;
        this.dest = type == ChangeType.BOTTLE_MOVED ? dest : null;
        this.oldBottle = oldBottle;
        if(changeHistory.size() > 10)
            changeHistory.remove(0);
        changeHistory.add(this);
    }

    public void undo() {
        switch (type) {

            case SPOT_FILLED:
                src.setBottle(null);
                break;

            case BOTTLE_MOVED:
                src.setBottle(oldBottle);
                dest.setBottle(null);
                break;

            case SPOT_EMPTIED:

            case BOTTLE_CHANGED:
                src.setBottle(oldBottle);
                break;

        }
        Saver.doChange();
    }

    public static ObservableList <Change> getChangeHistory() {
        return changeHistory;
    }

    public enum ChangeType {

        SPOT_FILLED,
        SPOT_EMPTIED,
        BOTTLE_CHANGED,
        BOTTLE_MOVED;

    }

}
