package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Interaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * Return the singleton of the Class
     * @return the instance of the class
     */
    public static synchronized GuiExchanger getInstance(){
        if(instance==null)
            instance = new GuiExchanger();

        return instance;
    }

    /**
     * Return the actual interaction
     * @return the actual interaction
     */
    public synchronized Interaction getActualInteraction() {
        return actualInteraction;
    }

    /**
     * Return the last interaction (the one before the actual one)
     * @return the last interaction (the one before the actual one)
     */
    public Interaction getLastRealInteraction() {
        return lastRealInteraction;
    }

    /**
     * Set the request for the GUI
     * @param interaction the interaction that has to be done
     * @param message the message to show the user
     * @param request the data of the request
     * @param mustChoose if the user must or not choose
     */
    public synchronized void setRequest(Interaction interaction, String message, Object request, boolean mustChoose){
        instance.actualInteraction = interaction;
        instance.message = message;
        instance.request = request;
        instance.mustChoose = mustChoose;
        notifyAll(); //notify the gui
    }

    /**
     * Set the actual interaction
     * @param actualInteraction whill be the new actual interaction
     */
    public synchronized void setActualInteraction(Interaction actualInteraction) {
        this.lastRealInteraction = this.actualInteraction;
        this.actualInteraction = actualInteraction;
        if(actualInteraction == Interaction.NONE)
            notifyAll(); //notify the GuiInterface
    }

    /**
     * Reset the last interaction
     */
    public synchronized void resetLastRealInteraction() {
        actualInteraction = lastRealInteraction;
        notifyAll();
    }

    /**
     * Wait until the instance is free to be used for a new request
     */
    public synchronized void waitFreeToUse(){
        while(actualInteraction != Interaction.NONE)
            try {
                wait();
            }catch(InterruptedException e){
                ;
            }
        return;
    }

    /**
     * Wait untill a new request is incoming
     */
    public synchronized void waitRequestIncoming(){
        while(actualInteraction == Interaction.NONE ||
                actualInteraction == Interaction.WAITINGUSER ||
                actualInteraction ==Interaction.SHOWINGMESSAGE)
            try {
                wait();
            }catch(InterruptedException e){
                ;
            }
        return;
    }

    /**
     *
     * @return the request
     */
    public synchronized Object getRequest() {
        return request;
    }

    /**
     *
     * @return the answer
     */
    public synchronized Object getAnswer() {
        return answer;
    }

    /**
     *
     * @param answer set the answer
     */
    public synchronized void setAnswer(Object answer) {
        this.answer = answer;
    }

    /**
     *
     * @return the message
     */
    public synchronized String getMessage() {
        return message;
    }

    /**
     *
     * @return the must choose flag
     */
    public synchronized boolean isMustChoose() {
        return mustChoose;
    }

    /**
     * Tells the gui if the current action needs the skip button or not
     * Used for showing the skip button inside popups
     * @return True if it needs a popup, false otherwise
     */
    public synchronized boolean needsPopup()
    {
        Interaction[] noSkip = { Interaction.CHOOSEWEAPONACTION, Interaction.DISCARDPOWER, Interaction.CHOOSEROOM, Interaction.CHOOSEDIRECTION  };

        return !Arrays.asList(noSkip).contains(actualInteraction);
    }
}
