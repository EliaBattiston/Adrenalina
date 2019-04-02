package it.polimi.ingsw.model;


import java.util.List;

public class Deck<T extends Card> {
    protected List<T> cards;

    Deck()
    {}

    public void shuffle()
    {}

    public void add(T card)
    {}

    public T draw()
    {
        return cards.get(0);
    }

    public Deck<T> clone()
    {
        return new Deck<T>();
    }

    protected List<T> getCards()
    {
        return cards;
    }
}
