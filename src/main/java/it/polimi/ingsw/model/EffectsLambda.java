package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.Collections;
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

            //TODO ceck the power up "granata venom"
        };
    }

    /**
     * Move the player
     * @param newPosition position where to move
     * @return the lambda that has to be run from the player who receive the action
     */
    public static PlayerLambda move(Point newPosition){
        return (damage, marks, position, weapons, powers, ammo)->{
            try {
                position.set(newPosition.getX(), newPosition.getY());
            }catch(WrongPointException ex){
                LOGGER.log( Level.SEVERE, ex.toString(), ex );
            }
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
}
