package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import javafx.application.Application;
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

    private double backgroundWidth;
    private double backgroundHeight;

    private static String IMG_BACKGROUND = "file:images/background.png";
    private static String DIR_PLAYERBOARD = "file:images/playerBoard/";

    @Override
    public void start(Stage primaryStage) throws Exception {
        //A possible faster solution can be to adda directly to a single canvas all the images, we have to check it won't
        //make impossible stuffs like click handling

        backgroundWidth = 1600;
        backgroundHeight = backgroundWidth*9/16;
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

            game.getPowersDeck().shuffle();
            powers[0] = game.getPowersDeck().draw();
            powers[1] = game.getPowersDeck().draw();

            //ammo = new Ammunitions();
            ammo.add(Color.YELLOW, 2);
            ammo.add(Color.BLUE, 1);
        }));

        //END of settings for testing

        p.getChildren().addAll(drawBackground(), drawMap(), drawAllPlayersBoards(players,false),
                drawMyWeapons(me.getWeapons()), drawMyPowers(me.getPowers()), drawMyAmmo(me.getAmmo()));

        primaryStage.setScene(new Scene(p));
        primaryStage.setResizable(false);
        primaryStage.show();

        //Event handlers
        /*primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            backgroundWidth = newVal.doubleValue();
            rearrangeItems();
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            backgroundHeight = newVal.doubleValue();
            rearrangeItems();
        });*/
    }

    private Pane drawBackground(){
        StackPane pane = new StackPane();

        Canvas canvas = new Canvas(backgroundWidth,backgroundHeight);
        pane.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image( IMG_BACKGROUND ), 0, 0, backgroundWidth, backgroundHeight);

        return pane;
    }

    private StackPane drawMap(){
        double widthMult = 0.605;
        double heightMult = 0.815;
        double width = backgroundWidth*widthMult;
        double height = backgroundHeight*heightMult;
        double x = 18 * widthMult;
        double y = x;

        StackPane s = new StackPane();

        Canvas canvas = new Canvas(width,height);
        s.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image("file:images/map/map1.png"), x, y, width, height); //TODO print the right map

        return s;
    }

    private void drawLootOnMap(GraphicsContext mapGc, Map map){

        //dimensions are the same
        double widthMult = 0.026;
        double xMult = 0.277;
        double yMult = 0.3;
        double width = backgroundWidth * widthMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.053;

        for(int i=0; i<4; i++){
            for(int j=0; j<3; j++){
                if(map.getCell(i, j) != null){
                    //TODO start from here

                    //Image l = new Image("file:images/loot/" + map.getCell(i,j));
                }
            }
            //CardGui card = new CardGui(p, backgroundWidth, backgroundHeight, width, height, x, y);
            //root.getChildren().add(card);
            x += deltaX;
        }
    }

    private StackPane drawAllPlayersBoards(List<Player> players, boolean adrenalineMode){
        StackPane root = new StackPane();

        //dimensions are the same for all the players
        double widthMult = 0.29;
        double heightMult = 0.124;
        double xMult = 0.6365;
        double yMult = 0.0685;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double delta_y = backgroundHeight * 0.1565;

        for(Player p : players){
            root.getChildren().add(drawPlayerBoard(p, adrenalineMode, width, height, x, y));
            y += delta_y;
        }

        return root;
    }

    private StackPane drawPlayerBoard(Player player, boolean adrenalineMode, double width, double height, double x, double y){
        StackPane s = new StackPane();

        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        s.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image(DIR_PLAYERBOARD + player.getCharacter().toString() + (adrenalineMode?"_A":"") + ".png"), x, y, width, height);

        return s;
    }

    private StackPane drawMyWeapons(List<Weapon> weapons){
        StackPane root = new StackPane();

        //dimensions are the same
        double widthMult = 0.0625;
        double heightMult = 0.188;
        double xMult = 0.024;
        double yMult = 0.8;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.0725;

        for(Weapon w : weapons){
            CardGui card = new CardGui(w, backgroundWidth, backgroundHeight, width, height, x, y);
            root.getChildren().add(card);
            x += deltaX;
        }

        return root;
    }

    private StackPane drawMyPowers(List<Power> powers){
        StackPane root = new StackPane();

        //dimensions are the same
        double widthMult = 0.048;
        double heightMult = 0.135;
        double xMult = 0.253;
        double yMult = 0.853;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.053;

        for(Power p : powers){
            CardGui card = new CardGui(p, backgroundWidth, backgroundHeight, width, height, x, y);
            root.getChildren().add(card);
            x += deltaX;
        }

        return root;
    }

    private StackPane drawMyAmmo(Ammunitions ammo){
        StackPane root = new StackPane();

        //dimensions are the same
        double widthMult = 0.018;
        double xMult = 0.435;
        double yMult = 0.887;
        double width = backgroundWidth * widthMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.0224;
        double deltaY = backgroundHeight * 0.0333;

        Canvas ammoCanvas = new Canvas(backgroundWidth, backgroundHeight);
        GraphicsContext gc = ammoCanvas.getGraphicsContext2D();
        Image blue = new Image("file:images/loot/blue.png");
        Image red = new Image("file:images/loot/red.png");
        Image yellow = new Image("file:images/loot/yellow.png");

        for(int i=0; i<ammo.getBlue(); i++){
            gc.drawImage(blue, x, y, width, width);
            x += deltaX;
        }
        x = backgroundWidth * xMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getRed(); i++){
            gc.drawImage(red, x, y, width, width);
            x += deltaX;
        }
        x = backgroundWidth * xMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getYellow(); i++){
            gc.drawImage(yellow, x, y, width, width);
            x += deltaX;
        }

        root.getChildren().add(ammoCanvas);
        return root;
    }



    /*private Pane createBoard(){
        double width = backgroundWidth;
        double height = backgroundWidth;

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

    public static void main(String[] args) {
        launch(args);
    }

}
