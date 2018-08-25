package cz.stechy.drd.core.connection;

import cz.stechy.drd.net.message.IMessage;
import java.io.IOException;

/**
 * Rozhraní definující metody připojeného klienta
 */
public interface IClient {

    /**
     * Odešle asynchronně klientovi zprávu
     *
     * @param message {@link IMessage} Zpráva, která se má odeslat
     */
    void sendMessageAsync(IMessage message);

    /**
     * Odešle synchronně klientovi zprávu
     *
     * @param message {@link IMessage} Zpráva, která se má odelsat
     * @throws IOException Pokud se nepodaří zprávu odeslat
     */
    void sendMessage(IMessage message) throws IOException;

    /**
     * Uzavře spojení s klientem
     */
    void close();

}
