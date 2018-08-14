package cz.stechy.drd.net;

import cz.stechy.drd.net.message.IMessage;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentující vlákno, které přijímá data ze serveru
 */
public class ReaderThread extends Thread {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderThread.class);

    // endregion

    // region Variables

    private final InputStream inputStream;
    private final OnDataReceivedListener listener;
    private final LostConnectionHandler lostConnectionHandler;
    private boolean interrupt = false;

    // endregion

    // region Constructors

    public ReaderThread(final InputStream inputStream, OnDataReceivedListener listener,
        LostConnectionHandler lostConnectionHandler) {
        super("ReaderThread");
        this.lostConnectionHandler = lostConnectionHandler;
        assert listener != null;
        this.listener = listener;
        LOGGER.info("Bylo vytvořeno nové čtecí vlákno.");
        this.inputStream = inputStream;
    }

    // endregion

    // region Public methods

    /**
     * Ukončí činnost čtecího vlákna
     */
    public void shutdown() {
        interrupt = true;
    }

    // endregion

    @Override
    public void run() {
        LOGGER.info("Spouštím nekonečnou smyčku pro komunikaci se serverem.");
        try (final ObjectInputStream reader = new ObjectInputStream(inputStream)) {
            IMessage received;
            while ((received = (IMessage) reader.readObject()) != null && !interrupt) {
                LOGGER.info(String.format("Byla přijata nějaká data: '%s'", received.toString()));
                listener.onDataReceived(received);
            }
        } catch (EOFException e) {
            LOGGER.warn("Spojení bylo nečekaně ukončeno.");
        } catch (IOException e) {
            LOGGER.warn("Spojení bylo ukončeno.");
        } catch (ClassNotFoundException e) {
            // Nikdy by nemělo nastat
            LOGGER.error("Nebyla nalezena třída.", e);
        } catch (Exception e) {
            LOGGER.error("Neznámá chyba.", e);
        } finally {
            if (lostConnectionHandler != null) {
                lostConnectionHandler.onLostConnection();
            }
        }

        LOGGER.warn("Čtecí vlákno bylo ukončeno.");
    }
}
