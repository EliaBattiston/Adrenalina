package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Player;

import java.util.List;

/**
 * A GuiClickableObject that draws a pawn
 */
public class GuiClickableObjectPawn extends GuiClickableObject {
    private Player pl;

    public GuiClickableObjectPawn(Player pl, double size){
        super(size, size);
        this.pl = pl;
        img = GuiImagesMap.getImage( "playerPawn/" + pl.getCharacter().toString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, size, size);

        setStyle("-fx-effect: dropshadow(gaussian, #ffffff, 15, 0.7, 0, 0);");
    }

    /**
     * Determine if the card represented by this GuiClickableObject is inside the list
     * @param list of possible matches
     * @return true if in the list
     */
    public boolean inList(List<Player> list){
        for(Player p:list)
            if(p.getCharacter() == this.pl.getCharacter())
                return true;

        return false;
    }

    public Player getPlayer() {
        return pl;
    }
}
