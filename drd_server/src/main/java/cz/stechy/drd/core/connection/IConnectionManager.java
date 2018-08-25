package cz.stechy.drd.core.connection;

import java.io.IOException;
import java.net.Socket;

public interface IConnectionManager {

    /**
     * Vrátí počet aktivních připojených klientů
     *
     * @return Počet aktivních připojených klientů
     */
    int getConnectedClientCount();

    void addClient(Socket socket) throws IOException;

    void onServerStart();

    void onServerStop();
}
