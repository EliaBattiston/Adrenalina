package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Player;

import java.util.List;

public class GuiCardPawn extends GuiCard {
    private Player pl;

    public GuiCardPawn(Player pl, double size){
        super(size, size);
        this.pl = pl;
        img = GuiImagesMap.getImage( "playerPawn/" + pl.getCharacter().toString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, size, size);

        setOnMousePressed(e ->{
            System.out.println(e.getX() + "   " + e.getY() + "Clicked " + pl.getCharacter().toString() + getLayoutX()  + "  " + getLayoutY() + "  " + size);
        });
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);") );
    }

    /**
     * Determine if the card represented by this GuiCard is inside the list
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
