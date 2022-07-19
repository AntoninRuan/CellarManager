package fr.antoninruan.cellarmanager.view;

import fr.antoninruan.cellarmanager.utils.BottleFilter;
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
    private Label title;
    @FXML
    private Label settingLanguage;
    @FXML
    private Label settingCheckUpdateAtStart;
    @FXML
    private Label settingNeverConnectToGitHub;
    @FXML
    private Label settingDoubleClickDelay;

    @FXML
    private ChoiceBox<Locale> lang;

    @FXML
    private CheckBox checkUpdateAtStart;

    @FXML
    private CheckBox neverConnectOnGitHub;

    @FXML
    private Slider doubleClickDelay;

    @FXML
    private ChoiceBox<BottleFilter.SearchCriteria> defaultSort;

    @FXML
    private Button applyButton;

    private BooleanProperty noChange = new SimpleBooleanProperty(true);

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        lang.getItems().addAll(Locale.FRENCH, Locale.ENGLISH);
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
            settingDoubleClickDelay.setText("Délai du double clic (" + (newValue.intValue() * 50) + "ms)");
            if(newValue.intValue() * 50 != PreferencesManager.getDoubleClickDelay() && noChange.getValue()) {
                noChange.setValue(false);
            }
        });
        doubleClickDelay.setValue(PreferencesManager.getDoubleClickDelay() / 50);
        settingDoubleClickDelay.setText("Délai du double clic (" + PreferencesManager.getDoubleClickDelay() + "ms)");

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

        defaultSort.getItems().setAll(BottleFilter.SearchCriteria.values());
        defaultSort.setValue(PreferencesManager.getDefaultSort());
        defaultSort.setConverter(new StringConverter <BottleFilter.SearchCriteria>() {
            @Override
            public String toString(BottleFilter.SearchCriteria object) {
                return PreferencesManager.getLangBundle().getString(object.getId());
            }

            @Override
            public BottleFilter.SearchCriteria fromString(String string) {
                return BottleFilter.SearchCriteria.fromId(string);
            }
        });
        defaultSort.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != PreferencesManager.getDefaultSort() && noChange.getValue())
                noChange.setValue(false);
        });

        applyButton.disableProperty().bind(noChange);
    }

    public void updateLang() {
        title.setText(PreferencesManager.getLangBundle().getString("settings"));
        settingLanguage.setText(PreferencesManager.getLangBundle().getString("setting_language"));
        settingCheckUpdateAtStart.setText(PreferencesManager.getLangBundle().getString("setting_check_update_at_start"));
        settingNeverConnectToGitHub.setText(PreferencesManager.getLangBundle().getString("setting_never_connect_to_github"));
        settingDoubleClickDelay.setText(PreferencesManager.getLangBundle().getString("setting_double_click_delay"));
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
        PreferencesManager.setDefaultSort(defaultSort.getValue());

        noChange.set(true);
    }

    @FXML
    public void handleClose() {
        stage.close();
    }

}
