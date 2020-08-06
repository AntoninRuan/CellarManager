package fr.antoninruan.cellarmanager.utils.javafx;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.layout.Region;

/**
 * @author Antonin Ruan
 */
public class MaxSizedContextMenu extends ContextMenu {

    public MaxSizedContextMenu() {
        addEventHandler(Menu.ON_SHOWING, e -> {
            Node content = getSkin().getNode();
            if (content instanceof Region) {
                ((Region) content).setMaxHeight(getMaxHeight());
            }
        });
    }
}
