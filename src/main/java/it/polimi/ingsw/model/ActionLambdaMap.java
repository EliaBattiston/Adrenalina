package it.polimi.ingsw.model;

import java.util.HashMap;

public class ActionLambdaMap {
    private java.util.Map<String, ActionLambda> data;
    private static ActionLambdaMap instance = null;

    private ActionLambdaMap(){
        data = new HashMap<>();

        //TODO write here all the lambdas, this is just an example
        data.put("w0", (pl, m, playerList)->{
            pl.getAmmo(Color.RED);
        });
    }

    public static java.util.Map<String, ActionLambda> getActionsLambda(){
        if(instance == null)
            instance = new ActionLambdaMap();

        return new HashMap<>(instance.data);
    }
}
