package cz.stechy.drd.net;

import cz.stechy.drd.net.message.ServerStatusMessage.ServerStatusData;
import cz.stechy.drd.util.ObservableMergers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Třída starající se o nalezení dostupných serverů v lokální síti
 */
public class LanServerFinder implements Runnable {

    // region Variables

    // Kolekce nalezených serverů
    private final ObservableSet<ServerStatusData> serverSet = FXCollections.observableSet();
    // Broadcast adresa
    private final InetAddress broadcastAddress;
    // Socket, na kterém se naslouchá
    private final MulticastSocket socket;

    private boolean interrupt = false;

    // endregion

    // region Constructors

    public LanServerFinder() throws IOException {
        this.broadcastAddress = InetAddress.getByName(NetConfig.BROADCAST_ADDRESS);
        this.socket = new MulticastSocket(NetConfig.BROADCAST_PORT);
        this.socket.setSoTimeout(5000);
        this.socket.joinGroup(this.broadcastAddress);
    }

    // endregion

    public void shutdown() {
        interrupt = true;
    }

    @Override
    public void run() {
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
                final ServerStatusData statusData = (ServerStatusData) ois.readObject();
                Platform.runLater(() -> serverSet.add(statusData));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<ServerStatusData> getServerList() {
        final ObservableList<ServerStatusData> observableList = FXCollections.observableArrayList();
        ObservableMergers.listObserveSet(serverSet, observableList);

        return observableList;
    }
}
