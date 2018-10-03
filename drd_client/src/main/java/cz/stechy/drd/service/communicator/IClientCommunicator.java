package cz.stechy.drd.service.communicator;

import cz.stechy.drd.net.ConnectionState;
import cz.stechy.drd.net.OnDataReceivedListener;
import cz.stechy.drd.net.message.IMessage;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface IClientCommunicator {

    /**
     * Pokusí se připojit na server
     *
     * @param host Adresa serveru
     * @param port Port, na kterém server naslouchá
     */
    CompletableFuture<Boolean> connect(String host, int port);

    /**
     * Ukončí spojení se serverem
     */
    CompletableFuture<Boolean> disconnect();

    /**
     * Pošle zprávu na server
     *
     * @param message {@link IMessage} Zpráva, která se má odeslat
     */
    void sendMessage(IMessage message);

    /**
     * Pošle zprávu na server a počká, až přijde odpověď
     *
     * @param message {@link IMessage} Zprává, která se má odeslat
     * @return {@link CompletableFuture<IMessage>} Budoucnost s odpovědí
     */
    CompletableFuture<IMessage> sendMessageFuture(IMessage message);

    /**
     * Zaregistruje posluchače na určitý typ zprávy
     *
     * @param messageType {@link String} Typ zprávy
     * @param listener {@link OnDataReceivedListener} Listener
     */
    void registerMessageObserver(String messageType, OnDataReceivedListener listener);

    /**
     * Odhlásí odběr od určitého typu zpráv
     *
     * @param messageType {@link String} Typ zprávy
     * @param listener {@link OnDataReceivedListener} Listener
     */
    void unregisterMessageObserver(String messageType, OnDataReceivedListener listener);

    ReadOnlyObjectProperty<ConnectionState> connectionStateProperty();

    ConnectionState getConnectionState();

    String getConnectedServerName();
}
