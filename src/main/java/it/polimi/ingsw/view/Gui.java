package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Interaction;
import it.polimi.ingsw.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Math.abs;


//TODO check inputs like the ip
public class Gui extends Application{
    static String imgRoot = "file:images/";//for the entire package only
    private static Executor uiExec = Platform::runLater ;

    //Data
    private MatchView match;
    private GuiExchanger exchanger;

    //View
    private double backgroundWidth;
    private double backgroundHeight;
    private double dimMult;
    private GraphicsContext gc;

    //Canvases
    private List<GuiCardWeapon> lootWeapons;
    private List<GuiCardWeapon> myWeapons;
    private List<GuiCardPower> myPowers;
    private List<GuiCardPawn> playersPawns;

    //Actions -> we handle the clicks on these Canvases
    private GuiCardClickableArea runAction;
    private GuiCardClickableArea pickAction;
    private GuiCardClickableArea shootAction;
    private GuiCardClickableArea adrPickAction;
    private GuiCardClickableArea adrShootAction;

    //Move in cells
    private GuiCardClickableArea[][] mapOfCells = new GuiCardClickableArea[4][3];

    //Canvases
    private Canvas infoTextCanvas; //canvas where we write the infos for the users

    //Stage
    private Stage primaryS;

    @Override
    public void start(Stage primaryStage){
        primaryS = primaryStage;

        backgroundWidth = 960;
        backgroundHeight = backgroundWidth*9/16;

        dimMult = backgroundWidth/1920;

        primaryStage.setTitle("Adrenalina");
        Canvas loading = new Canvas(backgroundWidth, backgroundHeight);
        loading.getGraphicsContext2D().drawImage(GuiImagesMap.getImage(imgRoot + "background/adrenalina.jpg"), 0, 0, backgroundWidth, backgroundHeight);
        primaryStage.setScene(new Scene(new StackPane(loading)));
        //primaryStage.setScene(new Scene(drawGame()));
        primaryStage.setResizable(true);
        //primaryStage.setFullScreen(true);
        primaryStage.show();

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

        //OnClose
        primaryStage.setOnCloseRequest((t)->{
            Platform.exit();
            System.exit(0);
        });


       //TODO use this for fixed ratio, find the good way to implement them
        //primaryStage.minHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16));
        //primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16));

        //Run the thread that handles the connection with the GuiInterface
        new Thread(this::listenRequests).start();
    }

    private StackPane initializerBackground(){
        StackPane root = new StackPane();
        Canvas c = new Canvas(backgroundWidth, backgroundHeight);
        c.getGraphicsContext2D().drawImage(GuiImagesMap.getImage(imgRoot + "background/adrenalinaWithBox.jpg"), 0, 0, backgroundWidth, backgroundHeight);
        root.getChildren().addAll(c);
        return root;
    }

    private Pane drawGame(){
        Pane masterPane;
        Canvas canvas;
        StackPane a, b, c, d, e;

        masterPane = new Pane();

        canvas = new Canvas(backgroundWidth, backgroundHeight);
        gc = canvas.getGraphicsContext2D();

        gc.drawImage( GuiImagesMap.getImage(imgRoot + "background/gameBoard.png"), 0, 0, backgroundWidth, backgroundHeight);
        drawMap(match.getGame().getMap());
        drawDecks();

        //weapons, powers, loot
        //gc = canvas.getGraphicsContext2D(); //from now on the global gc will be the one for the dynamics data

        drawAllPlayersBoards(match.getGame().getPlayers(),false); //FRENZY?
        drawMyAmmo(match.getMyPlayer().getAmmo());
        drawPoints(10);

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

        //canvas for text
        infoTextCanvas = new Canvas(backgroundWidth, backgroundHeight);
        infoTextCanvas.setPickOnBounds(false);

        //infoTextCanvas needs to be before the clickable ones because the PickOnBounds doesn't work
        masterPane.getChildren().addAll( canvas, infoTextCanvas,  a, b, c, d, e, runAction, pickAction, shootAction, adrPickAction, adrShootAction);

        for(GuiCardClickableArea[] t:mapOfCells)
            for(GuiCardClickableArea s:t)
                if(s!=null)
                    masterPane.getChildren().add(s);

        return masterPane;
    }

    private void drawMap(Map map){
        double width = 1142 * dimMult;
        double height = 866 * dimMult;
        double x = 18 * dimMult;
        double y = x;

        gc.drawImage( GuiImagesMap.getImage("file:images/map/map" + map.getId() + ".png"), x, y, width, height);

        //get position of the first cell
        x = ((double)500)/2545 * width;
        y = ((double)480)/1928 * height;
        double distX = ((double)430)/2545 * width;//here because after we reuse the width and height
        double distY = ((double)450)/1928 * height;
        width = ((double)330)/2545 * width;
        height = ((double)360)/1928 * height;

        for(int xC=0; xC<4; xC++) {
            for (int yC = 0; yC < 3; yC++) {
                if(map.getCell(xC, yC) != null) {
                    mapOfCells[xC][yC] = new GuiCardClickableArea(x+ distX*xC, y + distY*yC, width, height);
                }
            }
        }

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
        double positionFix = 35; //position fix for drawing the weapons on the map
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

        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(new Font("Verdana",18*dimMult));
        gc.fillText(player.getNick() + " - " + player.getCharacter().toString(), x, y-(8*dimMult));

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
        //todo do the finalfrenzy too
        if(player.getNick().equals(match.getMyPlayer().getNick())){
            double actionsY = ((float)48)/ 270 * height;
            double actionsHeight = ((float)42)/270*height;
            double actionsWidth = ((float)69)/1121*width;

            runAction = new GuiCardClickableArea(x, y+actionsY, actionsWidth, actionsHeight);
            pickAction = new GuiCardClickableArea(x, y + 2*actionsY, actionsWidth, actionsHeight);
            shootAction = new GuiCardClickableArea(x, y + 3*actionsY, actionsWidth, actionsHeight);

            actionsY = ((float)56)/ 270 * height;
            adrPickAction = new GuiCardClickableArea(x + ((float)230)/1121*width, y+actionsY, actionsWidth, actionsHeight);
            adrShootAction = new GuiCardClickableArea(x + ((float)423)/1121*width, y+actionsY, actionsWidth, actionsHeight);
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

    private void drawPoints(int points){
        double x = 1055 * dimMult;
        double y = 970 * dimMult;

        gc.setFont(new Font("Verdana",18*dimMult));
        gc.setFill(javafx.scene.paint.Color.WHITE); //FIXME it draws the points in black
        gc.strokeText(Integer.toString(points), x, y);
    }

    //
    //
    //
    //

    private void listenRequests(){
        //TODO decide how to handle the possibility of a resize while a request in in action. we can:
        // a) reset the actualInteraction so this code will re-runAction FAST
        // b) not re-paint all the stuffs but just move&resize them CLEANER

        //TODO implement the mustChoose for the ones that need it
        //On my linux (Andrea) I need to wait a while the javafx app before starting the tests
        try{Thread.sleep(1500);}catch (InterruptedException e){ ; }

        exchanger = GuiExchanger.getInstance();
        while(exchanger.getActualInteraction()!=Interaction.CLOSEAPP) {
            if (exchanger.guiRequestIncoming()) {
                System.out.println(exchanger.getActualInteraction().toString());
                switch (exchanger.getActualInteraction()) {
                    case CHOOSEBASEACTION:
                        chooseBaseAction();
                        break;
                    case CHOOSEWEAPON:
                    case GRABWEAPON:
                    case DISCARDWEAPON:
                    case RELOAD:
                        chooseWeaponCard();
                        break;
                    case DISCARDPOWER:
                    case CHOOSEPOWER:
                        choosePowerCard();
                        break;
                    case MOVEPLAYER:
                    case MOVEENEMY:
                    case CHOOSEPOSITION:
                        chooseCell();
                        break;
                    case CHOOSETARGET:
                        chooseEnemy();
                        break;
                    case CHOOSEROOM:
                        showAlert(this::guiChooseRoom, exchanger.getMessage());
                        break;
                    case CHOOSEDIRECTION:
                        showAlert(this::guiChooseDirection, exchanger.getMessage());
                        break;
                    case CHOOSEMAP:
                        showAlert(this::askMap, exchanger.getMessage());
                        break;
                    case CHOOSEFRENZY:
                        showAlert(this::askFrenzy, exchanger.getMessage());
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
                        showAlert(this::askSkulls, exchanger.getMessage());
                        break;
                    case GETFIGHTER:
                        showAlert(this::askFighter, exchanger.getMessage());
                        break;
                    case UPDATEVIEW://todo get the new match
                        uiExec.execute(() -> {
                            match = (MatchView) exchanger.getRequest();
                            primaryS.setScene(new Scene(drawGame()));
                            exchanger.setActualInteraction(Interaction.NONE);
                        });
                        exchanger.setActualInteraction(Interaction.WAITINGUSER);
                        break;
                    case NONE:
                    default:
                        break;
                }
            }
        }
        Platform.exit();
    }

    /**
     * chooseBaseAction
     */
    private void chooseBaseAction(){
        exchanger.setActualInteraction(Interaction.WAITINGUSER);
        List<Action> possible = (List<Action>) exchanger.getRequest();

        for(Action a:possible){
            switch (a.getLambdaID()){
                case "a-b1":
                    runAction.setOnMousePressed(e -> {
                        exchanger.setAnswer(a);
                        exchanger.setActualInteraction(Interaction.NONE);
                        //After finishing the click event, reset all the events to the original option -> just call the redraw game
                    });
                    runAction.setEventsChoosable();
                    break;
                case "a-b2":
                    pickAction.setOnMousePressed(e -> {
                        exchanger.setAnswer(a);
                        exchanger.setActualInteraction(Interaction.NONE);
                    });
                    pickAction.setEventsChoosable();
                    break;
                case "a-b3":
                    shootAction.setOnMousePressed(e -> {
                        exchanger.setAnswer(a);
                        exchanger.setActualInteraction(Interaction.NONE);
                    });
                    shootAction.setEventsChoosable();
                    break;
                case "a-a1":
                    adrPickAction.setOnMousePressed(e -> {
                        exchanger.setAnswer(a);
                        exchanger.setActualInteraction(Interaction.NONE);
                    });
                    adrPickAction.setEventsChoosable();
                    break;
                case "a-a2":
                    adrShootAction.setOnMousePressed(e -> {
                        exchanger.setAnswer(a);
                        exchanger.setActualInteraction(Interaction.NONE);
                    });
                    adrShootAction.setEventsChoosable();
                    break;
                default:
                    break;
                //todo add the finalfrenzy ones
            }
        }
    }

    /**
     * Used for: chooseWeapon, grapWeapon, reload, discardWeapon
     */
    private void chooseWeaponCard(){
        showInfoOnMap(exchanger.getMessage());

        List<Weapon> choosable = (List<Weapon>) exchanger.getRequest();

        List<GuiCardWeapon> cards = lootWeapons.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());
        cards.addAll(myWeapons.stream().filter(c->c.inList(choosable)).collect(Collectors.toList())); //add also my cards

        for(GuiCardWeapon c : cards){
            c.setOnMousePressed(e -> {
                exchanger.setAnswer(c.getWeapon());
                exchanger.setActualInteraction(Interaction.NONE);
                clearInfoOnMap();
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
    private void choosePowerCard(){
        showInfoOnMap(exchanger.getMessage());

        List<Power> choosable = (List<Power>) exchanger.getRequest();

        List<GuiCardPower> cards = myPowers.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());

        for(GuiCardPower c : cards){
            c.setOnMousePressed(e -> {
                exchanger.setAnswer(c.getPower());
                exchanger.setActualInteraction(Interaction.NONE);
                clearInfoOnMap();
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
    private void chooseCell(){
        showInfoOnMap(exchanger.getMessage()); //fixme in the chooseCell it doesn't work!!!
        exchanger.setActualInteraction(Interaction.WAITINGUSER);

        List<Point> possible = (List<Point>) exchanger.getRequest();

        for(Point p:possible) {
            if (mapOfCells[p.getX()][p.getY()] != null) {
                mapOfCells[p.getX()][p.getY()].setOnMousePressed(e -> {
                    exchanger.setAnswer(p);
                    exchanger.setActualInteraction(Interaction.NONE);
                    clearInfoOnMap();
                    //After finishing the click event, reset all the events to the original option
                    for(GuiCardClickableArea[] t:mapOfCells)
                        for(GuiCardClickableArea s:t)
                            if(s!=null)
                                s.resetEventsStyle();
                });
                mapOfCells[p.getX()][p.getY()].setEventsChoosable();
            }
        }
    }

    /**
     * Used for: chooseTarget
     */
    private void chooseEnemy(){
        showInfoOnMap(exchanger.getMessage());

        List<Player> choosable = (List<Player>) exchanger.getRequest();

        List<GuiCardPawn> pawns = playersPawns.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());

        for(GuiCardPawn p : pawns){
            p.setOnMousePressed(e -> {
                System.out.println("Clicked player: " + p.getPlayer().getNick());
                exchanger.setAnswer(p.getPlayer());
                exchanger.setActualInteraction(Interaction.NONE);
                clearInfoOnMap();
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

    private GridPane gridMaker(){
        GridPane grid = new GridPane();
        double maxWidth = 480 * dimMult;

        grid.setAlignment(Pos.CENTER);
        //grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(maxWidth);
        grid.setMinWidth(maxWidth);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(maxWidth);
        grid.getColumnConstraints().add(cc);
        return grid;
    }

    /**
     * Used for IP, nick, phrase
     * @param message
     */
    private void askSetting(String message){
        StackPane root = initializerBackground();
        GridPane grid = gridMaker();

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));

        TextField field = new TextField();
        //field.setMaxWidth(maxWidth);

        Button submit = new Button("Conferma");
        submit.setOnAction((e)->{
            String answer = field.getText();
            System.out.println(answer);
            exchanger.setAnswer(answer);
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });
        submit.setDefaultButton(true);

        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);
        grid.add(field,0,1);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,2);

        root.getChildren().addAll(grid);

        primaryS.setScene(new Scene(root));
    }

    private void askRMI(String message){
        Pane root = initializerBackground();
        GridPane grid = gridMaker();

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));

        ToggleGroup group = new ToggleGroup();
        RadioButton radio1 = new RadioButton("Socket");
        radio1.setToggleGroup(group);
        radio1.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        radio1.setSelected(true);
        RadioButton radio2 = new RadioButton("RMI");
        radio2.setToggleGroup(group);
        radio2.setTextFill(javafx.scene.paint.Color.web("#ffffff"));

        Button submit = new Button("Conferma");
        submit.setOnAction((e)->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(answer.equalsIgnoreCase("RMI"));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });
        submit.setDefaultButton(true);

        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);
        grid.add(radio1,0,1);
        grid.add(radio2,0,2);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,3);

        root.getChildren().addAll(grid);

        primaryS.setScene(new Scene(root));
    }

    private void askFighter(String message){
        Pane root = initializerBackground();
        GridPane grid = gridMaker();

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);

        ToggleGroup radioGroup = new ToggleGroup();

        List<Fighter> available = (List<Fighter>) exchanger.getRequest();

        int row = 1;
        for(Fighter f:available){
            RadioButton radio = new RadioButton(f.toString());
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
            if(row==1)
                radio.setSelected(true);

            grid.add(radio,0,row);
            row++;
        }

        Button submit = new Button("Conferma");
        submit.setOnAction(rs -> {
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(Fighter.valueOf(answer));
            exchanger.setActualInteraction(Interaction.NONE);
        });
        submit.setDefaultButton(true);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        root.getChildren().addAll(grid);

        primaryS.setScene(new Scene(root));
    }

    private void askSkulls(String message){
        Pane root = initializerBackground();
        GridPane grid = gridMaker();

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);

        ToggleGroup radioGroup = new ToggleGroup();

        int row = 1;
        for(int i = 5; i<=8; i++){
            RadioButton radio = new RadioButton(Integer.toString(i));
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
            if(row==1)
                radio.setSelected(true);
            grid.add(radio,0,row);
            row++;
        }

        Button submit = new Button("Conferma");
        submit.setOnAction(rs -> {
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(Integer.parseInt(answer));
            exchanger.setActualInteraction(Interaction.NONE);
        });
        submit.setDefaultButton(true);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        root.getChildren().addAll(grid);

        primaryS.setScene(new Scene(root));
    }

    private void showInfoOnMap(String message) {
        double x = 40 * dimMult;
        double y = 845 * dimMult;

        infoTextCanvas.getGraphicsContext2D().clearRect(0,0, backgroundWidth, backgroundHeight);//we use always the same canvas

        infoTextCanvas.getGraphicsContext2D().setFill(javafx.scene.paint.Color.WHITE);
        infoTextCanvas.getGraphicsContext2D().setFont(new Font("Verdana",34*dimMult));
        infoTextCanvas.getGraphicsContext2D().fillText(message, x, y);
        infoTextCanvas.setPickOnBounds(false);
    }

    private void clearInfoOnMap(){
        infoTextCanvas.getGraphicsContext2D().clearRect(0,0, backgroundWidth, backgroundHeight);
    }

   /* half work done
   private void askRoom(String message){
        Pane root = initializerBackground();
        GridPane grid = gridMaker();

        List<Integer> rooms = (List<Integer>) exchanger.getRequest();

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);

        ToggleGroup radioGroup = new ToggleGroup();

        int row = 1;
        List<String> roomsNames = new ArrayList<>();

        if(rooms.contains(0))
            roomsNames.add("Rossa");
        if(rooms.contains(1))
            roomsNames.add("Bianca");
        if(rooms.contains(2))
            roomsNames.add("Blu");
        if(rooms.contains(3))
            roomsNames.add("Viola");
        if(rooms.contains(4))
            roomsNames.add("Verde");
        if(rooms.contains(5))
            roomsNames.add("Gialla");
        for(String s : roomsNames){
            RadioButton radio = new RadioButton(s);
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
            grid.add(radio,0,row);
            row++;
        }

        //continue here
        Button submit = new Button("Conferma");
        submit.setOnAction(rs -> {
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(Integer.parseInt(answer));
            exchanger.setActualInteraction(Interaction.NONE);
        });
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        root.getChildren().addAll(grid);

        primaryS.setScene(new Scene(root));
    }*/

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

    private void askMap(String message){
        Pane root = initializerBackground();
        GridPane grid = gridMaker();

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));

        int row = 1;
        ToggleGroup radioGroup = new ToggleGroup();
        for(int i = 5; i<=8; i++){
            RadioButton radio = new RadioButton("Mappa " + (i-4));
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
            grid.add(radio,0,row);
            row++;
        }

        Button submit = new Button("Conferma");
        submit.setOnAction((e)->{
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            answer = answer.substring(5);
            System.out.println(answer);
            exchanger.setAnswer(Integer.parseInt(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });
        submit.setDefaultButton(true);

        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        root.getChildren().addAll(grid);

        primaryS.setScene(new Scene(root));
    }

    private void askFrenzy(String message){
        Pane root = initializerBackground();
        GridPane grid = gridMaker();

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));

        ToggleGroup group = new ToggleGroup();
        RadioButton radio1 = new RadioButton("Con frenesia");
        radio1.setToggleGroup(group);
        radio1.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        radio1.setSelected(true);
        RadioButton radio2 = new RadioButton("Senza frenesia");
        radio2.setToggleGroup(group);
        radio2.setTextFill(javafx.scene.paint.Color.web("#ffffff"));

        Button submit = new Button("Conferma");
        submit.setOnAction((e)->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(answer.equalsIgnoreCase("Con frenesia"));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });
        submit.setDefaultButton(true);

        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);
        grid.add(radio1,0,1);
        grid.add(radio2,0,2);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,3);

        root.getChildren().addAll(grid);

        primaryS.setScene(new Scene(root));
    }
}