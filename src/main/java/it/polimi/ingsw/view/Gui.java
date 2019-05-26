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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

//On Monday together the background and the drops

//TODO (ALESSANDRO) check inputs like the ip
//todo (ELIA) add the decks directly on the image
//todo (ELIA) square around the cell when you don't want to move
//todo (ELIA) draw the skulls on the board
//todo (ALESSANDRO) do the fixme of the pawns
//todo (ANDREA) if a fourth power has been picked up, show it over the powers' deck for letting the user discard the card
//todo (EVERYONE) check that all the text are written
//todo (ALESSANDRO) add the settingsScreen "Waiting other users for the game"
//DONE (ANDREA) add the different IPs and make the radio button method reusable
//todo (ELIA) make the unloaded card of the user less visible
//DONE (ANDREA) make the popup reusable
//todo (ELIA)  make the popup for the unloaded enemies weapons

//FIXME It looks like the additional actions don't work. Add also the possibility of no use of the additional
//FIXME the grabWeapon method (in GuiInterface) seems to never be called. when you have to choose between weapons to grab the chooseWeapon is called instead (or at least it seems)
public class Gui extends Application{
    final static String MYFONT = "EthnocentricRg-Italic"; //todo fix this
    final static double SETTINGSFONTDIM = 28;
    final static double POPUPFONTDIM = 22;

    //Ui Executor
    private static Executor uiExec = Platform::runLater;

    //Data
    private MatchView match;
    private GuiExchanger exchanger;

    //View
    private double backgroundWidth;
    private double backgroundHeight;
    private double dimMult;
    private GraphicsContext gc;
    private Pane masterPane;
    private TextArea logArea;
    private String loggedText;

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
    private GuiCardClickableArea powerAction;

    private Canvas skipAction; //todo change the image with a rectangular one and put it just under the red spawn loot

    //Move in cells
    private GuiCardClickableArea[][] mapOfCells = new GuiCardClickableArea[4][3];

    //Canvases
    private Canvas infoTextCanvas; //canvas where we write the infos for the users

    //Stage
    private Stage primaryS;

    @Override
    public void start(Stage primaryStage){
        Font.loadFont(getClass().getResourceAsStream("font/ethnocentric_rg.ttf"), 14);
        loggedText = "";

        primaryS = primaryStage;

        backgroundWidth = 960;
        backgroundHeight = backgroundWidth*9/16;

        dimMult = backgroundWidth/1920;

        primaryStage.setTitle("Adrenalina");
        Canvas loading = new Canvas(backgroundWidth, backgroundHeight);
        loading.getGraphicsContext2D().drawImage(GuiImagesMap.getImage("background/adrenalina.jpg"), 0, 0, backgroundWidth, backgroundHeight);
        primaryStage.setScene(new Scene(new StackPane(loading)));
        primaryStage.setResizable(true);
        //primaryStage.setFullScreen(true);
        primaryStage.show();

        //Event handlers
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(abs(newVal.doubleValue() - backgroundWidth) > 20) {
                backgroundWidth = newVal.doubleValue();
                backgroundHeight = backgroundWidth * 9 / 16;
                dimMult = backgroundWidth / 1920;

                if(match != null) //if it's null it's still in the settings
                    primaryStage.setScene(new Scene(drawGame()));

                //ANDREA: on my linux with tiling, when one of these are called, the window is going to be resized
                if(exchanger.getLastRealInteraction() != Interaction.CHOOSEDIRECTION && exchanger.getLastRealInteraction() != Interaction.CHOOSEROOM)
                    exchanger.resetLastRealInteraction();
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
        c.getGraphicsContext2D().drawImage(GuiImagesMap.getImage("background/adrenalinaWithBox.jpg"), 0, 0, backgroundWidth, backgroundHeight);
        root.getChildren().addAll(c);
        return root;
    }

    private Pane drawGame(){
        Canvas canvas;
        StackPane myWeapons, MyPowers, weaponsLoot, mapLoot, pawns;

        masterPane = new Pane();

        canvas = new Canvas(backgroundWidth, backgroundHeight);
        gc = canvas.getGraphicsContext2D();

        gc.drawImage( GuiImagesMap.getImage("background/gameBoard.png"), 0, 0, backgroundWidth, backgroundHeight);
        drawMap(match.getGame().getMap());

        //weapons, powers, loot
        //gc = canvas.getGraphicsContext2D(); //from now on the global gc will be the one for the dynamics data

        drawAllPlayersBoards(match.getGame().getPlayers(),false); //FRENZY?
        drawMyAmmo(match.getMyPlayer().getAmmo());
        drawPoints(match.getMyPlayer().getPoints());

        myWeapons = drawMyWeapons(match.getMyPlayer().getWeapons());
        MyPowers = drawMyPowers(match.getMyPlayer().getPowers());
        weaponsLoot = drawWeaponsLoot(match.getGame().getMap());
        mapLoot = drawLootOnMap(match.getGame().getMap());
        pawns = drawPawnsOnMap(match.getGame().getMap());

        myWeapons.setPickOnBounds(false);
        MyPowers.setPickOnBounds(false);
        weaponsLoot.setPickOnBounds(false);
        mapLoot.setPickOnBounds(false);
        pawns.setPickOnBounds(false);
        masterPane.setPickOnBounds(false);

        //canvas for text
        infoTextCanvas = new Canvas(backgroundWidth, backgroundHeight);
        infoTextCanvas.setPickOnBounds(false);

        //canvases for the cells
        StackPane cellsClick = new StackPane();
        for(GuiCardClickableArea[] t:mapOfCells)
            for(GuiCardClickableArea s:t)
                if(s!=null)
                    cellsClick.getChildren().add(s);
        cellsClick.setPickOnBounds(false);

        logArea = new TextArea();
        logArea.setFont(new Font(MYFONT, 16*dimMult));
        logArea.setLayoutX(1222 * dimMult);
        logArea.setLayoutY(920 * dimMult);
        logArea.setMaxWidth(560 * dimMult);
        logArea.setMaxHeight(134 * dimMult);
        logArea.setEditable(false);
        logArea.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent;");
        logArea.setText(loggedText);
        logArea.setScrollTop(90000000);

        skipAction = new Canvas(backgroundWidth, backgroundHeight);
        skipAction.getGraphicsContext2D().drawImage(GuiImagesMap.getImage("skipAction.png"), 40*dimMult, 825*dimMult, 40*dimMult, 40*dimMult);
        skipAction.setPickOnBounds(false);

        masterPane.getChildren().addAll( canvas, infoTextCanvas,  myWeapons, MyPowers, weaponsLoot, mapLoot, cellsClick, pawns,
                runAction, pickAction, shootAction, powerAction, adrPickAction, adrShootAction, logArea);

        return masterPane;
    }

    private void drawMap(Map map){
        double width = 1142 * dimMult;
        double height = 866 * dimMult;
        double x = 18 * dimMult;
        double y = x;

        gc.drawImage( GuiImagesMap.getImage("map/map" + map.getId() + ".png"), x, y, width, height);

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
        if(map.getCell(0, 0) != null && ((RegularCell)map.getCell(0, 0)).getLoot() != null) {
            x = 310 * dimMult;
            y = 325 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(0, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 0) != null && ((RegularCell)map.getCell(1, 0)).getLoot() != null) {
            x = 526 * dimMult;
            y = 325 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(1, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 0) != null && ((RegularCell)map.getCell(3, 0)).getLoot() != null) {
            x = 900 * dimMult;
            y = 325 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(3, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 1) != null && ((RegularCell)map.getCell(1, 1)).getLoot() != null) {
            x = 526 * dimMult;
            y = 530 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(1, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 1) != null && ((RegularCell)map.getCell(2, 1)).getLoot() != null) {
            x = 725 * dimMult;
            y = 530 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(2, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 1) != null && ((RegularCell)map.getCell(3, 1)).getLoot() != null) {
            x = 900 * dimMult;
            y = 530 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(3, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(0, 2) != null && ((RegularCell)map.getCell(0, 2)).getLoot() != null) {
            x = 335 * dimMult;
            y = 730 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(0, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 2) != null && ((RegularCell)map.getCell(1, 2)).getLoot() != null) {
            x = 526 * dimMult;
            y = 730 * dimMult;
            GuiCardLoot card = new GuiCardLoot(((RegularCell)map.getCell(1, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 2) != null && ((RegularCell)map.getCell(2, 2)).getLoot() != null) {
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

    private void drawAllPlayersBoards(List<Player> players, boolean frenzyMode){
        //dimensions are the same for all the players
        double width = 560 * dimMult;
        double height = 134 * dimMult;
        double x = 1222 * dimMult;
        double y = 74 * dimMult;

        //calculate distance from board to board
        double deltaY = 169 * dimMult;

        for(Player p : players){
            drawPlayerBoard(p, players, frenzyMode, width, height, x, y);
            y += deltaY;
        }
    }

    private void drawPlayerBoard(Player player, List<Player> players, boolean frenzyMode, double width, double height, double x, double y){
        double pbMult = width/1123; //(dimMult * width@1080p)/textureWidth -> internal reference based on the card
        double xDrop = (frenzyMode?130:116) * pbMult + x;
        double yDrop = 116 * pbMult + y;
        double widthDrop = 30 * pbMult;
        double heightDrop = 45 * pbMult;

        double deltaX = (frenzyMode?61:63) * pbMult;

        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(new Font(MYFONT,18*dimMult));
        gc.fillText(player.getNick() + " - " + player.getCharacter().toString(), x, y-(8*dimMult));

        gc.drawImage( GuiImagesMap.getImage("playerBoard/" + player.getCharacter().toString() + (frenzyMode?"_F":"") + ".png"), x, y, width, height);

        //damages
        for(int i=0; i<12; i++){
            if(player.getReceivedDamage()[i] != null)
                gc.drawImage( GuiImagesMap.getImage("drops/" + Player.fighterFromNick(players, player.getReceivedDamage()[i]) + ".png"), xDrop, yDrop, widthDrop, heightDrop);

            xDrop += deltaX;
        }

        //marks
        xDrop = 537 * pbMult + x;
        yDrop = 4 * pbMult + y;
        deltaX = widthDrop * 1.1; //put just a little bit of space, we don't know how many marks a player will get
        for(String p: player.getReceivedMarks()){
            if(p != null)
                gc.drawImage( GuiImagesMap.getImage("drops/" + Player.fighterFromNick(players, p) + ".png"), xDrop, yDrop, widthDrop, heightDrop);

            xDrop += deltaX;
        }

        //for my player I need to ahve the actions clickable
        if(player.getNick().equals(match.getMyPlayer().getNick()) && !frenzyMode){
            double actionsY = ((float)45)/ 270 * height;
            double actionsHeight = ((float)42)/270*height;
            double actionsWidth = ((float)69)/1121*width;

            runAction = new GuiCardClickableArea(x, y+actionsY, actionsWidth, actionsHeight);
            pickAction = new GuiCardClickableArea(x, y + 2*actionsY, actionsWidth, actionsHeight);
            shootAction = new GuiCardClickableArea(x, y + 3*actionsY, actionsWidth, actionsHeight);

            actionsY = ((float)56)/ 270 * height;
            powerAction = new GuiCardClickableArea(x + ((float)116)/1121*width, y+actionsY, actionsWidth, actionsHeight);
            adrPickAction = new GuiCardClickableArea(x + ((float)230)/1121*width, y+actionsY, actionsWidth, actionsHeight);
            adrShootAction = new GuiCardClickableArea(x + ((float)423)/1121*width, y+actionsY, actionsWidth, actionsHeight);
        }
        else if(frenzyMode && player.getNick().equals(match.getMyPlayer().getNick())){
            //todo
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

        Image blue = GuiImagesMap.getImage("loot/blue.png");
        Image red = GuiImagesMap.getImage("loot/red.png");
        Image yellow = GuiImagesMap.getImage("loot/yellow.png");

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
        double x = 1045 * dimMult;
        double y = 990 * dimMult;

        gc.setFont(new Font(MYFONT,32*dimMult));
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.fillText(Integer.toString(points), x, y);
        gc.strokeText(Integer.toString(points), x, y);
    }

    //
    //
    //
    //

    private void listenRequests(){
        //TODO implement the mustChoose for the ones that need it

        //try{Thread.sleep(500);}catch (InterruptedException e){ ; } //On my linux (Andrea) I need to wait a while the javafx app before starting the tests

        exchanger = GuiExchanger.getInstance();
        while(exchanger.getActualInteraction()!=Interaction.CLOSEAPP) {
            exchanger.waitRequestIncoming();

            System.out.println(exchanger.getActualInteraction().toString());
            switch (exchanger.getActualInteraction()) {
                case CHOOSEBASEACTION:
                    chooseBaseAction();
                    break;
                case CHOOSEWEAPONACTION:
                    exchanger.setActualInteraction(Interaction.WAITINGUSER);
                    uiExec.execute(this::chooseWeaponAction);
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
                    showAlert(this::askRoom, exchanger.getMessage());
                    break;
                case CHOOSEDIRECTION:
                    showAlert(this::askDirection, exchanger.getMessage());
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
                case ASKLOCALADDRESS:
                    showAlert(this::askLocalAddress, exchanger.getMessage());
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
                case UPDATEVIEW:
                    uiExec.execute(() -> {
                        match = (MatchView) exchanger.getRequest();
                        primaryS.setScene(new Scene(drawGame()));
                        exchanger.setActualInteraction(Interaction.NONE);
                    });
                    exchanger.setActualInteraction(Interaction.WAITINGUSER);
                    break;
                case LOG:
                    loggedText += "\n\n" + exchanger.getMessage();
                    if(logArea != null)
                        uiExec.execute(()->{
                            logArea.setText(loggedText);
                            logArea.setScrollTop(90000000);
                        });
                        //logArea.setText(logArea.getText() + "\n" + exchanger.getMessage());
                    exchanger.setActualInteraction(Interaction.NONE);
                    break;
                case NONE:
                default:
                    break;
            }
            //handle the mustChoose
            if(!exchanger.isMustChoose()){
                uiExec.execute(()->{
                    masterPane.getChildren().add(skipAction);
                    skipAction.setOnMousePressed(e->{
                        exchanger.setAnswer(null);
                        exchanger.setRequest(Interaction.UPDATEVIEW, "", match, true);
                    });
                });
            }
        }
        Platform.exit();
    }

    /**
     * chooseBaseAction
     */
    private void chooseBaseAction(){
        exchanger.setActualInteraction(Interaction.WAITINGUSER);
        showInfoOnMap(exchanger.getMessage());
        List<Action> possible = (List<Action>) exchanger.getRequest();

        javafx.event.EventHandler<javafx.scene.input.MouseEvent> onClick;

        for(Action a:possible){
            onClick = (e ->{
                exchanger.setAnswer(a);
                clearAllActions();
                clearInfoOnMap();
                exchanger.setActualInteraction(Interaction.NONE);
                //After finishing the click event, reset all the events to the original option -> just call the redraw game
            });
            switch (a.getLambdaID()){
                case "a-b1":
                    runAction.setOnMousePressed(onClick);
                    runAction.setEventsChoosable();
                    break;
                case "a-b2":
                    pickAction.setOnMousePressed(onClick);
                    pickAction.setEventsChoosable();
                    break;
                case "a-b3":
                    shootAction.setOnMousePressed(onClick);
                    shootAction.setEventsChoosable();
                    break;
                case "a-a1":
                    adrPickAction.setOnMousePressed(onClick);
                    adrPickAction.setEventsChoosable();
                    break;
                case "a-a2":
                    adrShootAction.setOnMousePressed(onClick);
                    adrShootAction.setEventsChoosable();
                    break;
                case "a-p":
                    powerAction.setOnMousePressed(onClick);
                    powerAction.setEventsChoosable();
                    break;
                default:
                    break;
                //todo add the finalfrenzy ones
            }
        }
    }

    private Canvas createPopupCanvas(){
        double x = backgroundWidth * 0.2;
        double y = backgroundHeight * 0.2;
        double w = backgroundWidth * 0.6;
        double h = backgroundHeight * 0.6;
        double r = 100 * dimMult;

        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        canvas.getGraphicsContext2D().setFill(javafx.scene.paint.Color.rgb(140,140,140,0.8));
        canvas.getGraphicsContext2D().fillRoundRect(x, y, w, h, r, r);

        return canvas;
    }

    private void chooseWeaponAction(){
        showInfoOnMap(exchanger.getMessage());
        List<Action> possible = (List<Action>) exchanger.getRequest();

        Pane popupPane = new StackPane();
        Canvas canvas = createPopupCanvas();
        popupPane.getChildren().addAll(canvas);

        double cardX = backgroundWidth * 0.215;
        double cardY = backgroundHeight * 0.23;
        double cardH = backgroundHeight * 0.54;
        double cardW = cardH * ((double) 104)/174;
        double xGridText = backgroundWidth * 0.24 + cardW;
        double yGridText = backgroundHeight * 0.23;

        //card
        String lambdaId = possible.get(0).getLambdaID();
        lambdaId = lambdaId.substring(1, lambdaId.indexOf('-'));
        canvas.getGraphicsContext2D().drawImage(GuiImagesMap.getImage( "weapon/weapon" + lambdaId + ".png" ), cardX, cardY, cardW, cardH);

        //Text and button
        GridPane grid = gridMaker(backgroundWidth * 0.8 - xGridText - 20*dimMult);
        grid.setVgap(4);
        popupPane.getChildren().addAll(grid);
        Label popupTitle = new Label(exchanger.getMessage());
        popupTitle.setFont(new Font(MYFONT,POPUPFONTDIM * 1.5 * dimMult));
        popupTitle.setWrapText(true);
        grid.add(popupTitle,0,0);
        int row = 1;
        for(Action a: possible) {
            Label title = new Label(a.getName());
            title.setFont(new Font(MYFONT,POPUPFONTDIM * 1.3 * dimMult));
            title.setWrapText(true);
            grid.add(title,0,row++);

            Label description = new Label(a.getDescription());
            description.setFont(new Font(MYFONT,POPUPFONTDIM*dimMult));
            description.setWrapText(true);
            grid.add(description,0,row++);

            Button buttonAction = new Button("Usa " + a.getName());
            buttonAction.setFont(new Font(MYFONT,POPUPFONTDIM*dimMult*0.8));
            buttonAction.setOnAction((e)->{
                System.out.println("Scelto: " + a.getName());
                exchanger.setAnswer(a);
                clearInfoOnMap();
                exchanger.setActualInteraction(Interaction.NONE);
                masterPane.getChildren().remove(popupPane);
            });
            GridPane.setHalignment(buttonAction, HPos.CENTER);
            grid.add(buttonAction,0,row++);
        }
        //both alignment are NECESSARY
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setTranslateX(xGridText);
        grid.setTranslateY(yGridText);

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    private void clearAllActions(){
        runAction.resetEventsStyle();
        pickAction.resetEventsStyle();
        shootAction.resetEventsStyle();
        adrPickAction.resetEventsStyle();
        adrShootAction.resetEventsStyle();
        powerAction.resetEventsStyle();
        //todo add the frenzy
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

        //if the request ask for discarding a power when I have three and I'm picking up another one (fourth)
        if(choosable.size() > 3){
            List<Power> p = cards.stream().map(GuiCardPower::getPower).collect(Collectors.toList());//get the already showed powers
            //find the missing power
            for(int i =choosable.size(); i>=0; i--)
                for(int j=0; j<p.size();j++)
                    if(choosable.get(i).getId() == p.get(j).getId())
                        choosable.remove(i);

            //draw the missing power
            GuiCardPower temp = new GuiCardPower(choosable.get(0), 100, 100);
        }

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
        showInfoOnMap(exchanger.getMessage()); //fixme in the chooseCell (and other times) it doesn't work!!!
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
            System.out.println(p.getPlayer().getCharacter().toString());//debug
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
        System.out.println("Waiting");
        uiExec.execute(() -> dialog.accept(message));
    }

    private GridPane gridMaker(double width){
        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        //grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(width);
        grid.setMinWidth(width);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(width);
        grid.getColumnConstraints().add(cc);
        return grid;
    }

    /**
     * Used for IP, nick, phrase
     * @param message
     */
    private void askSetting(String message){
        StackPane root = initializerBackground();
        GridPane grid = gridMaker(480 * dimMult);

        Label l = new Label(message);
        l.setFont(Font.font(MYFONT, SETTINGSFONTDIM*dimMult));
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));

        TextField field = new TextField("localhost");
        field.setFont(Font.font(MYFONT, SETTINGSFONTDIM*dimMult));
        //field.setMaxWidth(maxWidth);

        Button submit = new Button("Conferma");
        submit.setFont(Font.font(MYFONT, SETTINGSFONTDIM*dimMult));
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

    private void askWithRadio(String message, ToggleGroup group, List<String> buttons, javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler){
        Pane root = initializerBackground();
        GridPane grid = gridMaker(480 * dimMult);

        Label l = new Label(message);
        l.setFont(Font.font(MYFONT, SETTINGSFONTDIM*dimMult));
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);


        int row = 1;
        for(String s:buttons){
            RadioButton radio = new RadioButton(s);
            radio.setToggleGroup(group);
            radio.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
            radio.setFont(Font.font(MYFONT, SETTINGSFONTDIM*dimMult));
            if(row==1)
                radio.setSelected(true);

            grid.add(radio, 0, row++);
        }

        Button submit = new Button("Conferma");
        submit.setFont(Font.font(MYFONT, SETTINGSFONTDIM*dimMult));
        submit.setOnAction(eventHandler);
        submit.setDefaultButton(true);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        root.getChildren().addAll(grid);
        primaryS.setScene(new Scene(root));
    }

    private void askLocalAddress(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = (List<String>) exchanger.getRequest();

        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(answer);
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });

        askWithRadio(message, group, buttons, eventHandler);
    }

    private void askRMI(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();
        buttons.add("Socket");
        buttons.add("RMI");
        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(answer.equalsIgnoreCase("RMI"));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });

        askWithRadio(message, group, buttons, eventHandler);
    }

    private void askFighter(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();

        List<Fighter> available = (List<Fighter>) exchanger.getRequest();
        for(Fighter f:available)
            buttons.add(f.toString());

        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(Fighter.valueOf(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });

        askWithRadio(message, group, buttons, eventHandler);
    }

    private void askSkulls(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();

        for(int i = 5; i<=8; i++)
            buttons.add(Integer.toString(i));

        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(Integer.parseInt(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });

        askWithRadio(message, group, buttons, eventHandler);
    }

    private void showInfoOnMap(String message) {
        double x = 70 * dimMult; //it was 40 before the button
        double y = 845 * dimMult;

        infoTextCanvas.getGraphicsContext2D().clearRect(0,0, backgroundWidth, backgroundHeight);//we use always the same canvas

        infoTextCanvas.getGraphicsContext2D().setFill(javafx.scene.paint.Color.WHITE);
        infoTextCanvas.getGraphicsContext2D().setFont(new Font(MYFONT,34*dimMult));
        infoTextCanvas.getGraphicsContext2D().fillText(message, x, y);
        infoTextCanvas.setPickOnBounds(false);
    }

    private void clearInfoOnMap(){
        infoTextCanvas.getGraphicsContext2D().clearRect(0,0, backgroundWidth, backgroundHeight);
    }

    private void askRoom(String message){
        List<Integer> rooms = (List<Integer>) exchanger.getRequest();

        Pane popupPane = new Pane();
        Canvas canvas = createPopupCanvas();
        popupPane.getChildren().addAll(canvas);

        GridPane grid = gridMaker(backgroundWidth);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setTranslateX(backgroundWidth * 0.24 );
        grid.setTranslateY(backgroundHeight * 0.23);
        popupPane.getChildren().addAll(grid);

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);

        List<String> roomsNames = new ArrayList<>();
        if(rooms.contains(0))
           roomsNames.add("Rossa");
        if(rooms.contains(1))
           roomsNames.add("Blu");
        if(rooms.contains(2))
           roomsNames.add("Gialla");
        if(rooms.contains(3))
           roomsNames.add("Bianca");
        if(rooms.contains(4))
           roomsNames.add("Viola");
        if(rooms.contains(5))
           roomsNames.add("Verde");

        ToggleGroup radioGroup = new ToggleGroup();
        int row = 1;
        for(String s : roomsNames){
           RadioButton radio = new RadioButton(s);
           radio.setToggleGroup(radioGroup);
           radio.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
            GridPane.setHalignment(radio, HPos.CENTER);
            grid.add(radio,0,row++);
        }

        Button submit = new Button("Conferma");
        submit.setOnAction(rs -> {
           String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
           System.out.println(answer + ": " + roomsNames.indexOf(answer));
           exchanger.setAnswer(roomsNames.indexOf(answer));
           exchanger.setActualInteraction(Interaction.NONE);
           masterPane.getChildren().remove(popupPane);
        });
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        //Show the pane
        masterPane.getChildren().add(popupPane);
   }

    private void askDirection(String message){
        List<Direction> dirs = (List<Direction>) exchanger.getRequest();

        Pane popupPane = new Pane();
        Canvas canvas = createPopupCanvas();
        popupPane.getChildren().addAll(canvas);

        GridPane grid = gridMaker(backgroundWidth*0.6);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setTranslateX(backgroundWidth * 0.24 );
        grid.setTranslateY(backgroundHeight * 0.23);
        popupPane.getChildren().addAll(grid);

        Label l = new Label(message);
        l.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);


        ToggleGroup radioGroup = new ToggleGroup();
        int row = 1;
        for(Direction d:dirs){
            RadioButton radio = new RadioButton(d.toString());
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web("#ffffff"));
            GridPane.setHalignment(radio, HPos.CENTER);
            grid.add(radio,0,row++);
        }

        //continue here
        Button submit = new Button("Conferma");
        submit.setOnAction(rs -> {
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(Direction.valueOf(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            masterPane.getChildren().remove(popupPane);
        });
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    private void askMap(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();
        buttons.add("Mappa 1");
        buttons.add("Mappa 2");
        buttons.add("Mappa 3");
        buttons.add("Mappa 4");
        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            answer = answer.substring(6);//get the number
            System.out.println(answer);
            exchanger.setAnswer(Integer.parseInt(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });

        askWithRadio(message, group, buttons, eventHandler);
    }

    private void askFrenzy(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();
        buttons.add("Con Frenesia");
        buttons.add("Senza Frenesia");
        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            System.out.println(answer);
            exchanger.setAnswer(answer.equalsIgnoreCase("Con Frenesia"));
            exchanger.setActualInteraction(Interaction.NONE);
            primaryS.setScene(new Scene(initializerBackground()));
        });

        askWithRadio(message, group, buttons, eventHandler);
    }
}