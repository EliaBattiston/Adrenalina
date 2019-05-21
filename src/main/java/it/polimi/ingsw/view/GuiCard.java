package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Loot;
import it.polimi.ingsw.model.Power;
import it.polimi.ingsw.model.Weapon;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.List;

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


    public void resetEventsStyle(){
        //setOnMousePressed(e -> System.out.println("Clicked a card"));
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);") );

        setStyle("-fx-effect: innershadow(gaussian, #ffffff, 0, 0, 0, 0);");
    }

    public void setEventsChoosable(){
        setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0);");
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #a8ff22, 10, 0.7, 0, 0);"));//different green
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0)"));
    }
}
