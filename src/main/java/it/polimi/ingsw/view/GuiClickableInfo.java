package it.polimi.ingsw.view;

/**
 * Draw an info button
 */
public class GuiClickableInfo extends GuiClickableObject {

    public GuiClickableInfo(double width, double height){
        super(width, height);

        img = GuiImagesMap.getImage( "info.png" );

        getGraphicsContext2D().drawImage(img, 0, 0, width, height);

        resetEventsStyle();
    }
}
