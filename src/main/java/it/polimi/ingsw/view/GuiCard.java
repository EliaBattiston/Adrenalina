package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Weapon;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Remember to relocate the card after making it in the class that use it
 */
public class GuiCard {
    private it.polimi.ingsw.model.Card data;
    private Image img;
    private ImageView iv;

    public GuiCard(it.polimi.ingsw.model.Weapon weapon, GraphicsContext gc, double imgWidth, double imgHeight, double x, double y, int rotation){
        this.data = weapon;
        img = new Image( "file:images/weapon/weapon" + weapon.getId() + ".png" );

        iv = new ImageView(img);
        iv.setRotate(rotation);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        
        Image fixImg = iv.snapshot(params, null);
        if(rotation != 0){
            gc.drawImage(fixImg, x, y, imgHeight, imgWidth); //the rotation should be of +-90 degrees so height and width will be inverted
        }
        else
            gc.drawImage( fixImg, x, y, imgWidth, imgHeight);

        iv.setOnMousePressed(e ->{
            System.out.println("Clicked " + ((Weapon)data).getName());
        });
    }

    public GuiCard(it.polimi.ingsw.model.Loot loot, GraphicsContext gc, double width, double height, double x, double y){
        this.data = loot;
        img = new Image( "file:images/loot/" + loot.getContentAsString() + ".png" );

        gc.drawImage( img, x, y, width, height);
    }

    public GuiCard(it.polimi.ingsw.model.Power power, GraphicsContext gc, double imgWidth, double imgHeight, double x, double y){
        this.data = power;
        img = new Image( "file:images/power/power" + (power.getId()<12 ? power.getId() : power.getId()/2) + ".png" );

        gc.drawImage( img, x, y, imgWidth, imgHeight);
    }
}
