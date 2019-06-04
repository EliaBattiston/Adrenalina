package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.exceptions.WrongPointException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
                damage[i] = damageGiver.getNick();
                d--;
                i++;
            }
            while(i<12 && marks.contains(damageGiver.getNick())){
                damage[i] = damageGiver.getNick();
                marks.remove(damageGiver.getNick());
                i++;
            }
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

                try {
                    position.set(newPosition);
                    map.getCell(newPosition).addPawn(pl);
                }catch(WrongPointException ex){
                    LOGGER.log( Level.SEVERE, ex.toString(), ex );
                    map.getCell(position).addPawn(pl);
                }
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
                marks.add(damageGiver.getNick());
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

    /**
     * Remove power card from the player's hand
     * @param toRemove Card to remove
     * @param remover Player who removes the power card
     * @return Desired lambda function
     */
    public static PlayerLambda removePower(Power toRemove, Player remover)
    {
        return ((damage, marks, position, weapons, powers, ammo) -> {
            int index = Arrays.asList(powers).indexOf(toRemove);
            remover.throwPower(powers[index]);

            powers[index] = null;
        });
    }

    /**
     * Make the player pay the due amount of ammunition
     * @param cost List of coloured ammunition
     * @return Desired lambda function
     */
    public static PlayerLambda payAmmo(List<Color> cost){
        return ((damage, marks, position, weapons, powers, ammo) -> {
            ammo.useRed( (int)cost.stream().filter(c -> c == Color.RED).count() );
            ammo.useBlue( (int)cost.stream().filter(c -> c == Color.BLUE).count() );
            ammo.useYellow( (int)cost.stream().filter(c -> c == Color.YELLOW).count() );
        });
    }

    /**
     * Remove weapon card from the player's hand
     * @param toRemove Card to remove
     * @param cell Spawn cell where to leave the weapon
     * @return Desired lambda function
     */
    public static PlayerLambda removeWeapon(Weapon toRemove, SpawnCell cell)
    {
        return ((damage, marks, position, weapons, powers, ammo) -> {
            int pos = Arrays.asList(weapons).indexOf(toRemove);
            if(pos>-1 && pos<=3)
            {
                weapons[pos].setLoaded(true);
                cell.refillWeapon(weapons[pos]);
                weapons[pos] = null;
            }
            else
            {
                Logger.getGlobal().log(Level.SEVERE, "Weapon to be discarded is not in the player\'s hand");
            }
        });
    }

    public static void giveDamage(Player giver, Player taker, int damage) throws ClientDisconnectedException
    {
        taker.applyEffects(EffectsLambda.damage(damage, giver));

        //Mirino
        if(giver.getPowers().stream().anyMatch(p->p.getBase().getLambdaID().equals("p1")) &&
                (giver.getAmmo(Color.RED, true)>0 || giver.getAmmo(Color.BLUE, true)>0 || giver.getAmmo(Color.YELLOW, true)>0))
        {
            Power chosen = giver.getConn().discardPower(giver.getPowers().stream().filter(p->p.getBase().getLambdaID().equals("p1")).collect(Collectors.toList()), false);
            if(chosen != null)
            {
                List<Color> available = new ArrayList<>();
                for(Color c : Color.values())
                {
                    if(giver.getAmmo(c, true) > 0)
                        available.add(c);
                }

                List<Color> cost = new ArrayList<>();
                cost.add(giver.getConn().chooseAmmo(available, true));

                //Give additional damage
                taker.applyEffects(EffectsLambda.damage(1, giver));

                //Remove power
                giver.applyEffects(EffectsLambda.removePower(chosen, giver));

                giver.applyEffects(EffectsLambda.payAmmo(cost));
            }
        }

        //Granata venom
        if(taker.getPowers().stream().anyMatch(p->p.getBase().getLambdaID().equals("p3")))
        {
            Power chosen = taker.getConn().discardPower(taker.getPowers().stream().filter(p->p.getBase().getLambdaID().equals("p3")).collect(Collectors.toList()), false);
            if(chosen != null);
            {
                //Give mark
                giver.applyEffects(EffectsLambda.marks(1, taker));

                //Remove power
                taker.applyEffects(((damage1, marks, position, weapons, powers, ammo) -> {
                    int index = Arrays.asList(powers).indexOf(chosen);
                    taker.throwPower(powers[index]);

                    powers[index] = null;
                }));
            }
        }
    }
}
