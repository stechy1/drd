package cz.stechy.drd.net;

import cz.stechy.drd.net.message.IMessage;
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
    private final Queue<IMessage> messageQueue = new ConcurrentLinkedQueue<>();
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
                } catch (InterruptedException ignored) {
                    LOGGER.error("Interupt", ignored);
                }
            }

            LOGGER.info("Vzbudil jsme se na semaforu, jdu pracovat.");
            while (!messageQueue.isEmpty()) {
                final IMessage msg = messageQueue.poll();
                LOGGER.info(String.format("Odesílám zprávu: '%s'", msg.toString()));
                try {
                    writer.writeObject(msg);
                    writer.flush();
                    LOGGER.info("Zpráva byla úspěšně odeslána.");
                } catch (IOException e) {
                    LOGGER.info("Zprávu se nepodařilo odeslat.", e);
                }
            }
        } while(!interrupt);
    }

    public void addMessageToQueue(IMessage message) {
        LOGGER.info(String.format("Přidávám zprávu '%s' do fronty zpráv.", message));
        messageQueue.add(message);
        LOGGER.info("Probouzím vlákno spící na semaforu.");
        semaphore.release();
    }
}
