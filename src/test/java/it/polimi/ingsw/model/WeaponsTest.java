package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * Tests about the correctness of weapon actions.
 * User connection is emulated by always choosing the first available choice.
 */
public class WeaponsTest {

    /**
     * Test the correct behaviour of Distruttore
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseDistruttore() throws ClientDisconnectedException
    {
        //Dai 2 danni e un marchio a un bersaglio che puoi vedere

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w1-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(1, marks);
    }

    /**
     * Test the correct behaviour of Distruttore
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAddDistruttore() throws ClientDisconnectedException
    {
        //Dai 1 marchio a un altro bersaglio che puoi vedere.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        ActionLambdaMap.getLambda("w1-ad1").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(0, damages);
        assertEquals(0, damages2);
        assertEquals(0, marks);
        assertEquals(1, marks2);
    }

    /**
     * Test the correct behaviour of Mitragliatrice
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseMitragliatrice() throws ClientDisconnectedException
    {
        //Scegli 1 o 2 bersagli che puoi vedere e dai 1 danno a entrambi.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(0,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w2-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(1, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Mitragliatrice
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAdd1Mitragliatrice() throws ClientDisconnectedException
    {
        //Dai 1 danno aggiuntivo a uno dei due bersagli.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(0,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        memory[1] = enemy2;
        ActionLambdaMap.getLambda("w2-ad1").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(0, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Mitragliatrice
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAdd2Mitragliatrice() throws ClientDisconnectedException
    {
        //Dai 1 danno aggiuntivo all'altro dei bersagli e/o dai 1 danno a un bersaglio differente che puoi vedere.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(4);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);
        Player enemy3 = game.getPlayers().get(3);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(0,1), game.getMap()));
        enemy3.applyEffects(EffectsLambda.move(enemy3, new Point(2,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        memory[1] = enemy2;
        ActionLambdaMap.getLambda("w2-ad2").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int damages3 = Collections.frequency(Arrays.asList( enemy3.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());
        int marks3 = Collections.frequency(enemy3.getReceivedMarks(), me.getNick());

        assertEquals(0, damages);
        assertEquals(1, damages2);
        assertEquals(1, damages3);
        assertEquals(0, marks);
        assertEquals(0, marks2);
        assertEquals(0, marks3);
    }

    /**
     * Test the correct behaviour of Torpedine
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseTorpedine() throws ClientDisconnectedException
    {
        //Dai 2 danni a 1 bersaglio che puoi vedere.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w3-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Torpedine
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAdd1Torpedine() throws ClientDisconnectedException
    {
        //Dai 1 danno a un secondo bersaglio che il tuo primo bersaglio può vedere.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        ActionLambdaMap.getLambda("w3-ad1").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(0, damages);
        assertEquals(1, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Torpedine
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAdd2Torpedine() throws ClientDisconnectedException
    {
        //Dai 2 danni a un terzo bersaglio che il tuo secondo bersaglio può vedere. Non puoi usare questo effetto se prima non hai usato reazione a catena.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(4);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);
        Player enemy3 = game.getPlayers().get(3);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(1,1), game.getMap()));
        enemy3.applyEffects(EffectsLambda.move(enemy3, new Point(1,2), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        memory[1] = enemy2;
        ActionLambdaMap.getLambda("w3-ad2").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int damages3 = Collections.frequency(Arrays.asList( enemy3.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());
        int marks3 = Collections.frequency(enemy3.getReceivedMarks(), me.getNick());

        assertEquals(0, damages);
        assertEquals(0, damages2);
        assertEquals(2, damages3);
        assertEquals(0, marks);
        assertEquals(0, marks2);
        assertEquals(0, marks3);
    }

    /**
     * Test the correct behaviour of Fucile al Plasma
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseFucilePlasma() throws ClientDisconnectedException
    {
        //Dai 2 danni a 1 bersaglio che puoi vedere.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w4-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Fucile al Plasma
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAdd2FucilePlasma() throws ClientDisconnectedException
    {
        //Dai 1 danno aggiuntivo al tuo bersaglio.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        ActionLambdaMap.getLambda("w4-ad2").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Fucile di Precisione
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseFucilePrecisione() throws ClientDisconnectedException
    {
        //Dai 3 danni e 1 marchio a 1 bersaglio che puoi vedere. Il bersaglio deve essere ad almeno 2 movimenti da te.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(2,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w5-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(3, damages);
        assertEquals(1, marks);
    }

    /**
     * Test the correct behaviour of Falce Protonica
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseFalceProtonica() throws ClientDisconnectedException
    {
        //Dai 1 danno a ogni altro giocatore presente nel quadrato in cui ti trovi.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w6-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Falce Protonica
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltFalceProtonica() throws ClientDisconnectedException
    {
        //Dai 2 danni a ogni altro giocatore presente nel quadrato in cui ti trovi.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w6-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(2, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Raggio Traente
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseRaggioTraente() throws ClientDisconnectedException
    {
        //Muovi un bersaglio di 0, 1 o 2 quadrati fino a un quadrato che puoi vedere e dagli 1 danno.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w7-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Raggio Traente
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltRaggioTraente() throws ClientDisconnectedException
    {
        //Scegli un bersaglio 0, 1, o 2 movimenti da te. Muovi quel bersaglio nel quadrato in cui ti trovi e dagli 3 danni.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w7-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(3, damages);
        assertEquals(0, marks);

        //Check that the enemy has been moved to the player's position
        assertEquals(me.getPosition().getX(), enemy.getPosition().getX());
        assertEquals(me.getPosition().getY(), enemy.getPosition().getY());
    }

    /**
     * Test the correct behaviour of Cannone Vortex
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseCannoneVortex() throws ClientDisconnectedException
    {
        //Scegli un quadrato che puoi vedere ad almeno 1 movimento di distanza. Un vortice si apre in quel punto. Scegli un bersaglio nel quadrato
        //in cui si trova il vortice o distante 1 movimento. Muovi il bersaglio nel quadrato in cui si trova il vortice e dagli 2 danni.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(2,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w8-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(0, marks);

        //Test that the enemy has been moved to the correct cell
        assertEquals(1, enemy.getPosition().getX());
        assertEquals(0, enemy.getPosition().getY());
    }

    /**
     * Test the correct behaviour of Cannone Vortex
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAdd1CannoneVortex() throws ClientDisconnectedException
    {
        //Scegli fino ad altri 2 bersagli nel quadrato in cui si trova il vortice o distanti 1 movimento.
        // Muovili nel quadrato in cui si trova il vortice e dai loro 1 danno ciascuno.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(4);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);
        Player enemy3 = game.getPlayers().get(3);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(1,0), game.getMap()));
        enemy3.applyEffects(EffectsLambda.move(enemy3, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        ActionLambdaMap.getLambda("w8-ad1").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int damages3 = Collections.frequency(Arrays.asList( enemy3.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());
        int marks3 = Collections.frequency(enemy3.getReceivedMarks(), me.getNick());

        assertEquals(0, damages);
        assertEquals(1, damages2);
        assertEquals(1, damages3);
        assertEquals(0, marks);
        assertEquals(0, marks2);
        assertEquals(0, marks3);

        //Test that the enemies have been moved to the correct cell
        assertEquals(1, enemy2.getPosition().getX());
        assertEquals(0, enemy2.getPosition().getY());
        assertEquals(1, enemy3.getPosition().getX());
        assertEquals(0, enemy3.getPosition().getY());
    }

    /**
     * Test the correct behaviour of Vulcanizzatore
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseVulcanizzatore() throws ClientDisconnectedException
    {
        //Scegli una stanza che puoi vedere, ma non la stanza in cui ti trovi. Dai 1 danno a ognuno in quella stanza.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(2,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w9-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(1, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Vulcanizzatore
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltVulcanizzatore() throws ClientDisconnectedException
    {
        //Scegli un quadrato distante esattamente 1 movimento. Dai 1 danno e 1 marchio a ognuno in quel quadrato.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w9-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(1, damages2);
        assertEquals(1, marks);
        assertEquals(1, marks2);
    }

    /**
     * Test the correct behaviour of Razzo Termico
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseRazzoTermico() throws ClientDisconnectedException
    {
        //Scegli 1 bersaglio che non puoi vedere e dagli 3 danni.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w10-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(3, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Raggio Solare
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseRaggioSolare() throws ClientDisconnectedException
    {
        //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai un marchio a quel bersaglio e a chiunque altro in quel quadrato.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,1), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(0,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w11-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(0, damages2);
        assertEquals(1, marks);
        assertEquals(1, marks2);
    }

    /**
     * Test the correct behaviour of Raggio Solare
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltRaggioSolare() throws ClientDisconnectedException
    {
        //Dai 1 danno a 1 bersaglio che puoi vedere e distante almeno 1 movimento. Poi dai 2 marchi a quel bersaglio e a chiunque altro in quel quadrato.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,1), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(0,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w11-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(0, damages2);
        assertEquals(2, marks);
        assertEquals(2, marks2);
    }

    /**
     * Test the correct behaviour of Lanciafiamme
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseLanciafiamme() throws ClientDisconnectedException
    {
        //Scegli un quadrato distante 1 movimento e possibilmente un secondo quadrato distante ancora 1 movimento
        //nella stessa direzione. In ogni quadrato puoi scegliere 1 bersaglio e dargli 1 danno.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(2,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w12-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(1, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Lanciafiamme
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltLanciafiamme() throws ClientDisconnectedException
    {
        //Scegli 2 quadrati come prima. (come w12-b) Dai 2 danni a chiunque sia nel primo quadrato e 1 danno a chiunque si trovi nel secondo quadrato.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(2,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w12-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(1, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Lanciagranate
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseLanciagranate() throws ClientDisconnectedException
    {
        //Dai 1 danno a 1 bersaglio che puoi vedere. Poi puoi muovere il bersaglio di 1 quadrato.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w13-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Lanciagranate
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAddLanciagranate() throws ClientDisconnectedException
    {
        //Dai 1 danno a ogni giocatore in quadrato che puoi vedere. Puoi usare questo effetto prima o dopo il movimento dell'effetto base.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w13-ad1").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(1, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Lanciarazzi
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseLanciarazzi() throws ClientDisconnectedException
    {
        //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w14-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Lanciarazzi
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAdd2Lanciarazzi() throws ClientDisconnectedException
    {
        //Dai 2 danni a 1 bersaglio che puoi vedere e che non si trova nel tuo quadrato. Poi puoi muovere il bersaglio di 1 quadrato.
        //Durante l'effetto base, dai 1 danno a ogni giocatore presente nel quadrato in cui si trovava originariamente il bersaglio, incluso il bersaglio, anche se lo hai mosso.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(4);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);
        Player enemy3 = game.getPlayers().get(3);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(1,0), game.getMap()));
        enemy3.applyEffects(EffectsLambda.move(enemy3, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w14-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int damages3 = Collections.frequency(Arrays.asList( enemy3.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());
        int marks3 = Collections.frequency(enemy3.getReceivedMarks(), me.getNick());

        assertEquals(3, damages);
        assertEquals(1, damages2);
        assertEquals(1, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
        assertEquals(0, marks3);
    }

    /**
     * Test the correct behaviour of Fucile Laser
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseFucileLaser() throws ClientDisconnectedException
    {
        //Scegli una direzione cardinale e 1 bersaglio in quella direzione. Dagli 3 danni.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,1), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w15-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(3, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Fucile Laser
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltFucileLaser() throws ClientDisconnectedException
    {
        //Scegli una direzione cardinale e 1 o 2 bersagli in quella direzione. Dai 2 danni a ciascuno.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,1), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,1), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(3,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w15-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(2, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks2);
    }

    /**
     * Test the correct behaviour of Spada Fotonica
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseSpadaFotonica() throws ClientDisconnectedException
    {
        //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w16-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Spada Fotonica
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAddSpadaFotonica() throws ClientDisconnectedException
    {
        //Dai 2 danni a un bersaglio differente nel quadrato in cui ti trovi. Il passo d'ombra può essere usato prima o dopo questo effetto.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        memory[0] = enemy;
        ActionLambdaMap.getLambda("w16-ad2").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(0, damages);
        assertEquals(2, damages2);
        assertEquals(0, marks);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of ZX-2
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseZX2() throws ClientDisconnectedException
    {
        //Dai 1 danno e 2 marchi a 1 bersaglio che puoi vedere.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w17-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(2, marks);
    }

    /**
     * Test the correct behaviour of ZX-2
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltZX2() throws ClientDisconnectedException
    {
        //Scegli fino a 3 bersagli che puoi vedere e dai 1 marchio a ciascuno.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(4);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);
        Player enemy3 = game.getPlayers().get(3);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(1,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(2,0), game.getMap()));
        enemy3.applyEffects(EffectsLambda.move(enemy3, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w17-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int damages3 = Collections.frequency(Arrays.asList( enemy3.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());
        int marks3 = Collections.frequency(enemy3.getReceivedMarks(), me.getNick());

        assertEquals(0, damages);
        assertEquals(0, damages2);
        assertEquals(0, damages3);
        assertEquals(1, marks);
        assertEquals(1, marks2);
        assertEquals(1, marks3);
    }

    /**
     * Test the correct behaviour of Fucile a Pompa
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseFucilePompa() throws ClientDisconnectedException
    {
        //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi. Se vuoi puoi muovere quel bersaglio di 1 quadrato.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w18-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(3, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Fucile a Pompa
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltFucilePompa() throws ClientDisconnectedException
    {
        //Dai 2 danni a 1 bersaglio in un quadrato distante esattamente 1 movimento.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w18-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Cyberguanto
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseCyberguanto() throws ClientDisconnectedException
    {
        //Scegli 1 bersaglio in un quadrato distante esattamente 1 movimento. Muovi in quel quadrato e dai al bersaglio 1 danno e 2 marchi.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w19-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(2, marks);

        //Check that my player moved to the enemy position
        assertEquals(enemy.getPosition().getX(), me.getPosition().getX());
        assertEquals(enemy.getPosition().getY(), me.getPosition().getY());
    }

    /**
     * Test the correct behaviour of Cyberguanto
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltCyberguanto() throws ClientDisconnectedException
    {
        //Scegli un quadrato distante esattamente 1 movimento. Muovi in quel quadrato. Puoi dare 2 danni a 1 bersaglio in quel quadrato. Se vuoi puoi muovere
        // ancora di 1 quadrato nella stessa direzione (ma solo se è un movimento valido). Puoi dare 2 danni a un bersaglio anche in quel quadrato.

        //THE DEFINITION MEANS: I can just run and not shot in the first square nor the second!

        //Initialize test game
        Game game = GameTest.initializeGameForTest(3);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(1,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy, new Point(2,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w19-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(2, damages);
        assertEquals(0, marks);
        assertEquals(0, marks);

        //Check that my player moved to the last enemy position
        assertEquals(enemy2.getPosition().getX(), me.getPosition().getX());
        assertEquals(enemy2.getPosition().getY(), me.getPosition().getY());
    }

    /**
     * Test the correct behaviour of Onda d'urto
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseOndaUrto() throws ClientDisconnectedException
    {
        //Scegli fino a 3 bersagli su quadrati differenti, ognuno distante esattamente 1 movimento da me. Dai 1 danno a ogni bersaglio.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(4);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);
        Player enemy3 = game.getPlayers().get(3);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(1,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(2,0), game.getMap()));
        enemy3.applyEffects(EffectsLambda.move(enemy3, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w20-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int damages3 = Collections.frequency(Arrays.asList( enemy3.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());
        int marks3 = Collections.frequency(enemy3.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(1, damages2);
        assertEquals(1, damages3);
        assertEquals(0, marks);
        assertEquals(0, marks2);
        assertEquals(0, marks3);
    }

    /**
     * Test the correct behaviour of Onda d'urto
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltOndaUrto() throws ClientDisconnectedException
    {
        //Dai 1 danno a tutti i bersagli che sono distanti esattamente 1 movimento.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(4);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);
        Player enemy2 = game.getPlayers().get(2);
        Player enemy3 = game.getPlayers().get(3);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(1,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));
        enemy2.applyEffects(EffectsLambda.move(enemy2, new Point(2,0), game.getMap()));
        enemy3.applyEffects(EffectsLambda.move(enemy3, new Point(1,1), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w20-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int damages2 = Collections.frequency(Arrays.asList( enemy2.getReceivedDamage() ), me.getNick());
        int damages3 = Collections.frequency(Arrays.asList( enemy3.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());
        int marks2 = Collections.frequency(enemy2.getReceivedMarks(), me.getNick());
        int marks3 = Collections.frequency(enemy3.getReceivedMarks(), me.getNick());

        assertEquals(1, damages);
        assertEquals(1, damages2);
        assertEquals(1, damages3);
        assertEquals(0, marks);
        assertEquals(0, marks2);
        assertEquals(0, marks3);
    }

    /**
     * Test the correct behaviour of Martello Ionico
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestBaseMartelloIonico() throws ClientDisconnectedException
    {
        //Dai 2 danni a 1 bersaglio nel quadrato in cui ti trovi.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w21-b").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(2, damages);
        assertEquals(0, marks);
    }

    /**
     * Test the correct behaviour of Martello Ionico
     * @throws ClientDisconnectedException Will never be thrown by ConnectionTest
     */
    @Test
    public void TestAltMartelloIonico() throws ClientDisconnectedException
    {
        //Dai 3 danni a 1 bersaglio nel quadrato in cui ti trovi, poi muovi quel bersaglio di 0, 1 o 2 quadrati in una direzione.

        //Initialize test game
        Game game = GameTest.initializeGameForTest(2);
        Player me = game.getPlayers().get(0);
        Player enemy = game.getPlayers().get(1);

        //Position test players
        me.applyEffects(EffectsLambda.move(me, new Point(0,0), game.getMap()));
        enemy.applyEffects(EffectsLambda.move(enemy, new Point(0,0), game.getMap()));

        //Make memory variable for some actions and execute
        Player[] memory = new Player[2];
        ActionLambdaMap.getLambda("w21-al").execute(me, game.getMap(), memory);

        //Count damages
        int damages = Collections.frequency(Arrays.asList( enemy.getReceivedDamage() ), me.getNick());
        int marks = Collections.frequency(enemy.getReceivedMarks(), me.getNick());

        assertEquals(3, damages);
        assertEquals(0, marks);
    }
}
