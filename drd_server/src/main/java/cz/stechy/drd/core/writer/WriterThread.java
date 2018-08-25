package cz.stechy.drd.core.writer;

import cz.stechy.drd.net.message.IMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriterThread extends Thread implements IWriterThread {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(WriterThread.class);

    private final Semaphore semaphore = new Semaphore(0);
    private final Queue<QueueTuple> messageQueue = new ConcurrentLinkedQueue<>();
    private boolean working = false;
    private boolean interrupt = false;

    public WriterThread() {
        super("WriterThread");
    }

    @Override
    public void sendMessage(ObjectOutputStream writer, IMessage message) {
        messageQueue.add(new QueueTuple(writer, message));
        if (!working) {
            working = true;
            LOGGER.info("Probouzím vlákno na semaforu.");
            semaphore.release();
        }
    }

    @Override
    public void run() {
        LOGGER.info("Spouštím zapisovací vlákno.");
        while(!interrupt) {
            LOGGER.info("Jdu spát.");
            while(messageQueue.isEmpty() && !interrupt) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ignored) {}
            }

            working = true;
            LOGGER.info("Vzbudil jsem se na semaforu, jdu pracovat.");
            while(!messageQueue.isEmpty()) {
                final QueueTuple entry = messageQueue.poll();
                assert entry != null;
                LOGGER.info(String.format("Odesílám zprávu: '%s'", entry.message));
                try {
                    entry.writer.writeObject(entry.message);
                    entry.writer.flush();
                    LOGGER.info("Zpráva byla úspěšně odeslána.");
                } catch (IOException e) {
                    LOGGER.info("Zprávu se nepodařio doručit.", e);
                }
            }
            working = false;
        }

        LOGGER.info("Ukončuji writer thread.");
    }

    @Override
    public void shutdown() {
        interrupt = true;
        semaphore.release();
        try {
            join();
        } catch (InterruptedException ignored) { }
    }

    private static final class QueueTuple {
        final IMessage message;
        final ObjectOutputStream writer;

        private QueueTuple(ObjectOutputStream writer, IMessage message) {
            this.message = message;
            this.writer = writer;
        }
    }
}
