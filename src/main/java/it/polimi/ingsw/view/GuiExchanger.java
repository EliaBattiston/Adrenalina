package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Interaction;

/**
 * Singleton that handles the information sharing through the GuiInterface and the Gui
 */
public class GuiExchanger {
    private static GuiExchanger instance = null;
    private Interaction actualInteraction;
    private Interaction lastRealInteraction;
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
        notifyAll(); //notify the gui
    }

    public synchronized void setActualInteraction(Interaction actualInteraction) {
        this.lastRealInteraction = this.actualInteraction;
        this.actualInteraction = actualInteraction;
        if(actualInteraction == Interaction.NONE)
            notifyAll(); //notify the GuiInterface
    }

    public synchronized void resetLastRealInteraction() {
        actualInteraction = lastRealInteraction;
        notifyAll();
    }

    public synchronized void waitFreeToUse(){
        if(actualInteraction != Interaction.NONE)
            try {
                wait();
            }catch(InterruptedException e){
                ;
            }
        return;
    }

    public synchronized void waitRequestIncoming(){
        if(actualInteraction == Interaction.NONE ||
                actualInteraction == Interaction.WAITINGUSER ||
                actualInteraction ==Interaction.SHOWINGMESSAGE)
            try {
                wait();
            }catch(InterruptedException e){
                ;
            }
        return;
    }

    public synchronized boolean guiRequestIncoming(){
        return actualInteraction != Interaction.NONE && actualInteraction != Interaction.WAITINGUSER && actualInteraction !=Interaction.SHOWINGMESSAGE;
    }

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
