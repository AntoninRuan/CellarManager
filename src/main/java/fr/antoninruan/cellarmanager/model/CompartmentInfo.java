package fr.antoninruan.cellarmanager.model;

import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.utils.Saver;

/**
 * @author Antonin Ruan
 */
public class CompartmentInfo {

    private String name;
    private int row;
    private int column;
    private boolean before;
    private int index;

    public CompartmentInfo(String name, int row, int column, boolean before) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.before = before;
        this.index = -1;
    }

    public CompartmentInfo(String name, int row, int column, int index) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.before = false;
        this.index = index;
    }

    public Compartment createCompartment() {
        if(index == -1) {
            if(MainApp.getCompartements() == null  || MainApp.getCompartements().isEmpty()) {
                index = 0;
            } else {
                index = MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed();
                if(!before) {
                    index ++;
                }
            }
        }

        if(before && !MainApp.getCompartements().isEmpty()) {
            Compartment compartment = MainApp.getCompartement(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
            for(Spot[] spotColumn : compartment.getSpots()) {
                for(Spot spot : spotColumn) {
                    MainApp.getSpots().remove(spot);
                }
            }
            MainApp.getCompartements().remove(compartment.getId());
        }

        Compartment compartment = new Compartment(name, row, column, index);
        if(!before) {
            for(Compartment c : MainApp.getCompartements().values()) {
                if(c.getIndex() >= index) {
                    c.setIndex(c.getIndex() + 1);
                }
            }
        }
        MainApp.getCompartements().put(compartment.getId(), compartment);
        if(MainApp.getCompartementDisplayController() != null)
            MainApp.getCompartementDisplayController().setCurrentCompartementDisplayed(index);

        Saver.doChange();
        return compartment;
    }

}
