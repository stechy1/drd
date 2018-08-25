package cz.stechy.drd.net;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.crypto.RSA.CypherKey;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.net.message.CryptoMessage;
import cz.stechy.drd.net.message.HelloMessage;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.net.message.ServerStatusMessage;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatus;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatusData;
import cz.stechy.drd.service.CryptoService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída starající se o komunikaci se serverem
 */
@Singleton
public final class ClientCommunicator {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCommunicator.class);

    // endregion

    // region Variables

    private final ObjectProperty<Socket> socket = new SimpleObjectProperty<>(this, "socket", null);
    private final ObjectProperty<ConnectionState> connectionState = new SimpleObjectProperty<>(this,
        "connectionState", ConnectionState.DISCONNECTED);
    private final HashMap<String, List<OnDataReceivedListener>> listeners = new HashMap<>();
    private final StringProperty host = new SimpleStringProperty(this, "host", null);
    private final IntegerProperty port = new SimpleIntegerProperty(this, "port", -1);
    private final StringProperty connectedServer = new SimpleStringProperty(this, "connectedServer",
        null);
    private final ObjectProperty<ServerStatus> serverStatus = new SimpleObjectProperty<>(this, "serverStatus", ServerStatus.EMPTY);

    private CryptoService cryptoService;
    private ReaderThread readerThread;
    private WriterThread writerThread;

    // endregion

    // region Constructors

    public ClientCommunicator(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        socket.addListener(this::socketListener);
        connectedServer.bind(Bindings.createStringBinding(
            () -> String.format("%s:%d", host.get(), port.get()),
            host, port, connectionState));
    }
    // endregion

    // region Private methods

    private void socketListener(ObservableValue<? extends Socket> observableValue, Socket oldSocket,
        Socket newSocket) {
        if (newSocket == null) {
            readerThread = null;
            writerThread = null;
            unregisterMessageObserver(ServerStatusMessage.MESSAGE_TYPE, this.serverStatusListener);
            unregisterMessageObserver(HelloMessage.MESSAGE_TYPE, this.helloListener);
            unregisterMessageObserver(CryptoMessage.MESSAGE_TYPE, this.cryptoListener);
            return;
        }

        try {
            readerThread = new ReaderThread(newSocket.getInputStream(), listener,
                lostConnectionHandler);
            writerThread = new WriterThread(newSocket.getOutputStream(), lostConnectionHandler);

            readerThread.start();
            writerThread.start();
            registerMessageObserver(ServerStatusMessage.MESSAGE_TYPE, this.serverStatusListener);
            registerMessageObserver(HelloMessage.MESSAGE_TYPE, this.helloListener);
            registerMessageObserver(CryptoMessage.MESSAGE_TYPE, this.cryptoListener);
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

    private void changeState(ConnectionState state) {
        connectionState.set(state);
    }

    private final LostConnectionHandler lostConnectionHandler = () -> {
        disconnect();
        LOGGER.info("Spojení bylo ztraceno");
    };

    private final OnDataReceivedListener serverStatusListener = message -> {
        final ServerStatusMessage statusMessage = (ServerStatusMessage) message;
        final ServerStatusData status = (ServerStatusData) statusMessage.getData();
        serverStatus.set(status.serverStatus);
    };

    private final OnDataReceivedListener helloListener = message -> {
       sendMessage(new HelloMessage(MessageSource.CLIENT));
       sendMessage(new CryptoMessage(MessageSource.CLIENT, cryptoService.getClientPublicKey()));
       Platform.runLater(() -> changeState(ConnectionState.CONNECTED));
    };

    private final OnDataReceivedListener cryptoListener = message -> {
        final CypherKey serverKey = (CypherKey) message.getData();
        cryptoService.setServerPublicCey(serverKey);
        LOGGER.info("Ukládám veřejný klíč serveru.");
    };

    // endregion

    // region Public methods

    /**
     * Pokusí se připojit na server
     *
     * @param host Adresa serveru
     * @param port Port, na kterém server naslouchá
     */
    public CompletableFuture<Boolean> connect(String host, int port) {
        if (isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        changeState(ConnectionState.CONNECTING);

        return CompletableFuture.supplyAsync(() -> {
            final Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host, port), 3000);
                return socket;
            } catch (IOException e) {
                return null;
            }
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(socket -> {
                this.socket.set(socket);
                if (socket != null) {
                    this.host.set(host);
                    this.port.set(port);
//                    changeState(ConnectionState.CONNECTED);
                } else {
                    changeState(ConnectionState.DISCONNECTED);
                    this.host.set(null);
                    this.port.set(-1);
                }
                return socket != null;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    /**
     * Ukončí spojení se serverem
     */
    public CompletableFuture<Boolean> disconnect() {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Ukončuji spojení se serverem.");
            try {
                socket.get().close();

                LOGGER.info("Ukončuji čtecí vlákno.");
                readerThread.shutdown();
                try {
                    readerThread.join();
                } catch (InterruptedException ignored) {
                }
                LOGGER.info("Čtecí vlákno bylo úspěšně ukončeno.");

                LOGGER.info("Ukončuji zapisovací vlákno.");
                writerThread.shutdown();
                try {
                    writerThread.join();
                } catch (InterruptedException ignored) {
                }
                LOGGER.info("Zapisovací vlákno bylo úspěšně ukončeno.");

                LOGGER.info("Spojení se podařilo ukončit");
            } catch (IOException e) {
                LOGGER.error("Nastala neočekávaná chyba při uzavírání socketu.", e);
                return false;
            }

            return true;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(success -> {
                if (success) {
                    this.socket.set(null);
                    changeState(ConnectionState.DISCONNECTED);
                }

                return success;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    /**
     * Pošle zprávu na server
     */
    public synchronized void sendMessage(IMessage message) {
        if (writerThread != null) {
            writerThread.addMessageToQueue(message);
        }
    }

    /**
     * Zaregistruje posluchače na určitý typ zprávy
     *
     * @param messageType {@link String} Typ zprávy
     * @param listener {@link OnDataReceivedListener} Listener
     */
    public synchronized void registerMessageObserver(String messageType, OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners
            .computeIfAbsent(messageType, k -> new ArrayList<>());

        listenerList.add(listener);
    }

    /**
     * Odhlásí odběr od určitého typu zpráv
     *
     * @param messageType {@link String} Typ zprávy
     * @param listener {@link OnDataReceivedListener} Listener
     */
    public synchronized void unregisterMessageObserver(String messageType,
        OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners.get(messageType);
        if (listenerList == null) {
            return;
        }

        listenerList.remove(listener);
    }

    // endregion

    // region Getters & Setters

    public ConnectionState getConnectionState() {
        return connectionState.get();
    }

    public ObjectProperty<ConnectionState> connectionStateProperty() {
        return connectionState;
    }

    public boolean isConnected() {
        return connectionState.get() == ConnectionState.CONNECTED;
    }

    public String getConnectedServer() {
        return connectedServer.get();
    }

    public StringProperty connectedServerProperty() {
        return connectedServer;
    }

    public ServerStatus getServerStatus() {
        return serverStatus.get();
    }

    public ObjectProperty<ServerStatus> serverStatusProperty() {
        return serverStatus;
    }

    // endregion
}
