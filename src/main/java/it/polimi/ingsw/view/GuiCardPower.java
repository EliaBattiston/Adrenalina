package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Power;

import java.util.List;

public class GuiCardPower extends GuiCard {
    private Power power;

    public GuiCardPower(Power power, double width, double height){
        super(width, height);
        this.power = power;
        img = GuiImagesMap.getImage( Gui.imgRoot + "power/power" + (power.getId()<=12 ? power.getId() : power.getId()-12) + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, width, height);

        setOnMousePressed(e ->{
            System.out.println("Clicked " + power.getName());
        });
        setOnMouseEntered(e -> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 10, 0.5, 0, 0);"));
        setOnMouseExited(e-> setStyle("-fx-effect: innershadow(gaussian, #d1d331, 0, 0, 0, 0);") );
    }

    /**
     * Determine if the card represented by this GuiCard is inside the list
     * @param list of possible matches
     * @return true if in the list
     */
    public boolean inList(List<Power> list){
        for(Power p:list)
            if(p.getId() == this.power.getId())
                return true;

        return false;
    }

    public Power getPower() {
        return power;
    }
}
