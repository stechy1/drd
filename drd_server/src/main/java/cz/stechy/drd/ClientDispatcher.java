package cz.stechy.drd;

import cz.stechy.drd.net.message.KeepAliveMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDispatcher extends Thread {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDispatcher.class);

    private static final int SLEEP_TIME = 5000;

    private boolean interupt = false;
    private final Semaphore semaphore = new Semaphore(0);
    private final Queue<Client> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Collection<Client> clientsToRemove = new ArrayList<>();
    private final int waitingQueueSize;

    public ClientDispatcher(int waitingQueueSize) {
        super("ClientDispatcher");
        this.waitingQueueSize = waitingQueueSize;
    }

    public void shutdown() {
        interupt = true;
        semaphore.release();
    }

    @Override
    public void run() {
        LOGGER.info("Spouštím client dispatchera.");
        while(!interupt) {
            while(waitingQueue.isEmpty() && !interupt) {
                try {
                    LOGGER.info("Jdu spát na semaforu");
                    semaphore.acquire();
                } catch (InterruptedException e) {}
            }

            waitingQueue.iterator().forEachRemaining(client -> {
                try {
                    client.writer.writeObject(new KeepAliveMessage());
                } catch (IOException e) {
                    LOGGER.info("Klient neudržel spojení, musím se ho zbavit.");
                    clientsToRemove.add(client);
                }
            });

            LOGGER.info("Zbavuji se všech klientů, kteří neudrželi spojení.");
            waitingQueue.removeAll(clientsToRemove);
            for (Client client : clientsToRemove) {
                client.close();
            }
            clientsToRemove.clear();

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {}
        }

        LOGGER.info("Client dispatcher končí.");
    }

    public boolean hasClientInQueue() {
        return !waitingQueue.isEmpty();
    }

    public Client getClientFromQueue() {
        return waitingQueue.poll();
    }

    public boolean addClientToQueue(Client client) {
        if (waitingQueue.size() < waitingQueueSize) {
            waitingQueue.add(client);
            semaphore.release();
            return true;
        }

        return false;
    }
}
