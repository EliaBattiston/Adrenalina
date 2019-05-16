package it.polimi.ingsw.model;


import it.polimi.ingsw.exceptions.EmptyDeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A normal deck for non reusable cards
 * @param <T> The type of cards handled by the deck
 */
public class Deck<T extends Card> {
    protected List<T> cards;

    /**
     * Build an empty Deck
     */
    public Deck(){ cards = new ArrayList<>(); }

    /**
     * Clone the Deck
     */
    public Deck(Deck<T> original)
    {
        cards = new ArrayList<>(original.cards);
    }

    /**
     * Add a card to the Deck
     * @param card Card to be added to the Deck
     */
    public void add(T card){ cards.add(card); }

    /**
     * Get the first card of the deck and remove from it
     * @return the first card of the deck
     * @throws EmptyDeckException if the deck is empty and you try to draw a card
     */
    public T draw()
    {
        try{
            T card = cards.get(0);
            if (card == null)
                throw new EmptyDeckException();
            cards.remove(0);
            return card;
        }catch (IndexOutOfBoundsException e){

            throw new EmptyDeckException();
        }

    }

    /**
     * Shuffle the deck
     */
    public void shuffle(){ Collections.shuffle(cards); }

    /**
     * Used for the Junit testing
     * @return the internal representation of the deck
     */
    protected List<T> getCards() { return cards; }
}
