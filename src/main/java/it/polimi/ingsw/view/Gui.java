package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.controller.Interaction;
import it.polimi.ingsw.model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class Gui extends Application{
    private static String imgRoot = "file:images/";

    //Data
    private MatchView match;
    private GameView game;
    private GuiExchanger exchanger;

    //View
    private Scene mainScene;
    private double backgroundWidth;
    private double backgroundHeight;
    private double dimMult;
    private static double positionFix = 35; //position fix for drawing the weapons on the map
    private GraphicsContext gc;

    //Canvases
    private List<GuiCardWeapon> lootWeapons;
    private List<GuiCardWeapon> myWeapons;
    private List<GuiCardPower> myPowers;
    private Canvas run;
    private Canvas runGrab;
    private Canvas shoot;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        backgroundWidth = 1280;
        backgroundHeight = backgroundWidth*9/16;

        dimMult = backgroundWidth/1920;

        initForTest();

        mainScene = new Scene(drawGame());

        primaryStage.setTitle("Adrenalina");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        //primaryStage.setFullScreen(true);
        primaryStage.show();

       /* while(true){
            if(GuiExchanger.getInstance().getActualInteraction() != Interaction.NONE)
                System.out.println(GuiExchanger.getInstance().getActualInteraction().toString());
        }*/

        //Event handlers
       primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(abs(newVal.doubleValue() - backgroundWidth) > 20) {
                backgroundWidth = newVal.doubleValue();
                backgroundHeight = backgroundWidth * 9 / 16;
                dimMult = backgroundWidth / 1920;

                drawDecks();
                primaryStage.setScene(new Scene(drawGame()));
            }
        });

       //TODO use this for fixed ratio, find the good way to implement them
        //primaryStage.minHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16));
        //primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16));

        // separate non-FX thread
        //TODO decide how to handle the possibility of a resize while a request in in action. we can:
        // a) reset the actualInteraction so this code will re-run FAST
        // b) not re-paint all the stuffs but just move&resize them CLEANER
       new Thread(()->{

           //On my linux (Andrea) I need to wait a while the javafx app before starting the tests
           try{
                    Thread.sleep(1500);
           }catch (InterruptedException e){
           }

           exchanger = GuiExchanger.getInstance();
            while(true)
                if(exchanger.guiRequestIncoming()) {
                    System.out.println(exchanger.getActualInteraction().toString());
                    switch (exchanger.getActualInteraction()) {
                        case CHOOSEACTION:
                            chooseAction();
                            break;
                        case CHOOSEWEAPON:
                        case GRABWEAPON:
                        case DISCARDWEAPON:
                        case RELOAD:
                            chooseWeaponCard((List<Weapon>) exchanger.getRequest());
                            break;
                        case DISCARDPOWER:
                        case CHOOSEPOWER:
                            choosePowerCard((List<Power>) exchanger.getRequest());
                            break;
                        case MOVEPLAYER:
                            chooseCell();
                            break;
                        case CHOOSETARGET:
                            chooseEnemy();
                            break;
                        case MOVEENEMY:
                            break;
                        case CHOOSEROOM:
                        case CHOOSEDIRECTION:
                        case CHOOSEPOSITION:
                        case CHOOSEMAP:
                        case CHOOSEFRENZY:
                            chooseDialog((String) exchanger.getMessage());
                            break;
                        case GETNICKNAME:
                        case GETPHRASE:
                        case GETFIGHTER:
                        case GETSKULLSNUM:
                            chooseUserSettings();
                            break;
                        case UPDATEVIEW:
                            break;
                        case NONE:
                        default:
                            break;
                    }
                }
       }).start();
    }

    private Pane drawGame(){
        Pane masterPane;
        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        Canvas fixedGraphics;
        StackPane a, b, c, d;

        masterPane = new Pane();

        //The background, map and decks never need to be updated they can be run just when the window's dimensions change,
        // to do so, move also the fixedGraphics canvas
        fixedGraphics = new Canvas(backgroundWidth, backgroundHeight);
        gc = fixedGraphics.getGraphicsContext2D();
        drawBackground();
        drawMap(match.getGame().getMap());
        drawDecks();

        //weapons, powers, loot
        gc = canvas.getGraphicsContext2D(); //from now on the global gc will be the one for the dynamics data

        drawPawnsOnMap(match.getGame().getMap());

        drawAllPlayersBoards(match.getGame().getPlayers(),false); //FRENZY?
        drawMyAmmo(match.getMyPlayer().getAmmo());

        a = drawMyWeapons(match.getMyPlayer().getWeapons());
        b = drawMyPowers(match.getMyPlayer().getPowers());
        c = drawWeaponsLoot(match.getGame().getMap());
        d = drawLootOnMap(match.getGame().getMap());

        a.setPickOnBounds(false);
        b.setPickOnBounds(false);
        c.setPickOnBounds(false);
        d.setPickOnBounds(false);
        masterPane.setPickOnBounds(false);
        masterPane.getChildren().addAll( fixedGraphics, canvas,  a, b, c, d);

        return masterPane;
    }

    private void drawBackground(){
        gc.drawImage( GuiImagesMap.getImage(imgRoot + "background.png"), 0, 0, backgroundWidth, backgroundHeight);
    }

    private void drawMap(Map map){
        double width = 1142 * dimMult;
        double height = 866 * dimMult;
        double x = 18 * dimMult;
        double y = x;

        gc.drawImage( GuiImagesMap.getImage("file:images/map/map" + map.getId() + ".png"), x, y, width, height);
    }

    private StackPane drawLootOnMap (Map map){
        StackPane root = new StackPane();

        //dimensions are the same
        double size = 55 * dimMult;
        double x;
        double y;

        //Manually add for each cell because they haven't the same dim. Create row by row from the top one
        if(map.getCell(0, 0) != null) {
            x = 310 * dimMult;
            y = 325 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(0, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 0) != null) {
            x = 526 * dimMult;
            y = 325 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(1, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 0) != null) {
            x = 900 * dimMult;
            y = 900 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(3, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 1) != null) {
            x = 526 * dimMult;
            y = 530 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(1, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 1) != null) {
            x = 725 * dimMult;
            y = 530 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(2, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 1) != null) {
            x = 900 * dimMult;
            y = 530 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(3, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(0, 2) != null) {
            x = 335 * dimMult;
            y = 730 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(0, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 2) != null) {
            x = 526 * dimMult;
            y = 730 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(1, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 2) != null) {
            x = 725 * dimMult;
            y = 730 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(2,2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }

        return root;
    }

    private void drawPawnsOnMap (Map map){
        double size = 50 * dimMult;
        double baseX = 224 * dimMult;
        double baseY = 224 * dimMult;
        double x;
        double y;
        double deltaCellX = 186 * dimMult;
        double deltaCellY = 200 * dimMult;
        boolean xNotY;

        //FIXME actually it print a max of 3 players in a single cell because of the wrong managing of the xNotY, find a better way
        for(int j=0; j<3; j++){
            for(int i=0; i<4; i++){
                xNotY = true;
                x = baseX + i * (deltaCellX + (i==3?10:0)); //the last column is farther
                y = baseY + j * deltaCellY;
                if(map.getCell(i, j) != null) { //here X and Y are pointing at the top-left corner of the cell
                    for (Player p : map.getCell(i, j).getPawns()) {
                        gc.drawImage(GuiImagesMap.getImage(imgRoot + "playerPawn/" + p.getCharacter().toString() + ".png"), x, y, size, size);
                        if (xNotY) {
                            x += size;
                            y = baseY + j * deltaCellY;
                        } else {
                            y += size;
                            x = baseX + i * (deltaCellX +(i==3?10:0));
                        }

                        xNotY = !xNotY;
                    }
                }
            }
        }
    }

    private StackPane drawWeaponsLoot(Map map){
        StackPane root = new StackPane();
        GuiCardWeapon card;

        //dimensions are the same
        double width = 104 * dimMult;
        double height = 174 * dimMult;
        double x = (624 - positionFix)* dimMult;
        double y = 4 * dimMult;

        //calculate distance from board to board
        double deltaX = 126 * dimMult;
        double deltaY = deltaX;

        lootWeapons = new ArrayList<>();
        //First the blue spawn
        SpawnCell c = (SpawnCell) map.getCell(2,0);
        for(Weapon w:c.getWeapons()){
            card = new GuiCardWeapon(w, width, height, 0);
            lootWeapons.add(card);

            card.setPickOnBounds(false);
            card.setTranslateX(x);
            card.setTranslateY(y);

            root.getChildren().add(card);
            x += deltaX;
        }

        //set values for new position (red)
        x = 4 * dimMult;
        y = (335 - positionFix) * dimMult;
        //Red spawn
        c = (SpawnCell) map.getCell(0, 1);
        for(Weapon w:c.getWeapons()){
            card = new GuiCardWeapon(w, width, height, -90);

            lootWeapons.add(card);

            card.setPickOnBounds(false);
            card.setTranslateX(x);
            card.setTranslateY(y);
            root.getChildren().add(card);

            y += deltaY;
        }

        //set values for new position (yellow)
        x = 1006 * dimMult;
        y = (510 - positionFix) * dimMult;
        //Yellow spawn
        c = (SpawnCell) map.getCell(3, 2);
        for(Weapon w:c.getWeapons()){
            card = new GuiCardWeapon(w, width, height, +90);

            lootWeapons.add(card);

            card.setPickOnBounds(false);
            card.setTranslateX(x);
            card.setTranslateY(y);
            root.getChildren().add(card);

            y += deltaY;
        }
        return root;
    }

    private void drawDecks(){
        //PowersDeck
        double width = 80 * dimMult;
        double height = 109 * dimMult;
        double x = 1044 * dimMult;
        double y = 65 * dimMult;
        gc.drawImage(GuiImagesMap.getImage("file:images/power/powerBackPile.png"), x, y, width, height);

        //WeaponsDeck
        width = 100 * dimMult;
        height = 174 * dimMult;
        x = 1018 * dimMult;
        y = 252 * dimMult;
        gc.drawImage(GuiImagesMap.getImage("file:images/weapon/weaponBackPile.png"), x, y, width, height);
    }

    private void drawAllPlayersBoards(List<Player> players, boolean adrenalineMode){
        //dimensions are the same for all the players
        double width = 560 * dimMult;
        double height = 134 * dimMult;
        double x = 1222 * dimMult;
        double y = 74 * dimMult;

        //calculate distance from board to board
        double deltaY = 169 * dimMult;

        for(Player p : players){
            drawPlayerBoard(p, players, adrenalineMode, width, height, x, y);
            y += deltaY;
        }
    }

    private void drawPlayerBoard(Player player, List<Player> players, boolean adrenalineMode, double width, double height, double x, double y){
        double pbMult = width/1123; //(dimMult * width@1080p)/textureWidth -> internal reference based on the card
        double xDrop = (adrenalineMode?130:116) * pbMult + x;
        double yDrop = 116 * pbMult + y;
        double widthDrop = 30 * pbMult;
        double heightDrop = 45 * pbMult;

        double deltaX = (adrenalineMode?61:63) * pbMult;

        //FIXME it stays in the middle of the window, find a way to move it
        //y - 30 -> write the name of the player
        /*Text t = new Text(backgroundWidth, backgroundHeight, player.getNick() + " - " + player.getCharacter().toString());
        t.setX(x);
        t.setY(y);
        t.setFont(Font.font ("Verdana", 20));
        t.setFill(javafx.scene.paint.Color.WHITE);
        pane.getChildren().add(t);*/

        gc.drawImage( GuiImagesMap.getImage(imgRoot + "playerBoard/" + player.getCharacter().toString() + (adrenalineMode?"_A":"") + ".png"), x, y, width, height);

        //damages
        for(int i=0; i<12; i++){
            if(player.getReceivedDamage()[i] != null)
                gc.drawImage( GuiImagesMap.getImage(imgRoot + "drops/" + Player.fighterFromNick(players, player.getReceivedDamage()[i]) + ".png"), xDrop, yDrop, widthDrop, heightDrop);

            xDrop += deltaX;
        }

        //marks
        xDrop = 537 * pbMult + x;
        yDrop = 4 * pbMult + y;
        deltaX = widthDrop * 1.1; //put just a little bit of space, we don't know how many marks a player will get
        for(String p: player.getReceivedMarks()){
            if(p != null)
                gc.drawImage( GuiImagesMap.getImage(imgRoot + "drops/" + Player.fighterFromNick(players, p) + ".png"), xDrop, yDrop, widthDrop, heightDrop);

            xDrop += deltaX;
        }

        //for my player I need to ahve the actions clickable
        if(player.getNick() == match.getMyPlayer().getNick()){
            double actionsY = 53 / 225 * height;
            double actionsHeight = 47/225*height;
            double actionsWidth = 69/1121*width;

            run = new Canvas(actionsWidth, actionsHeight);
            run.setPickOnBounds(false);
            run.setTranslateX(x+0);
            run.setTranslateY(y+actionsY);
        }
    }

    private StackPane drawMyWeapons(List<Weapon> weapons){
        StackPane root = new StackPane();

        //dimensions are the same
        double width = 120 * dimMult;
        double height = 203 * dimMult;
        double x = 46 * dimMult;
        double y = 865 * dimMult;

        //calculate distance from board to board
        double deltaX = 136 * dimMult;

        myWeapons = new ArrayList<>();
        for(Weapon w : weapons){
            GuiCardWeapon card = new GuiCardWeapon(w, width, height, 0);
            myWeapons.add(card);
            root.getChildren().add(card);

            card.setPickOnBounds(false);

            card.setTranslateX(x);
            card.setTranslateY(y);
            x += deltaX;
        }

        return root;
    }

    private StackPane drawMyPowers(List<Power> powers){
        StackPane root = new StackPane();

        //dimensions are the same
        double width = 92 * dimMult;
        double height = 146 * dimMult;
        double x = 484 * dimMult;
        double y = 920 * dimMult;

        //calculate distance from board to board
        double deltaX = 102 * dimMult;

        myPowers = new ArrayList<>();
        for(Power p : powers){
            GuiCardPower card = new GuiCardPower(p, width, height);
            myPowers.add(card);
            card.setPosition(x, y);
            root.getChildren().add(card);
            x += deltaX;
        }

        return root;
    }

    private void drawMyAmmo(Ammunitions ammo){
        //dimensions are the same
        double width = 35 * dimMult;
        double x = 837 * dimMult;
        double y = 958 * dimMult;

        //calculate distance from board to board
        double deltaX = 43 * dimMult;
        double deltaY = 36 * dimMult;

        Image blue = GuiImagesMap.getImage("file:images/loot/blue.png");
        Image red = GuiImagesMap.getImage("file:images/loot/red.png");
        Image yellow = GuiImagesMap.getImage("file:images/loot/yellow.png");

        for(int i=0; i<ammo.getBlue(); i++){
            gc.drawImage(blue, x, y, width, width);
            x += deltaX;
        }
        x = 837 * dimMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getRed(); i++){
            gc.drawImage(red, x, y, width, width);
            x += deltaX;
        }
        x = 837 * dimMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getYellow(); i++){
            gc.drawImage(yellow, x, y, width, width);
            x += deltaX;
        }
    }

    private StackPane drawPoints(int points){

        StackPane s = new StackPane();
        double x = 1055 * dimMult;
        double y = 970 * dimMult;

        //FIXME show points @ the right pos like players' names
        //s.getChildren().add(new Text(x, y, Integer.toString(points)));

        return s;
    }

    /**
     * chooseAction
     */
    private void chooseAction(){}

    /**
     * Used for: getNickname, getPhrase, getFighter, getSkulls
     */
    private void chooseUserSettings(){}

    /**
     * Used for: chooseWeapon, grapWeapon, reload, discardWeapon
     */
    private void chooseWeaponCard(List<Weapon> choosable){
        List<GuiCardWeapon> cards = lootWeapons.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());
        cards.addAll(myWeapons.stream().filter(c->c.inList(choosable)).collect(Collectors.toList())); //add also my cards

        for(GuiCardWeapon c : cards){
            c.setOnMousePressed(e -> {
                System.out.println("Clicked a super-weapon " + c.getWeapon().getName());
                exchanger.setAnswer(c.getWeapon());
                exchanger.setActualInteraction(Interaction.NONE);
                //After finishing the click event, reset all the events to the original option
                for(GuiCardWeapon c2 : cards)
                    c2.resetEventsStyle();
            });
            c.setEventsChoosable();
        }

        exchanger.setActualInteraction(Interaction.WAITINGUSER);
    }

    /**
     * Used for: discardPower, choosePower
     */
    private void choosePowerCard(List<Power> choosable){
        List<GuiCardPower> cards = myPowers.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());

        for(GuiCardPower c : cards){
            c.setOnMousePressed(e -> {
                System.out.println("Clicked a super-weapon " + c.getPower().getName());
                exchanger.setAnswer(c.getPower());
                exchanger.setActualInteraction(Interaction.NONE);
                //After finishing the click event, reset all the events to the original option
                for(GuiCardPower c2 : cards)
                    c2.resetEventsStyle();
            });
            c.setEventsChoosable();
        }

        exchanger.setActualInteraction(Interaction.WAITINGUSER);
    }

    /**
     * Used for: movePlayer, choosePosition
     */
    private void chooseCell(){}

    /**
     * Used for: chooseTarget, moveEnemy
     */
    private void chooseEnemy(){}

    /**
     * Used for: chooseRoom, chooseDirection, chooseMap, chooseFrenzy
     */
    private void chooseDialog(String message){
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Here...");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!\n"+message);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
                exchanger.setActualInteraction(Interaction.NONE);
            }
        });

        exchanger.setActualInteraction(Interaction.WAITINGUSER);*/
    }

    private void initForTest() throws FileNotFoundException {
        //Settings for testing
        Game allGame = Game.jsonDeserialize("resources/baseGame.json");
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("p1", "!", Fighter.VIOLETTA));
        players.add(new Player("p2", "!", Fighter.DSTRUTTOR3));
        players.add(new Player("p3", "!", Fighter.SPROG));
        players.add(new Player("p4", "!", Fighter.BANSHEE));
        players.add(new Player("p5", "!", Fighter.DOZER));

        Player me = players.get(0);

        me.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            allGame.getWeaponsDeck().shuffle();
            weapons[0] = allGame.getWeaponsDeck().draw();
            weapons[1] = allGame.getWeaponsDeck().draw();

            allGame.getPowersDeck().shuffle();
            powers[0] = allGame.getPowersDeck().draw();
            powers[1] = allGame.getPowersDeck().draw();

            ammo.add(Color.YELLOW, 2);
            ammo.add(Color.BLUE, 1);

            damage[0] = "p2";
            damage[1] = "p2";
            damage[2] = "p3";

            marks.addAll(Arrays.asList("p2", "p3", "p3"));
            marks.addAll(Arrays.asList("p4", "p5", "p4"));
        }));

        allGame.loadMap(1);

        for(int x = 0; x < 4; x++)
            for(int y = 0; y < 3; y++)
                if(allGame.getMap().getCell(x, y) != null)
                    allGame.getMap().getCell(x, y).refill(allGame);

        //it's just for test
        for(Player p:players){
            int x, y;
            do {
                x = new Random().nextInt(4);
                y = new Random().nextInt(3);
            }while(allGame.getMap().getCell(x, y) == null);

            allGame.getMap().getCell(x, y).addPawn(p);
        }

        game = new GameView(allGame.getMap(), players, null);

        match = new MatchView(game, me, me, 3, GamePhase.REGULAR, true, me);
    }
}