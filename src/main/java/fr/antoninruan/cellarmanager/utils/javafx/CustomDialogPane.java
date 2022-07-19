package fr.antoninruan.cellarmanager.utils.javafx;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import fr.antoninruan.cellarmanager.utils.PreferencesManager;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;

public class CustomDialogPane extends DialogPane {

    @Override
    protected Node createDetailsButton() {
        final Hyperlink detailsButton = new Hyperlink();
        final String moreText = PreferencesManager.getLangBundle().getString("show_details"); //$NON-NLS-1$
        final String lessText = PreferencesManager.getLangBundle().getString("hide_details"); //$NON-NLS-1$

        InvalidationListener expandedListener = o -> {
            final boolean isExpanded = isExpanded();
            detailsButton.setText(isExpanded ? lessText : moreText);
            detailsButton.getStyleClass().setAll("details-button", (isExpanded ? "less" : "more")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        };

        // we call the listener immediately to ensure the state is correct at start up
        expandedListener.invalidated(null);
        expandedProperty().addListener(expandedListener);

        detailsButton.setOnAction(ae -> setExpanded(!isExpanded()));
        return detailsButton;
    }
}
