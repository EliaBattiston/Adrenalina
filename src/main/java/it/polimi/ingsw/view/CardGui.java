package it.polimi.ingsw.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Remember to relocate the card after making it in the class that use it
 */
public class CardGui extends Canvas {
    private it.polimi.ingsw.model.Card data;
    private Image img;
    private Canvas canvas;

    public CardGui(it.polimi.ingsw.model.Weapon weapon, double canvasWidth, double canvasHeight, double imgWidth, double imgHeight, double x, double y){
        super(canvasWidth, canvasHeight);
        this.data = weapon;
        img = new Image( "file:images/weapon/weapon" + weapon.getId() + ".png" );

        this.getGraphicsContext2D().drawImage( img, x, y, imgWidth, imgHeight);

        setOnMousePressed(e ->{
            System.out.println("Clicked " + data.toString());
        });
    }

    public CardGui(it.polimi.ingsw.model.Loot loot, Gui gui, double width, double height, double x, double y){
        this.data = loot;
        img = new Image( "file:images/loot/" + loot.getContentAsString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, x, y, width, height);

    }

    public CardGui(it.polimi.ingsw.model.Power power, double canvasWidth, double canvasHeight, double imgWidth, double imgHeight, double x, double y){
        super(canvasWidth, canvasHeight);
        this.data = power;
        img = new Image( "file:images/power/power" + (power.getId()<12 ? power.getId() : power.getId()/2) + ".png" );

        this.getGraphicsContext2D().drawImage( img, x, y, imgWidth, imgHeight);

    }
}
