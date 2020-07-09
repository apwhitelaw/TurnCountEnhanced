package turncount;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SetupPopup {

    Stage stage = new Stage();
    TextField usernameField;
    TextField siteCodeField;
    TextField startTimeField;
    Button quitButton;
    Button startButton;

    public void setupPopup(Stage primaryStage) {
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
        quitButton = new Button("Quit");
        quitButton.setOnAction(actionEvent -> {
            System.exit(0);
        });
        startButton = new Button("Start");
        HBox controlBox = new HBox(quitButton, startButton);
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


        Scene scene = new Scene(gridPane, 300, 200);
        stage.setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.show();
        primaryStage.setOpacity(0.8);

    }

}
