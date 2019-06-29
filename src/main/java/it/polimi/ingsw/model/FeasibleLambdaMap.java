package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FeasibleLambdaMap
{
    private HashMap<String, FeasibleLambda> data;
    private static FeasibleLambdaMap instance = null;

    private FeasibleLambdaMap()
    {
        data = new HashMap<>();

        //Base weapon lambdas
        data.put("w1-b", (pl, map, memory)->{
            //Dai 2 danni e un marchio a un bersaglio che puoi vedere

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w2-b", (pl, map, memory)->{
            //Scegli 1 o 2 bersagli che puoi vedere e dai 1 danno a entrambi.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w3-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        //CLONE
        data.put("w4-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w5-b", (pl, map, memory)->{
            //Dai 3 danni e 1 marchio a 1 bersaglio che puoi vedere. Il bersaglio deve essere ad almeno 2 movimenti da te.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)>=2);
            return !targets.isEmpty();
        });

        data.put("w6-b", (pl, map, memory)->{
            //Dai 1 danno a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==0);
            targets.remove(pl);
            return !targets.isEmpty();
        });

        data.put("w7-b", (pl, map, memory)->{
            //Muovi un bersaglio di 0, 1 o 2 quadrati fino a un quadrato che puoi vedere e dagli 1 danno.

            List<Point> pointsAll = Map.visiblePoints(pl.getPosition(), map, 2);
            List<Player> targets = new ArrayList<>();

            for(Player p:Map.playersInTheMap(map))
                if(p!=pl && pointsAll.contains(p.getPosition()))
                        targets.add(p);

            return !targets.isEmpty();
        });

        data.put("w8-b", (pl, map, memory)->{
            //Scegli un quadrato che puoi vedere ad almeno 1 movimento di distanza. Un vortice si apre in quel punto. Scegli un bersaglio nel quadrato
            //in cui si trova il vortice o distante 1 movimento. Muovi il bersaglio nel quadrato in cui si trova il vortice e dagli 2 danni.
            List<Point> points = Map.visiblePoints(pl.getPosition(), map, 0);

            boolean found = false;
            for(int i = 0; i < points.size() && !found; i++) {
                if(points.get(i).samePoint(pl.getPosition())) {
                    points.remove(i);
                    found = true;
                }
            }

            List<Player> targets = new ArrayList<>();
            Player fakePlayer = new Player("vortex", "", Fighter.VIOLETTA);
            for(Point p : points) {
                fakePlayer.applyEffects((damage, marks, position, weapons, powers, ammo) -> position.set(p));
                targets.addAll(Map.playersAtGivenDistance(fakePlayer, map, true, (p1, p2)->map.distance(p1, p2)<=1));
                targets.remove(pl);
            }

            return !targets.isEmpty();
        });

        data.put("w9-b", (pl, map, memory)->{
            //Scegli una stanza che puoi vedere, ma non la stanza in cui ti trovi. Dai 1 danno a ognuno in quella stanza.
            List<Player> allInMap = Map.playersInTheMap(map);
            allInMap.remove(pl);

            List<Integer> visibleRooms = Map.visibleRooms(pl.getPosition(), map);
            List<Integer> playersRooms = new ArrayList<>();

            for(Player p : allInMap)
            {
                if( visibleRooms.contains( map.getCell(p.getPosition()).getRoomNumber() ) )
                    playersRooms.add(map.getCell(p.getPosition()).getRoomNumber());
            }

            //Without converting to integer it removes the one @ position instead of the value - Andrea Aspesi
            visibleRooms.remove(((Integer)(map.getCell(pl.getPosition()).getRoomNumber())));

            return !playersRooms.isEmpty();
        });

        data.put("w10-b", (pl, map, memory)->{
            //Scegli 1 bersaglio che non puoi vedere e dagli 3 danni.

            List<Player> targets = Map.playersInTheMap(map);
            boolean found = false;
            targets.remove(pl);
            targets.removeAll(Map.visiblePlayers(pl, map));
            return !targets.isEmpty();
        });

        data.put("w11-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai un marchio a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)>=1);
            return !targets.isEmpty();
        });

        data.put("w12-b", (pl, map, memory)->{
            //Scegli un quadrato distante 1 movimento e possibilmente un secondo quadrato distante ancora 1 movimento nella stessa direzione. In ogni quadrato puoi scegliere 1 bersaglio e dargli 1 danno.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==1);
            return !targets.isEmpty();
        });

        data.put("w13-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w14-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)!=0);
            return !targets.isEmpty();
        });

        data.put("w15-b", (pl, map, memory)->{
            //Scegli una direzione cardinale e 1 bersaglio in quella direzione. Dagli 3 danni.

            List<Player> targets = Map.visiblePlayers(pl, map, Direction.NORTH);
            targets.addAll(Map.visiblePlayers(pl, map, Direction.EAST));
            targets.addAll(Map.visiblePlayers(pl, map, Direction.SOUTH));
            targets.addAll(Map.visiblePlayers(pl, map, Direction.WEST));
            return !targets.isEmpty();
        });

        data.put("w16-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

        data.put("w17-b", (pl, map, memory)->{
            //Dai 1 danno e 2 marchi a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w18-b", (pl, map, memory)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi. Se vuoi puoi muovere quel bersaglio di 1 quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

        data.put("w19-b", (pl, map, memory)->{
            //Scegli 1 bersaglio in un quadrato distante esattamente 1 movimento. Muovi in quel quadrato e dai al bersaglio 1 danno e 2 marchi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==1);
            return !targets.isEmpty();
        });

        data.put("w20-b", (pl, map, memory)->{
            //Scegli fino a 3 bersagli su quadrati differenti, ognuno distante esattamente 1 movimento da me. Dai 1 danno a ogni bersaglio.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)->map.distance(p1,p2)==1);
            return !targets.isEmpty();
        });

        data.put("w21-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

    //Additional weapon lambdas
        //Player[] memory
        data.put("w1-ad1", (pl, map, memory)->{
            //Dai 1 marchio a un altro bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            targets.removeAll(Arrays.asList((Player[])memory));
            return !targets.isEmpty();
        });

        //((Player[])memory)[0]
        //Dai 1 danno aggiuntivo a uno dei due bersagli.
        data.put("w2-ad1", (pl, map, memory)-> memory!=null);

        //((Player[])memory)[0]
        //Dai 1 danno aggiuntivo all'altro dei bersagli e/o dai 1 danno a un bersaglio differente che puoi vedere.
        data.put("w2-ad2", (pl, map, memory)-> memory!=null && ((Player[])memory)[1]!=null);

        //((Player[])memory)[0]
        data.put("w3-ad1", (pl, map, memory)->{
            //Dai 1 danno a un secondo bersaglio che il tuo primo bersaglio può vedere.

            List<Player> targets = Map.visiblePlayers(((Player[])memory)[0], map);
            targets.remove(pl);
            return !targets.isEmpty();
        });

        //((Player[])memory)[1]
        data.put("w3-ad2", (pl, map, memory)->{
            //Dai 2 danni a un terzo bersaglio che il tuo secondo bersaglio può vedere. Non puoi usare questo effetto se prima non hai usato reazione a catena.

            if(memory != null) {
                if (((Player[]) memory).length > 1 && ((Player[]) memory)[1] != null) {
                    List<Player> targets = Map.visiblePlayers(((Player[]) memory)[1], map);
                    targets.remove(pl);
                    targets.remove(((Player[]) memory)[0]);
                    return !targets.isEmpty();
                }
                else return false; //if ad1 has not been used
            }
            Logger.getGlobal().log(Level.SEVERE, "Wrong memory in w3-ad2");
            return false;
        });

        //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.
        data.put("w4-ad1", (pl, map, memory)-> true);

        //((Player[])memory)[0]
        //Dai 1 danno aggiuntivo al tuo bersaglio.
        data.put("w4-ad2", (pl, map, memory)-> memory != null && ((Player[])memory).length > 0 && ((Player[])memory)[0] != null);

        //((Player[])memory)[0]
        data.put("w8-ad1", (pl, map, memory)->{
            //Scegli fino ad altri 2 bersagli nel quadrato in cui si trova il vortice o distanti 1 movimento. Muovili nel quadrato in cui si trova il vortice e dai loro 1 danno ciascuno.

            Player fakePlayer = new Player("vortex", "", Fighter.VIOLETTA);
            Player shot = ((Player[])memory)[0];
            fakePlayer.applyEffects((damage, marks, position, weapons, powers, ammo) ->  position.set(shot.getPosition()));
            List<Player> targets = Map.playersAtGivenDistance(fakePlayer, map, true, ((p1, p2) -> map.distance(p1,p2)<=1));
            targets.remove(shot);
            targets.remove(pl);
            return !targets.isEmpty() && memory != null;
        });

        //Dai 1 danno a ogni giocatore in quadrato che puoi vedere. Puoi usare questo effetto prima o dopo il movimento dell'effetto base.
        data.put("w13-ad1", (pl, map, memory)-> !Map.visiblePlayers(pl, map).isEmpty());

        //Muovi di 1 o 2 quadrati. Questo effetto può essere usato prima o dopo l'effetto base.
        data.put("w14-ad1", (pl, map, memory)-> true);

        //From additional (w14-ad2) it becomes alternative (w14-al)
        data.put("w14-al", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.
            //Durante l'effetto base, dai 1 danno a ogni giocatore presente nel quadrato in cui si trovava originariamente il bersaglio, incluso il bersaglio, anche se lo hai mosso.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)!=0);
            return !targets.isEmpty();
        });

        //Muovi di 1 quadrato prima o dopo l'effetto base.
        data.put("w16-ad1", (pl, map, memory)-> true);

        //Dai 2 danni a un bersaglio differente nel quadrato in cui ti trovi. Il passo d'ombra può essere usato prima o dopo questo effetto.
        data.put("w16-ad2", (pl, map, memory)->{

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==0);
            targets.remove(((Player[])memory)[0]);
            return !targets.isEmpty();
        });

        //Alternative effects
        data.put("w6-al", (pl, map, memory)->{
            //Dai 2 danni a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

        data.put("w7-al", (pl, map, memory)->{
            //Scegli un bersaglio 0, 1, o 2 movimenti da te. Muovi quel bersaglio nel quadrato in cui ti trovi e dagli 3 danni.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, false, (p1, p2)-> map.distance(p1, p2)<=2);
            return !targets.isEmpty();
        });

        data.put("w9-al", (pl, map, memory)->{
            //Scegli un quadrato distante esattamente 1 movimento. Dai 1 danno e 1 marchio a ognuno in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)->map.distance(p1,p2)==1);
            return !targets.isEmpty();
        });

        data.put("w11-al", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai 2 marchi a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)>=1);
            return !targets.isEmpty();
        });

        data.put("w12-al", (pl, map, memory)-> {
            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2) -> map.distance(p1, p2) == 1);
            return !targets.isEmpty();
        });

        data.put("w15-al", (pl, map, memory)->{
            //Scegli una direzione cardinale e 1 o 2 bersagli in quella direzione. Dai 2 danni a ciascuno.

            List<Player> targets = Map.visiblePlayers(pl, map, Direction.NORTH);
            targets.addAll(Map.visiblePlayers(pl, map, Direction.EAST));
            targets.addAll(Map.visiblePlayers(pl, map, Direction.SOUTH));
            targets.addAll(Map.visiblePlayers(pl, map, Direction.WEST));
            return !targets.isEmpty();
        });

        data.put("w17-al", (pl, map, memory)->{
            //Scegli fino a 3 bersagli che puoi vedere e dai 1 marchio a ciascuno.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w18-al", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio in un quadrato distante esattamente 1 movimento.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==1);
            return !targets.isEmpty();
        });

        data.put("w19-al", (pl, map, memory)->{
            //Scegli un quadrato distante esattamente 1 movimento. Muovi in quel quadrato. Puoi dare 2 danni a 1 bersaglio in quel quadrato. Se vuoi puoi muovere
            // ancora di 1 quadrato nella stessa direzione (ma solo se è un movimento valido). Puoi dare 2 danni a un bersaglio anche in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==1);
            return !targets.isEmpty();
        });

        data.put("w20-al", (pl, map, memory)->{
            //Dai 1 danno a tutti i bersagli che sono distanti esattamente 1 movimento.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==1);
            targets.remove(pl);
            return !targets.isEmpty();
        });

        data.put("w21-al", (pl, map, memory)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi, poi muovi quel bersaglio di 0, 1 o 2 quadrati in una direzione.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

    //Powers lambdas
        //memory = List<Player>
        //Puoi giocare questa carta quando stai dando danno a uno o più bersagli. Paga 1 cubo munizioni di qualsiasi colore. Scegli 1 dei bersagli
        // e dagli 1 segnalino danno aggiuntivo. Nota: non puoi usare questo potenziamento per dare 1 danno a un bersaglio che sta solo ricevendo marchi.
        data.put("p1", (pl, map, memory)-> !((List<Player>)memory).isEmpty());

        //Puoi giocare questa carta nel tuo turno prima o dopo aver svolto qualsiasi azione. Scegli la miniatura di un altro giocatore e muovila di 1 o 2
        // quadrati in una direzione. (Non puoi usare questo potenziamento per muovere una miniatura dopo che è stata rigenerata alla fine del tuo turno, è troppo tardi.)
        data.put("p2", (pl, map, memory)-> {
            List<Player> targets = Map.playersInTheMap(map);
            targets.remove(pl);
            return !targets.isEmpty();
        });

        //memory = player that gave damage
        data.put("p3", (pl, map, memory)-> Map.visiblePlayers(pl, map).contains((Player) memory));//someone you don't see can attack you

        //Puoi giocare questa carta nel tuo turno prima o dopo aver svolto qualsiasi azione. Prendi la tua miniatura e piazzala in un qualsiasi quadrato sulla plancia.
        // (Non puoi usare questo potenziamento dopo che hai visto dove un altro giocatore si rigenera alla fine del tuo turno, è troppo tardi.)
        data.put("p4", (pl, map, memory)-> true);

        //Activities lambdas
        data.put("a-p", (pl, map, memory)-> pl.getPowers().stream().anyMatch(power -> (power.getBase().getLambdaID().equals("p2") || power.getBase().getLambdaID().equals("p4")) && power.getBase().isFeasible(pl, map, null) ));

        data.put("a-b1", (pl, map, memory)-> true);

        data.put("a-b2", (pl, map, memory)->  possibleLoot(pl, map, 1));

        data.put("a-b3",(pl, map, memory) -> pl.getWeapons().stream().filter(Weapon::isLoaded).anyMatch(w -> w.getBase().isFeasible(pl, map, null) || (w.getAlternative() != null && w.getAlternative().isFeasible(pl, map, null) && ActionLambdaMap.enoughAmmo(pl, w.getAlternative().getCost(), true))) );

        data.put("a-a1", (pl, map, memory)-> possibleLoot(pl, map, 2));

        data.put("a-a2", (pl, map, memory)-> possibleShoot(pl, map, 1) );

        data.put("a-f1", (pl, map, memory)-> possibleShoot(pl, map, 1) );

        data.put("a-f2", (pl, map, memory)-> true);

        data.put("a-f3", (pl, map, memory)-> possibleLoot(pl, map, 2));

        data.put("a-f4", (pl, map, memory)-> possibleShoot(pl, map, 2) );

        data.put("a-f5", (pl, map, memory)-> possibleLoot(pl, map, 3));
    }

    /**
     * SINGLETON: Determines whether the lambda is currently feasible looking through the Map
     * @param lambdaName Relevant lambda identifier
     * @param pl Lambda's player
     * @param map Lambda's map
     * @param memory Lambda's memory
     * @return True if feasible, false otherwise
     */
    public static boolean isFeasible(String lambdaName, Player pl, Map map, Object memory){
        if(instance == null)
            instance = new FeasibleLambdaMap();

        return instance.data.get(lambdaName).execute(pl, map, memory);
    }

    private static boolean possibleShoot(Player pl, Map map, int steps){
        List<Point> possible = Map.possibleMovements(pl.getPosition(), steps, map);
        List<Point> destinations = new ArrayList<>(possible);

        Point initialPosition = new Point(pl.getPosition());

        for(Point p : possible)
        {
            //Put the player in the simulated future position
            pl.applyEffects(EffectsLambda.move(pl, p, map));

            //If no weapon has suitable action, we can't propose to move to this position
            if( pl.getWeapons().stream().filter(Weapon::isLoaded).noneMatch(w -> w.getBase().isFeasible(pl, map, null)) &&
                    pl.getWeapons().stream().filter(Weapon::isLoaded).noneMatch(w-> w.getAlternative() != null && w.getAlternative().isFeasible(pl, map, null) && ActionLambdaMap.enoughAmmo(pl, w.getAlternative().getCost(), true) ) )
                    destinations.remove(p);
        }

        //Return the player to its real position
        pl.applyEffects(EffectsLambda.move(pl, initialPosition, map));

        return !destinations.isEmpty();
    }

    private static boolean possibleLoot(Player pl, Map map, int steps)
    {
        List<Point> possible = Map.possibleMovements(pl.getPosition(), steps, map);
        List<Point> destinations = new ArrayList<>(possible);

        for(Point p : possible)
        {
            if(!map.getCell(p).hasItems(pl))
            {
                destinations.remove(p);
            }
        }

        return !destinations.isEmpty();
    }

    /**
     * Checks if the player has currently reloadable weapons
     * @param pl Current player
     * @return True if the action is possible, false otherwise
     */
    public static boolean possibleReload(Player pl)
    {
        List<Weapon> unloaded = pl.getWeapons().stream().filter(weapon -> !weapon.isLoaded()).collect(Collectors.toList());
        List<Weapon> reloadable =  new ArrayList<>(unloaded); //Only the weapons the player can currently reload
        List<Color> cost = new ArrayList<>();

        for(Weapon w : unloaded)
        {
            cost.clear();
            cost.add(w.getColor());
            if(w.getBase().getCost() != null)
                cost.addAll(w.getBase().getCost());

            if(!ActionLambdaMap.enoughAmmo(pl, cost, true))
            {
                reloadable.remove(w);
            }
        }

        return !reloadable.isEmpty();
    }
}
