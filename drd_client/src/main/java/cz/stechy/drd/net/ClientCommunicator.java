package cz.stechy.drd.net;

import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientCommunicator {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCommunicator.class);

    private final Socket socket;
    private final ReaderThread readerThread;
    private final WriterThread writerThread;

    public ClientCommunicator(Socket socket, OnDataReceivedListener listener) throws IOException {
        assert listener != null;
        LOGGER.info("Vytvářím klientský komunikátor.");
        this.socket = socket;
        readerThread = new ReaderThread(socket.getInputStream(), listener);
        writerThread = new WriterThread(socket.getOutputStream());

        readerThread.start();
        writerThread.start();
    }

    public void close() {
        LOGGER.info("Ukončuji spojení se serverem.");
        try {
            socket.close();

            LOGGER.info("Ukončuji čtecí vlákno.");
            readerThread.shutdown();
            try {
                readerThread.join();
            } catch (InterruptedException e) {}
            LOGGER.info("Čtecí vlákno bylo úspěšně ukončeno.");

            LOGGER.info("Ukončuji zapisovací vlákno.");
            writerThread.shutdown();
            try {
                writerThread.join();
            } catch (InterruptedException e) {}
            LOGGER.info("Zapisovací vlákno bylo úspěšně ukončeno.");

            LOGGER.info("Spojení se podařilo ukončit");
        } catch (IOException e) {
            LOGGER.error("Nastala neočekávaná chyba při uzavírání socketu.", e);
        }
    }

    public void sendMessage(String message) {
        writerThread.addMessageToQueue(message);
    }
}
