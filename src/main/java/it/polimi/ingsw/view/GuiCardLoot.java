package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Loot;

import java.util.List;
import java.util.logging.Logger;

public class GuiCardLoot extends GuiCard {
    private Loot loot;

    public GuiCardLoot(Loot loot, double size){
        super(size, size);
        this.loot = loot;
        img = GuiImagesMap.getImage( "loot/" + loot.getContentAsString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, size, size);
    }
}
