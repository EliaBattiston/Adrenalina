package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

class Payload {
    private Interaction type;
    private String parameters;
    private Player enemy;
    private boolean mustChoose;

    public Interaction getType() {
        return type;
    }

    public void setType(Interaction type) {
        this.type = type;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Player getEnemy() {
        return enemy;
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public boolean isMustChoose() {
        return mustChoose;
    }

    public void setMustChoose(boolean mustChoose) {
        this.mustChoose = mustChoose;
    }
}
