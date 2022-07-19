package fr.antoninruan.cellarmanager.view;

import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.model.Compartment;
import fr.antoninruan.cellarmanager.model.Spot;
import fr.antoninruan.cellarmanager.utils.PreferencesManager;
import fr.antoninruan.cellarmanager.utils.javafx.CustomSpinnerValueFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;

public class CompartmentEditController {

    @FXML
    private HBox container;

    @FXML
    private ScrollPane scrollPane;

    private Stage stage;

    private ObservableMap<VBox, Compartment> map = FXCollections.observableHashMap();
    private ObservableMap<Compartment, Pair<Spinner<Integer>, Spinner<Integer>>> editSpinners = FXCollections.observableHashMap();
    private ObservableMap<Compartment, TextField> editTextField = FXCollections.observableHashMap();

    private Timer movingTimer = new Timer();
    private boolean moving = false;

    @FXML
    private void initialize() {

        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setOpacity(0);

        separator.setOnDragOver(event -> {
            if(event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        separator.setOnDragEntered(event -> {
            if(event.getDragboard().hasString()) {
                separator.setOpacity(.5);
            }
        });

        separator.setOnDragExited(event -> {
            if(event.getDragboard().hasString()) {
                separator.setOpacity(0);
            }
        });

        separator.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasString()) {
                int startIndex = Integer.parseInt(dragboard.getString());
                int currentIndex = getIndex(separator);

                ObservableList<Node> copy = FXCollections.observableArrayList(container.getChildren());

                if(startIndex < currentIndex) {
                    Collections.rotate(copy.subList(startIndex, currentIndex + 1), -1);
                    Collections.rotate(copy.subList(startIndex, currentIndex + 1), -1);
                } else {
                    Collections.rotate(copy.subList(currentIndex, startIndex + 1), 1);
                    Collections.rotate(copy.subList(currentIndex, startIndex + 1), 1);
                }

//                Collections.rotate(copy.subList(Math.min(startIndex, currentIndex), Math.max(startIndex, currentIndex)+1),
//                        (startIndex < currentIndex) ? -1 : 1);

                container.getChildren().setAll(copy);

            }
        });

        container.getChildren().add(separator);

        for(int index = 0; index < MainApp.getCompartements().size(); index ++) {
            createComparmentDisplay(container, MainApp.getCompartement(index));
        }
        Platform.runLater(container::requestFocus);
    }

    private void slide(ScrollPane scrollPane, double value, boolean activate) {
        movingTimer.cancel();
        movingTimer = new Timer();

        if(activate) {
            movingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("moved " + value);
                    scrollPane.setHvalue(scrollPane.getHvalue() + value);
                    Platform.runLater(() -> {
                    });
                }
            }, 0, 50);
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void createComparmentDisplay(HBox container, Compartment compartment) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(5));
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getStyleClass().add("compartment");

        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setOpacity(0);

        TextField textField = new TextField(compartment.getName());
        textField.setAlignment(Pos.CENTER);
        textField.getStyleClass().add("name");

        Label row = new Label(PreferencesManager.getLangBundle().getString("row"));
        row.getStyleClass().add("info");
        Label column = new Label(PreferencesManager.getLangBundle().getString("column"));
        column.getStyleClass().add("info");

        final Spinner <Integer> rowSpinner = new Spinner <>(new CustomSpinnerValueFactory(1, 40, compartment.getRow()));
        ((CustomSpinnerValueFactory) rowSpinner.getValueFactory()).setSpinner(rowSpinner);
        rowSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                rowSpinner.increment(0);
            }
        });
        rowSpinner.setEditable(true);
        rowSpinner.setPrefWidth(100);

        final Spinner<Integer> columnSpinner = new Spinner <>(new CustomSpinnerValueFactory(1, 10, compartment.getColumn()));
        ((CustomSpinnerValueFactory) columnSpinner.getValueFactory()).setSpinner(columnSpinner);
        columnSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                columnSpinner.increment(0);
            }
        });
        columnSpinner.setEditable(true);
        columnSpinner.setPrefWidth(100);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        for(ColumnConstraints columnConstraints : gridPane.getColumnConstraints()) {
            columnConstraints.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }

        gridPane.add(row, 0, 0);
        gridPane.add(rowSpinner, 1, 0);
        gridPane.add(column, 0, 1);
        gridPane.add(columnSpinner, 1, 1);

        vBox.getChildren().addAll(textField, gridPane);

        map.put(vBox, compartment);
        editTextField.put(compartment, textField);
        editSpinners.put(compartment, new Pair <>(rowSpinner, columnSpinner));

        vBox.setOnDragDetected(event -> {
            if(event.getButton() == MouseButton.SECONDARY || event.getButton() == MouseButton.MIDDLE) {
                return;
            }

            Dragboard dragboard = vBox.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(getIndex(vBox)));
            dragboard.setContent(content);
        });

        separator.setOnDragOver(event -> {
            if(event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        separator.setOnDragEntered(event -> {
            if(event.getDragboard().hasString()) {
                separator.setOpacity(.5);
            }
        });

        separator.setOnDragExited(event -> {
            if(event.getDragboard().hasString()) {
                separator.setOpacity(0);
            }
        });

        separator.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasString()) {
                int startIndex = Integer.parseInt(dragboard.getString());
                int currentIndex = getIndex(separator);

                ObservableList<Node> copy = FXCollections.observableArrayList(container.getChildren());

                if(startIndex < currentIndex) {
                    Collections.rotate(copy.subList(startIndex, currentIndex + 1), -1);
                    Collections.rotate(copy.subList(startIndex, currentIndex + 1), -1);
                } else {
                    Collections.rotate(copy.subList(currentIndex, startIndex + 1), 1);
                    Collections.rotate(copy.subList(currentIndex, startIndex + 1), 1);
                }

//                Collections.rotate(copy.subList(Math.min(startIndex, currentIndex), Math.max(startIndex, currentIndex)+1),
//                        (startIndex < currentIndex) ? -1 : 1);

                container.getChildren().setAll(copy);

            }
        });

        container.getChildren().addAll(vBox, separator);
    }

    public void handleOk() {
        for(VBox vBox : map.keySet()) {
            int index = (getIndex(vBox) - 1) / 2;
            map.get(vBox).setIndex(index);
        }

        for(Compartment compartment : editSpinners.keySet()) {

            Pair<Spinner<Integer>, Spinner<Integer>> spinners = editSpinners.get(compartment);
            Spinner<Integer> rowSpinner = spinners.getKey();
            Spinner<Integer> columnSpinner = spinners.getValue();

            compartment.setSize(rowSpinner.getValue(), columnSpinner.getValue());
            compartment.setName(editTextField.get(compartment).getText());
        }

        if(MainApp.getCompartementDisplayController() != null)
            MainApp.getCompartementDisplayController().setCurrentCompartementDisplayed(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());

        movingTimer.cancel();
        stage.close();
    }

    public void handleCancel() {
        movingTimer.cancel();
        stage.close();
    }

    private int getIndex(Node node) {
        int index = -1;
        for(int i = 0; i < container.getChildren().size(); i ++) {
            if(container.getChildren().get(i).equals(node)) {
                index = i;
                break;
            }
        }
        return index;
    }

}
