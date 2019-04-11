package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.SInteraction;

import java.util.HashMap;
import java.util.List;
import java.util.Collections;

public class ActionLambdaMap {
    private java.util.Map<String, ActionLambda> data;
    private static ActionLambdaMap instance = null;

    private ActionLambdaMap(){
        data = new HashMap<>();

        //TODO write here all the lambdas
        data.put("w1-b", (pl, m, playerList)->{
            //Dai 2 danni e un marchio a un bersaglio che puoi vedere
            List<Player> targets = Map.visibles(pl);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });
    }

    public static java.util.Map<String, ActionLambda> getActionsLambda(){
        if(instance == null)
            instance = new ActionLambdaMap();

        return new HashMap<>(instance.data);
    }
}
