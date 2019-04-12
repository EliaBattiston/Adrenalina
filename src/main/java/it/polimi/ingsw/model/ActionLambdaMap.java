package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.SInteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

public class ActionLambdaMap {
    private HashMap<String, ActionLambda> data;
    private static ActionLambdaMap instance = null;

    private ActionLambdaMap(){
        data = new HashMap<>();

    //Base lambdas
        data.put("w1-b", (pl, map, playerList)->{
            //Dai 2 danni e un marchio a un bersaglio che puoi vedere

            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w2-b", (pl, map, playerList)->{
            //Scegli 1 o 2 bersagli che puoi vedere e dai 1 danno a entrambi.

            List<Player> targets = Map.visibles(pl, map);
            Player chosen1 = SInteraction.chooseTarget(pl.getConn(), targets);
            Player chosen2 = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen1.applyEffects(EffectsLambda.damage(1, pl));
            chosen2.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w3-b", (pl, map, playerList)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w4-b", (pl, map, playerList)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w5-b", (pl, map, playerList)->{
            //Dai 3 danni e 1 marchio a 1 bersaglio che puoi vedere. Il bersaglio deve essere ad almeno 2 movimenti da te.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)>=2)
                    targets.add(p);

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w6-b", (pl, map, playerList)->{
            //Dai 1 danno a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)==0)
                    targets.add(p);

            for(Player p: targets)
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        //TODO all
        data.put("w7-b", (pl, map, playerList)->{
            //Muovi un bersaglio di 0, 1 o 2 quadrati fino a un quadrato che puoi vedere e dagli 1 danno.
            /*List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(, pl));
            chosen.applyEffects(EffectsLambda.marks(, pl));*/
        });

        //TODO
        data.put("w8-b", (pl, map, playerList)->{
            //Scegli un quadrato che puoi vedere ad almeno 1 movimento di distanza. Un vortice si apre in quel punto. Scegli un bersaglio nel quadrato
            //in cui si trova il vortice o distante 1 movimento. Muovi il bersaglio nel quadrato in cui si trova il vortice e dagli 2 danni.
            List<Point> visiblePoints = null;
            List<Player> targets = Map.visibles(pl, map);//TODO min distance
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        //TODO
        data.put("w9-b", (pl, map, playerList)->{
            //Scegli una stanza che puoi vedere, ma non la stanza in cui ti trovi. Dai 1 danno a ognuno in quella stanza.

            List<Player> targets = Map.visibles(pl, map);
            //Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            /*ArrayList<Integer> rooms = null;
            ArrayList<Player> players = SInteraction.chooseRoom(pl.getConn(), List<Integer> rooms);
            chosen.applyEffects(EffectsLambda.damage(, pl));
            chosen.applyEffects(EffectsLambda.marks(, pl));*/
        });

        data.put("w10-b", (pl, map, playerList)->{
            //Scegli 1 bersaglio che non puoi vedere e dagli 3 danni.

            List<Player> targets = Map.playersInTheMap(map);
            targets.removeAll(Map.visibles(pl, map));
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
        });

        data.put("w11-b", (pl, map, playerList)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai un marchio a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)>=1)
                    targets.add(p);

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));

            //Mark him and the others in that square
            for(Player p:map.getCell(chosen.getPosition().getX(), chosen.getPosition().getY()).getPawns())
                p.applyEffects(EffectsLambda.marks(1, pl));
        });

        //TODO
        data.put("w12-b", (pl, map, playerList)->{
            //Scegli un quadrato distante 1 movimento e possibilmente un secondo quadrato distante ancora 1 movimento nella stessa direzione. In ogni quadrato puoi scegliere 1 bersaglio e dargli 1 danno.

            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(0, pl));
            chosen.applyEffects(EffectsLambda.marks(0, pl));
        });

        data.put("w13-b", (pl, map, playerList)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = SInteraction.displace(pl.getConn(), chosen, dest);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.move(where));
        });

        data.put("w14-b", (pl, map, playerList)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)!=0)
                    targets.add(p);

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = SInteraction.displace(pl.getConn(), chosen, dest);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            chosen.applyEffects(EffectsLambda.move(where));
        });

        //TODO
        data.put("w15-b", (pl, map, playerList)->{
            //Scegli una direzione cardinale e 1 bersaglio in quella direzione. Dagli 3 danni.
/*
            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(, pl));
            chosen.applyEffects(EffectsLambda.marks(, pl));*/
        });

        data.put("w16-b", (pl, map, playerList)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)==0)
                    targets.add(p);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w17-b", (pl, map, playerList)->{
            //Dai 1 danno e 2 marchi a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w18-b", (pl, map, playerList)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi. Se vuoi puoi muovere quel bersaglio di 1 quadrato.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)==0)
                    targets.add(p);

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = SInteraction.displace(pl.getConn(), chosen, dest);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
            chosen.applyEffects(EffectsLambda.move(where));
        });

        data.put("w19-b", (pl, map, playerList)->{
            //Scegli 1 bersaglio in un quadrato distante esattamente 1 movimento. Muovi in quel quadrato e dai al bersaglio 1 danno e 2 marchi.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)==1)
                    targets.add(p);

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            pl.applyEffects(EffectsLambda.move(chosen.getPosition()));
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.marks(2, pl));
        });

        //TODO
        data.put("w20-b", (pl, map, playerList)->{
            //Scegli fino a 3 bersagli su quadrati differenti, ognuno distante esattamente 1 movimento. Dai 1 danno a ogni bersaglio.
            /*
            List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(, pl));
            chosen.applyEffects(EffectsLambda.marks(, pl));*/
        });

        data.put("w21-b", (pl, map, playerList)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)==0)
                    targets.add(p);

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

    //Additional lambdas
        data.put("w1-ad1", (pl, map, playerList)->{
            //Dai 1 marchio a un altro bersaglio che puoi vedere.

            List<Player> targets = Map.visibles(pl, map);
            targets.removeAll(playerList);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w2-ad1", (pl, map, playerList)->{
            //Dai 1 danno aggiuntivo a uno dei due bersagli.

            List<Player> targets = playerList;
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        //TODO
        data.put("w2-ad2", (pl, map, playerList)->{
            //Dai 1 danno aggiuntivo all'altro dei bersagli e/o dai 1 danno a un bersaglio differente che puoi vedere.

            /*List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));*/
        });

        data.put("w3-ad1", (pl, map, playerList)->{
            //Dai 1 danno a un secondo bersaglio che il tuo primo bersaglio può vedere.

            List<Player> targets = Map.visibles(playerList.get(0), map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
        });

        //TODO somewhere say that this method requests as a playerlist the first and the second enemy targetted becouse
        // it works only if w3-ad1 has been used
        //TODO check if it's possible to hit the same target and test this a little bit
        data.put("w3-ad2", (pl, map, playerList)->{
            //Dai 2 danni a un terzo bersaglio che il tuo secondo bersaglio può vedere. Non puoi usare questo effetto se prima non hai usato reazione a catena.

            List<Player> targets = Map.visibles(playerList.get(1), map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w4-ad1", (pl, map, playerList)->{
            //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 2, map);
            Point chosenPoint = SInteraction.move(pl.getConn(), points);
            pl.applyEffects(EffectsLambda.move(chosenPoint));
        });

        data.put("w4-ad2", (pl, map, playerList)->{
            //Dai 1 danno aggiuntivo al tuo bersaglio.

            playerList.get(0).applyEffects(EffectsLambda.damage(1, pl));
        });

        //TODO
        data.put("w8-ad1", (pl, map, playerList)->{
            //Scegli fino ad altri 2 bersagli nel quadrato in cui si trova il vortice o distanti 1 movimento. Muovili nel quadrato in cui si trova il vortice e dai loro 1 danno ciascuno.

            /*List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(, pl));
            chosen.applyEffects(EffectsLambda.marks(, pl));*/
        });

        //TODO
        data.put("w13-ad1", (pl, map, playerList)->{
            //Dai 1 danno a ogni giocatore in quadrato che puoi vedere. Puoi usare questo effetto prima o dopo il movimento dell'effetto base.

            /*List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);//TODO say that you're going to give damage to all the ones in that square
            chosen.applyEffects(EffectsLambda.damage(, pl));
            chosen.applyEffects(EffectsLambda.marks(, pl));*/
        });

        data.put("w14-ad1", (pl, map, playerList)->{
            //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 2, map);
            Point chosenPoint = SInteraction.move(pl.getConn(), points);
            pl.applyEffects(EffectsLambda.move(chosenPoint));
        });

        //TODO
        data.put("w14-ad2", (pl, map, playerList)->{
            //Durante l'effetto base, dai 1 danno a ogni giocatore presente nel quadrato in cui si trovava originariamente il bersaglio, incluso il bersaglio, anche se lo hai mosso.

            /*List<Player> targets = Map.visibles(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(, pl));
            chosen.applyEffects(EffectsLambda.marks(, pl));*/
        });

        data.put("w16-ad1", (pl, map, playerList)->{
            //Muovi di 1 quadrato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 1, map);
            Point chosenPoint = SInteraction.move(pl.getConn(), points);
            pl.applyEffects(EffectsLambda.move(chosenPoint));
        });

        data.put("w16-ad2", (pl, map, playerList)->{
            //Dai 2 danni a un bersaglio differente nel quadrato in cui ti trovi. Il passo d'ombra può essere usato prima o dopo questo effetto.

            List<Player> visibles = Map.visibles(pl, map);
            List<Player> targets = new ArrayList<>();
            for(Player p:visibles)
                if(Map.distance(pl, p)==0)
                    targets.add(p);

            targets.removeAll(playerList); //not to the same player

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });


    }

    public static HashMap<String, ActionLambda> getActionsLambda(){
        if(instance == null)
            instance = new ActionLambdaMap();

        return new HashMap<>(instance.data);
    }
}
