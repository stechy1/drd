package cz.stechy.drd.net;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public final class ClientCommunicator {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCommunicator.class);

    // endregion

    // region Variables

    private final ObjectProperty<Socket> socket = new SimpleObjectProperty<>(this, "socket", null);
    private final BooleanProperty connected = new SimpleBooleanProperty(this, "connected", false);
    private final HashMap<MessageType, List<OnDataReceivedListener>> listeners = new HashMap<>();
    private ReaderThread readerThread;
    private WriterThread writerThread;

    // endregion

    // region Constructors

    public ClientCommunicator() {
        connected.bind(socket.isNotNull());
        socket.addListener(this::socketListener);
    }
    // endregion

    // region Private methods

    private void socketListener(ObservableValue<? extends Socket> observableValue, Socket oldSocket,
        Socket newSocket) {
        if (newSocket == null) {
            readerThread = null;
            writerThread = null;
            return;
        }

        try {
            readerThread = new ReaderThread(newSocket.getInputStream(), listener);
            writerThread = new WriterThread(newSocket.getOutputStream());

            readerThread.start();
            writerThread.start();
        } catch (IOException e) {
            LOGGER.error("Vyskytl se problém při vytváření komunikace se serverem.");
        }
    }

    private final OnDataReceivedListener listener = message -> {
        final List<OnDataReceivedListener> listenerList = listeners.get(message.getType());
        if (listenerList == null) {
            return;
        }

        for (OnDataReceivedListener listener : listenerList) {
            listener.onDataReceived(message);
        }
    };

    // endregion

    // region Public methods

    /**
     * Pokusí se připojit na server
     *
     * @param host Adresa serveru
     * @param port Port, na kterém server naslouchá
     */
    public CompletableFuture<Void> connect(String host, int port) {
        if (isConnected()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            final Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host, port), 3000);
                return socket;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(socket -> {
                this.socket.set(socket);
                return null;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    /**
     * Ukončí spojení se serverem
     */
    public void disconnect() {
        if (!isConnected()) {
            return;
        }

        LOGGER.info("Ukončuji spojení se serverem.");
        try {
            socket.get().close();

            LOGGER.info("Ukončuji čtecí vlákno.");
            readerThread.shutdown();
            try {
                readerThread.join();
            } catch (InterruptedException e) {
            }
            LOGGER.info("Čtecí vlákno bylo úspěšně ukončeno.");

            LOGGER.info("Ukončuji zapisovací vlákno.");
            writerThread.shutdown();
            try {
                writerThread.join();
            } catch (InterruptedException e) {
            }
            LOGGER.info("Zapisovací vlákno bylo úspěšně ukončeno.");

            LOGGER.info("Spojení se podařilo ukončit");
        } catch (IOException e) {
            LOGGER.error("Nastala neočekávaná chyba při uzavírání socketu.", e);
        }

        this.socket.set(null);
    }

    /**
     * Pošle zprávu na server
     * @param message
     */
    public void sendMessage(IMessage message) {
        writerThread.addMessageToQueue(message);
    }

    /**
     * Zaregistruje posluchače na určitý typ zprávy
     *
     * @param messageType {@link MessageType} Typ zprávy
     * @param listener {@link OnDataReceivedListener} Listener
     */
    public void registerMessageObserver(MessageType messageType, OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners.get(messageType);
        if (listenerList == null) {
            listenerList = new ArrayList<>();
            listeners.put(messageType, listenerList);
        }

        listenerList.add(listener);
    }

    /**
     * Odhlásí odběr od určitého typu zpráv
     *
     * @param messageType {@link MessageType} Typ zprávy
     * @param listener {@link OnDataReceivedListener} Listener
     */
    public void unregisterMessageObserver(MessageType messageType, OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners.get(messageType);
        if (listenerList == null) {
            return;
        }

        listenerList.remove(listener);
    }

    // endregion

    // region Getters & Setters

    public boolean isConnected() {
        return connected.get();
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    // endregion
}