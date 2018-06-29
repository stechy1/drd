package cz.stechy.drd.net;

import cz.stechy.drd.net.message.IMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentující vlákno, která posílá data na server
 */
public class WriterThread extends Thread {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(WriterThread.class);

    // endregion

    // region Variables

    private final Semaphore semaphore = new Semaphore(0);
    private final Queue<IMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean working = new AtomicBoolean(false);
    private final ObjectOutputStream writer;
    private final LostConnectionHandler lostConnectionHandler;
    private boolean interrupt = false;

    // endregion

    // region Constructors

    public WriterThread(final OutputStream outputStream,
        LostConnectionHandler lostConnectionHandler) throws IOException {
        super("WriterThread");
        this.lostConnectionHandler = lostConnectionHandler;
        LOGGER.info("Bylo vytvořeno nové zapisovací vlákno.");
        this.writer = new ObjectOutputStream(outputStream);
    }

    // endregion

    // region Public methods

    /**
     * Ukončí činnost zapisovacího vlákna
     */
    public void shutdown() {
        interrupt = true;
        messageQueue.clear();
        semaphore.release();
    }

    /**
     * Přidá zprávu do fronty k odeslání
     *
     * @param message {@link IMessage} Zpráva, která se má odeslat
     */
    public void addMessageToQueue(IMessage message) {
        messageQueue.add(message);
        if (!working.get()) {
            LOGGER.info("Probouzím vlákno spící na semaforu.");
            semaphore.release();
        }
    }

    // endregion

    @Override
    public void run() {
        do {
            while(messageQueue.isEmpty() && !interrupt) {
                try {
                    LOGGER.info("Jdu spát na semaforu.");
                    semaphore.acquire();
                } catch (InterruptedException ignored) {
                }
            }

            LOGGER.info("Vzbudil jsme se na semaforu, jdu pracovat.");
            working.set(true);
            while (!messageQueue.isEmpty()) {
                final IMessage msg = messageQueue.poll();
                assert msg != null;
                LOGGER.info(String.format("Odesílám zprávu: '%s'.", msg.toString()));
                try {
                    writer.writeObject(msg);
                    writer.flush();
                    LOGGER.info("Zpráva byla úspěšně odeslána.");
                } catch (IOException e) {
                    LOGGER.info("Zprávu se nepodařilo odeslat, ukončuji spojení.", e);
                    interrupt = true;
                    if (lostConnectionHandler != null) {
                        lostConnectionHandler.onLostConnection();
                    }
                }
            }
            working.set(false);
        } while(!interrupt);
    }
}
