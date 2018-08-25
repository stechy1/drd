package cz.stechy.drd.old;

import cz.stechy.drd.net.NetConfig;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída sloužící k rozesílání multicast zpráv pro notifikaci klientů, že je server dostupny
 */
public class MulticastSender extends Thread {

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

    public MulticastSender(ServerInfoProvider serverInfoProvider) throws IOException {
        super("MulticastSender");
        this.serverInfoProvider = serverInfoProvider;
        this.broadcastAddress = InetAddress.getByName(NetConfig.BROADCAST_ADDRESS);
        this.socket = new DatagramSocket();
    }

    // endregion

    public void shutdown() {
        interrupt = true;
    }

    @Override
    public void run() {
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
