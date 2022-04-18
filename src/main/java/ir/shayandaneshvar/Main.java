package ir.shayandaneshvar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        AnchorPane root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
        scene = new Scene(root, 255, 520, false, SceneAntialiasing.BALANCED);
    }

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Karger-Stein");
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(status -> System.exit(0));
    }
}