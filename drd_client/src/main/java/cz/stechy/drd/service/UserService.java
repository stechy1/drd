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

/**
 * Služba pro přístup ke správě uživatelů
 */
@Singleton
public class UserService {

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
        final byte[] usernameRaw = cryptoService.encrypt(username.getBytes());
        final byte[] passwordRaw = cryptoService.encrypt(password.getBytes());
        return communicator.sendMessageFuture(new AuthMessage(MessageSource.CLIENT, AuthAction.LOGIN, new AuthMessageData(usernameRaw, passwordRaw)))
            .thenApply(responce -> {
                if (!responce.isSuccess()) {
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
        final byte[] usernameRaw = cryptoService.encrypt(username.getBytes());
        final byte[] passwordRaw = cryptoService.encrypt(password.getBytes());
        return communicator.sendMessageFuture(new AuthMessage(MessageSource.CLIENT, AuthAction.REGISTER, new AuthMessageData(usernameRaw, passwordRaw)))
            .thenApplyAsync(responce -> {
                if (!responce.isSuccess()) {
                    throw new RuntimeException("Registrace se nezdařila.");
                }

                return null;
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
