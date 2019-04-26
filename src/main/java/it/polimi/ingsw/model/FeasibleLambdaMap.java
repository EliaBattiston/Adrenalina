package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FeasibleLambdaMap
{
    private HashMap<String, FeasibleLambda> data;
    private static FeasibleLambdaMap instance = null;

    private FeasibleLambdaMap()
    {
        data = new HashMap<>();

        //Base weapon lambdas
        //(List<Player>)memory
        data.put("w1-b", (pl, map, memory)->{
            //Dai 2 danni e un marchio a un bersaglio che puoi vedere

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        //memory = Player[2]
        data.put("w2-b", (pl, map, memory)->{
            //Scegli 1 o 2 bersagli che puoi vedere e dai 1 danno a entrambi.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        //((Player[])memory)[0]
        data.put("w3-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        //CLONE
        //((Player[])memory)[0]
        data.put("w4-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w5-b", (pl, map, memory)->{
            //Dai 3 danni e 1 marchio a 1 bersaglio che puoi vedere. Il bersaglio deve essere ad almeno 2 movimenti da te.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)>=2);
            return !targets.isEmpty();
        });

        data.put("w6-b", (pl, map, memory)->{
            //Dai 1 danno a ogni altro giocatore presente nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            targets.remove(pl);
            return !targets.isEmpty();
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

            return !targets.isEmpty();
        });

        //(Point) memory
        data.put("w8-b", (pl, map, memory)->{
            //Scegli un quadrato che puoi vedere ad almeno 1 movimento di distanza. Un vortice si apre in quel punto. Scegli un bersaglio nel quadrato
            //in cui si trova il vortice o distante 1 movimento. Muovi il bersaglio nel quadrato in cui si trova il vortice e dagli 2 danni.
            List<Point> points = Map.visiblePoints(pl.getPosition(), map, 0);
            points.remove(pl.getPosition());

            return !points.isEmpty();
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

            return !playersRooms.isEmpty();
        });

        data.put("w10-b", (pl, map, memory)->{
            //Scegli 1 bersaglio che non puoi vedere e dagli 3 danni.

            List<Player> targets = Map.playersInTheMap(map);
            targets.removeAll(Map.visiblePlayers(pl, map));
            return !targets.isEmpty();
        });

        data.put("w11-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai un marchio a quel bersaglio e a chiunque altro in quel quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)>=1);
            return !targets.isEmpty();
        });

        //TODO w12-b

        data.put("w13-b", (pl, map, memory)->{
            //Dai 1 danno a 1 bersaglio che puoi vedere. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w14-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)!=0);
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

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

        data.put("w17-b", (pl, map, memory)->{
            //Dai 1 danno e 2 marchi a 1 bersaglio che puoi vedere.

            List<Player> targets = Map.visiblePlayers(pl, map);
            return !targets.isEmpty();
        });

        data.put("w18-b", (pl, map, memory)->{
            //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi. Se vuoi puoi muovere quel bersaglio di 1 quadrato.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

        data.put("w19-b", (pl, map, memory)->{
            //Scegli 1 bersaglio in un quadrato distante esattamente 1 movimento. Muovi in quel quadrato e dai al bersaglio 1 danno e 2 marchi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==1);
            return !targets.isEmpty();
        });

        data.put("w20-b", (pl, map, memory)->{
            //Scegli fino a 3 bersagli su quadrati differenti, ognuno distante esattamente 1 movimento da me. Dai 1 danno a ogni bersaglio.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)->Map.distance(p1,p2)==1);
            return !targets.isEmpty();
        });

        data.put("w21-b", (pl, map, memory)->{
            //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

            List<Player> targets = Map.playersAtGivenDistance(pl, map, true, (p1, p2)-> Map.distance(p1, p2)==0);
            return !targets.isEmpty();
        });

    //Activities lambdas
        data.put("a-p", (pl, map, memory)->{
            return pl.getPowers().stream().filter(power -> power.getId() == 6 || power.getId() == 8).count() > 0;
        });

        data.put("a-b1", (pl, map, memory)->{
            return true;
        });

        data.put("a-b2", (pl, map, memory)->{
            return true;
        });

        data.put("a-b3",(pl, map, memory) ->{
            /*shoot()*/
            return false;
        });

        data.put("a-a1", (pl, map, memory)->{
            return true;
        });

        data.put("a-a2", (pl, map, memory)->{
            /*run(pl, map,1,false);
            shoot(pl, map);*/
            return false;
        });

        data.put("a-f1", (pl, map, memory)->{
            /*run(pl, map,1,false);
            reload(pl);
            shoot(pl, map);*/
            return false;
        });

        data.put("a-f2", (pl, map, memory)->{
            return true;
        });

        data.put("a-f3", (pl, map, memory)->{
            return true;
        });

        data.put("a-f4", (pl, map, memory)->{
            /*run(pl, map,2,false);
            reload(pl);
            shoot(pl, map);*/
            return false;
        });

        data.put("a-f5", (pl, map, memory)->{
            return true;
        });
    }

    public static boolean isFeasible(String lambdaName, Player pl, Map map, Object memory){
        if(instance == null)
            instance = new FeasibleLambdaMap();

        return instance.data.get(lambdaName).execute(pl, map, memory);
    }
}
