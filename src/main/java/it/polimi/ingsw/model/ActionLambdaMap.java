package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ActionLambdaMap {
    private java.util.Map<String, ActionLambda> data;
    private ActionLambdaMap instance = null;

    private ActionLambdaMap(){
        data = new HashMap<>();

        //TODO restart from here
        data.put("w1", new ActionLambda() {
            @Override
            public void execute(Player pl, Map m, List<Player> playersList) {
                //pl.applyEffects();
            }
        });
    }

    public java.util.Map<String, ActionLambda> getActionsLambda(){
        if(instance == null)
            instance = new ActionLambdaMap();

        return instance.data;
    }
}
