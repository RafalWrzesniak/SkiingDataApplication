package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {

    public GridPane gridpane = new GridPane();

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//        Label labelka = new Label();
//        labelka.setText("Asadsadour");

        Group root = new Group();
        Scene scene = new Scene(root, 300, 300, Color.WHITE);

        gridpane.gridLinesVisibleProperty().setValue(true);
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        Label label = new Label("Label 2");
        Label label2 = new Label("Label");
        Label label3 = new Label("Label 3");
        Label label4 = new Label("Label4");
        GridPane.setHalignment(label, HPos.CENTER);
        gridpane.add(label, 0, 0);
        gridpane.add(label2, 10, 10);
        gridpane.add(label3, 1, 0);
        gridpane.add(label4, 0, 1);


        root.getChildren().add(gridpane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your ski application");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
