package it.polimi.ingsw.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

/**
 * A GuiClickableObject is a canvas with special methods for positioning and highlighting
 */
public abstract class GuiClickableObject extends Canvas {
    protected Image img;

    protected GuiClickableObject(double width, double height){
        super(width, height);
        setPickOnBounds(false);
    }

    /**
     * Set the position of the GuiClicableObject and set the clicks through the transparences of it
     * @param x new x pos
     * @param y new y pos
     */
    public void setPosition(double x, double y){
        setPickOnBounds(false);
        setTranslateX(x);
        setTranslateY(y);
    }

    /**
     * Resets the styles to the basic ones
     */
    public void resetEventsStyle(){
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);") );

        setStyle("-fx-effect: innershadow(gaussian, #ffffff, 0, 0, 0, 0);");
    }

    /**
     * Sets the styles to clickable ones
     */
    public void setEventsChoosable(){
        setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0);");
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #a8ff22, 30, 0.7, 0, 0);"));//different green
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0)"));
    }
}
