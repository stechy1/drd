package cz.stechy.drd;

import cz.stechy.drd.net.message.IMessage;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements Runnable {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private final Socket socket;
    private final InputStream inputStream;
    final ObjectOutputStream writer;
    private final WriterThread writerThread;

    private OnConnectionClosedListener connectionClosedListener;
    private boolean interrupt = false;

    public Client(Socket client, WriterThread writerThread) throws IOException {
        this.writerThread = writerThread;
        this.inputStream = client.getInputStream();
        this.writer = new ObjectOutputStream(client.getOutputStream());
        socket = client;
        LOGGER.info("Nový klient byl vytvořen.");
    }

    @Override
    public void run() {
        LOGGER.info("Spouštím nekonečnou smyčku pro komunikaci s klientem.");
        try (ObjectInputStream reader = new ObjectInputStream(inputStream)) {
            Object received;
            while ((received = reader.readObject()) != null && !interrupt) {
                LOGGER.debug(String.format("Bylo přijato: '%s'", received));
                writer.writeObject("responce" + received);
                if (received.equals("konec")) {
                    interrupt = true;
                }
            }
        } catch (EOFException|SocketException e) {
            LOGGER.info("Klient ukončil spojení.");
        } catch (IOException e) {
            LOGGER.warn("Nastala neočekávaná vyjímka.", e);
        } catch (ClassNotFoundException e) {
            // Nikdy by nemělo nastat
            LOGGER.error("Nebyla nalezena třída.", e);
        } finally {
            if (connectionClosedListener != null) {
                connectionClosedListener.onConnectionClosed();
            }
            close();
        }
    }

    public void setConnectionClosedListener(OnConnectionClosedListener connectionClosedListener) {
        this.connectionClosedListener = connectionClosedListener;
    }

    public void close() {
        try {
            LOGGER.info("Uzavírám socket.");
            socket.close();
            LOGGER.info("Socket byl úspěšně uzavřen.");
        } catch (IOException e) {
            LOGGER.error("Socket se nepodařilo uzavřít!", e);
        }
    }

    public void disconnect() {
        interrupt = true;
    }

    public void sendMessage(IMessage message) {
        writerThread.sendMessage(writer, message);
    }

    @FunctionalInterface
    public interface OnConnectionClosedListener {
        void onConnectionClosed();
    }
}
