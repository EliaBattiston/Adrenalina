package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Loot;

import java.util.logging.Logger;

public class GuiCardLoot extends GuiCard {
    private Loot loot;

    public GuiCardLoot(Loot loot, double size){
        super(size, size);
        this.loot = loot;
        img = GuiImagesMap.getImage( "file:images/loot/" + loot.getContentAsString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, size, size);

        setOnMousePressed(e ->{
            System.out.println("Clicked " + loot.getContentAsString());
        });
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);") );
    }
}
