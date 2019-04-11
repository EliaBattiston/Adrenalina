package it.polimi.ingsw.model;

public class EffectsLambda {
    public static PlayerLambda damage(int d, Player damageGiver){
        //TODO implement the real number of damages, check it and add marks
        return (damage, marks, position, weapons, powers, ammo)->{
            //Trova il primo posto vuoto nei damage e metti due segnalini di pl
            int i;
            for(i =0; i<12 && damage[i]!=null ;i++)
                ;
            if(i<12)
            {
                damage[i] = damageGiver;
            }
            i++;
            if(i<12)
            {
                damage[i] = damageGiver;
            }
        };
    }

    //TODO method for movement

    //TODO method for marks
}
