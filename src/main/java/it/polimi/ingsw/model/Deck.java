package it.polimi.ingsw.model;


import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Deck<T extends Card> {
    protected List<T> cards;

    /**
     * Build an empty Deck
     */
    Deck(){ cards = new ArrayList<>(); }

    /**
     * Add a card to the Deck
     * @param card
     */
    public void add(T card){ cards.add(card); }

    /**
     * Get the first card of the deck and remove from it
     * @return the first card
     * @throws EmptyDeckException if the deck is empty and you try to draw a card
     */
    public T draw() throws EmptyDeckException
    {
        T card = cards.get(0);
        if (card == null)
            throw new EmptyDeckException();
        cards.remove(0);
        return card;
    }

    /**
     * Shuffle the deck
     */
    public void shuffle(){
        Collections.shuffle(cards);
    }

    /**
     * Clone the Deck
     * @return the clone
     */
    public Deck<T> clone()
    {
        Deck<T> d = new Deck<>();
        for(T c : cards){
            d.add(c);//TODO check the order (maybe it's not needed)
        }
        return d;
    }

    /**
     * Used for the Junit testing
     * @return the internal representation of the deck
     */
    protected List<T> getCards()
    {
        return cards;
    }
}
