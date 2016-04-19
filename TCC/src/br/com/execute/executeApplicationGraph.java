package br.com.execute;/**
 * Created by herbeton on 08/04/16.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class executeApplicationGraph extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("br/com/view/windowGraph.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setMaximized(true);
        primaryStage.setTitle("Gráfico da PSV");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
