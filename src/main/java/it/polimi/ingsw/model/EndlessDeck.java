package it.polimi.ingsw.model;

import java.util.List;

public class EndlessDeck<T extends Card> extends Deck {
    private List<T> scraps;

    @Override
    public T draw()
    {
        //TODO: check why the type asks for a cast
        return (T) this.cards.get(0);
    }
}
