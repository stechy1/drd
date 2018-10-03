package cz.stechy.drd.service.communicator;

import com.google.inject.Inject;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.annotation.Service;
import cz.stechy.drd.crypto.RSA.CypherKey;
import cz.stechy.drd.net.ConnectionState;
import cz.stechy.drd.net.LostConnectionHandler;
import cz.stechy.drd.net.OnDataReceivedListener;
import cz.stechy.drd.net.ReaderThread;
import cz.stechy.drd.net.Request;
import cz.stechy.drd.net.WriterThread;
import cz.stechy.drd.net.message.CryptoMessage;
import cz.stechy.drd.net.message.HelloMessage;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.net.message.ServerStatusMessage;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatus;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatusData;
import cz.stechy.drd.service.crypto.CryptoService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
@Service
public final class ClientCommunicator implements IClientCommunicator {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCommunicator.class);

    // endregion

    // region Variables

    private final ObjectProperty<Socket> socket = new SimpleObjectProperty<>(this, "socket", null);
    private final ReadOnlyObjectWrapper<ConnectionState> connectionState = new ReadOnlyObjectWrapper<>(this, "connectionState", ConnectionState.DISCONNECTED);
    private final HashMap<String, List<OnDataReceivedListener>> listeners = new HashMap<>();
    private final StringProperty host = new SimpleStringProperty(this, "host", null);
    private final IntegerProperty port = new SimpleIntegerProperty(this, "port", -1);
    private final StringProperty connectedServerName = new SimpleStringProperty(this, "connectedServerName", null);
    private final ObjectProperty<ServerStatus> serverStatus = new SimpleObjectProperty<>(this, "serverStatus", ServerStatus.EMPTY);
    private final Queue<Request> requests = new LinkedBlockingQueue<>();

    private CryptoService cryptoService;
    private ReaderThread readerThread;
    private WriterThread writerThread;

    // endregion

    // region Constructors

    @Inject
    public ClientCommunicator(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        socket.addListener(this::socketListener);
        connectedServerName.bind(Bindings.createStringBinding(
            () -> String.format("%s:%d", host.get(), port.get()),
            host, port, connectionState));
    }
    // endregion

    // region Private methods

    private void socketListener(ObservableValue<? extends Socket> observableValue, Socket oldSocket, Socket newSocket) {
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
        if (message.isResponce()) {
            final Request poll = requests.poll();
            if (poll != null) {
                poll.onResponce(message);
            }
            return;
        }

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

    @Override
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


    @Override
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

    @Override
    public synchronized void sendMessage(IMessage message) {
        if (writerThread != null) {
            writerThread.addMessageToQueue(message);
        }
    }

    @Override
    public synchronized CompletableFuture<IMessage> sendMessageFuture(IMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            sendMessage(message);
            return null;
        })
            .thenCompose(ignored -> {
                Request request = new Request();
                requests.add(request);
                return request.getFuture();
            });
    }


    @Override
    public synchronized void registerMessageObserver(String messageType, OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners
            .computeIfAbsent(messageType, k -> new ArrayList<>());

        listenerList.add(listener);
    }


    @Override
    public synchronized void unregisterMessageObserver(String messageType, OnDataReceivedListener listener) {
        List<OnDataReceivedListener> listenerList = listeners.get(messageType);
        if (listenerList == null) {
            return;
        }

        listenerList.remove(listener);
    }

    // endregion

    // region Getters & Setters

    @Override
    public ConnectionState getConnectionState() {
        return connectionState.get();
    }

    @Override
    public ReadOnlyObjectProperty<ConnectionState> connectionStateProperty() {
        return connectionState.getReadOnlyProperty();
    }

    public boolean isConnected() {
        return connectionState.get() == ConnectionState.CONNECTED;
    }

    @Override
    public String getConnectedServerName() {
        return connectedServerName.get();
    }

    public StringProperty connectedServerNameProperty() {
        return connectedServerName;
    }

    public ServerStatus getServerStatus() {
        return serverStatus.get();
    }

    public ObjectProperty<ServerStatus> serverStatusProperty() {
        return serverStatus;
    }

    // endregion
}