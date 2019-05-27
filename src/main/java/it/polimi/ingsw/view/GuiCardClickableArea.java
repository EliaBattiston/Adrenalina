package it.polimi.ingsw.view;

public class GuiCardClickableArea extends GuiCard {
    private double w;
    private double h;

    public GuiCardClickableArea(double x, double y, double w, double h){
        super(w, h);
        setPosition(x, y);

        this.w = w;
        this.h = h;
    }

    @Override
    public void setEventsChoosable() {
        getGraphicsContext2D().setStroke(javafx.scene.paint.Color.rgb(255,255,255,0.6));
        getGraphicsContext2D().setLineWidth(7);
        getGraphicsContext2D().strokeRect(0,0,w, h);
        super.setEventsChoosable();
    }

    @Override
    public void resetEventsStyle() {
        getGraphicsContext2D().clearRect(0,0,w,h);
        super.resetEventsStyle();
    }
}
