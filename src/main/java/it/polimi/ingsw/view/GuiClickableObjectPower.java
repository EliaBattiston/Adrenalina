package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Power;

import java.util.List;

/**
 * A GuiClickableObject that draws a power
 */
public class GuiClickableObjectPower extends GuiClickableObject {
    private Power power;

    public GuiClickableObjectPower(Power power, double width, double height){
        super(width, height);
        this.power = power;
        img = GuiImagesMap.getImage(  "power/power" + (power.getId()<=12 ? power.getId() : power.getId()-12) + ".png" );

        this.getGraphicsContext2D().drawImage( img, 0, 0, width, height);
    }

    /**
     * Determine if the card represented by this GuiClickableObject is inside the list
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
