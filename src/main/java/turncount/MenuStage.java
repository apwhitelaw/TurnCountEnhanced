package turncount;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.ArrayList;

public class MenuStage {

    private Stage   stage;
    private Scene   menuScene;
    private Scene   gotoScene;
    private Scene   settingsScene;

    private VBox    menuBox;
    private Button  continueButton;
    private Button  nextButton;
    private Button  gotoButton;
    private Button  deleteButton;
    private Button  saveButton;
    private Button  settingsButton;
    private Button  quitButton;
    private ArrayList<Button> buttonList;

    public void createMenu(Stage mainStage) {

        menuScene     = setupMenuScene();
        gotoScene     = setupGotoScene();
        settingsScene = setupSettingsScene();

        menuScene.getStylesheets().add("mainTheme.css");
        gotoScene.getStylesheets().add("mainTheme.css");
        settingsScene.getStylesheets().add("mainTheme.css");

        stage = new Stage();
        stage.setScene(menuScene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);
        stage.initStyle(StageStyle.UNDECORATED);
        setupKeys();


        //menuBox.requestFocus();
    }

    public Scene setupMenuScene() {

        continueButton  = new Button("Continue (C)");
        nextButton      = new Button("Next Interval (T)");
        gotoButton      = new Button("Go to Interval (G)");
        deleteButton    = new Button("Delete Interval (D)");
        saveButton      = new Button("Save Count (S)");
        settingsButton  = new Button("Settings");
        quitButton      = new Button ("Quit");

        buttonList = new ArrayList<>();
        buttonList.add(continueButton);
        buttonList.add(nextButton);
        buttonList.add(gotoButton);
        buttonList.add(deleteButton);
        buttonList.add(saveButton);
        buttonList.add(settingsButton);
        buttonList.add(quitButton);

        menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(10));
        menuBox.setSpacing(5);
        menuBox.setBackground(Helper.createBackground(Color.rgb(45, 45, 41)));
        menuBox.setBorder(new Border(new BorderStroke(Color.rgb(33,90,109), BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3))));
        //menuBox.getStyleClass().add("menu-box");
        //menuBox.setId("menu-box");

        EventHandler removeFocus = (EventHandler<ActionEvent>) actionEvent -> menuBox.requestFocus();

        for(Button b : buttonList) {
            b.setPrefWidth(150);
            b.setOnAction(removeFocus);
            menuBox.getChildren().add(b);
        }

        continueButton.setOnAction(actionEvent -> stage.close());
        gotoButton.setOnAction(actionEvent -> stage.setScene(gotoScene));
        quitButton.setOnAction(actionEvent -> System.exit(0));

        return new Scene(menuBox, 170, 240);
    }

    public void positionMenu() {
        Window owner = stage.getOwner();
        double x =  owner.getX() + (owner.getWidth() / 2) - (menuBox.getWidth() / 2) ;
        double y =  owner.getY() + (owner.getHeight() / 2) - (menuBox.getHeight() / 2); // owner.getHeight()+30
        stage.setX(x);
        stage.setY(y);
    }

    public Scene setupGotoScene() {
        VBox vb = new VBox();
        return new Scene(vb, 10, 10);
    }

    public Scene setupSettingsScene() {
        // Setting for length of time skips? 1sec,3sec,10sec

        VBox vb = new VBox();
        return new Scene(vb, 10, 10);
    }

    public void setupKeys() {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch(keyEvent.getCode()) {
                case C: continueButton.fire();  break;
                case T: nextButton.fire();      break;
                case G: gotoButton.fire();      break;
                case D: deleteButton.fire();    break;
                case S: saveButton.fire();      break;
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    public VBox getMenuBox() {
        return menuBox;
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getGotoButton() {
        return gotoButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    //    public void setupGotoScene() {
//        Stage stage = new Stage();
//        Label label = new Label("Go to time:");
//        //label.setAlignment(Pos.CENTER);
//        HBox hBox = new HBox(label);
//        hBox.setAlignment(Pos.CENTER);
//        TextField timeField = new TextField("Enter time");
//        Button goButton = new Button("Go");
//        goButton.setMinWidth(33);
//        HBox.setHgrow(goButton, Priority.NEVER);
//        goButton.setOnAction(actionEvent -> {
//            String text = timeField.getText();
//            LocalTime time = LocalTime.parse(text);
//            for(Interval interval: countData) {
//                if(interval.getStartTime().equals(time)) {
//                    currentInterval = interval;
//                    updatePropertyIntegers();
//                    changeBank(0);
//                    updateIntervalText();
//                }
//            }
//            stage.close();
//        });
//        Button cancelButton = new Button("Cancel");
//        HBox hBox2 = new HBox(timeField, goButton);
//        HBox.setHgrow(timeField, Priority.ALWAYS);
//        hBox2.setAlignment(Pos.CENTER);
//        hBox2.setSpacing(5);
//        VBox vBox = new VBox(hBox, hBox2);
//        vBox.setAlignment(Pos.CENTER);
//        vBox.setSpacing(5);
//        vBox.setPadding(new Insets(10));
//        vBox.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//        vBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
//        Scene timeFieldScene = new Scene(vBox, 200, 60);
//        stage.setScene(timeFieldScene);
//        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initOwner(countStage);
//        stage.initStyle(StageStyle.UNDECORATED);
//        stage.show();
//    }

}
