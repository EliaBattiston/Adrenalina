package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Loot;
import it.polimi.ingsw.model.Power;
import it.polimi.ingsw.model.Weapon;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Remember to relocate the card after making it in the class that use it
 */
public class GuiCard extends Canvas {
    private it.polimi.ingsw.model.Card data;
    private Image img;

    public GuiCard(it.polimi.ingsw.model.Weapon weapon, double width, double height, int rotation){
        super((rotation==0?width:height), (rotation==0?height:width));
        this.data = weapon;

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

        setOnMousePressed(e ->{
            System.out.println("Clicked " + ((Weapon)data).getName());
        });

        setOnMouseEntered(e ->{
            setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);");
        });
        setOnMouseExited(e->{
            setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);");
        });
    }

    public GuiCard(it.polimi.ingsw.model.Loot loot, double size){
        super(size, size);
        this.data = loot;
        img = GuiImagesMap.getImage( "file:images/loot/" + loot.getContentAsString() + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, size, size);

        setOnMousePressed(e ->{
            System.out.println("Clicked " + ((Loot)data).getContentAsString());
        });
    }

    public GuiCard(it.polimi.ingsw.model.Power power, double width, double height){
        super(width, height);
        this.data = power;
        img = GuiImagesMap.getImage( "file:images/power/power" + (power.getId()<=12 ? power.getId() : power.getId()/2) + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, width, height);

        setOnMousePressed(e ->{
            System.out.println("Clicked " + ((Power)data).getName());
        });
    }

    public void setPosition(double x, double y){
        setPickOnBounds(false);
        setTranslateX(x);
        setTranslateY(y);
    }
}
