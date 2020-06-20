package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Compartement;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Updater;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.util.Optional;

/**
 * @author Antonin Ruan
 */
public class RootLayoutController {

    private MainApp mainApp;

    @FXML
    private MenuItem versionMenuItem;

   /* @FXML
    private Pagination pagination;

    @FXML
    private GridPane compartementDisplay;*/

    @FXML
    private TitledPane descriptionPane;

    @FXML
    private TextArea description;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> criteriaChoiceBox;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        versionMenuItem.setText("Version: " + Updater.VERSION);
        /*pagination.setPageCount(MainApp.getCompartements().size());
        MainApp.getCompartements().addListener((ListChangeListener <? super Compartement>) c -> {
            while (c.next()) {
                pagination.setPageCount(MainApp.getCompartements().size());
            }
        });*/
        /*pagination.setPageFactory((index) -> {

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
                        else
                            imageView.setImage(spotFill);

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

                            if(spot.isEmpty()) {

                                if(event.getButton() == MouseButton.PRIMARY) {

                                    Optional<Bottle> result = DialogUtils.chooseBottle(true);

                                    result.ifPresent(bottle -> {
                                        spot.setBottle(bottle);
                                        imageView.setImage(spotFill);
                                    });

                                }

                            } else {

                                if (event.getButton() == MouseButton.PRIMARY) {

                                    showBottleDetails(spot);

                                } else if (event.getButton() == MouseButton.SECONDARY) {

                                    ContextMenu contextMenu = new ContextMenu();

                                    MenuItem show = new MenuItem("Afficher");
                                    show.setOnAction(event1 -> {
                                        showBottleDetails(spot);
                                    });

                                    MenuItem modify = new MenuItem("Modifier");
                                    modify.setOnAction(event1 -> {
                                        Optional<Bottle> result = DialogUtils.chooseBottle(true);
                                        result.ifPresent(bottle -> {
                                            spot.setBottle(bottle);
                                            showBottleDetails(spot);
                                        });
                                    });

                                    MenuItem remove = new MenuItem("Enlever");
                                    remove.setOnAction(event1 -> {
                                        spot.setBottle(null);
                                        imageView.setImage(spotEmpty);
                                    });

                                    contextMenu.getItems().addAll(show, modify, remove);
                                    contextMenu.show(imageView, event.getScreenX(), event.getScreenY());

                                } else if(event.getButton() == MouseButton.MIDDLE) {
                                    spot.setBottle(null);
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
        });*/
        String name = "Nom";
        String region = "Région";
        String type = "Type";
        String edition = "Édition";
        String domain = "Domaine";
        String year = "Année";
        criteriaChoiceBox.getItems().addAll(name, region, type, edition, domain, year);
        criteriaChoiceBox.setValue(name);
        BottleFilter.setCriteria(name);
        criteriaChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            BottleFilter.setCriteria(newValue);
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue.trim().isEmpty()) {
                BottleFilter.endSearching();
            } else
                BottleFilter.search(newValue);

        });
//        pagination.setCurrentPageIndex(0);
    }

    public void showBottleDetails(Spot spot) {

        description.setText("Nom: " + spot.getBottle().getName() + "\n"+
                "Domaine: " + spot.getBottle().getDomain() + "\n"+
                "Edition: " + spot.getBottle().getEdition() + "\n"+
                "Année: " + spot.getBottle().getYear() + "\n"+
                "Type: " + spot.getBottle().getType() + "\n"+
                "Région: " + spot.getBottle().getRegion() + "\n"+
                "Commentaire: " + spot.getBottle().getComment() + "\n");

        if(!descriptionPane.isExpanded()) {
            descriptionPane.setExpanded(true);
        }
    }

    public void handleNewCompartement() {
        mainApp.createNewCompartements(true);
    }

    public void handleOpenBottleList() {
        DialogUtils.chooseBottle(false);
    }

    public void handleCheckUpdate() {
        boolean newUpdate = Updater.checkUpdate();
        if(newUpdate) {
            DialogUtils.updateAvailable();
        } else {
            DialogUtils.noUpdateAvailable();
        }
    }

    public void handleDeleteCompartement() {
        if(MainApp.getCompartements().size() != 1) {
            Compartement compartement = MainApp.getCompartements().get(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
            for(Spot[] spotColumn : compartement.getSpots()) {
                for(Spot spot : spotColumn) {
                    MainApp.getSpots().remove(spot);
                }
            }
            MainApp.getCompartements().remove(compartement);
        } else {
            DialogUtils.needAtLeastOneCompartement();
        }

    }

    public void handleClose() {
        MainApp.saveFiles();
        MainApp.getPrimaryStage().close();
    }


}
