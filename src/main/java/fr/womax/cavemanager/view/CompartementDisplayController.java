package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Compartement;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.model.WineType;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Saver;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Antonin Ruan
 */
public class CompartementDisplayController {

    @FXML
    private VBox layout;

    @FXML
    private VBox vBox;

    @FXML
    private Label name;

    @FXML
    private Pagination pagination;

    @FXML
    private GridPane compartementDisplay;

    private Image spotFill = new Image(this.getClass().getClassLoader().getResource("img/spot_fill.png").toString());
    private Image spotEmpty = new Image(this.getClass().getClassLoader().getResource("img/spot_empty.png").toString());
    private Image spotAdd = new Image(this.getClass().getClassLoader().getResource("img/spot_add.png").toString());

    private Image spotRed = new Image(this.getClass().getClassLoader().getResource("img/spot_red.png").toString());
    private Image spotRose = new Image(this.getClass().getClassLoader().getResource("img/spot_rose.png").toString());
    private Image spotChampagne = new Image(this.getClass().getClassLoader().getResource("img/spot_champagne.png").toString());
    private Image spotWhite = new Image(this.getClass().getClassLoader().getResource("img/spot_white.png").toString());

    private Image hover = new Image(this.getClass().getClassLoader().getResource("img/hover.png").toString());
    private Image highlight = new Image(this.getClass().getClassLoader().getResource("img/highlight.png").toString());

    @FXML
    private void initialize() {
        pagination.setPageCount(MainApp.getCompartements().size());
        MainApp.getCompartements().addListener((MapChangeListener <? super Integer, ? super Compartement>) c -> {
            pagination.setPageCount(MainApp.getCompartements().size());
        });
        ContextMenu contextMenu = new ContextMenu();
        pagination.setPageFactory((index) -> {

            if(!MainApp.getCompartements().isEmpty()) {
                Compartement toDisplay = MainApp.getCompartements().get(index);
                name.setText(toDisplay.getName());

                Spot[][] spots = toDisplay.getSpots();

                compartementDisplay.getChildren().clear();
                compartementDisplay.getColumnConstraints().clear();
                compartementDisplay.getRowConstraints().clear();

                for(int i = 0; i < toDisplay.getColumn(); i++){
                    compartementDisplay.getColumnConstraints().add(new ColumnConstraints(64));
                }

                for(int i = 0; i < toDisplay.getRow(); i ++) {
                    compartementDisplay.getRowConstraints().add(new RowConstraints(64));
                    for(int j = 0; j < toDisplay.getColumn(); j++) {
                        Spot spot = spots[i][j];
                        StackPane stackPane = new StackPane();
                        stackPane.setPrefSize(64, 64);
                        stackPane.setCursor(Cursor.HAND);

                        ImageView hoverView = new ImageView(hover);
                        hoverView.setFitWidth(64);
                        hoverView.setFitHeight(64);
                        hoverView.setVisible(false);
                        hoverView.setTranslateY(1);

                        ImageView highlightView = new ImageView(highlight);
                        highlightView.setFitHeight(64);
                        highlightView.setFitWidth(64);
                        highlightView.setVisible(false);

                        ImageView imageView = new ImageView();

                        stackPane.getChildren().addAll(imageView, highlightView, hoverView);

                        WineTypeChangeListener changeListener = new WineTypeChangeListener(imageView);

                        if(spot.isEmpty()) {
                            imageView.setImage(spotEmpty);
                        }
                        else{
                            if(spot.isHighlighted()) {
                                highlightView.setVisible(true);
                            } else {
//                                imageView.setImage(spotFill);
                                renderBottle(spot, imageView, changeListener);
                            }
                        }

                        spot.bottleProperty().addListener((observable, oldValue, newValue) -> {
                            if(newValue == null) {
                                imageView.setImage(spotEmpty);
                            } else {
                                renderBottle(spot, imageView, changeListener);
                            }
                        });

                        stackPane.hoverProperty().addListener((observable, oldValue, newValue) -> {
                            if(spot.isEmpty()) {
                                if(newValue) {
                                    imageView.setImage(spotAdd);
                                }
                                else {
                                    imageView.setImage(spotEmpty);
                                }
                            } else {
                                hoverView.setVisible(newValue);
                            }
                        });

                        stackPane.setOnMouseClicked(event -> {

                            contextMenu.hide();
                            contextMenu.getItems().clear();

                            if(spot.isEmpty()) {

                                if(event.getButton() == MouseButton.PRIMARY) {

                                    Optional <Bottle> result = DialogUtils.chooseBottle(true);

                                    result.ifPresent(bottle -> {
                                        spot.setBottle(bottle);
                                        spot.getBottle().typeProperty().addListener(changeListener);
                                        switch (bottle.getType()) {
                                            case ROSE:
                                                imageView.setImage(spotRose);
                                                break;
                                            case BLANC:
                                                imageView.setImage(spotWhite);
                                                break;
                                            case ROUGE:
                                                imageView.setImage(spotRed);
                                                break;
                                            case CHAMPAGNE:
                                                imageView.setImage(spotChampagne);
                                                break;
                                            case AUTRES:
                                                imageView.setImage(spotFill);
                                                break;
                                        }
                                        BottleFilter.research();
                                        Saver.doChange();
                                    });

                                }

                            } else {

                                if (event.getButton() == MouseButton.PRIMARY) {

                                    MainApp.getController().showBottleDetails(spot);

                                } else if (event.getButton() == MouseButton.SECONDARY) {

                                    MenuItem show = new MenuItem("Afficher");
                                    show.setOnAction(event1 -> {
                                        MainApp.getController().showBottleDetails(spot);
                                    });

                                    MenuItem modify = new MenuItem("Modifier");
                                    modify.setOnAction(event1 -> {
                                        Optional<Bottle> result = DialogUtils.chooseBottle(true);
                                        result.ifPresent(bottle -> {
                                            spot.getBottle().typeProperty().removeListener(changeListener);
                                            spot.setBottle(bottle);
//                                            renderBottle(spot, imageView, changeListener);
                                            BottleFilter.research();
                                            MainApp.getController().showBottleDetails(spot);
                                            Saver.doChange();
                                        });
                                    });

                                    MenuItem remove = new MenuItem("Enlever");
                                    remove.setOnAction(event1 -> {
                                        MainApp.getSpots().remove(spot);
                                        spot.getBottle().typeProperty().removeListener(changeListener);
                                        spot.setBottle(null);
                                        MainApp.getSpots().add(spot);
                                        hoverView.setVisible(false);
                                        highlightView.setVisible(false);
                                        imageView.setImage(spotEmpty);
                                    });

                                    contextMenu.getItems().addAll(show, modify, remove);
                                    contextMenu.show(imageView, event.getScreenX(), event.getScreenY());

                                } else if(event.getButton() == MouseButton.MIDDLE) {
                                    MainApp.getSpots().remove(spot);
                                    spot.setBottle(null);
                                    MainApp.getSpots().add(spot);
                                    hoverView.setVisible(false);
                                    highlightView.setVisible(false);
                                    imageView.setImage(spotEmpty);
                                }

                            }

                        });

                        stackPane.setOnDragDetected(event -> {
                            if(spot.getBottle() == null)
                                return;

                            Dragboard dragboard = stackPane.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent content = new ClipboardContent();
                            content.putString(Integer.toString(spot.getId()));
                            dragboard.setContent(content);
                        });

                        stackPane.setOnDragOver(event -> {
                            if(event.getGestureSource() != stackPane && event.getDragboard().hasString()) {
                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                            event.consume();
                        });

                        stackPane.setOnDragEntered(event -> {
                            if(event.getGestureSource() != stackPane && event.getDragboard().hasString()) {
                                stackPane.setOpacity(.3);
                            }
                        });

                        stackPane.setOnDragExited(event -> {
                            if(event.getGestureSource() != stackPane && event.getDragboard().hasString()) {
                                stackPane.setOpacity(1);
                            }
                        });

                        stackPane.setOnDragDropped(event -> {
                            if(spot.getBottle() != null)
                                return;

                            Dragboard db = event.getDragboard();
                            boolean success;
                            if(db.hasString()) {
                                int id = Integer.parseInt(db.getString());
                                int row = id / 100;
                                int column = id - (row * 100);

                                Spot src = spots[row][column];

                                spot.setBottle(src.getBottle());

                                src.setBottle(null);

                            }
                        });

                        imageView.setFitWidth(64);
                        imageView.setFitHeight(64);

                        spot.highlightedProperty().addListener((observable, oldValue, newValue) -> {
                            highlightView.setVisible(newValue);
                        });

                        compartementDisplay.add(stackPane, j, i);

                    }
                }

            }

            return new AnchorPane();
        });
        pagination.setCurrentPageIndex(0);

        final LocalDateTime[] lastClick = {LocalDateTime.now()};
        final boolean[] doubleClick = {false};
        name.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {

                Duration delta = Duration.between(lastClick[0], LocalDateTime.now());

                if(delta.toMillis() < 500 && !doubleClick[0]) {
                    doubleClick[0] = true;

                    HBox modifyNameHbox = new HBox();
                    modifyNameHbox.setPrefWidth(compartementDisplay.getWidth());
                    modifyNameHbox.setFillHeight(true);
                    modifyNameHbox.setSpacing(5);

                    TextField modifyNameTextField = new TextField(MainApp.getCompartements().get(getCurrentCompartementDisplayed()).getName());
                    modifyNameTextField.setFont(Font.font(Font.getDefault().getFamily(), 16));
                    modifyNameTextField.setPromptText("Nom");
                    modifyNameTextField.setAlignment(Pos.CENTER);
                    modifyNameTextField.setPrefWidth(compartementDisplay.getWidth() - modifyNameTextField.getHeight());
                    modifyNameTextField.setPrefHeight(name.getHeight());

                    ImageView view = new ImageView(new Image(CompartementDisplayController.class.getClassLoader().getResource("img/check.png").toString()));
                    view.setPreserveRatio(true);
                    view.setFitHeight(name.getHeight());

                    Button okButton = new Button("", view);
                    okButton.setBackground(new Background(new BackgroundFill(Color.valueOf("#264653"), new CornerRadii(0), new Insets(0))));

                    okButton.setOnAction(event1 -> {
                        MainApp.getCompartements().get(getCurrentCompartementDisplayed()).setName(modifyNameTextField.getText());
                        name.setText(modifyNameTextField.getText());

                        vBox.getChildren().remove(modifyNameHbox);
                        vBox.getChildren().add(0, name);
                    });

                    modifyNameHbox.getChildren().addAll(modifyNameTextField, okButton);

                    vBox.getChildren().remove(name);
                    vBox.getChildren().add(0, modifyNameHbox);

                    modifyNameTextField.requestFocus();

                    modifyNameTextField.setOnKeyPressed(event1 -> {
                        if(event1.getCode() == KeyCode.ENTER) {
                            okButton.fire();
                        }
                    });

                } else
                    doubleClick[0] = false;

                lastClick[0] = LocalDateTime.now();
            }
        });
    }

    private void renderBottle(Spot spot, ImageView imageView, WineTypeChangeListener changeListener) {
        spot.getBottle().typeProperty().addListener(changeListener);
        switch (spot.getBottle().getType()) {
            case ROSE:
                imageView.setImage(spotRose);
                break;
            case BLANC:
                imageView.setImage(spotWhite);
                break;
            case ROUGE:
                imageView.setImage(spotRed);
                break;
            case CHAMPAGNE:
                imageView.setImage(spotChampagne);
                break;
            case AUTRES:
                imageView.setImage(spotFill);
                break;
        }
    }

    public void handleLeft() {
        int currentPage = pagination.getCurrentPageIndex();
        int newPage = currentPage - 1;
        if(newPage < 0)
            newPage = pagination.getPageCount() - 1;
        pagination.setCurrentPageIndex(newPage);
    }

    public void handleRight() {
        int currentPage = pagination.getCurrentPageIndex();
        int newPage = currentPage + 1;
        if(newPage > pagination.getPageCount() - 1)
            newPage = 0;
        pagination.setCurrentPageIndex(newPage);
    }

    public int getCurrentCompartementDisplayed() {
        return pagination.getCurrentPageIndex();
    }

    public void setCurrentCompartementDisplayed(int index) {
        if(index == pagination.getCurrentPageIndex())
            pagination.getPageFactory().call(index);
        pagination.setCurrentPageIndex(index);
    }

    private class WineTypeChangeListener implements ChangeListener <WineType> {

        private ImageView imageView;

        public WineTypeChangeListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void changed(ObservableValue <? extends WineType> observable, WineType oldValue, WineType newValue) {
            switch (newValue) {
                case ROSE:
                    imageView.setImage(spotRose);
                    break;
                case BLANC:
                    imageView.setImage(spotWhite);
                    break;
                case ROUGE:
                    imageView.setImage(spotRed);
                    break;
                case CHAMPAGNE:
                    imageView.setImage(spotChampagne);
                    break;
                case AUTRES:
                    imageView.setImage(spotFill);
                    break;
            }
        }
    }

}
