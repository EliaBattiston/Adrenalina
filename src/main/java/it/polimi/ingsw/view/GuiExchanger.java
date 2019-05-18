package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Interaction;

public class GuiExchanger {
    private static GuiExchanger instance = null;
    private Interaction actualInteraction;
    private Object request;
    private Object answer;
    private String message;
    private boolean mustChoose;

    private GuiExchanger(){
        actualInteraction = Interaction.NONE;
    }

    public static synchronized GuiExchanger getInstance(){
        if(instance==null)
            instance = new GuiExchanger();

        return instance;
    }

    public synchronized Interaction getActualInteraction() {
        return actualInteraction;
    }

    public synchronized void setRequest(Interaction interaction, String message, Object request, boolean mustChoose){
        instance.actualInteraction = interaction;
        instance.message = message;
        instance.request = request;
        instance.mustChoose = mustChoose;
    }

    public synchronized void setActualInteraction(Interaction actualInteraction) {
        this.actualInteraction = actualInteraction;
    }

    public synchronized boolean isFreeToUse(){ return actualInteraction == Interaction.NONE; }

    public synchronized boolean guiRequestIncoming(){return actualInteraction != Interaction.NONE && actualInteraction != Interaction.WAITINGUSER && actualInteraction !=Interaction.SHOWINGMESSAGE; }

    public synchronized Object getRequest() {
        return request;
    }

    public synchronized Object getAnswer() {
        return answer;
    }

    public synchronized void setAnswer(Object answer) {
        this.answer = answer;
    }

    public synchronized String getMessage() {
        return message;
    }

    public synchronized boolean isMustChoose() {
        return mustChoose;
    }

}
