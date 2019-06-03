package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Player;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;

import java.util.List;

/**
 * A GuiClickableObject that draws a pawn
 */
public class GuiClickableObjectPawn extends GuiClickableObject {
    private Player pl;
    private boolean viewer;

    public GuiClickableObjectPawn(Player pl, double size, boolean viewer){
        super(size, size);
        this.pl = pl;
        img = GuiImagesMap.getImage( "playerPawn/" + pl.getCharacter().toString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, size, size);

        this.viewer = viewer;

        if(this.viewer)
            this.setEffect(makeGlow("#123aff"));
        else
            this.setEffect(makeGlow("#ffffff"));
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

    /**
     * Resets the styles to the basic ones
     */
    public void resetEventsStyle(){
        setOnMouseEntered(e -> setEffect(makeGlow("#d1d331")));
        setOnMouseExited(e-> setEffect(makeGlow("#ffffff")) );
        setEffect(makeGlow("#ffffff"));
    }

    /**
     * Sets the styles to clickable ones
     */
    public void setEventsChoosable(){
        setEffect(makeGlow("#36ff0e"));
        setOnMouseEntered(e -> setEffect(makeGlow("#e2ff24")) );//different green
        setOnMouseExited(e-> setEffect(makeGlow("#36ff0e")) );
    }

    private DropShadow makeGlow(String color)
    {
        return new DropShadow(BlurType.GAUSSIAN, javafx.scene.paint.Color.web(color), 10, 0.7, 0, 0);
    }
}
