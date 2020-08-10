package fr.antoninruan.cellarmanager.model;

import fr.antoninruan.cellarmanager.MainApp;

/**
 * @author Antonin Ruan
 */
public class CompartmentInfo {

    private String name;
    private int raw;
    private int column;
    private boolean before;

    public CompartmentInfo(String name, int raw, int column, boolean before) {
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
            Compartment compartment = MainApp.getCompartement(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
            for(Spot[] spotColumn : compartment.getSpots()) {
                for(Spot spot : spotColumn) {
                    MainApp.getSpots().remove(spot);
                }
            }
            MainApp.getCompartements().remove(compartment.getId());
        }

        Compartment compartment = new Compartment(name, raw, column, index);
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
    }

}
