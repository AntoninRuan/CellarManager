package fr.womax.cavemanager.model;

import fr.womax.cavemanager.MainApp;

/**
 * @author Antonin Ruan
 */
public class CompartementInfo {

    private int raw;
    private int column;
    private boolean before;

    public CompartementInfo(int raw, int column, boolean before) {
        this.raw = raw;
        this.column = column;
        this.before = before;
    }

    public void createCompartement(MainApp mainApp) {
        int index;
        if(MainApp.getCompartements() == null  || MainApp.getCompartements().isEmpty()) {
            index = 0;
        } else {
            index = MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed();
            if(!before) {
                index ++;
            }
        }

        MainApp.getCompartements().add(index, new Compartement(raw, column));
        if(MainApp.getCompartementDisplayController() != null)
            MainApp.getCompartementDisplayController().setCurrentCompartementDisplayed(index);
    }

}
