package cz.stechy.drd.net;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.app.server.ServerStatusModel;
import cz.stechy.drd.net.message.ServerStatusMessage;
import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatusData;
import cz.stechy.drd.util.ObservableMergers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Třída starající se o nalezení dostupných serverů v lokální síti
 */
public class LanServerFinder implements Runnable {

    // region Variables

    // Kolekce nalezených serverů
    private final ObservableMap<UUID, ServerStatusModel> serverMap = FXCollections.observableHashMap();
    // Broadcast adresa
    private final InetAddress broadcastAddress;
    // Socket, na kterém se naslouchá
    private final MulticastSocket socket;

    private final LanServerWatchdog watchdog;

    private boolean interrupt = false;

    // endregion

    // region Constructors

    public LanServerFinder() throws IOException {
        this.broadcastAddress = InetAddress.getByName(NetConfig.BROADCAST_ADDRESS);
        this.socket = new MulticastSocket(NetConfig.BROADCAST_PORT);
        this.socket.setSoTimeout(5000);
        this.socket.joinGroup(this.broadcastAddress);
        this.watchdog = new LanServerWatchdog(serverMap);
    }

    // endregion

    public void shutdown() {
        watchdog.shutdown();
        interrupt = true;
    }

    @Override
    public void run() {
        ThreadPool.COMMON_EXECUTOR.submit(watchdog);
        final byte[] data = new byte[1024];

        while(!interrupt) {
            final DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
            try {
                socket.receive(datagramPacket);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                break;
            }

            final ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
            try {
                final ObjectInputStream ois = new ObjectInputStream(bais);
                final ServerStatusMessage statusMessage = (ServerStatusMessage) ois.readObject();
                final ServerStatusData statusData = (ServerStatusData) statusMessage.getData();
                final UUID serverID = statusData.serverID;
                Platform.runLater(() -> {
                    if (serverMap.containsKey(serverID)) {
                        serverMap.get(serverID).update(statusData);
                    } else {
                        serverMap.put(serverID, new ServerStatusModel(statusData, datagramPacket.getAddress()));
                        watchdog.startWatchdog();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<ServerStatusModel> getServerList() {
        final ObservableList<ServerStatusModel> observableList = FXCollections.observableArrayList();
        ObservableMergers.listObserveMap(serverMap, observableList);

        return observableList;
    }
}
