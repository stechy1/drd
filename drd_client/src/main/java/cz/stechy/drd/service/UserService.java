package cz.stechy.drd.service;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.di.Inject;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.User;
import cz.stechy.drd.net.ClientCommunicator;
import cz.stechy.drd.net.message.AuthMessage;
import cz.stechy.drd.net.message.AuthMessage.AuthAction;
import cz.stechy.drd.net.message.AuthMessage.AuthMessageData;
import cz.stechy.drd.net.message.MessageSource;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Služba pro přístup ke správě uživatelů
 */
@Singleton
public class UserService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    // endregion

    // region Variables

    private final ObjectProperty<User> user = new SimpleObjectProperty<>(this, "user", null);
    private final CryptoService cryptoService;
    private ClientCommunicator communicator;

    // endregion

    // region Constructors

    public UserService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // endregion

    // region Private methods

    // endregion

    // region Public methods

    /**
     * Přihlásí uživatele do aplikace
     *
     * @param username Uživatelské jméno
     * @param password Uživatelské heslo
     */
    public CompletableFuture<User> loginAsync(String username, String password) {
        LOGGER.trace("Přihlašuji uživatele s loginem: {}.", username);
        final byte[] usernameRaw = cryptoService.encrypt(username.getBytes());
        final byte[] passwordRaw = cryptoService.encrypt(password.getBytes());
        return communicator.sendMessageFuture(new AuthMessage(MessageSource.CLIENT, AuthAction.LOGIN, new AuthMessageData(usernameRaw, passwordRaw)))
            .thenApply(responce -> {
                if (!responce.isSuccess()) {
                    LOGGER.error("Přihlášení uživatele {} se nezdařilo.", username);
                    throw new RuntimeException("Přihlášení se nezdařilo.");
                }

                final User user = new User(username, password);
                user.setId(((String) ((Object[]) responce.getData())[0]));
                return user;
            }).thenApplyAsync(user -> {
                this.user.setValue(user);
                getUser().setLogged(true);
                return user;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    /**
     * Odhlásí uživatele z aplikace
     */
    public CompletableFuture<Void> logoutAsync() {
        LOGGER.trace("Odhlašuji uživatele...");
        return CompletableFuture.supplyAsync(() -> {
            getUser().setLogged(false);
            user.set(null);
            return null;
        }, ThreadPool.JAVAFX_EXECUTOR);
    }

    /**
     * Zaregistruje nového uživatele
     *
     * @param username Uživatelské jméno
     * @param password Uživatelské heslo
     */
    public CompletableFuture<Void> registerAsync(String username, String password) {
        LOGGER.trace("Registruji uživatele {}.", username);
        final byte[] usernameRaw = cryptoService.encrypt(username.getBytes());
        final byte[] passwordRaw = cryptoService.encrypt(password.getBytes());
        return communicator.sendMessageFuture(new AuthMessage(MessageSource.CLIENT, AuthAction.REGISTER, new AuthMessageData(usernameRaw, passwordRaw)))
            .thenAcceptAsync(responce -> {
                if (!responce.isSuccess()) {
                    LOGGER.error("Registrace uživatele {} se nezdařila.", username);
                    throw new RuntimeException("Registrace se nezdařila.");
                }

            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    // endregion

    // region Gettsrs & Setters

    public final ReadOnlyObjectProperty<User> userProperty() {
        return user;
    }

    public final User getUser() {
        return user.get();
    }

    @SuppressWarnings("unused")
    @Inject
    public void setCommunicator(ClientCommunicator communicator) {
        this.communicator = communicator;
    }

    // endregion
}
