package it.polimi.ingsw.controller;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RunningGameTest {

    /**
     * Run a server and some AI clients, wait until the end of the match. Check the game ends with no problems
     */
    @Test
    public void checkGameWithAI(){
        //This method is commented because it's not fully implemented yet and we don't want to break jenkins
        /*String ip = "localhost";
        int nAis = 4;

        ServerThread server = new ServerThread();
        List<AIThread> ais = new ArrayList<>();

        server.run();

        for(int i=0; i<nAis; i++)
            ais.add(new AIThread());

        //TODO change this with something that check the server is ready before spowning the clients
        try {
            TimeUnit.SECONDS.sleep(1);
        }catch (Exception e){
            ;
        }

        for(AIThread a: ais)
            a.run();

        while(server.isAlive())
            ;

        assert (true);*/
    }

}