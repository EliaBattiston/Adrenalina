package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Fighter;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Weapon;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Gui extends Application {
    Game game;

    private double background_width;
    private double background_height;

    private static String IMG_BACKGROUND = "file:images/background.png";
    private static String DIR_PLAYERBOARD = "file:images/playerBoard/";

    @Override
    public void start(Stage primaryStage) throws Exception {
        background_width = 960;
        background_height = background_width*9/16;
        Pane p;

        primaryStage.setTitle("Adrenalina");

        game = Game.jsonDeserialize("resources/baseGame.json");

        p = new Pane();

        //Settings for testing
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("fsgb", "!", Fighter.VIOLETTA));
        players.add(new Player("fsgb", "!", Fighter.DSTRUTTOR3));
        players.add(new Player("fsgb", "!", Fighter.SPROG));
        players.add(new Player("fsgb", "!", Fighter.BANSHEE));
        players.add(new Player("fsgb", "!", Fighter.DOZER));

        Player me = players.get(0);

        me.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            game.getWeaponsDeck().shuffle();
            weapons[0] = game.getWeaponsDeck().draw();
            weapons[1] = game.getWeaponsDeck().draw();
        }));

        //END of settings for testing

        p.getChildren().addAll(drawBackground(), drawMap(), drawAllPlayersBoards(players,false), drawMyArea(me));

        primaryStage.setScene(new Scene(p));
        primaryStage.setResizable(false);
        primaryStage.show();

        //Event handlers
        /*primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            background_width = newVal.doubleValue();
            rearrangeItems();
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            background_height = newVal.doubleValue();
            rearrangeItems();
        });*/
    }

    private Pane drawBackground(){
        //Pane root = new Pane();
        //Group background = new Group();
        StackPane pane = new StackPane();

        Canvas canvas = new Canvas(background_width,background_height);
        pane.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image( IMG_BACKGROUND ), 0, 0, background_width, background_height);

        return pane;
    }

    private StackPane drawMap(){
        double width_mult = 0.605;
        double height_mult = 0.815;
        double width = background_width*width_mult;
        double height = background_height*height_mult;
        double x = 18 * width_mult;
        double y = x;

        StackPane s = new StackPane();

        Canvas canvas = new Canvas(width,height);
        s.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image("file:images/map/map1.png"), x, y, width, height); //TODO print the right map

        return s;
    }

    private StackPane drawAllPlayersBoards(List<Player> players, boolean adrenalineMode){
        StackPane root = new StackPane();

        //dimensions are the same for all the players
        double width_mult = 0.29;
        double height_mult = 0.124;
        double x_mult = 0.6365;
        double y_mult = 0.0685;
        double width = background_width * width_mult;
        double height = background_height * height_mult;
        double x = background_width * x_mult;
        double y = background_height * y_mult;

        //calculate distance from board to board
        double delta_y = background_height * 0.1565;

        for(Player p : players){
            root.getChildren().add(drawPlayerBoard(p, adrenalineMode, width, height, x, y));
            y += delta_y;
        }

        return root;
    }

    private StackPane drawPlayerBoard(Player player, boolean adrenalineMode, double width, double height, double x, double y){
        StackPane s = new StackPane();

        Canvas canvas = new Canvas(background_width, background_height);
        s.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image(DIR_PLAYERBOARD + player.getCharacter().toString() + (adrenalineMode?"_A":"") + ".png"), x, y, width, height);

        return s;
    }

    private StackPane drawMyArea(Player player){
        StackPane root = new StackPane();

        root.getChildren().add(drawAllMyWeapons(player.getWeapons()));

        return root;
    }

    private AnchorPane drawAllMyWeapons(List<Weapon> weapons){
        StackPane root = new StackPane();
        AnchorPane anchor = new AnchorPane();

        //dimensions are the same
        double width_mult = 0.0625;
        double height_mult = 0.188;
        double x_mult = 0.024;
        double y_mult = 0.8;
        double width = background_width * width_mult;
        double height = background_height * height_mult;
        double x = background_width * x_mult;
        double y = background_height * y_mult;

        //calculate distance from board to board
        double delta_x = background_width * 0.071;

        for(Weapon w : weapons){
            //root.getChildren().add(drawWeapon(w, width, height, x, y));
            CardGui card = new CardGui(w, width, height, x, y);
            AnchorPane.setTopAnchor(card, 10.0);
            /*AnchorPane.setLeftAnchor(card, 50.0);
            AnchorPane.setRightAnchor(card, 50.0);
            AnchorPane.setBottomAnchor(card, 50.0);*/

            anchor.getChildren().add(card);

           // root.getChildren().add(card);
            //card.relocateCanvas(x, y);
            x += delta_x;
        }

        anchor.setPadding(new Insets(300, 0, 0, 10));

        return anchor;
    }

    /*private Pane createBoard(){
        double width = background_width;
        double height = background_width;

        Pane root = new Pane();

        Group weaponG = new Group();
        Group lootG = new Group();
        Group powerG = new Group();

        root.setPrefSize(width, height);
        root.getChildren().add(drawMap());
        root.getChildren().addAll(weaponG, lootG, powerG);

        //ex
        CardGui c = new CardGui(game.getWeaponsDeck().draw(), this);
        CardGui l = new CardGui(game.getAmmoDeck().draw(), this);
        CardGui p = new CardGui(game.getPowersDeck().draw(), this);

        weaponG.getChildren().add(c);
        lootG.getChildren().add(l);
        powerG.getChildren().add(p);

        p.relocate(300,200);

        return root;
    }*/



    public double getWidth() {
        return background_width;
    }

    public double getHeight() {
        return background_height;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
