package it.polimi.ingsw.controller;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Timeout
{
    public Timeout(long timeout, TimeUnit unit, Match m)
    {
        final Runnable stuffToDo = new Thread() {
            @Override
            public void run() {
                m.playerTurn();
            }
        };

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(stuffToDo);
        executor.shutdown();

        try {
            future.get(timeout, unit);
        }
        catch (InterruptedException ie) {
            Logger.getGlobal().log(Level.SEVERE, "PlayerTurn interrupted unexpectedly");
        }
        catch (ExecutionException ee) {
            Logger.getGlobal().log(Level.SEVERE, "PlayerTurn interrupted unexpectedly");
        }
        catch (TimeoutException te) {
            Match.disconnectPlayer(m.getActive(), m.getGame().getPlayers());
        }

        if (!executor.isTerminated())
            executor.shutdownNow(); // If you want to stop the code that hasn't finished.
    }
}
