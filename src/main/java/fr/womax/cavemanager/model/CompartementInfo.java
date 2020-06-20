package fr.womax.cavemanager.model;

import fr.womax.cavemanager.MainApp;

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

        MainApp.getCompartements().put(index, new Compartement(name, raw, column));
        if(MainApp.getCompartementDisplayController() != null)
            MainApp.getCompartementDisplayController().setCurrentCompartementDisplayed(index);
    }

}
