package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.SInteraction;
import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionLambdaMap {
    private HashMap<String, ActionLambda> data;
    private static ActionLambdaMap instance = null;

    private ActionLambdaMap(){
        data = new HashMap<>();

    //Base lambdas
        //(List<Player>)memory
        data.put("w1-b", (pl, map, memory)->{
            //Dai 2 danni e un marchio a un bersaglio che puoi vedere

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            chosen.applyEffects(EffectsLambda.marks(1, pl));

            ((List<Player>)memory).add(chosen);
        });

        //memory = Player[2]
        data.put("w2-b", (pl, map, memory)->{
            //Scegli 1 o 2 bersagli che puoi vedere e dai 1 danno a entrambi.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen1 = SInteraction.chooseTarget(pl.getConn(), targets);
            Player chosen2 = SInteraction.chooseTarget(pl.getConn(), targets);
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
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            ((Player[])memory)[0] = chosen;
        });

        //CLONE
        //((Player[])memory)[0]
        data.put("w4-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            ((Player[])memory)[0] = chosen;
        });

        data.put("w5-b", (pl, map, memory)->{
            //Dai 3 danni e 1 marchio a 1 bersaglio che puoi vedere. Il bersaglio deve essere ad almeno 2 movimenti da te.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)>=2);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w6-b", (pl, map, memory)->{
            //Dai 1 danno a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==0);
            for(Player p: targets)
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w7-b", (pl, map, memory)->{
            //Muovi un bersaglio di 0, 1 o 2 quadrati fino a un quadrato che puoi vedere e dagli 1 danno.

            List<Point> pointsAll = Map.pointsAround(pl.getPosition(), map, 2);
            List<Point> pointsVisible = Map.pointsAround(pl.getPosition(), map, 0);
            List<Point> pointsNotVisible = new ArrayList<>(pointsAll);
            pointsNotVisible.removeAll(pointsVisible);
            List<Player> targets = new ArrayList<>();

            for(Player p:Map.playersInTheMap(map))
                if(pointsAll.contains(p.getPosition()))
                    targets.add(p);

            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);

            if(pointsNotVisible.contains(chosen.getPosition())){
                List<Point> movableTo = Map.possibleMovements(chosen.getPosition(), 2, map);
                movableTo.removeAll(pointsNotVisible);
                Point newPos = SInteraction.displace(pl.getConn(), chosen, movableTo);

                chosen.applyEffects(EffectsLambda.move(newPos));
            }

            chosen.applyEffects(EffectsLambda.damage(1, pl));
        });

        //(Point) memory
        data.put("w8-b", (pl, map, memory)->{
            //Scegli un quadrato che puoi vedere ad almeno 1 movimento di distanza. Un vortice si apre in quel punto. Scegli un bersaglio nel quadrato
            //in cui si trova il vortice o distante 1 movimento. Muovi il bersaglio nel quadrato in cui si trova il vortice e dagli 2 danni.
            List<Point> points = Map.pointsAround(pl.getPosition(), map, 0);
            points.remove(pl.getPosition());

            //Point vortexPoint = SInteraction.chooseCell();
            Point vortexPoint = null;

            Player fakePlayer = new Player("vortex", "", Fighter.VIOLETTA); //TODO check there's no problem if someone has that name
            fakePlayer.applyEffects(EffectsLambda.move(vortexPoint));
            List<Player> targets = Map.distanceStrategy(fakePlayer, map, (p1,p2)->Map.distance(p1,p2)<=1);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);

            chosen.applyEffects(EffectsLambda.move(vortexPoint));
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            try {
                ((Point) memory).set(vortexPoint.getX(), vortexPoint.getY());
            }catch (WrongPointException e){
                ;
            }
        });

        data.put("w9-b", (pl, map, memory)->{
            //Scegli una stanza che puoi vedere, ma non la stanza in cui ti trovi. Dai 1 danno a ognuno in quella stanza.

            List<Integer> visibleRooms = Map.visibleRooms(pl.getPosition(), map);
            visibleRooms.remove(map.getCell(pl.getPosition()).getRoomNumber());

            List<Player> allInMap = Map.playersInTheMap(map);

            int roomChosen = 1;
            //roomChosen = SInteraction.chooseRoom(); TODO

            for(Player p:allInMap)
                if(roomChosen == map.getCell(pl.getPosition()).getRoomNumber())
                    p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w10-b", (pl, map, memory)->{
            //Scegli 1 bersaglio che non puoi vedere e dagli 3 danni.

            List<Player> targets = Map.playersInTheMap(map);
            targets.removeAll(Map.visiblePlayers(pl, map));
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
        });

        data.put("w11-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai un marchio a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)>=1);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));

            //Mark him and the others in that square
            for(Player p:map.getCell(chosen.getPosition()).getPawns())
                p.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w12-b", (pl, map, memory)->{
            //Scegli un quadrato distante 1 movimento e possibilmente un secondo quadrato distante ancora 1 movimento nella stessa direzione. In ogni quadrato puoi scegliere 1 bersaglio e dargli 1 danno.

            List<Point> squares = Map.possibleMovements(pl.getPosition(), 1, map);

            Point chosen = null;
            //Point chosen = SInteraction.chooseCell(squares); TODO

            //Find the next X&Y in the same direction, it's needed for the second part of the effect
            int nX, nY;
            nX = pl.getPosition().getX();
            nY = pl.getPosition().getY();
            if(pl.getPosition().getY()-1 == chosen.getY())
                nY--;
            else if(pl.getPosition().getX()+1 == chosen.getX())
                nX++;
            else if(pl.getPosition().getY()+1 == chosen.getY())
                nY++;
            else if(pl.getPosition().getX()-1 == chosen.getX())
                nX--;

            for(Player p:map.getCell(chosen).getPawns())
                p.applyEffects(EffectsLambda.damage(2, pl));

            for(Player p:map.getCell(nX, nY).getPawns())
                p.applyEffects(EffectsLambda.damage(1, pl));


            List<Player> targets = map.getCell(chosen).getPawns();
            Player chosen1 = SInteraction.chooseTarget(pl.getConn(), targets);

            targets = map.getCell(nX,nY).getPawns();
            Player chosen2 = SInteraction.chooseTarget(pl.getConn(), targets);

            //Give damage
            chosen1.applyEffects(EffectsLambda.damage(1, pl));
            if(chosen2 != null)
                chosen2.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w13-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = SInteraction.displace(pl.getConn(), chosen, dest);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.move(where));
        });

        data.put("w14-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)!=0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = SInteraction.displace(pl.getConn(), chosen, dest);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            chosen.applyEffects(EffectsLambda.move(where));
        });

        data.put("w15-b", (pl, map, memory)->{
            //Scegli una direzione cardinale e 1 bersaglio in quella direzione. Dagli 3 danni.
            Direction dir = Direction.NORTH; //ex

            //dir = SInteraction.chooseDirection(); TODO

            List<Player> targets = Map.visiblePlayers(pl, map, dir);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
        });

        data.put("w16-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w17-b", (pl, map, memory)->{
            //Dai 1 danno e 2 marchi a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w18-b", (pl, map, memory)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi. Se vuoi puoi muovere quel bersaglio di 1 quadrato.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = SInteraction.displace(pl.getConn(), chosen, dest);
            chosen.applyEffects(EffectsLambda.damage(3, pl));
            if(where != null)
                chosen.applyEffects(EffectsLambda.move(where));
        });

        data.put("w19-b", (pl, map, memory)->{
            //Scegli 1 bersaglio in un quadrato distante esattamente 1 movimento. Muovi in quel quadrato e dai al bersaglio 1 danno e 2 marchi.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==1);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            pl.applyEffects(EffectsLambda.move(chosen.getPosition()));
            chosen.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w20-b", (pl, map, memory)->{
            //Scegli fino a 3 bersagli su quadrati differenti, ognuno distante esattamente 1 movimento da me. Dai 1 danno a ogni bersaglio.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1,p2)->Map.distance(p1,p2)==1);
            List<Player> chosen = new ArrayList<>();

            chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));
            for(Player p: map.getCell(chosen.get(0).getPosition()).getPawns())
                targets.remove(p);
            chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));
            if(chosen.size() > 1){
                for(Player p: map.getCell(chosen.get(1).getPosition()).getPawns())
                    targets.remove(p);
                chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));
            }

            for(Player p: chosen)
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w21-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

    //Additional lambdas
        data.put("w1-ad1", (pl, map, memory)->{
            //Dai 1 marchio a un altro bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            targets.removeAll((List<Player>)memory); //not the one I've already gave damage
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.marks(1, pl));
        });

        //((Player[])memory)[0]
        data.put("w2-ad1", (pl, map, memory)->{
            //Dai 1 danno aggiuntivo a uno dei due bersagli.

            ((Player[])memory)[0].applyEffects(EffectsLambda.marks(1, pl));
        });

        //((Player[])memory)[0]
        data.put("w2-ad2", (pl, map, memory)->{
            //Dai 1 danno aggiuntivo all'altro dei bersagli e/o dai 1 danno a un bersaglio differente che puoi vedere.

            List<Player> targets = new ArrayList<>();
            targets.add(((Player[])memory)[1]);
            Player chosenOther = SInteraction.chooseTarget(pl.getConn(), targets);
            if(chosenOther != null)
                chosenOther.applyEffects(EffectsLambda.damage(1, pl));

            targets = Map.visiblePlayers(pl, map);
            targets.remove(((Player[])memory)[0]);
            targets.remove(((Player[])memory)[1]);
            Player chosenDifferent = SInteraction.chooseTarget(pl.getConn(), targets);
            if(chosenDifferent != null)
                chosenDifferent.applyEffects(EffectsLambda.damage(1, pl));
        });

        //((Player[])memory)[0]
        data.put("w3-ad1", (pl, map, memory)->{
            //Dai 1 danno a un secondo bersaglio che il tuo primo bersaglio può vedere.

            List<Player> targets = Map.visiblePlayers(((Player[])memory)[0], map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));

            ((Player[])memory)[1] = chosen;
        });

        //((Player[])memory)[1]
        data.put("w3-ad2", (pl, map, memory)->{
            //Dai 2 danni a un terzo bersaglio che il tuo secondo bersaglio può vedere. Non puoi usare questo effetto se prima non hai usato reazione a catena.

            List<Player> targets = Map.visiblePlayers(((Player[])memory)[1], map);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w4-ad1", (pl, map, memory)->{
            //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 2, map);
            Point chosenPoint = SInteraction.move(pl.getConn(), points);
            pl.applyEffects(EffectsLambda.move(chosenPoint));
        });

        //((Player[])memory)[0]
        data.put("w4-ad2", (pl, map, memory)->{
            //Dai 1 danno aggiuntivo al tuo bersaglio.

            ((Player[])memory)[0].applyEffects(EffectsLambda.damage(1, pl));
        });

        //(Point) memory
        data.put("w8-ad1", (pl, map, memory)->{
            //Scegli fino ad altri 2 bersagli nel quadrato in cui si trova il vortice o distanti 1 movimento. Muovili nel quadrato in cui si trova il vortice e dai loro 1 danno ciascuno.
            Player fakePlayer = new Player("vortex", "", Fighter.VIOLETTA); //TODO check there's no problem if someone has that name
            fakePlayer.applyEffects(EffectsLambda.move((Point)memory));
            List<Player> targets = Map.distanceStrategy(fakePlayer, map, ((p1, p2) -> Map.distance(p1,p2)<=1));
            List<Player> chosen = new ArrayList<>();

            chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));
            targets.removeAll(chosen);
            chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));

            for(Player p:chosen){
                p.applyEffects(EffectsLambda.move((Point)memory));
                p.applyEffects(EffectsLambda.damage(1, pl));
            }
        });

        data.put("w13-ad1", (pl, map, memory)->{
            //Dai 1 danno a ogni giocatore in quadrato che puoi vedere. Puoi usare questo effetto prima o dopo il movimento dell'effetto base.

            List<Point> visiblePoints = Map.pointsAround(pl.getPosition(), map, 0);
            //Point chosenPoint = SInteraction.chooseCell(); TODO
            Point chosenPoint = null;

            for(Player p:map.getCell(chosenPoint).getPawns())
                if(p!=pl)
                    p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w14-ad1", (pl, map, memory)->{
            //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 2, map);
            Point chosenPoint = SInteraction.move(pl.getConn(), points);
            pl.applyEffects(EffectsLambda.move(chosenPoint));
        });

        //From additional (w14-ad2) it becomes alternative (w14-al)
        data.put("w14-al", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.
            //Durante l'effetto base, dai 1 danno a ogni giocatore presente nel quadrato in cui si trovava originariamente il bersaglio, incluso il bersaglio, anche se lo hai mosso.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)!=0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            List<Point> dest = Map.possibleMovements(chosen.getPosition(), 1, map);
            Point where = SInteraction.displace(pl.getConn(), chosen, dest);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
            for(Player p:map.getCell(chosen.getPosition()).getPawns())
                p.applyEffects(EffectsLambda.damage(1, pl));
            chosen.applyEffects(EffectsLambda.move(where));
        });

        data.put("w16-ad1", (pl, map, memory)->{
            //Muovi di 1 quadrato prima o dopo l'effetto base.

            List<Point> points = Map.possibleMovements(pl.getPosition(), 1, map);
            Point chosenPoint = SInteraction.move(pl.getConn(), points);
            pl.applyEffects(EffectsLambda.move(chosenPoint));
        });

        data.put("w16-ad2", (pl, map, memory)->{
            //Dai 2 danni a un bersaglio differente nel quadrato in cui ti trovi. Il passo d'ombra può essere usato prima o dopo questo effetto.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        //Alternative effects
        data.put("w6-al", (pl, map, memory)->{
            //Dai 2 danni a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==0);
            for(Player p:targets)
                p.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w7-al", (pl, map, memory)->{
            //Scegli un bersaglio 0, 1, o 2 movimenti da te. Muovi quel bersaglio nel quadrato in cui ti trovi e dagli 3 danni.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)<=2);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.move(pl.getPosition()));
            chosen.applyEffects(EffectsLambda.damage(3, pl));
        });

        data.put("w9-al", (pl, map, memory)->{
            //Scegli un quadrato distante esattamente 1 movimento. Dai 1 danno e 1 marchio a ognuno in quel quadrato.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1,p2)->Map.distance(p1,p2)==1);
            List<Point> visiblePoints = new ArrayList<>();

            for(Player p:targets)
                visiblePoints.add(p.getPosition());

            //Point chosenPoint = SInteraction.chooseCell(); TODO
            Point chosenPoint = null;

            for(Player p:map.getCell(chosenPoint).getPawns())
                if(p!=pl){
                    p.applyEffects(EffectsLambda.damage(1, pl));
                    p.applyEffects(EffectsLambda.marks(1, pl));
                }
        });

        data.put("w11-al", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai 2 marchi a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)>=1);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(1, pl));

            //Mark him and the others in that square
            for(Player p:map.getCell(chosen.getPosition()).getPawns())
                p.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w12-al", (pl, map, memory)->{
            //Scegli 2 quadrati come prima. (come w12-b) Dai 2 danni a chiunque sia nel primo quadrato e 1 danno a chiunque si trovi nel secondo quadrato.

            List<Point> squares = Map.possibleMovements(pl.getPosition(), 1, map);

            Point chosen = null;
            //Point chosen = SInteraction.chooseCell(squares); TODO

            //Find the next X&Y in the same direction, it's needed for the second part of the effect
            int nX;
            int nY;
            nX = pl.getPosition().getX();
            nY = pl.getPosition().getY();
            if(pl.getPosition().getY()-1 == chosen.getY())
                nY--;
            else if(pl.getPosition().getX()+1 == chosen.getX())
                nX++;
            else if(pl.getPosition().getY()+1 == chosen.getY())
                nY++;
            else if(pl.getPosition().getX()-1 == chosen.getX())
                nX--;

            for(Player p:map.getCell(chosen).getPawns())
                p.applyEffects(EffectsLambda.damage(2, pl));

            for(Player p:map.getCell(nX, nY).getPawns())
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w15-al", (pl, map, memory)->{
            //Scegli una direzione cardinale e 1 o 2 bersagli in quella direzione. Dai 2 danni a ciascuno.
            Direction dir = Direction.NORTH;//ex

            //dir = SInteraction.chooseDirection(); TODO

            List<Player> targets = Map.visiblePlayers(pl, map, dir);
            Player chosen1 = SInteraction.chooseTarget(pl.getConn(), targets);
            Player chosen2 = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen1.applyEffects(EffectsLambda.damage(2, pl));
            if(chosen2 != null)
                chosen2.applyEffects(EffectsLambda.marks(2, pl));
        });

        data.put("w17-al", (pl, map, memory)->{
            //Scegli fino a 3 bersagli che puoi vedere e dai 1 marchio a ciascuno.

            List<Player> targets = Map.visiblePlayers(pl, map);
            List<Player> chosen = new ArrayList<>();

            chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));
            targets.remove(chosen.get(0));
            chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));
            targets.remove(chosen.get(1));
            chosen.add(SInteraction.chooseTarget(pl.getConn(), targets));
            for(Player p:chosen)
                if(p!=null)
                    p.applyEffects(EffectsLambda.marks(1, pl));
        });

        data.put("w18-al", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio in un quadrato distante esattamente 1 movimento.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==1);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));
        });

        data.put("w19-al", (pl, map, memory)->{
            //Scegli un quadrato distante esattamente 1 movimento. Muovi in quel quadrato. Puoi dare 2 danni a 1 bersaglio in quel quadrato. Se vuoi puoi muovere
            // ancora di 1 quadrato nella stessa direzione (ma solo se è un movimento valido). Puoi dare 2 danni a un bersaglio anche in quel quadrato.

            //Movement
            List<Point> positions = Map.possibleMovements(pl.getPosition(), 1, map);
            Point posChosen = SInteraction.move(pl.getConn(), positions);
            //First find the next X&Y in the same direction, it's needed for the second part of the effect
            int nX;
            int nY;
            nX = pl.getPosition().getX();
            nY = pl.getPosition().getY();
            if(pl.getPosition().getY()-1 == posChosen.getY())
                nY--;
            else if(pl.getPosition().getX()+1 == posChosen.getX())
                nX++;
            else if(pl.getPosition().getY()+1 == posChosen.getY())
                nY++;
            else if(pl.getPosition().getX()-1 == posChosen.getX())
                nX--;

            pl.applyEffects(EffectsLambda.move(posChosen));

            //Give damage
            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)->Map.distance(p1,p2)==0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(2, pl));

            //Next move (just if there is a next cell)
            if(map.getCell(nX, nY) != null){
                try{
                    List<Point> nextPoints = new ArrayList<>();
                    nextPoints.add(new Point(nX,nY));
                    posChosen = SInteraction.move(pl.getConn(), nextPoints);
                    pl.applyEffects(EffectsLambda.move(posChosen));

                    targets = Map.distanceStrategy(pl, map, (p1, p2)->Map.distance(p1,p2)==0);

                    chosen = SInteraction.chooseTarget(pl.getConn(), targets);
                    chosen.applyEffects(EffectsLambda.damage(2, pl));
                }catch(WrongPointException e){

                }


            }
        });

        data.put("w20-al", (pl, map, memory)->{
            //Dai 1 danno a tutti i bersagli che sono distanti esattamente 1 movimento.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==1);
            for(Player p:targets)
                p.applyEffects(EffectsLambda.damage(1, pl));
        });

        data.put("w21-al", (pl, map, memory)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi, poi muovi quel bersaglio di 0, 1 o 2 quadrati in una direzione.

            List<Player> targets = Map.distanceStrategy(pl, map, (p1, p2)-> Map.distance(p1, p2)==0);
            Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);
            chosen.applyEffects(EffectsLambda.damage(3, pl));

            List<Point> possiblePos = Map.possibleMovements(chosen.getPosition(), 2, map);
            Point newPos = SInteraction.displace(pl.getConn(), chosen, possiblePos);
            chosen.applyEffects(EffectsLambda.move(newPos));
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
}