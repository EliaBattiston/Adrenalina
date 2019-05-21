package it.polimi.ingsw.view;

public class GuiCardAction extends GuiCard {

    public GuiCardAction(double x, double y, double w, double h){
        super(w, h);
        setPosition(x, y);

        getGraphicsContext2D().strokeRect(0,0,w, h);
    }
}
