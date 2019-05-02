package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Weapon;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

public class Gui extends Application {

    Game game;

    private double width;
    private double height;

    @Override
    public void start(Stage primaryStage) throws Exception {
        width = 1000;
        height = 750;

        primaryStage.setTitle("Adrenalina");

        game = Game.jsonDeserialize("resources/baseGame.json");

        primaryStage.setScene(createBoard());
        //primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            width = newVal.doubleValue();
            rearrangeItems();
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            height = newVal.doubleValue();
            rearrangeItems();
        });
    }

    private Scene createBoard(){
        Group weaponG = new Group();
        Group lootG = new Group();
        Group powerG = new Group();

        Pane root = new Pane();
        root.setPrefSize(width, height);
        root.getChildren().add(loadMap());
        root.getChildren().addAll(weaponG, lootG, powerG);

        //ex
        CardGui c = new CardGui(game.getWeaponsDeck().draw(), this);
        CardGui l = new CardGui(game.getAmmoDeck().draw(), this);
        CardGui p = new CardGui(game.getPowersDeck().draw(), this);

        weaponG.getChildren().add(c);
        lootG.getChildren().add(l);
        powerG.getChildren().add(p);

        p.relocate(300,200);

        return new Scene(root);
    }

    private void rearrangeItems(){
        ;//TODO rearrange to new width and height
    }

    private StackPane loadMap(){
        StackPane s = new StackPane();

        Canvas canvas = new Canvas(width,height);
        s.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image("file:images/map/map1.png"), 0, 0, width,height); //TODO print the right map

        return s;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
