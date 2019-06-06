package it.polimi.ingsw.view;

import it.polimi.ingsw.clientmodel.PlayerView;
import it.polimi.ingsw.model.Player;

/**
 * Draw an info button
 */
public class GuiClickableInfo extends GuiClickableObject {

    public GuiClickableInfo(PlayerView pl, double width, double height){
        super(width, height);

        img = GuiImagesMap.getImage( "info.png" );

        getGraphicsContext2D().drawImage(img, 0, 0, width, height);

        resetEventsStyle();
    }
}
