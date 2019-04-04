package it.polimi.ingsw.model;

import java.util.List;
import java.util.ArrayList;

/**
 * An endless deck is a type of deck in which the used cards are reused when the deck becomes empty
 * @param <T> The type of cards handled by the deck
 */
public class EndlessDeck<T extends Card> extends Deck<T> {
    private List<T> scraps;

    public EndlessDeck() {
        super();
        this.scraps = new ArrayList<>();
    }

    /**
     * Get the first card of the deck and remove from it. If the deck is empty but you have scrapped old cards, it will be recreated with that ones and give you the new first card.
     * @return the first card of the deck
     * @throws EmptyDeckException if both the deck and the scraps are empty.
     */
    @Override
    public T draw() throws EmptyDeckException
    {
        try{
            return super.draw();
        }catch(EmptyDeckException e){
            cards = scraps;
            super.shuffle();
            scraps = new ArrayList<>();
            return super.draw();
        }
    }

    /**
     * Scrap a card when used. It'll be used for the new deck
     * @param card
     */
    public void scrapCard(T card){
        scraps.add(card);
    }
}
