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

        img = GuiImagesMap.getImage( "file:images/weapon/weapon" + weapon.getId() + ".png" );

        if(rotation != 0){
            ImageView iv = new ImageView(img);
            iv.setPickOnBounds(false);
            iv.setRotate(rotation);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            Image rotatedImage = iv.snapshot(params, null);
            getGraphicsContext2D().drawImage(rotatedImage, 0, 0, height, width); //the rotation should be of +-90 degrees so height and width will be inverted
        }
        else
            getGraphicsContext2D().drawImage( img, 0, 0, width, height);

        resetEventsStyle();
    }

    public void resetEventsStyle(){
        setOnMousePressed(e -> System.out.println("Clicked " + weapon.getName()));
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);") );

        setStyle("-fx-effect: innershadow(gaussian, #ffffff, 0, 0, 0, 0);");
    }

    public void setEventsChoosable(){
        setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0);");
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 30, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #36ff0e, 10, 0.5, 0, 0)"));
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
