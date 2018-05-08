package cz.stechy.drd.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReaderThread extends Thread {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderThread.class);

    private final InputStream inputStream;
    private final OnDataReceivedListener listener;
    private boolean interrupt = false;

    public ReaderThread(final InputStream inputStream, OnDataReceivedListener listener) {
        super("ReaderThread");
        assert listener != null;
        this.listener = listener;
        LOGGER.info("Bylo vytvořeno nové čtecí vlákno.");
        this.inputStream = inputStream;
    }

    public void shutdown() {
        interrupt = true;
    }

    @Override
    public void run() {
        LOGGER.info("Spouštím nekonečnou smyčku pro komunikaci se serverem.");
        try (ObjectInputStream reader = new ObjectInputStream(inputStream)) {
            Object received;
            while((received = reader.readObject()) != null && !interrupt) {
                listener.onDataReceived(received);
            }
        } catch (IOException e) {
            LOGGER.warn("Čtecí vlákno bylo ukončeno.");
        } catch (ClassNotFoundException e) {
            // Nikdy by nemělo nastat
            LOGGER.error("Nebyla nalezena třída.", e);
        }
    }
}
