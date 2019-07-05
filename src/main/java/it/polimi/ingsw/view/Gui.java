package it.polimi.ingsw.view;

import it.polimi.ingsw.clientmodel.*;
import it.polimi.ingsw.controller.Interaction;
import it.polimi.ingsw.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.min;

//todo move all the strings printed in final Strings
//todo move the ratios in defines
/**
 * The Gui class that extends the JavaFX Application
 */
public class Gui extends Application{
    private static final double SETTINGSFONTDIM = 28;
    private static final double POPUPFONTDIM = 22;
    private static final String WHITE_HEX = "#ffffff";

    private static final String FONT_NAME = "font/ethnocentric_rg.ttf";

    //Strings for the texts
    private static final String CONFIRM_BUTTON_TEXT = "Conferma";
    private static final String DROPS = "drops/";
    private static final String CHIUDI = "Chiudi";
    private static final String ARMI_SCARICHE_DI = "Armi scariche di ";
    private static final String SCELTO = "Scelto: ";
    private static final String CLICKED_PLAYER = "Clicked player: ";
    private static final String ATTENDI_CHE_IL_NEMICO_SCELGA_UN_POWER = "Attendi che il nemico scelga un power";
    private static final String ROSSA = "Rossa";
    private static final String BLU = "Blu";
    private static final String GIALLA = "Gialla";
    private static final String BIANCA = "Bianca";
    private static final String VIOLA = "Viola";
    private static final String VERDE = "Verde";
    private static final String ROSSO = "Rosso";
    private static final String GIALLO = "Giallo";
    private static final String SOCKET = "Socket";
    private static final String RMI = "RMI";
    private static final String MAPPA = "Mappa";
    private static final String CON_FRENESIA = "Con Frenesia";
    private static final String SENZA_FRENESIA = "Senza Frenesia";

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
    private List<GuiClickableObjectWeapon> lootWeapons;
    private List<GuiClickableObjectWeapon> myWeapons;
    private List<GuiClickableObjectPower> myPowers;
    private List<GuiClickableObjectPawn> playersPawns;

    //Actions -> we handle the clicks on these Canvases
    private GuiClickableObjectNoImage runAction;
    private GuiClickableObjectNoImage pickAction;
    private GuiClickableObjectNoImage shootAction;
    private GuiClickableObjectNoImage adrPickAction;
    private GuiClickableObjectNoImage adrShootAction;
    private GuiClickableObjectNoImage powerAction;
    //frenzy ones
    private GuiClickableObjectNoImage frenzyRunReloadShoot;
    private GuiClickableObjectNoImage frenzyRunFour;
    private GuiClickableObjectNoImage frenzyRunTwoPick;
    private GuiClickableObjectNoImage frenzyRunTwoPickShoot;
    private GuiClickableObjectNoImage frenzyRunTreePick;

    private Canvas skipAction;

    //Move in cells
    private GuiClickableObjectNoImage[][] mapOfCells = new GuiClickableObjectNoImage[4][3];

    //Canvases
    private Canvas infoTextCanvas; //canvas where we write the infos for the users

    //Stage
    private Stage appStage;

    /**
     * Main method of the GUI that draws the settings and ask for request on the GuiExchanger instance
     */
    @Override
    public void start(Stage primaryStage){
        loggedText = "";

        appStage = primaryStage;
        appStage.getIcons().add(GuiImagesMap.getImage("icon.jpg"));

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

                //primaryStage.setHeight(backgroundHeight);

                if(match != null) { //if it's null it's still in the settings
                    masterPane = drawGame();
                    primaryStage.setScene(new Scene(masterPane));
                }

                //ANDREA: on my tiling window manager on linux, when one of these are called, the window is going to be resized
                if(exchanger != null && exchanger.getLastRealInteraction() != null )
                    exchanger.resetLastRealInteraction();
            }
        });

        //OnClose
        primaryStage.setOnCloseRequest(t->{
            Platform.exit();
            System.exit(0);
        });

        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.F11) {
                    primaryStage.setFullScreen(true);
                }
            }
        });

        //keep the aspect ratio
        //primaryStage.minHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16).add(-15));
        //primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().multiply(((double) 9)/16).add(15));

        //Run the thread that handles the connection with the GuiInterface

        new Thread(this::listenRequests).start();
    }

    /**
     * Initialize a simple background
     * @return the pane with the simple background
     */
    private StackPane initializerBackground(){
        StackPane root = new StackPane();
        Canvas c = new Canvas(backgroundWidth, backgroundHeight);
        c.getGraphicsContext2D().drawImage(GuiImagesMap.getImage("background/adrenalinaWithBox.jpg"), 0, 0, backgroundWidth, backgroundHeight);
        root.getChildren().addAll(c);
        return root;
    }

    /**
     * Draws the entire game board at the current state of the MatchView
     * @return the pane that contains the new board
     */
    private Pane drawGame(){
        Pane pane = new Pane();
        Canvas canvas;
        StackPane myWeaponsPane;
        StackPane myPowersPane;
        StackPane weaponsLoot;
        StackPane mapLoot;
        StackPane pawns;
        StackPane info;

        masterPane = new Pane();
        canvas = new Canvas(backgroundWidth, backgroundHeight);
        gc = canvas.getGraphicsContext2D();

        gc.drawImage( GuiImagesMap.getImage("background/gameBoard.png"), 0, 0, backgroundWidth, backgroundHeight);
        drawMap(match.getGame().getMap());

        drawAllPlayersBoards(match.getGame().getPlayers());
        drawMyAmmo(match.getMyPlayer().getAmmo());
        drawSkulls(match.getGame().getSkullsBoard());

        myWeaponsPane = drawMyWeapons(match.getMyPlayer().getWeapons());
        myPowersPane = drawMyPowers(match.getMyPlayer().getPowers());
        weaponsLoot = drawWeaponsLoot(match.getGame().getMap());
        mapLoot = drawLootOnMap(match.getGame().getMap());
        pawns = drawPawnsOnMap(match.getGame().getMap());
        info = drawEnemyInfo(match.getGame().getPlayers());

        myWeaponsPane.setPickOnBounds(false);
        myPowersPane.setPickOnBounds(false);
        weaponsLoot.setPickOnBounds(false);
        mapLoot.setPickOnBounds(false);
        pawns.setPickOnBounds(false);
        info.setPickOnBounds(false);
        masterPane.setPickOnBounds(false);

        //canvas for text
        infoTextCanvas = new Canvas(backgroundWidth, backgroundHeight);
        infoTextCanvas.setPickOnBounds(false);

        //canvases for the cells
        StackPane cellsClick = new StackPane();
        for(GuiClickableObjectNoImage[] t:mapOfCells)
            for(GuiClickableObjectNoImage s:t)
                if(s!=null)
                    cellsClick.getChildren().add(s);
        cellsClick.setPickOnBounds(false);

        //logArea
        logArea = new TextArea();
        logArea.setFont(new Font("Verdana", 16*dimMult));
        logArea.setLayoutX(1222 * dimMult);
        logArea.setLayoutY(920 * dimMult);
        logArea.setMaxWidth(560 * dimMult);
        logArea.setMaxHeight(134 * dimMult);
        logArea.setEditable(false);
        logArea.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent;");
        logArea.setText(loggedText);

        //SkipAction
        skipAction = new Canvas(45*((float)1588/500)*dimMult, 45*dimMult);
        skipAction.getGraphicsContext2D().drawImage(GuiImagesMap.getImage("skipAction.png"), 0, 0, 45*((float)1588/500)*dimMult, 45*dimMult);
        skipAction.setLayoutX(35*dimMult);
        skipAction.setLayoutY(725*dimMult);
        skipAction.setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0);");
        skipAction.setOnMouseEntered(e -> skipAction.setStyle("-fx-effect: innershadow(gaussian, #a8ff22, 20, 0.7, 0, 0);"));
        skipAction.setOnMouseExited(e-> skipAction.setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0);") );
        skipAction.setPickOnBounds(false);

        pane.getChildren().addAll( canvas, infoTextCanvas,  myWeaponsPane, myPowersPane, weaponsLoot, mapLoot, cellsClick, pawns, info, logArea);

        if(runAction!=null)//check if one it's initialized all are
            pane.getChildren().addAll(runAction, pickAction, shootAction, powerAction, adrPickAction, adrShootAction);
        else if(frenzyRunTreePick != null)
            pane.getChildren().addAll(frenzyRunFour, frenzyRunReloadShoot,frenzyRunTreePick,frenzyRunTwoPick,frenzyRunTwoPickShoot, powerAction);


        println("Re-drawn the pane!");
        return pane;
    }

    /**
     * Draw the map and the clickable areas on it
     * @param map the map that has to be drawn
     */
    private void drawMap(MapView map){
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
                    mapOfCells[xC][yC] = new GuiClickableObjectNoImage(x+ distX*xC, y + distY*yC, width, height);
                }
            }
        }

    }

    /**
     * Draw the loot on the map
     * @param map the map
     * @return the Pane that contains the loot
     */
    private StackPane drawLootOnMap (MapView map){
        StackPane root = new StackPane();

        //dimensions are the same
        double size = 55 * dimMult;
        double x;
        double y;

        //Manually add for each cell because they haven't the same dim. Create row by row from the top one
        if(map.getCell(0, 0) != null && ((RegularCellView)map.getCell(0, 0)).getLoot() != null) {
            x = 310 * dimMult;
            y = 325 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(0, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 0) != null && ((RegularCellView)map.getCell(1, 0)).getLoot() != null) {
            x = 526 * dimMult;
            y = 325 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(1, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 0) != null && ((RegularCellView)map.getCell(3, 0)).getLoot() != null) {
            x = 900 * dimMult;
            y = 325 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(3, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 1) != null && ((RegularCellView)map.getCell(1, 1)).getLoot() != null) {
            x = 526 * dimMult;
            y = 530 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(1, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 1) != null && ((RegularCellView)map.getCell(2, 1)).getLoot() != null) {
            x = 725 * dimMult;
            y = 530 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(2, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 1) != null && ((RegularCellView)map.getCell(3, 1)).getLoot() != null) {
            x = 900 * dimMult;
            y = 530 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(3, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(0, 2) != null && ((RegularCellView)map.getCell(0, 2)).getLoot() != null) {
            x = 335 * dimMult;
            y = 730 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(0, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 2) != null && ((RegularCellView)map.getCell(1, 2)).getLoot() != null) {
            x = 526 * dimMult;
            y = 730 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(1, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 2) != null && ((RegularCellView)map.getCell(2, 2)).getLoot() != null) {
            x = 725 * dimMult;
            y = 730 * dimMult;
            GuiClickableObjectLoot card = new GuiClickableObjectLoot(((RegularCellView)map.getCell(2,2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }

        return root;
    }

    /**
     * Draw the pawns
     * @param map the map
     * @return the pane that contains the pawns
     */
    private StackPane drawPawnsOnMap (MapView map){
        StackPane root = new StackPane();
        double size = 50 * dimMult;
        double baseX = 224 * dimMult;
        double baseY = 224 * dimMult;
        double x;
        double y;
        double deltaCellX = 186 * dimMult;
        double deltaCellY = 200 * dimMult;
        int n;

        playersPawns = new ArrayList<>();
        for(int j=0; j<3; j++){
            for(int i=0; i<4; i++){
                n=0;
                x = baseX + i * (deltaCellX + (i==3?10:0)); //the last column is farther
                y = baseY + j * deltaCellY;
                if(map.getCell(i, j) != null) { //here X and Y are pointing at the top-left corner of the cell
                    for (PlayerView p : map.getCell(i, j).getPawns()) {
                        GuiClickableObjectPawn pawn = new GuiClickableObjectPawn(p, size, p.getNick().equals(match.getMyPlayer().getNick()));
                        if(n==1 || n==3)
                            y = baseY +  j * deltaCellY + size;
                        if(n==2 || n==4){
                            x += size;
                            y=baseY + j * deltaCellY;
                        }
                        n++;
                        pawn.setPosition(x, y);

                        playersPawns.add(pawn);
                        root.getChildren().add(pawn);
                    }
                }
            }
        }

        return root;
    }

    /**
     * Draw the weapons loot on the spawn cells
     * @param map the map
     * @return the pane with the loot
     */
    private StackPane drawWeaponsLoot(MapView map){
        StackPane root = new StackPane();
        GuiClickableObjectWeapon card;

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
        SpawnCellView c = (SpawnCellView) map.getCell(2,0);
        for(Weapon w:c.getWeapons()){
            card = new GuiClickableObjectWeapon(w, width, height, 0);
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
        c = (SpawnCellView) map.getCell(0, 1);
        for(Weapon w:c.getWeapons()){
            card = new GuiClickableObjectWeapon(w, width, height, -90);

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
        c = (SpawnCellView) map.getCell(3, 2);
        for(Weapon w:c.getWeapons()){
            card = new GuiClickableObjectWeapon(w, width, height, +90);

            lootWeapons.add(card);

            card.setPickOnBounds(false);
            card.setTranslateX(x);
            card.setTranslateY(y);
            root.getChildren().add(card);

            y += deltaY;
        }
        return root;
    }

    /**
     * Draw the info icons for the enemies
     * @param players the players
     * @return the pane with the info buttons
     */
    private StackPane drawEnemyInfo(List<PlayerView> players) {
        double boardW = 560 * dimMult;
        double boardX = 1222 * dimMult;
        double boardY = 74 * dimMult;

        //calculate distance from board to board
        double deltaY = 169 * dimMult;

        double size = 40*dimMult;

        StackPane root = new StackPane();
        GuiClickableInfo info;

        for(PlayerView pl: players) {
            if(!pl.getNick().equals(match.getMyPlayer().getNick())) {
                info = new GuiClickableInfo(size, size);

                info.setOnMousePressed(e ->uiExec.execute(()->showEnemyInfo(pl)));

                info.setPickOnBounds(false);
                info.setPosition(boardX + boardW + (30 * dimMult), boardY + (53 * dimMult));
                root.getChildren().add(info);
            }

            boardY += deltaY;
        }

        return root;
    }

    /**
     * Draw the skulls
     * @param kills the array of kills
     */
    private void drawSkulls(List<KillView> kills) {
        double x = 108;
        double y = 73;

        double w = 30 * dimMult;
        double dropH = 46 * dimMult;
        double skullH = 40 * dimMult;

        for(int s = 0; s<8; s++) {
            if(kills.get(s).isUsed()) {
                if(kills.get(s).getSkull())
                    gc.drawImage(GuiImagesMap.getImage("skull.png"), x * dimMult, y * dimMult, w, skullH);
                else {
                    if(kills.get(s).getOverkill()) {
                        gc.drawImage(GuiImagesMap.getImageWithShadow(DROPS + kills.get(s).getKiller().getCharacter() + ".png", w, dropH), (x-6) * dimMult, (y-6) * dimMult);
                        gc.drawImage(GuiImagesMap.getImageWithShadow(DROPS + kills.get(s).getKiller().getCharacter() + ".png", w, dropH), (x+6) * dimMult, y * dimMult);
                    }
                    else
                        gc.drawImage(GuiImagesMap.getImageWithShadow(DROPS + kills.get(s).getKiller().getCharacter() + ".png", w, dropH), x * dimMult, (y-3) * dimMult);
                }
            }

            //Distance between skulls
            x+=48;
        }
    }

    /**
     * Draw all the players boards
     * @param players the players
     */
    private void drawAllPlayersBoards(List<PlayerView> players){
        //dimensions are the same for all the players
        double width = 560 * dimMult;
        double height = 134 * dimMult;
        double x = 1222 * dimMult;
        double y = 74 * dimMult;

        //calculate distance from board to board
        double deltaY = 169 * dimMult;

        for(PlayerView p : players){
            drawPlayerBoard(p, players, width, height, x, y);
            y += deltaY;
        }
    }

    /**
     * Draw a single player board
     * @param player the player who the board belong
     * @param players all the players (needed for the drawing of the damage)
     * @param width width of the card
     * @param height height of the card
     * @param x x pos
     * @param y y pos
     */
    private void drawPlayerBoard(PlayerView player, List<PlayerView> players, double width, double height, double x, double y){

        boolean frenzyMode = player.getFrenzyBoard();

        double pbMult = width/1123; //(dimMult * width@1080p)/textureWidth -> internal reference based on the card
        double xDrop = (frenzyMode?123:109) * pbMult + x;
        double yDrop = 100 * pbMult + y;
        double widthDrop = 45 * pbMult;
        double heightDrop = 68 * pbMult;

        double deltaX = (frenzyMode?61:63) * pbMult;

        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(getFont(18*dimMult));
        gc.fillText(player.getNick() + " - " + player.getCharacter().toString(), x, y-(8*dimMult));

        gc.drawImage( GuiImagesMap.getImage("playerBoard/" + player.getCharacter().toString() + (frenzyMode?"_F":"") + ".png"), x, y, width, height);

        if (match.getActive() != null && player.getNick().equals(match.getActive().getNick())) {
            gc.setStroke(javafx.scene.paint.Color.rgb(12,255,255,0.6));
            gc.setLineWidth(3);
            gc.strokeRect(x, y, width, height);
        }

        //damages
        for(int i=0; i<12; i++){
            if(player.getDamage(i) != null)
                gc.drawImage( GuiImagesMap.getImageWithShadow(DROPS + PlayerView.fighterFromNick(players, player.getDamage(i)) + ".png", widthDrop, heightDrop), xDrop, yDrop);

            xDrop += deltaX;
        }

        //marks
        xDrop = 537 * pbMult + x;
        yDrop = -8 * pbMult + y;
        deltaX = widthDrop * 1.1; //put just a little bit of space, we don't know how many marks a player will get
        for(String p: player.getReceivedMarks()){
            if(p != null)
                gc.drawImage( GuiImagesMap.getImageWithShadow(DROPS + PlayerView.fighterFromNick(players, p) + ".png", widthDrop, heightDrop), xDrop, yDrop);

            xDrop += deltaX;
        }

        //deaths
        double xSkull = frenzyMode? 152:115;
        double ySkull = 92;
        double wSkull = 30*dimMult;
        double hSkull = 40*dimMult;

        for(int i=0; i<player.getSkulls() && i< (frenzyMode?4:6); i++)
        {
            gc.drawImage(GuiImagesMap.getImage("skull.png"), x + (xSkull * dimMult), y + (ySkull * dimMult), wSkull, hSkull);
            xSkull += 30;
        }

        //My player needs clickable actions
        if(player.getNick().equals(match.getMyPlayer().getNick()) && !frenzyMode){
            double actionsY = ((float)45)/ 270 * height;
            double actionsHeight = ((float)42)/270*height;
            double actionsWidth = ((float)69)/1121*width;

            runAction = new GuiClickableObjectNoImage(x, y+actionsY, actionsWidth, actionsHeight);
            pickAction = new GuiClickableObjectNoImage(x, y + 2*actionsY, actionsWidth, actionsHeight);
            shootAction = new GuiClickableObjectNoImage(x, y + 3*actionsY, actionsWidth, actionsHeight);

            actionsY = ((float)56)/ 270 * height;
            powerAction = new GuiClickableObjectNoImage(x + ((float)116)/1121*width, y+actionsY, actionsWidth, actionsHeight);
            adrPickAction = new GuiClickableObjectNoImage(x + ((float)230)/1121*width, y+actionsY, actionsWidth, actionsHeight);
            adrShootAction = new GuiClickableObjectNoImage(x + ((float)423)/1121*width, y+actionsY, actionsWidth, actionsHeight);
        }
        else if(frenzyMode && player.getNick().equals(match.getMyPlayer().getNick())){
            double actionsY = ((float)40)/ 270 * height;
            double actionsHeight = ((float)33)/270*height;
            double actionsWidth = ((float)69)/1121*width;
            frenzyRunReloadShoot = new GuiClickableObjectNoImage(x, y+actionsY, actionsWidth, actionsHeight);
            frenzyRunFour = new GuiClickableObjectNoImage(x, y+actionsY + actionsHeight, actionsWidth, actionsHeight);
            frenzyRunTwoPick = new GuiClickableObjectNoImage(x, y+actionsY +actionsHeight*2, actionsWidth, actionsHeight);

            double secondActionsY = ((float)184)/ 270 * height;
            powerAction = new GuiClickableObjectNoImage(x + ((float)134)/1121*width, y+actionsY + 6*dimMult, actionsWidth, actionsHeight);
            frenzyRunTwoPickShoot = new GuiClickableObjectNoImage(x, y+secondActionsY+actionsY, actionsWidth, actionsHeight);
            frenzyRunTreePick = new GuiClickableObjectNoImage(x, y+secondActionsY+2*actionsY, actionsWidth, actionsHeight);
        }
    }

    /**
     * Draw my weapons
     * @param weapons my weapons
     * @return the pane with the weapons
     */
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
            GuiClickableObjectWeapon card = new GuiClickableObjectWeapon(w, width, height, 0);
            myWeapons.add(card);
            root.getChildren().add(card);

            card.setPickOnBounds(false);

            card.setTranslateX(x);
            card.setTranslateY(y);
            x += deltaX;
        }

        return root;
    }

    /**
     * Draw my powers
     * @param powers my powers
     * @return the pane with the powers
     */
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
            GuiClickableObjectPower card = new GuiClickableObjectPower(p, width, height);
            myPowers.add(card);
            card.setPosition(x, y);
            root.getChildren().add(card);
            x += deltaX;
        }

        return root;
    }

    /**
     * Draw my ammo on the shared GC
     * @param ammo my ammo
     */
    private void drawMyAmmo(AmmoView ammo){
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

    //
    //
    // NOW ALL THE METHODS FOR THE INTERACTIONS
    //
    //

    /**
     * The listener of the requests coming from the GuiExchanger. It needs to be runned in a separate thread
     */
    private void listenRequests(){
        exchanger = GuiExchanger.getInstance();
        boolean serverDisconnectedOnHisOwn = false;
        while(exchanger.getActualInteraction()!=Interaction.CLOSEAPP) {
            exchanger.waitRequestIncoming();

            //handle the mustChoose
            if(!exchanger.isMustChoose() && exchanger.needsPopup() && exchanger.getActualInteraction()!=Interaction.MOVEPLAYER ){
                uiExec.execute(()->{
                    masterPane.getChildren().add(skipAction);
                    skipAction.setOnMousePressed(e->{
                        exchanger.setAnswer(null);
                        exchanger.setRequest(Interaction.UPDATEVIEW, "", match, true);
                    });
                });
            }

            println(exchanger.getActualInteraction().toString());

            //start the timer -> it starts only if already in game
            timerDisconnectionStart();

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
                    exchanger.setActualInteraction(Interaction.WAITINGUSER);
                    uiExec.execute(this::discardPower);
                    break;
                case CHOOSEPOWER:
                    choosePowerCard();
                    break;
                case MOVEPLAYER:
                case MOVEENEMY:
                case CHOOSEPOSITION:
                    chooseCell();
                    break;
                case CHOOSETARGET:
                    chooseTarget();
                    break;
                case CHOOSEROOM:
                    showAlert(this::askRoom, exchanger.getMessage());
                    break;
                case CHOOSEAMMO:
                    showAlert(this::askAmmo, exchanger.getMessage());
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
                    exchanger.setActualInteraction(Interaction.WAITINGUSER);
                    uiExec.execute(() -> {
                        match = (MatchView) exchanger.getRequest();
                        masterPane = drawGame();
                        appStage.setScene(new Scene(masterPane));

                        if(match.getActive()!= null && match.getMyPlayer()!=null && !match.getActive().getNick().equals(match.getMyPlayer().getNick()))
                            uiExec.execute(()->showInfoOnMap("È il turno di " + match.getActive().getNick()));

                        exchanger.setActualInteraction(Interaction.NONE);
                    });
                    break;
                case LOG:
                    if(!exchanger.isServerDown()){
                    //if(!exchanger.getMessage().equalsIgnoreCase("Server disconnesso inaspettatamente, rilancia il client e riprova\n")) {

                        /*DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String logMessage = dtf.format(now) + " " + exchanger.getMessage();
                        loggedText = logMessage + "\n" + loggedText;*/
                        loggedText = exchanger.getLog();
                        if (logArea != null)
                            uiExec.execute(() -> logArea.setText(loggedText));
                        if (match == null) {
                            uiExec.execute(() -> this.settingsMessage(loggedText));
                        }
                        exchanger.setActualInteraction(Interaction.NONE);
                    }
                    else {
                        serverDisconnectedOnHisOwn = true;
                        exchanger.setActualInteraction(Interaction.DISCONNECTION);
                    }
                    break;
                case DISCONNECTION:
                    if(!serverDisconnectedOnHisOwn)
                        uiExec.execute(()-> settingsMessage("Disconnesso dal server per inattività"));
                    else
                        uiExec.execute(()-> settingsMessage(exchanger.getMessage()));
                    exchanger.setActualInteraction(Interaction.WAITINGUSER);
                    break;
                case ENDGAME:
                    uiExec.execute(()-> settingsMessage(exchanger.getMessage()));
                    exchanger.setActualInteraction(Interaction.WAITINGUSER);
                    break;
                case NONE:
                default:
                    break;
            }
        }
        Platform.exit();
    }

    /**
     * Start the disconnection timer inside the exchanger
     */
    private void timerDisconnectionStart(){
        if(match!=null && exchanger.getActualInteraction() != Interaction.UPDATEVIEW && exchanger.getActualInteraction()!=Interaction.LOG
            && exchanger.getActualInteraction() != Interaction.ENDGAME) {
            if(exchanger.getMyTimer() != null)
                exchanger.getMyTimer().interrupt();

            long time = match.getTimeForAction();
            exchanger.setMyTimer(new Thread(() -> {
                try {
                    Thread.sleep(time * 1000);
                    uiExec.execute(()-> settingsMessage("Disconnesso dal server per inattività"));
                    exchanger.setActualInteraction(Interaction.DISCONNECTION);
                    exchanger.setActualInteraction(Interaction.DISCONNECTION); //so BOTH the actual and the one before are DISCONNECTION
                }catch (InterruptedException ignore){
                    Thread.currentThread().interrupt();
                }
            }));
            exchanger.getMyTimer().start();
        }
    }

    /**
     * Draws the info message on the map
     * @param message the message to be wrote
     */
    private void showInfoOnMap(String message) {
        double x = 35 * dimMult;
        double y = 840 * dimMult;

        infoTextCanvas.getGraphicsContext2D().clearRect(0,0, backgroundWidth, backgroundHeight);//we use always the same canvas

        infoTextCanvas.getGraphicsContext2D().setFill(javafx.scene.paint.Color.WHITE);
        infoTextCanvas.getGraphicsContext2D().setFont(getFont(34*dimMult));
        infoTextCanvas.getGraphicsContext2D().fillText(message, x, y);
        infoTextCanvas.setPickOnBounds(false);
    }

    /**
     * Remove the info already on the map
     */
    private void clearInfoOnMap(){
        infoTextCanvas.getGraphicsContext2D().clearRect(0,0, backgroundWidth, backgroundHeight);
    }

    /**
     * Let the user choose between the different base actions
     */
    private void chooseBaseAction(){
        exchanger.setActualInteraction(Interaction.WAITINGUSER);
        showInfoOnMap(exchanger.getMessage());
        List<Action> possible = (List<Action>) exchanger.getRequest();

        javafx.event.EventHandler<javafx.scene.input.MouseEvent> onClick;

        for(Action a:possible){
            onClick = (e ->{
                exchanger.setAnswer(a);
                clearInfoOnMap();
                //clear the canvases
                powerAction.resetEventsStyle();
                if(runAction != null) {
                    runAction.resetEventsStyle();
                    pickAction.resetEventsStyle();
                    shootAction.resetEventsStyle();
                    adrPickAction.resetEventsStyle();
                    adrShootAction.resetEventsStyle();
                }
                if(frenzyRunReloadShoot != null) {
                    frenzyRunReloadShoot.resetEventsStyle();
                    frenzyRunFour.resetEventsStyle();
                    frenzyRunTwoPick.resetEventsStyle();
                    frenzyRunTwoPickShoot.resetEventsStyle();
                    frenzyRunTreePick.resetEventsStyle();
                }
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
                case "a-f1":
                    frenzyRunReloadShoot.setOnMousePressed(onClick);
                    frenzyRunReloadShoot.setEventsChoosable();
                    break;
                case "a-f2":
                    frenzyRunFour.setOnMousePressed(onClick);
                    frenzyRunFour.setEventsChoosable();
                    break;
                case "a-f3":
                    frenzyRunTwoPick.setOnMousePressed(onClick);
                    frenzyRunTwoPick.setEventsChoosable();
                    break;
                case "a-f4":
                    frenzyRunTwoPickShoot.setOnMousePressed(onClick);
                    frenzyRunTwoPickShoot.setEventsChoosable();
                    break;
                case "a-f5":
                    frenzyRunTreePick.setOnMousePressed(onClick);
                    frenzyRunTreePick.setEventsChoosable();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Create a grey popup Canvas used for asking the user about different choices in other methods
     * @return the canvas
     */
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

    /**
     * Ask the user which action of the weapon wants to use
     */
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
        popupTitle.setFont(getFont(POPUPFONTDIM * 1.5 * dimMult));
        popupTitle.setWrapText(true);
        grid.add(popupTitle,0,0);
        int row = 1;
        for(Action a: possible) {
            Label title = new Label(a.getName());
            title.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
            title.setWrapText(true);
            grid.add(title,0,row++);

            Label description = new Label(a.getDescription());
            description.setFont(getFont(POPUPFONTDIM*dimMult));
            description.setWrapText(true);
            grid.add(description,0,row++);

            Button buttonAction = new Button("Usa " + a.getName());
            buttonAction.setFont(getFont(POPUPFONTDIM*dimMult*0.8));
            buttonAction.setOnAction(e->{
                println(SCELTO + a.getName());
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

        row++;
        if(!exchanger.isMustChoose())
        {
            grid.add(skipAction, 0, row);
            skipAction.setOnMousePressed(e->{
                exchanger.setAnswer(null);
                exchanger.setRequest(Interaction.UPDATEVIEW, "", match, true);
            });
        }

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    /**
     * Show enemy info
     * @param pl the player which the infos'll be shown
     */
    private void showEnemyInfo(PlayerView pl) {
        Pane popupPane = new StackPane();
        Canvas canvas = createPopupCanvas();
        popupPane.getChildren().addAll(canvas);
        Canvas weapon;

        //Close button
        Button closeButton = new Button(CHIUDI);
        closeButton.setFont(getFont(POPUPFONTDIM*dimMult*0.8));
        closeButton.setOnAction(e -> masterPane.getChildren().remove(popupPane));
        StackPane.setAlignment(closeButton, Pos.CENTER);
        closeButton.setTranslateX(500*dimMult);
        closeButton.setTranslateY(-290*dimMult);

        //Player name
        Label nameLbl = new Label(ARMI_SCARICHE_DI + pl.getNick() + " - " + pl.getCharacter());
        nameLbl.setTextFill(javafx.scene.paint.Color.WHITE);
        nameLbl.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
        nameLbl.setWrapText(true);
        StackPane.setAlignment(nameLbl, Pos.CENTER_LEFT);
        nameLbl.setTranslateX(420*dimMult);
        nameLbl.setTranslateY(-280*dimMult);

        //Weapons
        double weaponX = 420;
        double weaponW = 240*dimMult;
        double weaponH = 406*dimMult;
        for(Weapon w : pl.getWeapons().stream().filter(w->!w.isLoaded()).collect(Collectors.toList()))
        {
            weapon = new Canvas(weaponW, weaponH);
            StackPane.setAlignment(weapon, Pos.CENTER_LEFT);
            weapon.setPickOnBounds(false);
            weapon.setTranslateX(weaponX*dimMult);
            weapon.setTranslateY(-40*dimMult);

            weapon.getGraphicsContext2D().drawImage(GuiImagesMap.getImage( "weapon/weapon" + w.getId() + ".png" ), 0, 0, weaponW, weaponH);

            weaponX += 260;
            popupPane.getChildren().add(weapon);
        }

        popupPane.getChildren().addAll(closeButton, nameLbl);

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    /**
     * Highlight the choosable weapons and handles the clicks on them
     * Used for: chooseWeapon, grapWeapon, reload, discardWeapon
     */
    private void chooseWeaponCard(){
        showInfoOnMap(exchanger.getMessage());

        List<Weapon> choosable = (List<Weapon>) exchanger.getRequest();

        List<GuiClickableObjectWeapon> cards = lootWeapons.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());
        cards.addAll(myWeapons.stream().filter(c->c.inList(choosable)).collect(Collectors.toList())); //add also my cards

        for(GuiClickableObjectWeapon c : cards){
            c.setOnMousePressed(e -> {
                exchanger.setAnswer(c.getWeapon());
                exchanger.setActualInteraction(Interaction.NONE);
                clearInfoOnMap();
                //After finishing the click event, reset all the events to the original option
                for(GuiClickableObjectWeapon c2 : cards)
                    c2.resetEventsStyle();
            });
            c.setEventsChoosable();
        }

        exchanger.setActualInteraction(Interaction.WAITINGUSER);
    }

    /**
     * Highlight the choosable powers and handles the clicks on them
     * Used for: choosePower
     */
    private void choosePowerCard(){
        showInfoOnMap(exchanger.getMessage());

        List<Power> choosable = (List<Power>) exchanger.getRequest();

        List<GuiClickableObjectPower> cards = myPowers.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());

        for(GuiClickableObjectPower c : cards){
            c.setOnMousePressed(e -> {
                exchanger.setAnswer(c.getPower());
                exchanger.setActualInteraction(Interaction.NONE);
                clearInfoOnMap();
                //After finishing the click event, reset all the events to the original option
                for(GuiClickableObjectPower c2 : cards)
                    c2.resetEventsStyle();
            });
            c.setEventsChoosable();
        }

        exchanger.setActualInteraction(Interaction.WAITINGUSER);
    }

    /**
     * Show a popup asking which power to discard
     */
    private void discardPower(){
        showInfoOnMap(exchanger.getMessage());

        List<Power> choosable = (List<Power>) exchanger.getRequest();

        Pane popupPane = new StackPane();
        Canvas canvas = createPopupCanvas();
        popupPane.getChildren().addAll(canvas);

        double w = backgroundWidth * 0.6 / 4 * 0.86; //0.6 is the popup width, /4 are the four cards, *.86 is for non touch with eachother
        double h = w * ((double)264/169);
        double xGridText = backgroundWidth * 0.24;
        double yGridText = backgroundHeight * 0.23;

        //Grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setMaxWidth(backgroundWidth*0.54);
        grid.setMinWidth(backgroundWidth*0.54);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(w);
        grid.getColumnConstraints().add(cc);
        popupPane.getChildren().addAll(grid);

        //Title
        Label popupTitle = new Label(exchanger.getMessage());
        popupTitle.setFont(getFont(POPUPFONTDIM * 1.5 * dimMult));
        popupTitle.setWrapText(true);
        grid.add(popupTitle,0,0, 4,1);

        //Cards
        int col = 0;
        for(Power p: choosable) {
            GuiClickableObjectPower powerCard = new GuiClickableObjectPower(p, w, h);
            grid.add(powerCard,col++,1);

            powerCard.setEventsChoosable();
            powerCard.setOnMousePressed(e->{
                println(SCELTO + p.getName());
                exchanger.setAnswer(p);
                exchanger.setActualInteraction(Interaction.NONE);
                clearInfoOnMap();
                masterPane.getChildren().remove(popupPane);
            });
        }
        //both alignment are NECESSARY
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setTranslateX(xGridText);
        grid.setTranslateY(yGridText);

        if(!exchanger.isMustChoose())
        {
            grid.add(skipAction, 0, 2);
            skipAction.setOnMousePressed(e->{
                exchanger.setAnswer(null);
                exchanger.setRequest(Interaction.UPDATEVIEW, "", match, true);
            });
        }

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    /**
     * Highlight the choosable cells and handles the clicks on them
     * Used for: movePlayer, choosePosition, moveEnemy
     */
    private void chooseCell(){
        showInfoOnMap(exchanger.getMessage());

        List<Point> possible = (List<Point>) exchanger.getRequest();
        boolean dontMove = !exchanger.isMustChoose() && exchanger.getActualInteraction() == Interaction.MOVEPLAYER;

        exchanger.setActualInteraction(Interaction.WAITINGUSER);

        if(dontMove)
            possible.add(match.getMyPlayer().getPosition());

        for(Point p:possible) {
            if (mapOfCells[p.getX()][p.getY()] != null) {
                Point answer;

                if(p.samePoint(match.getMyPlayer().getPosition()) && dontMove)
                    answer = null;
                else
                    answer = p;

                mapOfCells[p.getX()][p.getY()].setOnMousePressed(e -> {
                    exchanger.setAnswer(answer);

                    exchanger.setActualInteraction(Interaction.NONE);
                    clearInfoOnMap();
                    //After finishing the click event, reset all the events to the original option
                    for(GuiClickableObjectNoImage[] t:mapOfCells)
                        for(GuiClickableObjectNoImage s:t)
                            if(s!=null)
                                s.resetEventsStyle();
                });
                mapOfCells[p.getX()][p.getY()].setEventsChoosable();
            }
        }
    }

    /**
     * Highlight the choosable targets and handles the clicks on them
     * Used for: chooseTarget
     */
    private void chooseTarget(){
        showInfoOnMap(exchanger.getMessage());

        List<PlayerView> choosable = (List<PlayerView>) exchanger.getRequest();

        List<GuiClickableObjectPawn> pawns = playersPawns.stream().filter(c->c.inList(choosable)).collect(Collectors.toList());

        for(GuiClickableObjectPawn p : pawns){
            println(p.getPlayer().getCharacter().toString());//debug
            p.setOnMousePressed(e -> {
                println(CLICKED_PLAYER + p.getPlayer().getNick());
                exchanger.setAnswer(p.getPlayer());
                exchanger.setActualInteraction(Interaction.NONE);
                clearInfoOnMap();
                showInfoOnMap(ATTENDI_CHE_IL_NEMICO_SCELGA_UN_POWER);
                //After finishing the click event, reset all the events to the original option
                for(GuiClickableObjectPawn p2 : pawns)
                    p2.resetEventsStyle();
            });
            p.setEventsChoosable();
        }

        exchanger.setActualInteraction(Interaction.WAITINGUSER);
    }

    /**
     * Ask which room the user wants
     * @param message the message
     */
    private void askRoom(String message){
        List<Integer> rooms = (List<Integer>) exchanger.getRequest();

        Pane popupPane = new Pane();
        Canvas canvas = createPopupCanvas();
        popupPane.getChildren().addAll(canvas);

        GridPane grid = gridMaker(700*dimMult);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setTranslateX(backgroundWidth * 0.24 );
        grid.setTranslateY(backgroundHeight * 0.25);
        popupPane.getChildren().addAll(grid);

        Label l = new Label(message);
        l.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
        l.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
        grid.add(l,0,0);

        List<String> roomsNames = new ArrayList<>();
        if(rooms.contains(0))
            roomsNames.add(ROSSA);
        if(rooms.contains(1))
            roomsNames.add(BLU);
        if(rooms.contains(2))
            roomsNames.add(GIALLA);
        if(rooms.contains(3))
            roomsNames.add(BIANCA);
        if(rooms.contains(4))
            roomsNames.add(VIOLA);
        if(rooms.contains(5))
            roomsNames.add(VERDE);

        ToggleGroup radioGroup = new ToggleGroup();
        int row = 1;
        for(String s : roomsNames){
            RadioButton radio = new RadioButton(s);
            radio.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
            if(row==1)
                radio.setSelected(true);
            grid.add(radio,0,row++);
        }

        Button submit = new Button(CONFIRM_BUTTON_TEXT);
        submit.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
        submit.setOnAction(rs -> {
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            println(answer + ": " + roomsNames.indexOf(answer));
            exchanger.setAnswer(roomsNames.indexOf(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            masterPane.getChildren().remove(popupPane);
        });
        grid.add(submit,0,row);

        row++;
        if(!exchanger.isMustChoose())
        {
            grid.add(skipAction, 0, row);
            skipAction.setOnMousePressed(e->{
                exchanger.setAnswer(null);
                exchanger.setRequest(Interaction.UPDATEVIEW, "", match, true);
            });
        }

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    /**
     * Ask which ammo color to use
     * @param message Message to show to the user in the popup
     */
    private void askAmmo(String message) {
        List<Color> colors = (List<Color>) exchanger.getRequest();

        Pane popupPane = new Pane();
        Canvas canvas = createPopupCanvas();
        popupPane.getChildren().addAll(canvas);

        GridPane grid = gridMaker(700*dimMult);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setTranslateX(backgroundWidth * 0.24 );
        grid.setTranslateY(backgroundHeight * 0.25);
        popupPane.getChildren().addAll(grid);

        Label l = new Label(message);
        l.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
        l.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
        grid.add(l,0,0);

        List<String> colorNames = new ArrayList<>();

        if(colors.contains(Color.BLUE))
            colorNames.add(BLU);
        if(colors.contains(Color.RED))
            colorNames.add(ROSSO);
        if(colors.contains(Color.YELLOW))
            colorNames.add(GIALLO);

        ToggleGroup radioGroup = new ToggleGroup();
        int row = 1;
        for(String s : colorNames){
            RadioButton radio = new RadioButton(s);
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
            radio.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
            if(row==1)
                radio.setSelected(true);
            grid.add(radio,0,row++);
        }

        Button submit = new Button(CONFIRM_BUTTON_TEXT);
        submit.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
        submit.setOnAction(rs -> {
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            Color c = null;
            if(answer.equals(ROSSO))
                c = Color.RED;
            else if (answer.equals(BLU))
                c= Color.BLUE;
            else if(answer.equals(GIALLO))
                c=Color.YELLOW;
            println(answer + ": " + colorNames.indexOf(answer));
            exchanger.setAnswer(c);
            exchanger.setActualInteraction(Interaction.NONE);
            masterPane.getChildren().remove(popupPane);
        });
        grid.add(submit,0,row);

        row++;
        if(!exchanger.isMustChoose())
        {
            grid.add(skipAction, 0, row);
            skipAction.setOnMousePressed(e->{
                exchanger.setAnswer(null);
                exchanger.setRequest(Interaction.UPDATEVIEW, "", match, true);
            });
        }

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    /**
     * Ask the cardinal direction
     * @param message the message
     */
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
        l.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
        l.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);


        ToggleGroup radioGroup = new ToggleGroup();
        int row = 1;
        for(Direction d:dirs){
            RadioButton radio = new RadioButton(d.toString());
            radio.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
            radio.setToggleGroup(radioGroup);
            radio.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
            if(row==1)
                radio.setSelected(true);
            GridPane.setHalignment(radio, HPos.CENTER);
            grid.add(radio,0,row++);
        }

        //continue here
        Button submit = new Button(CONFIRM_BUTTON_TEXT);
        submit.setFont(getFont(POPUPFONTDIM * 1.3 * dimMult));
        submit.setOnAction(rs -> {
            String answer = ((RadioButton)radioGroup.getSelectedToggle()).getText();
            println(answer);
            exchanger.setAnswer(Direction.valueOf(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            masterPane.getChildren().remove(popupPane);
        });
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        row++;
        if(!exchanger.isMustChoose())
        {
            grid.add(skipAction, 0, row);
            skipAction.setOnMousePressed(e->{
                exchanger.setAnswer(null);
                exchanger.setRequest(Interaction.UPDATEVIEW, "", match, true);
            });
        }

        //Show the pane
        masterPane.getChildren().add(popupPane);
    }

    /**
     * It's the enter point for all the alerts/dialogs. It sets the executor for the next task
     * Used for: information messages, chooseRoom(List Integer ), chooseDirection(List Direction), chooseMap (List int to Integer), chooseFrenzy bool
     * @param dialog the method that handle the dialog you want to show
     * @param message the message to show
     */
    private void showAlert(Consumer<String> dialog, String message){
        exchanger.setActualInteraction(Interaction.WAITINGUSER);
        uiExec.execute(() -> dialog.accept(message));
    }

    /**
     * Creates a Grid of a defined width and one single column of that width
     * @param width the requested width
     * @return the Grid
     */
    private GridPane gridMaker(double width){
        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setMaxWidth(width);
        grid.setMinWidth(width);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(width);
        grid.getColumnConstraints().add(cc);
        return grid;
    }

    //
    //
    // Settings methods
    //
    //

    /**
     * (Settings method) Ask the user a String
     * Used for IP, nick, phrase
     * @param message to show
     */
    private void askSetting(String message){
        StackPane root = initializerBackground();
        GridPane grid = gridMaker(480 * dimMult);

        Label l = new Label(message);
        l.setFont(getFont(SETTINGSFONTDIM*dimMult));
        l.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
        l.setTextAlignment(TextAlignment.CENTER);
        l.setWrapText(true);

        TextField field = new TextField();
        field.setFont(Font.font(SETTINGSFONTDIM*dimMult));

        Button submit = new Button(CONFIRM_BUTTON_TEXT);
        submit.setFont(getFont(SETTINGSFONTDIM*dimMult));
        submit.setOnAction(e->{
            String answer = field.getText();
            if(!answer.isEmpty() && !answer.isBlank())
            {
                println(answer);
                exchanger.setAnswer(answer);
                exchanger.setActualInteraction(Interaction.NONE);
                appStage.setScene(new Scene(initializerBackground()));
            }
        });
        submit.setDefaultButton(true);

        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);
        grid.add(field,0,1);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,2);

        root.getChildren().addAll(grid);

        appStage.setScene(new Scene(root));
    }

    /**
     * (Settings method) Prints out messages from server received before match start
     * @param message to show
     */
    private void settingsMessage(String message){
        StackPane root = initializerBackground();
        GridPane grid = gridMaker(480 * dimMult);

        String[] pieces = message.split("\n");
        String formattedText = "";

        for(int i = min(3, pieces.length - 1); i >= 0; i--) {
            formattedText += pieces[i] + "\n\n";
        }

        Label l = new Label(formattedText);
        l.setPrefSize(480 * dimMult, 420 * dimMult);
        l.setWrapText(true);
        l.setFont(getFont(SETTINGSFONTDIM*0.8*dimMult));
        l.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));

        GridPane.setHalignment(l, HPos.RIGHT);
        GridPane.setValignment(l, VPos.TOP);
        grid.add(l,0,0);

        root.getChildren().addAll(grid);

        appStage.setScene(new Scene(root));
    }

    /**
     * (Settings method) Generic method for requests with radio
     * @param message the message
     * @param group the group of radio buttons
     * @param buttons the buttons
     * @param eventHandler the event handler
     */
    private void askSettingsRadio(String message, ToggleGroup group, List<String> buttons, javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler){
        Pane root = initializerBackground();
        GridPane grid = gridMaker(480 * dimMult);

        Label l = new Label(message);
        l.setFont(getFont(SETTINGSFONTDIM*dimMult));
        l.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
        l.setWrapText(true);
        l.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(l, HPos.CENTER);
        grid.add(l,0,0);


        int row = 1;
        for(String s:buttons){
            RadioButton radio = new RadioButton(s);
            radio.setToggleGroup(group);
            radio.setTextFill(javafx.scene.paint.Color.web(WHITE_HEX));
            radio.setFont(getFont( SETTINGSFONTDIM*dimMult));
            if(row==1)
                radio.setSelected(true);
            grid.add(radio, 0, row++);
        }

        Button submit = new Button(CONFIRM_BUTTON_TEXT);
        submit.setFont(getFont(SETTINGSFONTDIM*dimMult));
        submit.setOnAction(eventHandler);
        submit.setDefaultButton(true);
        GridPane.setHalignment(submit, HPos.CENTER);
        grid.add(submit,0,row);

        root.getChildren().addAll(grid);
        appStage.setScene(new Scene(root));
    }

    /**
     * (Settings method) ask which of the found local adresses is the right one
     * @param message the message to be shown
     */
    private void askLocalAddress(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = (List<String>) exchanger.getRequest();

        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            println(answer);
            exchanger.setAnswer(answer);
            exchanger.setActualInteraction(Interaction.NONE);
            appStage.setScene(new Scene(initializerBackground()));
        });

        askSettingsRadio(message, group, buttons, eventHandler);
    }

    /**
     * (Settings method) Ask the user between using the RMI or the Socket
     * @param message the message
     */
    private void askRMI(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();
        buttons.add(SOCKET);
        buttons.add(RMI);
        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            println(answer);
            exchanger.setAnswer(answer.equalsIgnoreCase(RMI));
            exchanger.setActualInteraction(Interaction.NONE);
            appStage.setScene(new Scene(initializerBackground()));
        });

        askSettingsRadio(message, group, buttons, eventHandler);
    }

    /**
     * (Settings method) Ask the user which fighter he wants to use
     * @param message the message
     */
    private void askFighter(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();

        List<Fighter> available = (List<Fighter>) exchanger.getRequest();
        for(Fighter f:available)
            buttons.add(f.toString());

        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            println(answer);
            exchanger.setAnswer(Fighter.valueOf(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            appStage.setScene(new Scene(initializerBackground()));
        });

        askSettingsRadio(message, group, buttons, eventHandler);
    }

    /**
     * (Settings method) ask the number of skulls he whants to play with
     * @param message the message
     */
    private void askSkulls(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();

        for(int i = 5; i<=8; i++)
            buttons.add(Integer.toString(i));

        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            println(answer);
            exchanger.setAnswer(Integer.parseInt(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            appStage.setScene(new Scene(initializerBackground()));
        });

        askSettingsRadio(message, group, buttons, eventHandler);
    }

    /**
     * (Settings method) Ask the map that the first user wants
     * @param message the message
     */
    private void askMap(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();
        buttons.add(MAPPA + " 1");
        buttons.add(MAPPA + " 2");
        buttons.add(MAPPA + " 3");
        buttons.add(MAPPA + " 4");
        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            answer = answer.substring(6);//get the number
            println(answer);
            exchanger.setAnswer(Integer.parseInt(answer));
            exchanger.setActualInteraction(Interaction.NONE);
            appStage.setScene(new Scene(initializerBackground()));
        });

        askSettingsRadio(message, group, buttons, eventHandler);
    }

    /**
     * (Settings method) Ask the first user if he wants the frenzy mode
     * @param message the message
     */
    private void askFrenzy(String message){
        ToggleGroup group = new ToggleGroup();
        List<String> buttons = new ArrayList<>();
        buttons.add(CON_FRENESIA);
        buttons.add(SENZA_FRENESIA);
        javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler = (e->{
            String answer = ((RadioButton)group.getSelectedToggle()).getText();
            println(answer);
            exchanger.setAnswer(answer.equalsIgnoreCase(CON_FRENESIA));
            exchanger.setActualInteraction(Interaction.NONE);
            appStage.setScene(new Scene(initializerBackground()));
        });

        askSettingsRadio(message, group, buttons, eventHandler);
    }

    private Font getFont(double dim){
        InputStream streamFont = getClass().getClassLoader().getResourceAsStream(FONT_NAME);

        return Font.loadFont(streamFont, dim*0.8); //the new font is bigger
    }

    private static void println(String s) { System.out.println(s);}
}