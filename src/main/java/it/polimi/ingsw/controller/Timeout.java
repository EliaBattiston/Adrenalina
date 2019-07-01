package it.polimi.ingsw.controller;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Timeout
{
    final ExecutorService executor;
    final Future future;

    public Timeout(long timeout, TimeUnit unit, Match m)
    {
        final Runnable stuffToDo = new Thread(m::playerTurn);

        executor = Executors.newSingleThreadExecutor();
        future = executor.submit(stuffToDo);
        executor.shutdown();

        try {
            future.get(timeout, unit);
        }
        catch (InterruptedException ie) {
            Logger.getGlobal().log(Level.SEVERE, "PlayerTurn interrupted unexpectedly InterruptedException", ie);
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException ee) {
            Logger.getGlobal().log(Level.SEVERE, "PlayerTurn interrupted unexpectedly ExecutionException", ee);
        }
        catch (TimeoutException te) {
            future.cancel(true);
            Match.disconnectPlayer(m.getActive(), m.getGame().getPlayers());
            Match.broadcastMessage(m.getActive().getNick() + " è stato troppo lento, il turno è terminato", m.getGame().getPlayers());
        }

        executor.shutdownNow(); // If you want to stop the code that hasn't finished.
    }
}
