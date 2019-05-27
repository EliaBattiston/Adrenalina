package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

/**
 * Draw an info button
 */
public class GuiInfo extends Canvas {
    //todo let it extends the GuiClickableObject class
    protected Image img;

    public GuiInfo(Player pl, double width, double height){
        super(width, height);
        setPickOnBounds(false);

        img = GuiImagesMap.getImage( "info.png" );

        getGraphicsContext2D().drawImage(img, 0, 0, width, height);

        resetEventsStyle();
    }

    public void setPosition(double x, double y){
        setPickOnBounds(false);
        setTranslateX(x);
        setTranslateY(y);
    }

    public void resetEventsStyle(){
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);") );

        setStyle("-fx-effect: innershadow(gaussian, #ffffff, 0, 0, 0, 0);");
    }
}
