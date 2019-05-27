package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Weapon;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;

import java.util.List;

public class GuiInfo extends Canvas {
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
