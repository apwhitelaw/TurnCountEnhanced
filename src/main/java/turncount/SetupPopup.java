package turncount;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SetupPopup {

    private Stage stage = new Stage();
    private Scene launchScene;
    private Scene newCountScene;
    private TextField usernameField;
    private TextField siteCodeField;
    private TextField startTimeField;
    private Button cancelButton = new Button("Cancel");
    private Button startButton = new Button("Start");
    private Button newButton = new Button("New");
    private Button openFileButton = new Button("Open");
    private Button quitButton = new Button("Quit");

    public SetupPopup(Stage stage) {
        setupPopup(stage);
    }

    public void setupPopup(Stage primaryStage) {

        launchScene = setupLaunchScene();
        newCountScene = setupNewCountScene();
        launchScene.setFill(Color.TRANSPARENT);

        stage.setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });

        stage.setScene(launchScene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.initStyle(StageStyle.TRANSPARENT);
        //stage.setX(0);
        //stage.setY(0);
        stage.show();
        primaryStage.setOpacity(0.7);
    }

    public Scene setupLaunchScene() {
        newButton.setOnAction(actionEvent -> {
            stage.setScene(newCountScene);
        });
        quitButton.setOnAction(actionEvent -> {
            System.exit(0);
        });
        GridPane gridPane = new GridPane();
        gridPane.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3))));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(20);
        //Label welcomeLabel = new Label("Welcome!");
        Text welcomeText = new Text("Welcome!");
        HBox title = new HBox();
        title.setAlignment(Pos.CENTER);
        title.getChildren().add(welcomeText);
        gridPane.add(title, 0, 0, 3, 1);
        gridPane.add(newButton,0, 1);
        gridPane.add(openFileButton,1, 1);
        gridPane.add(quitButton,2, 1);
//        RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
//                new Stop(0, Color.web("#495867")),
//                new Stop(0.5, Color.web("#495867")),
//                new Stop(0.6, Color.TRANSPARENT)
//        });
//        gridPane.setBackground(new Background(new BackgroundFill(gradient1, CornerRadii.EMPTY, Insets.EMPTY)));

        //gridPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        //gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        return new Scene(gridPane, 300, 100);
    }

    public Scene setupNewCountScene() {
        stage.setTitle("Setup Count...");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(20);
        gridPane.setHgap(10);

        Label usernameLabel = new Label("Username: ");
        Label siteCodeLabel = new Label("Site Code: ");
        Label startTimeLabel = new Label("Start Time: ");
//        usernameField = new TextField();
//        siteCodeField = new TextField();
//        startTimeField = new TextField("HH:mm");
        usernameField = new TextField("John");
        siteCodeField = new TextField("15879424 SBEB");
        startTimeField = new TextField("10:00");
        cancelButton.setOnAction(actionEvent -> {
            setScene(launchScene);
        });
        HBox controlBox = new HBox(cancelButton, startButton);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setMinWidth(200);
        controlBox.setSpacing(50);

        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0, 2, 1);
        gridPane.add(siteCodeLabel, 0, 1);
        gridPane.add(siteCodeField, 1, 1, 2, 1);
        gridPane.add(startTimeLabel, 0, 2);
        gridPane.add(startTimeField, 1, 2, 2, 1);
        gridPane.add(controlBox, 0, 3, 3, 1);

        //gridPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        //gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        return new Scene(gridPane, 300, 200);
    }

    public void openCount() {

    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public Stage getStage() {
        return stage;
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
