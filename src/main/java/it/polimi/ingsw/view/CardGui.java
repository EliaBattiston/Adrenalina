package it.polimi.ingsw.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CardGui extends StackPane {
    private it.polimi.ingsw.model.Card data;
    private Image img;
    private double x, y;
    private double width, height;

    public CardGui(it.polimi.ingsw.model.Weapon weapon, Gui gui){
        this.data = weapon;
        img = new Image( "file:images/weapon/weapon" + weapon.getId() + ".png" );

        width = gui.getWidth() * 0.085;
        height = width / 0.6;

        addImage(width, height);

        relocate(0, 0);

        setOnMousePressed(e ->{
            System.out.println("Clicked " + data.toString());
        });
    }

    public CardGui(it.polimi.ingsw.model.Loot loot, Gui gui){
        this.data = loot;
        img = new Image( "file:images/loot/" + loot.getContentAsString() + ".png" );

        width = gui.getWidth() * 0.05;
        height = gui.getWidth() * 0.05; //it's a square!

        addImage(width, height);

        relocate(400, 500);

    }

    public CardGui(it.polimi.ingsw.model.Power power, Gui gui){
        this.data = power;
        img = new Image( "file:images/power/power" + (power.getId()<12 ? power.getId() : power.getId()/2) + ".png" );

        width = gui.getWidth() * 0.0663;
        height = width / 0.64015;

        addImage(width, height);

    }

    private void addImage(double width, double height){
        Canvas canvas = new Canvas(width,height);
        getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( img, 0, 0, width,height);
    }
}
