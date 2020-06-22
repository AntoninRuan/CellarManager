package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Compartement;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Saver;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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

                Image spotFill = new Image(this.getClass().getClassLoader().getResource("img/spot_fill.png").toString());
                Image spotEmpty = new Image(this.getClass().getClassLoader().getResource("img/spot_empty.png").toString());
                Image spotAdd = new Image(this.getClass().getClassLoader().getResource("img/spot_add.png").toString());
                Image spotFillHover = new Image(this.getClass().getClassLoader().getResource("img/spot_fill_hover.png").toString());
                Image spotHighlighted = new Image(this.getClass().getClassLoader().getResource("img/spot_highlighted.png").toString());

                for(int i = 0; i < toDisplay.getRow(); i ++) {
                    compartementDisplay.getRowConstraints().add(new RowConstraints(64));
                    for(int j = 0; j < toDisplay.getColumn(); j++) {
                        Spot spot = spots[i][j];
                        ImageView imageView = new ImageView();
                        imageView.setCursor(Cursor.HAND);

                        if(spot.isEmpty())
                            imageView.setImage(spotEmpty);
                        else{
                            if(spot.isHighlighted()) {
                                imageView.setImage(spotHighlighted);
                            } else
                                imageView.setImage(spotFill);
                        }

                        imageView.hoverProperty().addListener((observable, oldValue, newValue) -> {
                            if(spot.isEmpty()) {
                                if(newValue) {
                                    imageView.setImage(spotAdd);
                                }
                                else {
                                    imageView.setImage(spotEmpty);
                                }
                            } else {
                                if(newValue) {
                                    imageView.setImage(spotFillHover);
                                } else {
                                    if(spot.isHighlighted())
                                        imageView.setImage(spotHighlighted);
                                    else
                                        imageView.setImage(spotFill);
                                }
                            }
                        });

                        imageView.setOnMouseClicked(event -> {

                            contextMenu.hide();
                            contextMenu.getItems().clear();

                            if(spot.isEmpty()) {

                                if(event.getButton() == MouseButton.PRIMARY) {

                                    Optional <Bottle> result = DialogUtils.chooseBottle(true);

                                    result.ifPresent(bottle -> {
                                        spot.setBottle(bottle);
                                        imageView.setImage(spotFill);
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
                                            spot.setBottle(bottle);
                                            BottleFilter.research();
                                            MainApp.getController().showBottleDetails(spot);
                                            Saver.doChange();
                                        });
                                    });

                                    MenuItem remove = new MenuItem("Enlever");
                                    remove.setOnAction(event1 -> {
                                        MainApp.getSpots().remove(spot);
                                        spot.setBottle(null);
                                        MainApp.getSpots().add(spot);
                                        imageView.setImage(spotEmpty);
                                    });

                                    contextMenu.getItems().addAll(show, modify, remove);
                                    contextMenu.show(imageView, event.getScreenX(), event.getScreenY());

                                } else if(event.getButton() == MouseButton.MIDDLE) {
                                    MainApp.getSpots().remove(spot);
                                    spot.setBottle(null);
                                    MainApp.getSpots().add(spot);
                                    imageView.setImage(spotEmpty);
                                }

                            }

                        });

                        imageView.setFitWidth(64);
                        imageView.setFitHeight(64);

                        spot.highlightedProperty().addListener((observable, oldValue, newValue) -> {
                            if(newValue) {
                                imageView.setImage(spotHighlighted);
                            } else {
                                imageView.setImage(spotFill);
                            }
                        });

                        compartementDisplay.add(imageView, j, i);

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

                } else
                    doubleClick[0] = false;

                lastClick[0] = LocalDateTime.now();
            }
        });
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

}
