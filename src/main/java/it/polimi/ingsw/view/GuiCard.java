package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Loot;
import it.polimi.ingsw.model.Power;
import it.polimi.ingsw.model.Weapon;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Remember to relocate the card after making it in the class that use it
 */
public abstract class GuiCard extends Canvas {
    protected Image img;

    protected GuiCard(double width, double height){
        super(width, height);
    }

    public void setPosition(double x, double y){
        setPickOnBounds(false);
        setTranslateX(x);
        setTranslateY(y);
    }
}
