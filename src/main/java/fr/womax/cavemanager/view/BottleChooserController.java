package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.BottleInfo;
import fr.womax.cavemanager.model.WineType;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Saver;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Optional;

/**
 * @author Antonin Ruan
 */
public class BottleChooserController {

    @FXML
    private TableView<Bottle> tableView;

    @FXML
    private TableColumn <Bottle, String> nameColumn;

    @FXML
    private TableColumn<Bottle, String> editionColumn;

    @FXML
    private TableColumn<Bottle, String> domainColumn;

    @FXML
    private TableColumn<Bottle, String> yearColumn;

    @FXML
    private TableColumn<Bottle, WineType> typeColumn;

    @FXML
    private TableColumn<Bottle, String> regionColumn;

    @FXML
    private TableColumn<Bottle, String> consumeYearColumn;

    @FXML
    private TableColumn<Bottle, String> countColumn;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<BottleFilter.SearchCriteria> criteriaChoiceBox;

    private ObservableList<Bottle> bottles = FXCollections.observableArrayList();

    private Stage dialogStage;
    private boolean okClicked = false;
    private Bottle selected;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {
        bottles.addAll(MainApp.getBottles().values());
        MainApp.getBottles().addListener((MapChangeListener <? super Integer, ? super Bottle>) change -> {
            if(change.wasAdded()) {
                bottles.add(change.getValueAdded());
            } else if (change.wasRemoved())
                bottles.remove(change.getValueRemoved());
        });

        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        editionColumn.setCellValueFactory(param -> param.getValue().editionProperty());
        domainColumn.setCellValueFactory(param -> param.getValue().domainProperty());
        yearColumn.setCellValueFactory(param -> param.getValue().yearProperty());
        consumeYearColumn.setCellValueFactory(param -> param.getValue().consumeYearProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().typeProperty());
        regionColumn.setCellValueFactory(param -> param.getValue().regionProperty());
        countColumn.setCellValueFactory(param -> param.getValue().countProperty());

        tableView.setItems(bottles);
        ContextMenu contextMenu = new ContextMenu();
        tableView.setOnMouseClicked(event -> {

            contextMenu.hide();

            if(event.getButton() == MouseButton.SECONDARY) {
                Bottle bottle = tableView.getSelectionModel().getSelectedItem();

                contextMenu.getItems().clear();

                MenuItem modify = new MenuItem("Modifier");
                modify.setOnAction(event1 -> {

                    Optional<BottleInfo> result = DialogUtils.addNewBottle(bottle);

                    result.ifPresent(bottleInfo -> {
                        bottleInfo.modifyBottle(bottle);
                        Saver.doChange();
                    });

                });

                MenuItem duplicate = new MenuItem("Dupliquer");
                duplicate.setOnAction(event1 -> {
                    BottleInfo bottleInfo = new BottleInfo(bottle.getName(), bottle.getRegion(), bottle.getDomain(),
                            bottle.getEdition(), bottle.getComment(), bottle.getYear(), bottle.getConsumeYear(), bottle.getType());

                    bottleInfo.createBottle();
                });

                MenuItem delete = new MenuItem("Supprimer");
                delete.setOnAction(event1 -> {
                    if(MainApp.hasBottle(bottle).size() != 0)
                        DialogUtils.bottlePresentInCave();
                    else
                        MainApp.getBottles().remove(bottle.getId());
                });

                contextMenu.getItems().addAll(modify, duplicate, new SeparatorMenuItem(), delete);
                contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
            }

        });
        tableView.setPlaceholder(new Label("Aucun résultat trouvé"));

        criteriaChoiceBox.getItems().setAll(BottleFilter.SearchCriteria.values());
        criteriaChoiceBox.setValue(BottleFilter.SearchCriteria.NAME);
        criteriaChoiceBox.setConverter(new StringConverter <BottleFilter.SearchCriteria>() {
            @Override
            public String toString(BottleFilter.SearchCriteria object) {
                return object.getName();
            }

            @Override
            public BottleFilter.SearchCriteria fromString(String string) {
                return BottleFilter.SearchCriteria.fromName(string);
            }
        });
        criteriaChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            BottleFilter.setCriteria(newValue);
            if(!searchField.getText().trim().isEmpty()) {
                ObservableList<Bottle> result = BottleFilter.researchInBottles();
                if(result != null && !result.isEmpty())
                    bottles.setAll(result);
                else
                    bottles.clear();
            }

        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.trim().isEmpty()) {
                bottles.setAll(BottleFilter.searchInBottles(newValue));
            } else {
                bottles.setAll(MainApp.getBottles().values());
            }
        });
    }

    @FXML
    private void handleNew() {
        Optional<BottleInfo> optionalBottleInfo = DialogUtils.addNewBottle(null);
        optionalBottleInfo.ifPresent(bottleInfo -> {
            Bottle bottle = bottleInfo.createBottle();
            tableView.getSelectionModel().select(bottle);
            Saver.doChange();
        });
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleOk() {
        this.okClicked = true;
        dialogStage.close();
    }

    public boolean isOkClicked() {
        this.selected = tableView.getSelectionModel().getSelectedItem();
        return okClicked;
    }

    public Bottle getSelected() {
        return selected;
    }

    public void setCancelable(boolean cancelable) {
        if(cancelable)
            cancelButton.setText("Annuler");
        else
            cancelButton.setText("Fermer");
    }

}
