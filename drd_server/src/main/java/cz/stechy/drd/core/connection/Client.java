package cz.stechy.drd.core.connection;

import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.core.writer.IWriterThread;
import cz.stechy.drd.net.message.IMessage;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída reprezentuje připojeného klienta a zprostředkovává komunikaci s klientem
 */
public class Client implements IClient, Runnable {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private final Socket socket;
    private final IWriterThread writerThread;
    private final IEventBus eventProcessor;
    private final ObjectOutputStream writer;

    private ConnectionClosedListener connectionClosedListener;

    Client(Socket socket, IWriterThread writerThread, IEventBus eventProcessor) throws IOException {
        this.socket = socket;
        this.writerThread = writerThread;
        writer = new ObjectOutputStream(socket.getOutputStream());
        this.eventProcessor = eventProcessor;
        LOGGER.info("Byl vytvořen nový klient.");
    }

    /**
     * Uzavře spojení s klientem
     */
    public void close() {
        try {
            LOGGER.info("Uzavírám socket.");
            socket.close();
            LOGGER.info("Socket byl úspěšně uzavřen.");
        } catch (IOException e) {
            LOGGER.error("Socket se nepodařilo uzavřít!", e);
        }
    }

    @Override
    public void sendMessageAsync(IMessage message) {
        writerThread.sendMessage(writer, message);
    }

    @Override
    public void sendMessage(IMessage message) throws IOException {
        writer.writeObject(message);
    }

    @Override
    public void run() {
        LOGGER.info("Spouštím nekonečnou smyčku pro komunikaci s klientem.");
        try (ObjectInputStream reader = new ObjectInputStream(socket.getInputStream())) {
            LOGGER.info("InputStream byl úspěšně vytvořen.");
            IMessage received;
            while ((received = (IMessage) reader.readObject()) != null) {
                LOGGER.info(String.format("Bylo přijato: '%s'", received));
                eventProcessor.publishEvent(new MessageReceivedEvent(received, this));
            }
        } catch (EOFException |SocketException e) {
            LOGGER.info("Klient ukončil spojení.");
        } catch (IOException e) {
            LOGGER.warn("Nastala neočekávaná vyjímka.", e);
        } catch (ClassNotFoundException e) {
            // Nikdy by nemělo nastat
            LOGGER.error("Nebyla nalezena třída.", e);
        } catch (Exception e) {
            LOGGER.error("Neznámá chyba.", e);
        } finally {
            LOGGER.info("Volám connectionClosedListener.");
            if (connectionClosedListener != null) {
                connectionClosedListener.onConnectionClosed();
            }
            close();
        }
    }

    /**
     * Nastaví listener na ztrátu spojení s klientem
     *
     * @param connectionClosedListener {@link ConnectionClosedListener}
     */
    void setConnectionClosedListener(ConnectionClosedListener connectionClosedListener) {
        this.connectionClosedListener = connectionClosedListener;
    }

    /**
     * Rozhraní obsahující metodu, která se zavolá v případě, že se ukončí spojení s klientem
     */
    @FunctionalInterface
    public interface ConnectionClosedListener {

        /**
         * Metoda se zavolá v případě, že se ukončí spojení s klientem
         */
        void onConnectionClosed();
    }
}
