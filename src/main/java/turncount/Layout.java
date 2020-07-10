package turncount;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Layout {

    Scene mainScene;
    TabPane tabPane = new TabPane();
    ArrayList<ArrayList<Label>> countLabels = new ArrayList<>();
    Button nextIntervalButton = new Button("Next Interval");
    Button prevIntervalButton = new Button("Previous Interval");
    Button delIntervalButton = new Button("Delete Interval");
    Button goToIntervalButton = new Button("Save Data");

    public void createSceneAndSetupStage(Stage primaryStage) {

        for(int i = 0; i < 5; i++) {
            countLabels.add(new ArrayList<>());
        }

        MenuBar menuBar = setupMenuBar();
        TabPane banksTabPane = setupBanksTabPane();
        Separator line = new Separator();
        ButtonBar controlsPane = setupControlsPane();

        VBox vBox = new VBox(menuBar, banksTabPane, line, controlsPane);
        VBox.setVgrow(banksTabPane, Priority.ALWAYS);

        mainScene = new Scene(vBox, 500, 300);

        primaryStage.setTitle("TurnCountEnhanced");
        primaryStage.setScene(mainScene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == true) {
                primaryStage.setOpacity(1.0);
            } else {
                primaryStage.setOpacity(0.3);
            }
        });

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

        return menuBar;
    }

    private TabPane setupBanksTabPane() {
        for(int i = 0; i < 5; i++) {
            Tab t = new Tab(String.format("Bank %d", i), new Label(String.format("Contains data for Bank %d", i)));
            t.setContent(setupCountGridPane(String.format("%s", i)));
            tabPane.getTabs().add(t);
        }
        return tabPane;
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

        return gp;
    }

    private VBox setupMovement(int bank, int index) {
        Interval.Movement move = Interval.Movement.getFromIndex(index);
        Label movementLabel = new Label(move.text);
        Label countLabel = new Label("0");
        countLabels.get(bank).add(countLabel);

        VBox vBox = new VBox(movementLabel, countLabel);
        return vBox;
    }

    private ButtonBar setupControlsPane() {
        ButtonBar.setButtonData(prevIntervalButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(nextIntervalButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(delIntervalButton, ButtonBar.ButtonData.RIGHT);
        ButtonBar.setButtonData(goToIntervalButton, ButtonBar.ButtonData.RIGHT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(prevIntervalButton, nextIntervalButton, delIntervalButton, goToIntervalButton);
        buttonBar.setPadding(new Insets(10));
        //controlsPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        return buttonBar;
    }

}
