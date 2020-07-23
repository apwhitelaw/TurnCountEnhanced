package turncount;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SetupPopupStage {

    private Stage       stage = new Stage();
    private Scene       launchScene;
    private Scene       newCountScene;
    private GridPane    startMenuPane;
    private GridPane    newCountPane;
    private TextField   usernameField;
    private TextField   siteCodeField;
    private TextField   startTimeField;
    private Button      cancelButton;
    private Button      startButton;
    private Button      newButton;
    private Button      openFileButton;
    private Button      quitButton;

    public SetupPopupStage(Stage stage) {
        setupPopup(stage);
    }

    public void setupPopup(Stage primaryStage) {

        launchScene = setupLaunchScene();
        launchScene.getStylesheets().add("mainTheme.css");
        newCountScene = setupNewCountScene();
        newCountScene.getStylesheets().add("mainTheme.css");

        stage.setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });

        stage.setScene(launchScene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        startMenuPane.requestFocus();
    }

    public Scene setupLaunchScene() {
        newButton       = new Button("New");
        openFileButton  = new Button("Open");
        quitButton      = new Button("Quit");

        EventHandler removeFocus = (EventHandler<ActionEvent>) actionEvent -> startMenuPane.requestFocus();
        newButton.addEventHandler(ActionEvent.ACTION, removeFocus);
        openFileButton.addEventHandler(ActionEvent.ACTION, removeFocus);
        quitButton.addEventHandler(ActionEvent.ACTION, removeFocus);

        newButton.setOnAction(actionEvent -> {
            stage.setScene(newCountScene);
        });
        quitButton.setOnAction(actionEvent -> {
            System.exit(0);
        });
        startMenuPane = new GridPane();
        startMenuPane.setBorder(new Border(new BorderStroke(Color.rgb(33,90,109), BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3))));
        startMenuPane.setBackground(Helper.createBackground(Color.rgb(45, 45, 41)));
        startMenuPane.setAlignment(Pos.CENTER);
        startMenuPane.setHgap(10);
        startMenuPane.setVgap(10);
        //Label welcomeLabel = new Label("Welcome!");
        Text welcomeText = new Text("Welcome!");
        welcomeText.setFont(new Font(30));
        welcomeText.setFill(Color.rgb(146,199,163));
        HBox title = new HBox();
        title.setAlignment(Pos.CENTER);
        title.getChildren().add(welcomeText);

        startMenuPane.add(title, 0, 0, 3, 1);
        startMenuPane.add(newButton,0, 1);
        startMenuPane.add(openFileButton,1, 1);
        startMenuPane.add(quitButton,2, 1);
        //gridPane.setPadding(new Insets(30));
//        RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
//                new Stop(0, Color.web("#495867")),
//                new Stop(0.5, Color.web("#495867")),
//                new Stop(0.6, Color.TRANSPARENT)
//        });
//        gridPane.setBackground(new Background(new BackgroundFill(gradient1, CornerRadii.EMPTY, Insets.EMPTY)));

        //gridPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        //gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        return new Scene(startMenuPane, 250, 100);
    }

    public Scene setupNewCountScene() {
        cancelButton    = new Button("Cancel");
        startButton     = new Button("Start");

        EventHandler removeFocus = (EventHandler<ActionEvent>) actionEvent -> newCountPane.requestFocus();
        newButton.addEventHandler(ActionEvent.ACTION, removeFocus);
        newButton.addEventHandler(ActionEvent.ACTION, removeFocus);

        stage.setTitle("Setup Count...");
        newCountPane = new GridPane();
        newCountPane.setAlignment(Pos.CENTER);
        newCountPane.setVgap(20);
        newCountPane.setHgap(10);
        newCountPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3))));
        newCountPane.setBackground(Helper.createBackground(Color.rgb(45, 45, 41)));

        Label usernameLabel = new Label("Username: ");
        Label siteCodeLabel = new Label("Site Code: ");
        Label startTimeLabel = new Label("Start Time: ");
        usernameLabel.setTextFill(Color.rgb(60,162,162));
        siteCodeLabel.setTextFill(Color.rgb(60,162,162));
        startTimeLabel.setTextFill(Color.rgb(60,162,162));
//        usernameField = new TextField();
//        siteCodeField = new TextField();
//        startTimeField = new TextField("HH:mm");
        usernameField = new TextField();
        siteCodeField = new TextField();
        startTimeField = new TextField();
        usernameField.setPromptText("John Smith");
        siteCodeField.setPromptText("15879424 SBEB");
        startTimeField.setPromptText("10:00");
        startTimeField.setPrefWidth(50);
        startTimeField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if(newVal.matches("[0-9]{2}:[0-9]{2}")) {
                    System.out.println("match");
                } else if(newVal.matches("[0-9:]{1,5}")) {
                    System.out.println("match 2");
                } else if(newVal.matches("")) {
                    System.out.println("blank");
                } else {
                        startTimeField.setText(oldVal);
                }
            }
        });

        cancelButton.setOnAction(actionEvent -> {
            setScene(launchScene);
        });

        HBox controlBox = new HBox(cancelButton, startButton);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setMinWidth(200);
        controlBox.setSpacing(50);

        newCountPane.add(usernameLabel, 0, 0);
        newCountPane.add(usernameField, 1, 0, 3, 1);
        newCountPane.add(siteCodeLabel, 0, 1);
        newCountPane.add(siteCodeField, 1, 1, 3, 1);
        newCountPane.add(startTimeLabel, 0, 2);
        newCountPane.add(startTimeField, 1, 2, 1, 1);
        newCountPane.add(controlBox, 0, 3, 4, 1);
        //gridPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        //gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        return new Scene(newCountPane, 300, 200);
    }

    public void openCount() {

    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public Stage getStage() {
        return stage;
    }

    public GridPane getGridPane() {
        return startMenuPane;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getSiteCodeField() {
        return siteCodeField;
    }

    public TextField getStartTimeField() {
        return startTimeField;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getNewButton() {
        return newButton;
    }

    public Button getOpenFileButton() {
        return openFileButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }
}
