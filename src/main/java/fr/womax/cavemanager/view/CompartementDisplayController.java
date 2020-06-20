package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Compartement;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.Optional;

/**
 * @author Antonin Ruan
 */
public class CompartementDisplayController {

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
                                            MainApp.getController().showBottleDetails(spot);
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
