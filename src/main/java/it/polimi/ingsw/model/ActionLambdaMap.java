package it.polimi.ingsw.model;

import com.sun.org.apache.regexp.internal.RE;
import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Contains implementations of every lambda for weapons, powers and base actions
 */
public class ActionLambdaMap {
    private HashMap<String, ActionLambda> data;
    private static ActionLambdaMap instance = null;

    private ActionLambdaMap(){
        data = new HashMap<>();

    //Base weapon lambdas
        //((Player[])memory)[0]
        data.put("w1-b", (pl, map, memory)->{
            //Dai 2 danni e un marchio a un bersaglio che puoi vedere

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            chosen.applyEffects(EffectsLambda.marks(1, pl));

            ((Player[])memory)[0] = chosen;
        });

        //((Player[])memory)[0]
        data.put("w2-b", (pl, map, memory)->{
            //Scegli 1 o 2 bersagli che puoi vedere e dai 1 danno a entrambi.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen1 = pl.getConn().chooseTarget(targets, true);
            targets.remove(chosen1);
            Player chosen2 = null;
            if(!targets.isEmpty())
                chosen2 = pl.getConn().chooseTarget(targets, false);
            chosen1.applyEffects(EffectsLambda.damage(1, pl));
            if(chosen2 != null)
                chosen2.applyEffects(EffectsLambda.damage(1, pl));

            ((Player[])memory)[0] = chosen1;
            ((Player[])memory)[1] = chosen2;

        });

        //((Player[])memory)[0]
        data.put("w3-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = pl.getConn().chooseTarget(targets,true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            ((Player[])memory)[0] = chosen;
        });

        //CLONE
        //((Player[])memory)[0]
        data.put("w4-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            ((Player[])memory)[0] = chosen;
        });

        data.put("w5-b", (pl, map, memory)->{
            //Dai 3 danni e 1 marchio a 1 bersaglio che puoi vedere. Il bersaglio deve essere ad almeno 2 movimenti da te.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)>=2);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w6-b", (pl, map, memory)->{
            //Dai 1 danno a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            targets.remove(pl);
            for(Player p: targets)
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w7-b", (pl, map, memory)->{
            //Muovi un bersaglio di 0, 1 o 2 quadrati fino a un quadrato che puoi vedere e dagli 1 danno.

            List<Point> pointsAll = Map.visiblePoints(pl.getPosition(), map, 2);
            List<Point> pointsVisible = Map.visiblePoints(pl.getPosition(), map, 0);
            List<Point> pointsNotVisible = new ArrayList<>(pointsAll);
            pointsNotVisible.removeAll(pointsVisible);
            List<Player> targets = new ArrayList<>();

            for(Player p:Map.playersInTheMap(map))
                if(pointsAll.contains(p.getPosition()))
                    targets.add(p);

            Player chosen = pl.getConn().chooseTarget(targets, true);

            if(pointsNotVisible.contains(chosen.getPosition())){
                List<Point> movableTo = Map.possibleMovements(chosen.getPosition(), 2, map);
                movableTo.removeAll(pointsNotVisible);
                Point newPos = pl.getConn().moveEnemy(chosen, movableTo, true);

                chosen.applyEffects(EffectsLambda.move(pl, newPos, map));
            }

            chosen.applyEffects(EffectsLambda.damage(1, pl));
        });

        //(Point) memory
        data.put("w8-b", (pl, map, memory)->{
            //Scegli un quadrato che puoi vedere ad almeno 1 movimento di distanza. Un vortice si apre in quel punto. Scegli un bersaglio nel quadrato
            //in cui si trova il vortice o distante 1 movimento. Muovi il bersaglio nel quadrato in cui si trova il vortice e dagli 2 danni.
            List<Point> points = Map.visiblePoints(pl.getPosition(), map, 0);
            points.remove(pl.getPosition());

            Point vortexPoint = pl.getConn().choosePosition(points, true);

            Player fakePlayer = new Player("vortex", "", Fighter.VIOLETTA);
            fakePlayer.applyEffects((damage, marks, position, weapons, powers, ammo) -> position.set(vortexPoint));
            List<Player> targets = Map.playersAtGivenDistance(fakePlayer, map, true, (p1, p2)->Map.distance(p1,p2)<=1);
            Player chosen = pl.getConn().chooseTarget(targets, true);

            chosen.applyEffects(EffectsLambda.move(chosen, vortexPoint, map));
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            ((Point) memory).set(vortexPoint);
        });

        data.put("w9-b", (pl, map, memory)->{
            //Scegli una stanza che puoi vedere, ma non la stanza in cui ti trovi. Dai 1 danno a ognuno in quella stanza.
            List<Player> allInMap = Map.playersInTheMap(map);

            List<Integer> visibleRooms = Map.visibleRooms(pl.getPosition(), map);
            List<Integer> playersRooms = new ArrayList<>();

            for(Player p : allInMap)
            {
                if( visibleRooms.contains( map.getCell(p.getPosition()).getRoomNumber() ) )
                    playersRooms.add(map.getCell(p.getPosition()).getRoomNumber());
            }

            visibleRooms.remove(map.getCell(pl.getPosition()).getRoomNumber());

            int roomChosen = pl.getConn().chooseRoom(playersRooms, true);

            for(Player p:allInMap)
                if(roomChosen == map.getCell(p.getPosition()).getRoomNumber() && p != pl)
                    p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w10-b", (pl, map, memory)->{
            //Scegli 1 bersaglio che non puoi vedere e dagli 3 danni.

            List<Player> targets = Map.playersInTheMap(map);
            targets.removeAll(Map.visiblePlayers(pl, map));
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
        });

        data.put("w11-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai un marchio a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)>=1);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(1, pl));

            //Mark him and the others in that square
            for(Player p:map.getCell(chosen.getPosition()).getPawns())
                p.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w12-b", (pl, map, memory)->{
            //Scegli un quadrato distante 1 movimento e possibilmente un secondo quadrato distante ancora 1 movimento nella stessa direzione. In ogni quadrato puoi scegliere 1 bersaglio e dargli 1 danno.

            List<Point> squares = Map.possibleMovements(pl.getPosition(), 1, map);

            Point chosen = pl.getConn().choosePosition(squares, true);

            Point secondPoint = Map.nextPointSameDirection(pl.getPosition(), chosen);

            List<Player> targets = map.getCell(chosen).getPawns();
            Player chosen1 = pl.getConn().chooseTarget(targets, true);

            targets = map.getCell(secondPoint).getPawns();
            Player chosen2 = pl.getConn().chooseTarget(targets, false);

            //Give damage
            chosen1.applyEffects(EffectsLambda.damage(1, pl));
            if(chosen2 != null)
                chosen2.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w13-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = pl.getConn().moveEnemy(chosen, dest, false);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            if(where != null)
                chosen.applyEffects(EffectsLambda.move(chosen, where, map));
        });

        data.put("w14-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)!=0);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = pl.getConn().moveEnemy(chosen, dest, false);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            if(where!=null)
                chosen.applyEffects(EffectsLambda.move(chosen, where, map));
        });

        data.put("w15-b", (pl, map, memory)->{
            //Scegli una direzione cardinale e 1 bersaglio in quella direzione. Dagli 3 danni.
            Direction dir = pl.getConn().chooseDirection(true);

            List<Player> targets = Map.visiblePlayers(pl, map, dir);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
        });

        //((Player[])memory)[0] = chosen;
        data.put("w16-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            ((Player[])memory)[0] = chosen;
        });

        data.put("w17-b", (pl, map, memory)->{
            //Dai 1 danno e 2 marchi a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w18-b", (pl, map, memory)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi. Se vuoi puoi muovere quel bersaglio di 1 quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = pl.getConn().moveEnemy(chosen, dest, false);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
            if(where != null)
                chosen.applyEffects(EffectsLambda.move(chosen, where, map));
        });

        data.put("w19-b", (pl, map, memory)->{
            //Scegli 1 bersaglio in un quadrato distante esattamente 1 movimento. Muovi in quel quadrato e dai al bersaglio 1 danno e 2 marchi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==1);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            pl.applyEffects(EffectsLambda.move(pl, chosen.getPosition(), map));
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w20-b", (pl, map, memory)->{
            //Scegli fino a 3 bersagli su quadrati differenti, ognuno distante esattamente 1 movimento da me. Dai 1 danno a ogni bersaglio.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)->Map.distance(p1,p2)==1);
            List<Player> chosen = new ArrayList<>();

            chosen.add(pl.getConn().chooseTarget(targets, true));
            for(Player p: map.getCell(chosen.get(0).getPosition()).getPawns())
                targets.remove(p);
            chosen.add(pl.getConn().chooseTarget(targets, false));
            if(chosen.size() > 1){
                for(Player p: map.getCell(chosen.get(1).getPosition()).getPawns())
                    targets.remove(p);
                chosen.add(pl.getConn().chooseTarget(targets, false));
            }

            for(Player p: chosen)
                if(p!=null)
                    p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w21-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

    //Additional weapon lambdas
        //((Player[])memory)[0]
        data.put("w1-ad1", (pl, map, memory)->{
            //Dai 1 marchio a un altro bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            targets.removeAll((List<Player>)memory); //not the one I've already gave damage
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        //((Player[])memory)[0]
        //Dai 1 danno aggiuntivo a uno dei due bersagli.
        data.put("w2-ad1", (pl, map, memory)-> ((Player[])memory)[0].applyEffects(EffectsLambda.damage(1, pl)));

        //((Player[])memory)[0]
        data.put("w2-ad2", (pl, map, memory)->{
            //Dai 1 danno aggiuntivo all'altro dei bersagli e/o dai 1 danno a un bersaglio differente che puoi vedere.

            List<Player> targets = new ArrayList<>();
            targets.add(((Player[])memory)[1]);
            Player chosenOther = pl.getConn().chooseTarget(targets, false);
            if(chosenOther != null)
                chosenOther.applyEffects(EffectsLambda.damage(1, pl));

            targets = Map.visiblePlayers(pl, map);
            targets.remove(((Player[])memory)[0]);
            targets.remove(((Player[])memory)[1]);
            Player chosenDifferent = pl.getConn().chooseTarget(targets, false);
            if(chosenDifferent != null)
                chosenDifferent.applyEffects(EffectsLambda.damage(1, pl));
        });

        //((Player[])memory)[0]
        data.put("w3-ad1", (pl, map, memory)->{
            //Dai 1 danno a un secondo bersaglio che il tuo primo bersaglio può vedere.

            List<Player> targets = Map.visiblePlayers(((Player[])memory)[0], map);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(1, pl));

            ((Player[])memory)[1] = chosen;
        });

        //((Player[])memory)[1]
        data.put("w3-ad2", (pl, map, memory)->{
            //Dai 2 danni a un terzo bersaglio che il tuo secondo bersaglio può vedere. Non puoi usare questo effetto se prima non hai usato reazione a catena.

            List<Player> targets = Map.visiblePlayers(((Player[])memory)[1], map);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w4-ad1", (pl, map, memory)->{
            //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 2, map);
            Point chosenPoint = pl.getConn().movePlayer(points, true);
            pl.applyEffects(EffectsLambda.move(pl, chosenPoint, map));
        });

        //((Player[])memory)[0]
        //Dai 1 danno aggiuntivo al tuo bersaglio.
        data.put("w4-ad2", (pl, map, memory)->((Player[])memory)[0].applyEffects(EffectsLambda.damage(1, pl)));

        //(Point) memory
        data.put("w8-ad1", (pl, map, memory)->{
            //Scegli fino ad altri 2 bersagli nel quadrato in cui si trova il vortice o distanti 1 movimento. Muovili nel quadrato in cui si trova il vortice e dai loro 1 danno ciascuno.

            Player fakePlayer = new Player("vortex", "", Fighter.VIOLETTA);
            fakePlayer.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                try {
                    position.set((Point)memory);
                }catch(WrongPointException ex){
                    Logger.getGlobal().log( Level.SEVERE, ex.toString(), ex );
                }
            }));
            List<Player> targets = Map.playersAtGivenDistance(fakePlayer, map, true, ((p1, p2) -> Map.distance(p1,p2)<=1));
            List<Player> chosen = new ArrayList<>();

            chosen.add(pl.getConn().chooseTarget(targets, true));
            targets.removeAll(chosen);
            chosen.add(pl.getConn().chooseTarget(targets, false));

            for(Player p:chosen){
                if(p!=null){
                    p.applyEffects(EffectsLambda.move(p, (Point)memory, map));
                    p.applyEffects(EffectsLambda.damage(1, pl));
                }
            }
        });

        data.put("w13-ad1", (pl, map, memory)->{
            //Dai 1 danno a ogni giocatore in quadrato che puoi vedere. Puoi usare questo effetto prima o dopo il movimento dell'effetto base.

            List<Point> visiblePoints = Map.visiblePoints(pl.getPosition(), map, 0);
            Point chosenPoint = pl.getConn().choosePosition(visiblePoints, true);

            for(Player p:map.getCell(chosenPoint).getPawns())
                if(p!=pl)
                    p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w14-ad1", (pl, map, memory)->{
            //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 2, map);
            Point chosenPoint = pl.getConn().movePlayer(points, true);
            pl.applyEffects(EffectsLambda.move(pl, chosenPoint, map));
        });

        //From additional (w14-ad2) it becomes alternative (w14-al)
        data.put("w14-al", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.
            //Durante l'effetto base, dai 1 danno a ogni giocatore presente nel quadrato in cui si trovava originariamente il bersaglio, incluso il bersaglio, anche se lo hai mosso.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)!=0);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = pl.getConn().moveEnemy(chosen, dest, false);

            chosen.applyEffects(EffectsLambda.damage(2, pl));
            for(Player p:map.getCell(chosen.getPosition()).getPawns())
                p.applyEffects(EffectsLambda.damage(1, pl));
            if(where != null)
                chosen.applyEffects(EffectsLambda.move(chosen, where, map));
        });

        data.put("w16-ad1", (pl, map, memory)->{
            //Muovi di 1 quadrato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 1, map);
            Point chosenPoint = pl.getConn().movePlayer(points, true);
            pl.applyEffects(EffectsLambda.move(pl, chosenPoint, map));
        });

        //((Player[])memory)[0] = chosen;
        data.put("w16-ad2", (pl, map, memory)->{
            //Dai 2 danni a un bersaglio differente nel quadrato in cui ti trovi. Il passo d'ombra può essere usato prima o dopo questo effetto.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            targets.remove(((Player[])memory)[0]);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        //Alternative effects
        data.put("w6-al", (pl, map, memory)->{
            //Dai 2 danni a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            for(Player p:targets)
                p.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w7-al", (pl, map, memory)->{
            //Scegli un bersaglio 0, 1, o 2 movimenti da te. Muovi quel bersaglio nel quadrato in cui ti trovi e dagli 3 danni.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, false, (p1, p2)-> Map.distance(p1, p2)<=2);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.move(chosen, pl.getPosition(), map));
            chosen.applyEffects(EffectsLambda.damage(3, pl));
        });

        data.put("w9-al", (pl, map, memory)->{
            //Scegli un quadrato distante esattamente 1 movimento. Dai 1 danno e 1 marchio a ognuno in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)->Map.distance(p1,p2)==1);
            List<Point> visiblePoints = new ArrayList<>();

            for(Player p:targets)
                visiblePoints.add(p.getPosition());

            Point chosenPoint = pl.getConn().choosePosition(visiblePoints, true);

            for(Player p:map.getCell(chosenPoint).getPawns()){
                p.applyEffects(EffectsLambda.damage(1, pl));
                p.applyEffects(EffectsLambda.marks(1, pl));
            }
        });

        data.put("w11-al", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai 2 marchi a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)>=1);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(1, pl));

            //Mark him and the others in that square
            for(Player p:map.getCell(chosen.getPosition()).getPawns())
                p.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w12-al", (pl, map, memory)->{
            //Scegli 2 quadrati come prima. (come w12-b) Dai 2 danni a chiunque sia nel primo quadrato e 1 danno a chiunque si trovi nel secondo quadrato.

            List<Point> squares = Map.possibleMovements(pl.getPosition(), 1, map);

            Point chosen = pl.getConn().choosePosition(squares, true);

            Point secondPoint = Map.nextPointSameDirection(pl.getPosition(), chosen);

            for(Player p:map.getCell(chosen).getPawns())
                p.applyEffects(EffectsLambda.damage(2, pl));

            for(Player p:map.getCell(secondPoint).getPawns())
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w15-al", (pl, map, memory)->{
            //Scegli una direzione cardinale e 1 o 2 bersagli in quella direzione. Dai 2 danni a ciascuno.
            Direction dir = pl.getConn().chooseDirection(true);

            List<Player> targets = Map.visiblePlayers(pl, map, dir);
            Player chosen1 = pl.getConn().chooseTarget(targets, true);
            targets.remove(chosen1);
            Player chosen2 = pl.getConn().chooseTarget(targets, false);
            chosen1.applyEffects(EffectsLambda.damage(2, pl));
            if(chosen2 != null)
                chosen2.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w17-al", (pl, map, memory)->{
            //Scegli fino a 3 bersagli che puoi vedere e dai 1 marchio a ciascuno.

            List<Player> targets = Map.visiblePlayers(pl, map);
            List<Player> chosen = new ArrayList<>();

            chosen.add(pl.getConn().chooseTarget(targets, true));
            targets.remove(chosen.get(0));
            chosen.add(pl.getConn().chooseTarget(targets, false));
            targets.remove(chosen.get(1));
            chosen.add(pl.getConn().chooseTarget(targets, false));
            for(Player p:chosen)
                if(p!=null)
                    p.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w18-al", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio in un quadrato distante esattamente 1 movimento.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==1);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w19-al", (pl, map, memory)->{
            //Scegli un quadrato distante esattamente 1 movimento. Muovi in quel quadrato. Puoi dare 2 danni a 1 bersaglio in quel quadrato. Se vuoi puoi muovere
            // ancora di 1 quadrato nella stessa direzione (ma solo se è un movimento valido). Puoi dare 2 danni a un bersaglio anche in quel quadrato.

            //Movement
            List<Point> positions = Map.possibleMovements(pl.getPosition(), 1, map);
            Point posChosen = pl.getConn().movePlayer(positions, true);
            Point secondPoint = Map.nextPointSameDirection(pl.getPosition(), posChosen);

            pl.applyEffects(EffectsLambda.move(pl, posChosen, map));

            //Give damage
            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)->Map.distance(p1,p2)==0);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            //Next move (just if there is a next cell)
            if(map.getCell(secondPoint) != null){
                try{
                    List<Point> nextPoints = new ArrayList<>();
                    nextPoints.add(secondPoint);
                    posChosen = pl.getConn().movePlayer(nextPoints, true);
                    pl.applyEffects(EffectsLambda.move(pl, posChosen, map));

                    targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)->Map.distance(p1,p2)==0);

                    chosen = pl.getConn().chooseTarget(targets, true);
                    chosen.applyEffects(EffectsLambda.damage(2, pl));
                }catch(WrongPointException ex){
                    Logger.getGlobal().log( Level.SEVERE, ex.toString(), ex );
                }
            }
        });

        data.put("w20-al", (pl, map, memory)->{
            //Dai 1 danno a tutti i bersagli che sono distanti esattamente 1 movimento.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==1);
            for(Player p:targets)
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w21-al", (pl, map, memory)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi, poi muovi quel bersaglio di 0, 1 o 2 quadrati in una direzione.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(3, pl));

            List<Point> possiblePos = Map.possibleMovementsAllSingleDirection(chosen.getPosition(), 2, map);
            Point newPos = pl.getConn().moveEnemy(chosen, possiblePos, true);
            chosen.applyEffects(EffectsLambda.move(chosen, newPos, map));
        });

    //Powers lambdas
        //memory = List<Player>
        data.put("p1", (pl, map, memory)->{
            //Puoi giocare questa carta quando stai dando danno a uno o più bersagli. Paga 1 cubo munizioni di qualsiasi colore. Scegli 1 dei bersagli
            // e dagli 1 segnalino danno aggiuntivo. Nota: non puoi usare questo potenziamento per dare 1 danno a un bersaglio che sta solo ricevendo marchi.

            List<Player> targets = new ArrayList(Arrays.asList((Player[]) memory));
            Player chosen = pl.getConn().chooseTarget(targets, true);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("p2", (pl, map, memory)->{
            //Puoi giocare questa carta nel tuo turno prima o dopo aver svolto qualsiasi azione. Scegli la miniatura di un altro giocatore e muovila di 1 o 2
            // quadrati in una direzione. (Non puoi usare questo potenziamento per muovere una miniatura dopo che è stata rigenerata alla fine del tuo turno, è troppo tardi.)

            List<Player> targets = Map.playersInTheMap(map);
            Player chosen = pl.getConn().chooseTarget(targets, true);
            List<Point> newPos = Map.possibleMovementsAllSingleDirection(chosen.getPosition(), 2, map);
            newPos.remove(chosen.getPosition());
            Point chosenPos = pl.getConn().moveEnemy(chosen, newPos, true);
            chosen.applyEffects(EffectsLambda.move(chosen, chosenPos, map));
        });

        //((Player[]) memory)[0]
        data.put("p3", (pl, map, memory)-> {
            //Puoi giocare questa carta quando ricevi un danno da un giocatore che puoi vedere. Dai 1 marchio a quel giocatore.

            List<Player> visibles = Map.visiblePlayers(pl, map);

            if(visibles.contains(((Player[]) memory)[0]))//someone you don't see can attack you
                (((Player[]) memory)[0]).applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("p4", (pl, map, memory)->{
            //Puoi giocare questa carta nel tuo turno prima o dopo aver svolto qualsiasi azione. Prendi la tua miniatura e piazzala in un qualsiasi quadrato sulla plancia.
            // (Non puoi usare questo potenziamento dopo che hai visto dove un altro giocatore si rigenera alla fine del tuo turno, è troppo tardi.)

            List<Point> newPos = Map.possibleMovements(pl.getPosition(), 10, map); //I'll get all the squares available
            Point chosenPos = pl.getConn().movePlayer(newPos, true);
            pl.applyEffects(EffectsLambda.move(pl, chosenPos, map));
        });

    //Activities lambdas
        data.put("a-p", (pl, map, memory)->{
            List<Power> inHand = pl.getPowers().stream().filter(power -> power.getId() == 6 || power.getId() == 8).collect(Collectors.toList());
            if(!inHand.isEmpty())
            {
                //TODO add choosepower interaction
            }
        });

        data.put("a-b1", (pl, map, memory)-> run(pl, map, 3, true) );

        data.put("a-b2", (pl, map, memory)->{
            runToLoot(pl, map,1);
            pick(pl, map, ((Game)memory).getAmmoDeck(), ((Game)memory).getPowersDeck());
        });

        data.put("a-b3",(pl, map, memory) -> shoot(pl, map) );

        data.put("a-a1", (pl, map, memory)->{
            runToLoot(pl, map,2);
            pick(pl, map,((Game)memory).getAmmoDeck(), ((Game)memory).getPowersDeck());
        });

        data.put("a-a2", (pl, map, memory)->{
            runToShoot(pl, map,1);
            shoot(pl, map);
        });

        data.put("a-f1", (pl, map, memory)->{
            runToShoot(pl, map,1);
            reload(pl);
            shoot(pl, map);
        });

        data.put("a-f2", (pl, map, memory)-> run(pl, map,4,true) );

        data.put("a-f3", (pl, map, memory)->{
            runToLoot(pl, map,2);
            pick(pl, map,((Game)memory).getAmmoDeck(), ((Game)memory).getPowersDeck());
        });

        data.put("a-f4", (pl, map, memory)->{
            runToShoot(pl, map,2);
            reload(pl);
            shoot(pl, map);
        });

        data.put("a-f5", (pl, map, memory)->{
            runToLoot(pl, map,3);
            pick(pl, map, ((Game)memory).getAmmoDeck(), ((Game)memory).getPowersDeck());
        });
    }

    /**
     * Look for the lambda with lambdaName
     * @param lambdaName the name of the lambda you're looking for
     * @return the lambda you searched or null if it doesn't exists
     */
    public static ActionLambda getLambda(String lambdaName){
        if(instance == null)
            instance = new ActionLambdaMap();

        return instance.data.get(lambdaName);
    }

    /**
     * Basic run action
     * @param pl Lambda's player
     * @param map Lambda's map
     * @param steps number of allowed steps
     * @param mustChoose False if the player doesn't have to run
     */
    private static void run(Player pl, Map map, int steps, boolean mustChoose)
    {
        List<Point> destinations = Map.possibleMovements(pl.getPosition(), steps, map);
        Point chosen = pl.getConn().movePlayer(destinations, mustChoose);

        if(chosen != null)
            pl.applyEffects(EffectsLambda.move(pl, chosen, map));
    }

    /**
     * Basic run action that only brings the player to cells with loot
     * @param pl Lambda's player
     * @param map Lambda's map
     * @param steps number of allowed steps
     */
    private static void runToLoot(Player pl, Map map, int steps)
    {
        List<Point> possible = Map.possibleMovements(pl.getPosition(), steps, map);
        List<Point> destinations = new ArrayList<>(possible);

        for(Point p : possible)
        {
            if(map.getCell(p) instanceof RegularCell && ((RegularCell) map.getCell(p)).getLoot() == null)
            {
                destinations.remove(p);
            }
        }

        Point chosen = pl.getConn().movePlayer(destinations, false);

        if(chosen != null)
            pl.applyEffects(EffectsLambda.move(pl, chosen, map));
    }

    /**
     * Basic run action that only brings the player to cells where he can shoot
     * @param pl Lambda's player
     * @param map Lambda's map
     * @param steps number of allowed steps
     */
    private static void runToShoot(Player pl, Map map, int steps)
    {
        List<Point> possible = Map.possibleMovements(pl.getPosition(), steps, map);
        List<Point> destinations = new ArrayList<>(possible);

        Point initialPosition = pl.getPosition();

        for(Point p : possible)
        {
            //Put the player in the simulated future position
            pl.applyEffects(EffectsLambda.move(pl, p, map));

            //If no weapon has suitable action, we can't propose to move to this position
            if( pl.getWeapons().stream().filter(Weapon::isLoaded).noneMatch(w -> w.getBase().isFeasible(pl, map, null)) )
                if( pl.getWeapons().stream().filter(Weapon::isLoaded).noneMatch(w-> w.getAlternative() != null && w.getAlternative().isFeasible(pl, map, null)) )
                {
                    destinations.remove(p);
                }
        }

        //Return the player to its real position
        pl.applyEffects(EffectsLambda.move(pl, initialPosition, map));


        Point chosen = pl.getConn().movePlayer(destinations, false);

        if(chosen != null)
            pl.applyEffects(EffectsLambda.move(pl, chosen, map));
    }

    /**
     * Basic pick item action
     * @param pl Lambda's player
     * @param map Lambda's map
     * @param lootDeck Deck where to scrap the picked loot card
     * @param powersDeck Deck for picking a power card
     */
    private static void pick(Player pl, Map map, EndlessDeck<Loot> lootDeck, EndlessDeck<Power> powersDeck)
    {
        Cell current = map.getCell(pl.getPosition());
        //Ask something only if the player is in a spawn cell
        if(current instanceof SpawnCell)
        {
            Weapon picked = pl.getConn().chooseWeapon(((SpawnCell) current).getWeapons(), true);
            ((SpawnCell)current).pickWeapon(picked);

            //If the player already has 3 weapons he has to discard one
            if(pl.getWeapons().size() >= 3)
            {
                Weapon discard = pl.getConn().discardWeapon(pl.getWeapons(), true);

                pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                    int pos = Arrays.asList(weapons).indexOf(discard);
                    if(pos>-1 && pos<=3)
                    {
                        weapons[pos].setLoaded(false);
                        ((SpawnCell)current).refillWeapon(weapons[pos]);
                        weapons[pos] = null;
                    }
                    else
                    {
                        Logger.getGlobal().log(Level.SEVERE, "Weapon to be discarded is not in the player's hand", pl);
                    }
                }));
            }

            //Give the new weapon to the player
            pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                int pos;
                for(pos=0; pos<3 && weapons[pos] != null; pos++)
                    ;
                if(pos<=3 && weapons[pos] == null)
                {
                    weapons[pos] = picked;
                }
                else
                {
                    Logger.getGlobal().log(Level.SEVERE, "No space for new weapon in player's hand", pl);
                }
            }));
        }
        else if(current instanceof RegularCell)
        {
            Loot picked = ((RegularCell) current).pickLoot();

            pl.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
                for(Color c : picked.getContent())
                {
                    if(c == Color.POWER)
                    {
                        Power newPower = powersDeck.draw();
                        Power discarded = null;

                        if(Arrays.stream(powers).noneMatch(Objects::isNull))
                        {
                            List<Power> inHand = new ArrayList<>(Arrays.asList(powers));
                            inHand.add(newPower);
                            discarded = pl.getConn().discardPower(inHand, true);

                            if(discarded == newPower)
                                discarded = null;
                            else
                                powersDeck.scrapCard(discarded);
                        }

                        int empty = Arrays.asList(powers).indexOf(discarded);

                        if(empty != -1)
                            powers[empty] = newPower;
                        else{
                            Logger.getGlobal().log(Level.WARNING, "Scrapped new card");
                            powersDeck.scrapCard(newPower);
                        }
                    }
                    else
                        ammo.add(c, 1);
                }
            }));
            lootDeck.scrapCard(picked);
        }
        else
        {
            Logger.getGlobal().log(Level.SEVERE, "Unkown type of Cell", current);
        }
    }

    /**
     * Basic shoot action
     * @param pl Lambda's player
     * @param map Lambda's map
     */
    private static void shoot(Player pl, Map map)
    {
        //Only loaded weapons
        List<Weapon> loaded = pl.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
        Weapon chosen = pl.getConn().chooseWeapon(loaded, true);

        //Take list of available "base" actions for the chosen weapon
        List<Action> weaponActions = new ArrayList<>();
        if(chosen.getBase() != null && chosen.getBase().isFeasible(pl, map, null))
            weaponActions.add(chosen.getBase());
        if(chosen.getAlternative() != null && chosen.getAlternative().isFeasible(pl, map, null))
            weaponActions.add(chosen.getAlternative());

        //Ask the user which one he wants to use
        Action toExecute = pl.getConn().chooseAction(weaponActions, true);
        Object mem;
        switch (toExecute.getLambdaID())
        {
            case "w1-b":
            case "w2-b":
            case "w3-b":
            case "w4-b":
            case "w16-b":
                mem = new Player[2];
                break;

            case "w8-b":
                mem = new Point(0, 0);
                break;

            default:
                mem = null;
                break;
        }
        toExecute.execute(pl, map, mem);

        if(toExecute.getLambdaID().contains("-b")) //Base action
        {
            //Additional actions management
            weaponActions.clear();
            if(chosen.getAdditional() != null)
                weaponActions.addAll( chosen.getAdditional().stream().filter(action->action.isFeasible(pl, map, mem)).collect(Collectors.toList()) );

            if(!weaponActions.isEmpty()){
                toExecute = pl.getConn().chooseAction(weaponActions, false);
                toExecute.execute(pl, map, mem);

                weaponActions.addAll( chosen.getAdditional().stream().filter(action->action.isFeasible(pl, map, mem)).collect(Collectors.toList()) );
                weaponActions.remove(toExecute);
                if(!weaponActions.isEmpty())
                {
                    toExecute = pl.getConn().chooseAction(weaponActions, false);
                    toExecute.execute(pl, map, mem);
                }
            }
        }

        //Unload the used weapon
        pl.applyEffects((damage, marks, position, weapons, powers, ammo) -> {
            weapons[Arrays.asList(weapons).indexOf(chosen)].setLoaded(false);
        });
    }

    /**
     * Basic reload action
     * @param pl Lambda's player
     */
    public static void reload(Player pl)
    {
        List<Weapon> unloaded = pl.getWeapons().stream().filter(weapon -> !weapon.isLoaded()).collect(Collectors.toList());
        List<Weapon> reloadable =  new ArrayList<>(unloaded); //Only the weapons the player can currently reload
        List<Color> cost = new ArrayList<>();
        Weapon chosen = null;

        for(Weapon w : unloaded)
        {
            cost.clear();
            cost.add(w.getColor());
            if(w.getBase().getCost() != null)
                cost.addAll(w.getBase().getCost());

            if(pl.getAmmo(Color.RED) < cost.stream().filter(c -> c == Color.RED).count()
                    || pl.getAmmo(Color.BLUE) < cost.stream().filter(c -> c == Color.BLUE).count()
                    || pl.getAmmo(Color.YELLOW) < cost.stream().filter(c -> c == Color.YELLOW).count())
            {
                reloadable.remove(w);
            }
        }

        if(!reloadable.isEmpty())
            chosen = reloadable.get(0);

        while(!reloadable.isEmpty() && chosen != null)
        {
            chosen = pl.getConn().chooseWeapon(reloadable, false);

            if(chosen != null)
            {
                chosen.setLoaded(true);
                unloaded.remove(chosen);
            }

            reloadable.clear();
            reloadable.addAll(unloaded);

            for(Weapon w : unloaded)
            {
                cost.clear();
                cost.add(w.getColor());
                if(w.getBase().getCost() != null)
                    cost.addAll(w.getBase().getCost());

                if(pl.getAmmo(Color.RED) < cost.stream().filter(c -> c == Color.RED).count()
                        || pl.getAmmo(Color.BLUE) < cost.stream().filter(c -> c == Color.BLUE).count()
                        || pl.getAmmo(Color.YELLOW) < cost.stream().filter(c -> c == Color.YELLOW).count())
                {
                    reloadable.remove(w);
                }
            }
        }
    }
}