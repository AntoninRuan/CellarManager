package fr.antoninruan.cellarmanager.view;

import fr.antoninruan.cellarmanager.utils.PreferencesManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Locale;

public class PreferencesController {

    @FXML
    private ChoiceBox<Locale> lang;

    @FXML
    private CheckBox checkUpdateAtStart;

    @FXML
    private CheckBox neverConnectOnGitHub;

    @FXML
    private Slider doubleClickDelay;

    @FXML
    private Label doubleClickDelayLabel;

    @FXML
    private Button applyButton;

    private BooleanProperty noChange = new SimpleBooleanProperty(true);

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        lang.getItems().add(Locale.FRENCH);
        lang.setConverter(new StringConverter<Locale>() {
            @Override
            public String toString(Locale locale) {
                return locale.getDisplayLanguage(locale).toUpperCase();
            }

            @Override
            public Locale fromString(String s) {
                return null;
            }
        });
        lang.setValue(PreferencesManager.getLang());
        lang.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != PreferencesManager.getLang() && noChange.getValue())
                noChange.setValue(false);
        });

        doubleClickDelay.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            doubleClickDelayLabel.setText("Délai du double clic (" + (newValue.intValue() * 50) + "ms)");
            if(newValue.intValue() * 50 != PreferencesManager.getDoubleClickDelay() && noChange.getValue()) {
                noChange.setValue(false);
            }
        });
        doubleClickDelay.setValue(PreferencesManager.getDoubleClickDelay() / 50);
        doubleClickDelayLabel.setText("Délai du double clic (" + PreferencesManager.getDoubleClickDelay() + "ms)");

        neverConnectOnGitHub.setSelected(PreferencesManager.isNeverConnectToGitHub());
        neverConnectOnGitHub.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != PreferencesManager.isNeverConnectToGitHub() && noChange.getValue())
                noChange.setValue(false);
        });

        checkUpdateAtStart.setSelected(PreferencesManager.doCheckUpdateAtStart());
        checkUpdateAtStart.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != PreferencesManager.doCheckUpdateAtStart() && noChange.getValue())
                noChange.setValue(false);
        });

        applyButton.disableProperty().bind(noChange);
    }

    @FXML
    public void handleOk() {
        handleApply();
        stage.close();
    }

    @FXML
    public void handleApply() {
        PreferencesManager.setCheckUpdateAtStart(checkUpdateAtStart.isSelected());
        PreferencesManager.setDoubleClickDelay((int) doubleClickDelay.getValue() * 50);
        PreferencesManager.setLang(lang.getValue());
        PreferencesManager.setNeverConnectToGitHub(neverConnectOnGitHub.isSelected());
    }

    @FXML
    public void handleClose() {
        stage.close();
    }

}
