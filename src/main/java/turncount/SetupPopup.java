package turncount;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SetupPopup {

    Stage stage = new Stage();
    Scene launchScene;
    Scene newCountScene;
    TextField usernameField;
    TextField siteCodeField;
    TextField startTimeField;
    Button cancelButton = new Button("Cancel");
    Button startButton = new Button("Start");
    Button newButton = new Button("New");
    Button openFileButton = new Button("Open");
    Button quitButton = new Button("Quit");

    public void setupPopup(Stage primaryStage) {

        launchScene = setupLaunchScene();
        newCountScene = setupNewCountScene();

        stage.setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });
        stage.setScene(launchScene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.initStyle(StageStyle.UNDECORATED);
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
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(20);
        Label welcomeLabel = new Label("Welcome!");
        HBox title = new HBox();
        title.setAlignment(Pos.CENTER);
        title.getChildren().add(welcomeLabel);
        gridPane.add(title, 0, 0, 3, 1);
        gridPane.add(newButton,0, 1);
        gridPane.add(openFileButton,1, 1);
        gridPane.add(quitButton,2, 1);

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
        usernameField = new TextField();
        siteCodeField = new TextField();
        startTimeField = new TextField("HH:mm");
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

        return new Scene(gridPane, 300, 200);
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public void openCount() {

    }

}
