package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class containing the static methods that are used to change the status of a player when they are targeted by an enemy action
 */
public class EffectsLambda {
    private static final Logger LOGGER = Logger.getLogger( EffectsLambda.class.getName() );

    private EffectsLambda(){}

    /**
     * Set the damage
     * @param damageReceived qtn of damage
     * @param damageGiver player who gave it
     * @return the lambda that has to be run from the player who receive the action
     */
    public static PlayerLambda damage(int damageReceived, Player damageGiver){
        return (damage, marks, position, weapons, powers, ammo)->{
            int i=0;
            int d = damageReceived;
            while(i<12 && damage[i]!=null)
                i++;
            while(i<12 && d>0){
                damage[i] = damageGiver;
                d--;
                i++;
            }
            while(i<12 && marks.contains(damageGiver)){
                damage[i] = damageGiver;
                marks.remove(damageGiver);
                i++;
            }

            //TODO check the power up "mirino"
            //TODO check the power up "granata venom"
        };
    }

    /**
     * Move the player by changing its position and the position of its pawn in the map
     * @param pl Player that has to be moved
     * @param newPosition Position where to move
     * @param map Map in which the reference of the player has to be moved
     * @return the lambda that has to be run from the player who receive the action
     */
    public static PlayerLambda move(Player pl, Point newPosition, Map map){
        return (damage, marks, position, weapons, powers, ammo)->{
            if(map.getCell(position) != null)
            {
                map.getCell(position).removePawn(pl);
            }

            try {
                position.set(newPosition.getX(), newPosition.getY());
            }catch(WrongPointException ex){
                LOGGER.log( Level.SEVERE, ex.toString(), ex );
            }

            map.getCell(newPosition).addPawn(pl);
        };
    }

    /**
     * give marks
     * @param marksReceived qtn of marks
     * @param damageGiver player who gave them
     * @return the lambda that has to be run from the player who receive the action
     */
    public static PlayerLambda marks(int marksReceived, Player damageGiver){
        return (damage, marks, position, weapons, powers, ammo)->{
            int actualMarks = Collections.frequency(marks, damageGiver);
            int recMarks = marksReceived;
            while(actualMarks < 3 && recMarks> 0){
                marks.add(damageGiver);
                recMarks--;
                actualMarks++;
            }
        };
    }

    /**
     * Remove power card from the player's hand
     * @param toRemove Card to remove
     * @param powersDeck Deck to scrap the card to
     * @return Desired lambda function
     */
    public static PlayerLambda removePower(Power toRemove, EndlessDeck<Power> powersDeck)
    {
        return ((damage, marks, position, weapons, powers, ammo) -> {
            int index = Arrays.asList(powers).indexOf(toRemove);
            powersDeck.scrapCard(powers[index]);

            powers[index] = null;
        });
    }

    public static PlayerLambda payAmmo(List<Color> cost)
    {
        return ((damage, marks, position, weapons, powers, ammo) -> {
            ammo.useRed( (int)cost.stream().filter(c -> c == Color.RED).count() );
            ammo.useBlue( (int)cost.stream().filter(c -> c == Color.BLUE).count() );
            ammo.useYellow( (int)cost.stream().filter(c -> c == Color.YELLOW).count() );
        });
    }
}
