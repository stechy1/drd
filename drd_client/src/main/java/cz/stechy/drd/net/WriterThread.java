package cz.stechy.drd.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriterThread extends Thread {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(WriterThread.class);

    private final Semaphore semaphore = new Semaphore(0);
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private final ObjectOutputStream writer;
    private boolean interrupt = false;

    public WriterThread(final OutputStream outputStream) throws IOException {
        super("WriterThread");
        LOGGER.info("Bylo vytvořeno nové zapisovací vlákno.");
        this.writer = new ObjectOutputStream(outputStream);
    }

    public void shutdown() {
        interrupt = true;
        messageQueue.clear();
        semaphore.release();
    }

    @Override
    public void run() {
        do {
            while(messageQueue.isEmpty() && !interrupt) {
                try {
                    LOGGER.info("Jdu spát na semaforu.");
                    semaphore.acquire();
                } catch (InterruptedException e) {}
            }

            LOGGER.info("Vzbudil jsme se na semaforu, jdu pracovat.");
            while (!messageQueue.isEmpty()) {
                final String msg = messageQueue.poll();
                LOGGER.info(String.format("Odesílám zprávu: '%s'", msg));
                try {
                    writer.writeObject(msg);
                    LOGGER.info("Zpráva byla úspěšně odeslána.");
                } catch (IOException e) {
                    LOGGER.info("Zprávu se nepodařilo odeslat.", e);
                }
            }
        } while(!interrupt);
    }

    public void addMessageToQueue(String message) {
        LOGGER.info(String.format("Přidávám zprávu '%s' do fronty zpráv.", message));
        messageQueue.add(message);
        semaphore.release();
    }
}
