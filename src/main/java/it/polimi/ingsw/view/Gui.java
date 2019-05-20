package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GamePhase;
import it.polimi.ingsw.controller.Interaction;
import it.polimi.ingsw.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class Gui extends Application{
    public static String imgRoot = "file:images/";
    private static Executor uiExec = Platform::runLater ;

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
    private List<GuiCardPawn> playersPawns;
    private Canvas run;
    private Canvas fixedGraphics;
    //private Canvas shoot;
    private Stage primaryS;

    //x and y of the user boaard
    private double xPlayerBoard;
    private double yPlayerBoard;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryS = primaryStage;

        backgroundWidth = 960;
        backgroundHeight = backgroundWidth*9/16;

        dimMult = backgroundWidth/1920;

        initForTest();

        //mainScene = new Scene(drawGame());

        primaryStage.setTitle("Adrenalina");
        primaryStage.setScene(new Scene(settingsBackground()));
        //primaryStage.setScene(mainScene);
        //primaryStage.setResizable(true);
        //primaryStage.setFullScreen(true);
        primaryStage.show();

        //Event handlers
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            /*if(abs(newVal.doubleValue() - backgroundWidth) > 20) {
                backgroundWidth = newVal.doubleValue();
                backgroundHeight = backgroundWidth * 9 / 16;
                dimMult = backgroundWidth / 1920;

                drawDecks();
                primaryStage.setScene(new Scene(drawGame()));
            }*/
        });

       //TODO use this for fixed ratio, find the good way to implement them
        //primaryStage.minHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16));
        //primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16));

        //Run the thread that handles the connection with the GuiInterface
        new Thread(this::listenRequests).start();
    }

    private Pane settingsBackground(){
        //socket+rmi and ip
        StackPane root = new StackPane();
        Canvas c = new Canvas(backgroundWidth, backgroundHeight);
        GraphicsContext gc = c.getGraphicsContext2D();

        gc.drawImage(GuiImagesMap.getImage(imgRoot + "adrenalina.jpg"), 0, 0, backgroundWidth, backgroundHeight);

        root.getChildren().addAll(c);
        return root;
    }

    private Pane drawGame(){
        Pane masterPane;
        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        StackPane a, b, c, d, e;

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

        drawAllPlayersBoards(match.getGame().getPlayers(),false); //FRENZY?
        drawMyAmmo(match.getMyPlayer().getAmmo());

        a = drawMyWeapons(match.getMyPlayer().getWeapons());
        b = drawMyPowers(match.getMyPlayer().getPowers());
        c = drawWeaponsLoot(match.getGame().getMap());
        d = drawLootOnMap(match.getGame().getMap());
        e = drawPawnsOnMap(match.getGame().getMap());

        a.setPickOnBounds(false);
        b.setPickOnBounds(false);
        c.setPickOnBounds(false);
        d.setPickOnBounds(false);
        e.setPickOnBounds(false);
        masterPane.setPickOnBounds(false);
        masterPane.getChildren().addAll( fixedGraphics, canvas,  a, b, c, d, e);

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

    private StackPane drawPawnsOnMap (Map map){
        StackPane root = new StackPane();
        double size = 50 * dimMult;
        double baseX = 224 * dimMult;
        double baseY = 224 * dimMult;
        double x;
        double y;
        double deltaCellX = 186 * dimMult;
        double deltaCellY = 200 * dimMult;
        boolean xNotY;

        //FIXME actually it print a max of 3 players in a single cell because of the wrong managing of the xNotY, find a better way
        playersPawns = new ArrayList<>();
        for(int j=0; j<3; j++){
            for(int i=0; i<4; i++){
                xNotY = true;
                x = baseX + i * (deltaCellX + (i==3?10:0)); //the last column is farther
                y = baseY + j * deltaCellY;
                if(map.getCell(i, j) != null) { //here X and Y are pointing at the top-left corner of the cell
                    for (Player p : map.getCell(i, j).getPawns()) {
                        GuiCardPawn pawn = new GuiCardPawn(p, size);
                        pawn.setPosition(x, y);

                        playersPawns.add(pawn);
                        root.getChildren().add(pawn);

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

        return root;
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
        gc.drawImage(GuiImagesMap.getImage(imgRoot + "power/powerBackPile.png"), x, y, width, height);

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

        if(player.getNick().equals(match.getMyPlayer().getNick())){
            xPlayerBoard = x;
            yPlayerBoard = y;
            setActionsClickable();//TODO just for text, remove it later
        }

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

    private void setActionsClickable(){
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

    //
    //
    //
    //

    private void listenRequests(){
        //TODO decide how to handle the possibility of a resize while a request in in action. we can:
        // a) reset the actualInteraction so this code will re-run FAST
        // b) not re-paint all the stuffs but just move&resize them CLEANER

        //TODO implement the mustChoose for the ones that need it
        //On my linux (Andrea) I need to wait a while the javafx app before starting the tests
        try{Thread.sleep(1500);}catch (InterruptedException e){ ; }

        exchanger = GuiExchanger.getInstance();
        while(exchanger.getActualInteraction()!=Interaction.CLOSEAPP) {
            if (exchanger.guiRequestIncoming()) {
                System.out.println(exchanger.getActualInteraction().toString());
                switch (exchanger.getActualInteraction()) {
                    case CHOOSEACTION:
                        chooseAction();//todo
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
                        chooseCell();//todo
                        break;
                    case CHOOSETARGET:
                        chooseEnemy((List<Player>) exchanger.getRequest());
                        break;
                    case CHOOSEROOM:
                        showAlert(this::guiChooseRoom, exchanger.getMessage());
                        break;
                    case CHOOSEDIRECTION:
                        showAlert(this::guiChooseDirection, exchanger.getMessage());
                        break;
                    case CHOOSEPOSITION:
                        //chooseCell(); //todo
                    case CHOOSEMAP:
                        showAlert(this::guiChooseMap, exchanger.getMessage());
                        break;
                    case CHOOSEFRENZY:
                        showAlert(this::guiChooseFrenzy, exchanger.getMessage());
                        break;
                    case SERVERIP:
                    case GETNICKNAME:
                    case GETPHRASE:
                        showAlert(this::askSetting, exchanger.getMessage());
                        break;
                    case RMIORSOCKET:
                        showAlert(this::askRMI, exchanger.getMessage());
                        break;
                    case GETSKULLSNUM:
                        showAlert(this::guiChooseSkulls, exchanger.getMessage());
                        break;
                    case GETFIGHTER:
                        showAlert(this::guiChooseFighter, exchanger.getMessage());
                        break;
                    case UPDATEVIEW://todo test this
                        primaryS.setScene(new Scene(drawGame()));
                        //or
                        //uiExec.execute(() -> primaryS.setScene(new Scene(drawGame())));
                        break;
                    case MOVEENEMY: //Nothing to do, not used in this context
                    case NONE:
                    default:
                        break;
                }
            }
        }

        //TODO here close the javafx app
    }


    private void askSetting(String message){
        Pane root = settingsBackground();
        double xText = 712 * dimMult;
        double yText = 434 * dimMult;
        double yBox = 500 * dimMult;
        double maxWidth = 480 * dimMult;
        double xButton = 894 * dimMult;
        double yButton = 569 * dimMult;

        double halfHeight = backgroundHeight/2;
        double halfWidth = backgroundWidth/2;

        Label l = new Label(message);
        //l.setTranslateX(xText-halfWidth);
        //l.setTranslateY(yText-halfHeight);
        l.setTranslateY(-50);

        TextField field = new TextField(message);
        System.out.println("before: " + field.getTranslateX() + "   " + field.getTranslateY());
        field.setMaxWidth(maxWidth);
        field.getText();
        field.setAlignment(Pos.TOP_LEFT);
        //field.setTranslateX(xText-halfWidth);
        //field.setTranslateY(yBox-halfHeight);
        field.setTranslateY(-10);
        System.out.println("after: " + field.getPadding().toString() + "   " + field.getTranslateX() + "   " + field.getTranslateY());


        Button submit = new Button("Conferma");
        //submit.setTranslateX(xButton-halfWidth);
        //submit.setTranslateY(yButton-halfHeight);
        submit.setTranslateY(+25);

        submit.setOnAction((e)->{
            String answer = field.getText();
            System.out.println(answer);
            exchanger.setAnswer(answer);
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(settingsBackground()));
        });

        root.getChildren().addAll(l, field, submit);

        primaryS.setScene(new Scene(root));
    }

    private void askRMI(String message){
        Pane root = settingsBackground();
        double xText = 712 * dimMult;
        double yText = 434 * dimMult;
        double yBox = 500 * dimMult;
        double maxWidth = 480 * dimMult;
        double xButton = 894 * dimMult;
        double yButton = 569 * dimMult;

        Label l = new Label(message);
        l.setTranslateX(xText);
        l.setTranslateY(yText);

        ToggleGroup group = new ToggleGroup();
        RadioButton button1 = new RadioButton("Socket");
        button1.setToggleGroup(group);
        button1.setSelected(true);
        button1.setTranslateX(xText);
        button1.setTranslateY(yBox);
        RadioButton button2 = new RadioButton("RMI");
        button2.setToggleGroup(group);
        button2.setTranslateX(xText + (maxWidth/2));
        button2.setTranslateY(yBox);

        Button submit = new Button("Conferma");
        submit.setTranslateX(xButton);
        submit.setTranslateY(yButton);

        submit.setOnAction((e)->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(answer.equalsIgnoreCase("RMI"));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(settingsBackground()));
        });

        root.getChildren().addAll(l, button1, button2, submit);

        primaryS.setScene(new Scene(root));
    }

    /**
     * chooseAction
     */
    private void chooseAction(){}


    /**
     * Used for: chooseWeapon, grapWeapon, reload, discardWeapon
     */
    private void chooseWeaponCard(List<Weapon> choosable){
        showAlert(this::guiShowInfo, exchanger.getMessage());

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
        showAlert(this::guiShowInfo, exchanger.getMessage());

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
     * Used for: movePlayer, choosePosition, moveEnemy
     */
    private void chooseCell(){}

    /**
     * Used for: chooseTarget
     */
    private void chooseEnemy(List<Player> choosable){
        showAlert(this::guiShowInfo, exchanger.getMessage());

        List<GuiCardPawn> pawns = playersPawns.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());

        for(GuiCardPawn p : pawns){
            p.setOnMousePressed(e -> {
                System.out.println("Clicked player: " + p.getPlayer().getNick());
                exchanger.setAnswer(p.getPlayer());
                exchanger.setActualInteraction(Interaction.NONE);
                //After finishing the click event, reset all the events to the original option
                for(GuiCardPawn p2 : pawns)
                    p2.resetEventsStyle();
            });
            p.setEventsChoosable();
        }

        exchanger.setActualInteraction(Interaction.WAITINGUSER);
    }

    /**
     * It's the enter point for all the alerts/dialogs. It sets the executor for the next task
     * Used for: information messages, chooseRoom(List<Integer>), chooseDirection(List<Direction>), chooseMap<Listint>->Integet, chooseFrenzy bool
     * @param dialog the method that handle the dialog you want to show
     */
    private void showAlert(Consumer<String> dialog, String message){
        exchanger.setActualInteraction(Interaction.WAITINGUSER);
        uiExec.execute(() -> dialog.accept(message));
    }

    private void guiShowInfo(String message){
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Adrenalina");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK)
                exchanger.setActualInteraction(Interaction.WAITINGUSER);
        });
    }

    /**
     * Used for: getNickname, getPhrase
     */
    private void guiAskAString(String message){
        TextInputDialog dialog = new TextInputDialog(" ");
        dialog.setTitle("Adrenalina");
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setContentText(message);

        dialog.showAndWait().ifPresent(answer -> {
            System.out.println("Answer: " + answer);
            exchanger.setAnswer(answer);
            exchanger.setActualInteraction(Interaction.NONE);
        });
    }

    private void guiChooseRMIorSocket(String message){
        ToggleGroup group = new ToggleGroup();
        RadioButton button1 = new RadioButton("Socket");
        button1.setToggleGroup(group);
        button1.setSelected(true);
        RadioButton button2 = new RadioButton("RMI");
        button2.setToggleGroup(group);
    }

    private void guiChooseFighter(String message){
        List<Fighter> available = (List<Fighter>) exchanger.getRequest();
        List<ButtonType> btns = new ArrayList<>();

        for(Fighter f:available)
            btns.add(new ButtonType(f.toString(), ButtonBar.ButtonData.OK_DONE));

        Alert alert = new Alert(Alert.AlertType.NONE, message);
        alert.getButtonTypes().setAll(btns);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Adrenalina");

        alert.showAndWait().ifPresent(rs -> {
            System.out.println(rs.getText());
            exchanger.setAnswer(Fighter.valueOf(rs.getText()));
            exchanger.setActualInteraction(Interaction.NONE);
        });
    }

    private void guiChooseSkulls(String message){
        List<ButtonType> btns = new ArrayList<>();

        for(int i=5; i<=8; i++)
            btns.add(new ButtonType(Integer.toString(i), ButtonBar.ButtonData.OK_DONE));

        Alert alert = new Alert(Alert.AlertType.NONE, message);
        alert.getButtonTypes().setAll(btns);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Adrenalina");

        alert.showAndWait().ifPresent(rs -> {
            System.out.println(rs.getText());
            exchanger.setAnswer(Integer.valueOf(rs.getText()));
            exchanger.setActualInteraction(Interaction.NONE);
        });
    }

    private void guiChooseRoom(String message){
        List<Integer> rooms = (List<Integer>) exchanger.getRequest();
        List<ButtonType> btns = new ArrayList<>();

        if(rooms.contains(0))
            btns.add(new ButtonType("Rossa", ButtonBar.ButtonData.OK_DONE));
        if(rooms.contains(1))
            btns.add(new ButtonType("Bianca", ButtonBar.ButtonData.OK_DONE));
        if(rooms.contains(2))
            btns.add(new ButtonType("Blu", ButtonBar.ButtonData.OK_DONE));
        if(rooms.contains(3))
            btns.add(new ButtonType("Viola", ButtonBar.ButtonData.OK_DONE));
        if(rooms.contains(4))
            btns.add(new ButtonType("Verde", ButtonBar.ButtonData.OK_DONE));
        if(rooms.contains(5))
            btns.add(new ButtonType("Gialla", ButtonBar.ButtonData.OK_DONE));

        Alert alert = new Alert(Alert.AlertType.NONE, message);
        alert.getButtonTypes().setAll(btns);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Adrenalina");

        alert.showAndWait().ifPresent(rs -> {
            System.out.println(rs.getText());
            switch (rs.getText()){
                case "Rossa":
                    exchanger.setAnswer(0);
                    break;
                case "Bianca":
                    exchanger.setAnswer(1);
                    break;
                case "Blu":
                    exchanger.setAnswer(2);
                    break;
                case "Viola":
                    exchanger.setAnswer(3);
                    break;
                case "Verde":
                    exchanger.setAnswer(4);
                    break;
                case "Gialla":
                    exchanger.setAnswer(5);
                    break;
                default:
                    Logger.getGlobal().log(Level.SEVERE, "Error while choosing the room");
            }
            exchanger.setActualInteraction(Interaction.NONE);
        });
    }

    private void guiChooseDirection(String message){
        List<Direction> dirs = (List<Direction>) exchanger.getRequest();
        List<ButtonType> btns = new ArrayList<>();

        for(Direction d:dirs)
            btns.add(new ButtonType(d.toString(), ButtonBar.ButtonData.OK_DONE));

        Alert alert = new Alert(Alert.AlertType.NONE, message);
        alert.getButtonTypes().setAll(btns);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Adrenalina");

        alert.showAndWait().ifPresent(rs -> {
            System.out.println(rs.getText());
            exchanger.setAnswer(Direction.valueOf(rs.getText()));
            exchanger.setActualInteraction(Interaction.NONE);
        });
    }

    private void guiChooseMap(String message){
        ButtonType m1 = new ButtonType("Mappa 1", ButtonBar.ButtonData.OK_DONE);
        ButtonType m2 = new ButtonType("Mappa 2", ButtonBar.ButtonData.OK_DONE);
        ButtonType m3 = new ButtonType("Mappa 3", ButtonBar.ButtonData.OK_DONE);
        ButtonType m4 = new ButtonType("Mappa 4", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.NONE, message, m1, m2, m3, m4);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Adrenalina");

        alert.showAndWait().ifPresent(rs -> {
            System.out.println(rs.getText());
            switch (rs.getText()){
                case "Mappa 1":
                    exchanger.setAnswer(1);
                    break;
                case "Mappa 2":
                    exchanger.setAnswer(2);
                    break;
                case "Mappa 3":
                    exchanger.setAnswer(3);
                    break;
                case "Mappa 4":
                    exchanger.setAnswer(4);
                    break;
                default:
                    break;
            }
            exchanger.setActualInteraction(Interaction.NONE);
        });
    }

    private void guiChooseFrenzy(String message){
        ButtonType frenzy = new ButtonType("Con frenesia", ButtonBar.ButtonData.OK_DONE);
        ButtonType noFrenzy = new ButtonType("Senza frenesia", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.NONE, message, frenzy, noFrenzy);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Adrenalina");

        alert.showAndWait().ifPresent(rs -> {
            System.out.println(rs.getText());
            exchanger.setAnswer(rs == frenzy);
            exchanger.setActualInteraction(Interaction.NONE);
        });
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