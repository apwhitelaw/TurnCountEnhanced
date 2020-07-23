package turncount;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import lk.vivoxalabs.customstage.CustomStage;
import lk.vivoxalabs.customstage.CustomStageBuilder;
import lk.vivoxalabs.customstage.tools.HorizontalPos;
import lk.vivoxalabs.customstage.view.controller.CustomStageController;

import java.net.URL;
import java.util.ArrayList;

public class CountStage {

    private CustomStage     stage;
    private Scene           scene;

    private MenuBar         menuBar;

    // Count Pane (HBox)
    private TabPane                 banksTabPane;
    private ListView<String>        listView;
    ArrayList<ArrayList<Label>>     countLabels;

    // Controls Pane (HBox)
    private HBox    controlsPane;
    private Text    intervalText;
    private Button  nextIntervalButton;
    private Button  prevIntervalButton;
    private Button  delIntervalButton;
    private Button  menuButton;

    private int feedCount;

    public CountStage() {
        createSceneAndSetupStage();
    }

    public void createSceneAndSetupStage() {

        // 5 ArrayList's for 5 Banks (0-4)
        countLabels = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            countLabels.add(new ArrayList<>());
        }

        menuBar         = setupMenuBar();
        banksTabPane    = setupBanksTabPane();
        listView        = setupListView();
        feedCount       = 0;

        HBox.setHgrow(banksTabPane, Priority.ALWAYS);
        HBox.setHgrow(listView, Priority.NEVER);
        HBox countPane = new HBox(banksTabPane, listView);
        countPane.setBackground(Helper.createBackground(Color.BLACK)); //Color.rgb(25,25,21)
        controlsPane = setupControlsPane();
        VBox vBox = new VBox(menuBar, countPane, controlsPane);
        VBox.setVgrow(banksTabPane, Priority.ALWAYS);
        vBox.setSpacing(0);
        RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
                new Stop(0, Color.web("#495867")),
                new Stop(1, Color.web("#bdd5ea"))
        });

        try {
            CustomStage customStage = new CustomStageBuilder()
                    .setWindowTitle("TurnCountEnhanced", HorizontalPos.RIGHT, HorizontalPos.CENTER)
                    .setTitleColor("rgb(146,199,163)")
                    .setWindowColor("rgb(33,90,109)")
                    .setDimensions(600, 320, 600, 320).build();
            customStage.setWidth(600);
            customStage.setHeight(320);
            customStage.changeScene(vBox);
            //customStage.setResizable(false);
            //customStage.initStyle(Stage);
            customStage.getScene().getStylesheets().add("mainTheme.css");
            stage = customStage;
            stage.show();
            System.out.println(customStage.getHeight());
            System.out.println(customStage.getWidth());
        } catch(Exception e) {
            System.out.println(e);
        }

    }

    public void setLabelBinds(SimpleIntegerProperty[][] propertyData) {
        for(int i = 0; i < 5; i++) {
            ArrayList<Label> labels = countLabels.get(i);
            for(int j = 0; j < 16; j++) {
                Label label = labels.get(j);
                SimpleIntegerProperty countProperty = propertyData[i][j];
                label.textProperty().bind(countProperty.asString());
            }
        }
    }

    private MenuBar setupMenuBar() {
        MenuItem save = new MenuItem("Save Count");
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        MenuItem quit = new MenuItem("Quit");
        quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(save, quit);
        MenuItem about = new MenuItem("About");
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(about);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        menuBar.setBackground(Helper.createBackground(Color.rgb(146,199,163)));

        return menuBar;
    }

    private TabPane setupBanksTabPane() {
        banksTabPane = new TabPane();
        for(int i = 0; i < 5; i++) {
            Tab t = new Tab(String.format("Bank %d", i), new Label(String.format("Contains data for Bank %d", i)));
            t.setContent(setupCountGridPane(String.format("%s", i)));
            t.setClosable(false);
            banksTabPane.getTabs().add(t);
        }
//        RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
//                new Stop(0, Color.web("#48CAE4")),
//                new Stop(1, Color.web("#CAF0F8"))
//        });
//        tabPane.setBackground(new Background(new BackgroundFill(gradient1, CornerRadii.EMPTY, Insets.EMPTY)));
        banksTabPane.setBackground(Helper.createBackground(Color.rgb(45,45,41)));
        return banksTabPane;
    }

    private GridPane setupCountGridPane(String id) {
        GridPane gp = new GridPane();
        gp.setId(id);
        int bank = Integer.parseInt(id);
        for(int i = 0; i<16; i++) {
            gp.add(setupMovement(bank, i), i%4, i/4);
        }
        gp.setHgap(80);
        gp.setVgap(10);
        gp.setAlignment(Pos.CENTER);
        gp.setBackground(Helper.createBackground(Color.rgb(45,45,41)));
//        RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
//                new Stop(0, Color.web("#b76935")),
//                new Stop(0.5, Color.web("#5c4d3c"))
//        });
//        gp.setBackground(new Background(new BackgroundFill(gradient1, CornerRadii.EMPTY, Insets.EMPTY)));

        return gp;
    }

    private VBox setupMovement(int bank, int index) {
        Interval.Movement move = Interval.Movement.getFromIndex(index);
        Label movementLabel = new Label(move.text);
        movementLabel.setTextFill(Color.rgb(146,199,163));
        Label countLabel = new Label("0");
        countLabel.setTextFill(Color.rgb(60,162,162));
        countLabels.get(bank).add(countLabel);

        VBox vBox = new VBox(movementLabel, countLabel);
        return vBox;
    }

    private ListView setupListView() {
        ListView<String> lv = new ListView();
        //listView.setBackground(new Background(new BackgroundFill(Color.rgb(45,45,41), CornerRadii.EMPTY, Insets.EMPTY)));
        lv.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                ListCell<String> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
//                        if(s == null) {
//                            setText(null);
//                            setTextFill(null);
//                            setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
//                        } else {
//                            //setPrefHeight((listView.getHeight() / 8) - 2);
//                            setText(s);
//                            setTextFill(Color.rgb(146,199,163));
//                            setBackground(new Background(new BackgroundFill(Color.rgb(45,45,41), CornerRadii.EMPTY, Insets.EMPTY)));
//                        }
                        if(s == null) {
                            setText(null);
                            setTextFill(null);
                            setBackground(Helper.createBackground(Color.WHITE));
                            System.out.println("is null");
                            setHeight(100);
                        } else if(s.equals("")) {
                            setText(s);
                            setTextFill(Color.rgb(146,199,163));
                            setBackground(Helper.createBackground(Color.rgb(35,35,31)));
                            System.out.println("is blank");
                        } else {
                            setText(s);
                            setTextFill(Color.rgb(146,199,163));
                            setBackground(Helper.createBackground(Color.rgb(45, 45, 41)));
                            System.out.println("is filled");
                        }
                    }
                };
                return cell;
            }
        });
        lv.setPrefWidth(100);
        lv.setPadding(new Insets(28, 0, 0, 0));
        lv.getItems().addAll("","","","","","","","","","","");
        lv.focusedProperty().addListener((observableValue, aBoolean, t1) -> banksTabPane.requestFocus());
        return lv;
    }

    private HBox setupControlsPane() {
        nextIntervalButton  = new Button("Next Interval");
        prevIntervalButton  = new Button("Previous Interval");
        delIntervalButton   = new Button("Delete Interval");
        menuButton          = new Button("Menu");

        EventHandler removeFocus = (EventHandler<ActionEvent>) actionEvent -> getBanksTabPane().requestFocus();
        nextIntervalButton.addEventHandler(ActionEvent.ACTION, removeFocus);
        prevIntervalButton.addEventHandler(ActionEvent.ACTION, removeFocus);
        delIntervalButton.addEventHandler(ActionEvent.ACTION, removeFocus);
        menuButton.addEventHandler(ActionEvent.ACTION, removeFocus);
        delIntervalButton.getStyleClass().add("button-delete");

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        intervalText = new Text("16:00 - 16:05");
        intervalText.setFont(new Font(20));
        intervalText.setFill(Color.rgb(60,162,162));

        HBox hb = new HBox();
        hb.setBackground(Helper.createBackground(Color.rgb(45, 45, 41)));
        hb.getChildren().addAll(prevIntervalButton, nextIntervalButton, spacer1, intervalText, spacer2, delIntervalButton, menuButton);
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(10));
        hb.setSpacing(10);
        return hb;
    }

    public void addToFeed(String string) {
        if(feedCount < 8) {
            listView.getItems().remove(0);
            listView.getItems().add(string);
            feedCount++;
        } else {
            listView.getItems().add(string);
        }
        listView.scrollTo(listView.getItems().size() - 1);
    }

    public void updateFeed(Interval interval) {
        listView.setItems(interval.getButtonFeed());
    }

    public Text getIntervalText() {
        return intervalText;
    }

    public ListView<String> getListView() {
        return listView;
    }

    public CustomStage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public TabPane getBanksTabPane() {
        return banksTabPane;
    }

    public HBox getControlsPane() {
        return controlsPane;
    }

    public Button getNextIntervalButton() {
        return nextIntervalButton;
    }

    public Button getPrevIntervalButton() {
        return prevIntervalButton;
    }

    public Button getDelIntervalButton() {
        return delIntervalButton;
    }

    public Button getMenuButton() {
        return menuButton;
    }
}
