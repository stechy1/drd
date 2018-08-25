package cz.stechy.drd.core.multicaster;

import cz.stechy.drd.core.ServerInfoProvider;
import cz.stechy.drd.net.NetConfig;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MulticastSender extends Thread implements IMulticastSender {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(MulticastSender.class);

    private static final long SLEEP_TIME = 2000L;

    // endregion

    // region Variables

    // Socket, na kterém se posílají zprávy
    private final DatagramSocket socket;
    // Broadcast adresa
    private final InetAddress broadcastAddress;
    // Poskytovatel informací o serveru
    private final ServerInfoProvider serverInfoProvider;

    private boolean interrupt = false;

    // endregion

    // region Constructors

    MulticastSender(ServerInfoProvider serverInfoProvider) {
        super("MulticastSender");
        this.serverInfoProvider = serverInfoProvider;
        InetAddress address = null;
        DatagramSocket datagramSocket = null;
        try {
            address = InetAddress.getByName(NetConfig.BROADCAST_ADDRESS);
            datagramSocket = new DatagramSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.socket = datagramSocket;
            this.broadcastAddress = address;
        }
    }

    // endregion

    public void shutdown() {
        interrupt = true;
        try {
            join();
        } catch (InterruptedException ignored) { }
    }

    @Override
    public void run() {
        if (socket == null || broadcastAddress == null) {
            interrupt = false;
        }

        while(!interrupt) {
            try {
                final byte[] data = serverInfoProvider.getServerStatusMessage().toByteArray();
                final DatagramPacket datagramPacket = new DatagramPacket(data, data.length, broadcastAddress, NetConfig.BROADCAST_PORT);
                this.socket.send(datagramPacket);
            } catch (IOException e) {
                LOGGER.error("Nezdařilo se poslat multicast datagram.", e);
                break;
            }

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignored) {}
        }
    }

}
