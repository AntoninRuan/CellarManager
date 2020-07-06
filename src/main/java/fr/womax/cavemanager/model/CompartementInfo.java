package fr.womax.cavemanager.model;

import fr.womax.cavemanager.MainApp;
import javafx.application.Platform;

/**
 * @author Antonin Ruan
 */
public class CompartementInfo {

    private String name;
    private int raw;
    private int column;
    private boolean before;

    public CompartementInfo(String name, int raw, int column, boolean before) {
        this.name = name;
        this.raw = raw;
        this.column = column;
        this.before = before;
    }

    public void createCompartement() {
        int index;
        if(MainApp.getCompartements() == null  || MainApp.getCompartements().isEmpty()) {
            index = 0;
        } else {
            index = MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed();
            if(!before) {
                index ++;
            }
        }

        if(before) {
            Compartement compartement = MainApp.getCompartement(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
            for(Spot[] spotColumn : compartement.getSpots()) {
                for(Spot spot : spotColumn) {
                    MainApp.getSpots().remove(spot);
                }
            }
            MainApp.getCompartements().remove(compartement.getId());
        }

        Compartement compartement = new Compartement(name, raw, column, index);
        if(!before) {
            for(Compartement c : MainApp.getCompartements().values()) {
                if(c.getIndex() >= index) {
                    c.setIndex(c.getIndex() + 1);
                }
            }
        }
        MainApp.getCompartements().put(compartement.getId(), compartement);
        if(MainApp.getCompartementDisplayController() != null)
            MainApp.getCompartementDisplayController().setCurrentCompartementDisplayed(index);
    }

}
