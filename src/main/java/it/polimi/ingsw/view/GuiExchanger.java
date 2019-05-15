package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Interaction;

public class GuiExchanger {
    private static GuiExchanger instance = null;
    private Interaction actualInteraction;
    private MatchView matchView;
    private Object request;
    private Object answer;

    private GuiExchanger(){
        actualInteraction = Interaction.NONE;
    }

    public static GuiExchanger getInstance(){
        if(instance==null)
            instance = new GuiExchanger();

        return instance;
    }

    public Interaction getActualInteraction() {
        return actualInteraction;
    }

    public void setActualInteraction(Interaction actualInteraction) {
        this.actualInteraction = actualInteraction;
    }

    public MatchView getMatchView() {
        return matchView;
    }

    public void setMatchView(MatchView matchView) {
        this.matchView = matchView;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }
}
