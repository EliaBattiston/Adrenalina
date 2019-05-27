package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Weapon;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.List;

public class GuiCardWeapon extends GuiCard {
    private Weapon weapon;

    public GuiCardWeapon(Weapon weapon, double width, double height, int rotation){
        super((rotation==0?width:height), (rotation==0?height:width));
        this.weapon = weapon;

        img = GuiImagesMap.getImage( "weapon/weapon" + weapon.getId() + ".png" );

        if(rotation != 0){
            ImageView iv = new ImageView(img);
            iv.setPickOnBounds(false);
            iv.setRotate(rotation);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            Image rotatedImage = iv.snapshot(params, null);
            getGraphicsContext2D().drawImage(rotatedImage, 0, 0, height, width); //the rotation should be of +-90 degrees so height and width will be inverted

            //Make it darker if not loaded
            if(!weapon.isLoaded())
            {
                getGraphicsContext2D().setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.7));
                getGraphicsContext2D().fillRect(0, 0, height, width);
            }
        }
        else
        {
            getGraphicsContext2D().drawImage(img, 0, 0, width, height);

            //Make it darker if not loaded
            if(!weapon.isLoaded())
            {
                getGraphicsContext2D().setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.7));
                getGraphicsContext2D().fillRect(0, 0, width, height);
            }
        }

        resetEventsStyle();
    }

    /*public boolean equalsWeapon(Weapon obj) {
        return weapon.getId() == obj.getId();
    }*/

    /**
     * Determine if the card represented by this GuiCard is inside the list
     * @param list of possible matches
     * @return true if in the list
     */
    public boolean inList(List<Weapon> list){
        for(Weapon w:list)
            if(w.getId() == this.weapon.getId())
                return true;

        return false;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
