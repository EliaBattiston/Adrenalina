package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Loot;

/**
 * A GuiClickableObject that draws loot
 */
public class GuiClickableObjectLoot extends GuiClickableObject {
    public GuiClickableObjectLoot(Loot loot, double size){
        super(size, size);
        img = GuiImagesMap.getImage( "loot/" + loot.getContentAsString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, size, size);
    }
}
