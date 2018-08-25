package cz.stechy.drd.service;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.di.Inject;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.User;
import cz.stechy.drd.net.ClientCommunicator;
import cz.stechy.drd.net.OnDataReceivedListener;
import cz.stechy.drd.net.message.AuthMessage;
import cz.stechy.drd.net.message.AuthMessage.AuthAction;
import cz.stechy.drd.net.message.AuthMessage.AuthMessageData;
import cz.stechy.drd.net.message.IMessage;
import cz.stechy.drd.net.message.MessageSource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
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
    private final Semaphore semaphore = new Semaphore(0);
    private final CryptoService cryptoService;
    private ClientCommunicator communicator;
    private String tmpId;
    private boolean success;

    // endregion

    // region Constructors

    public UserService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // endregion

    // region Private methods

    private final OnDataReceivedListener authListener = message -> {
        this.success = message.isSuccess();
        if (!success) {
            semaphore.release();
            return;
        }

        final AuthMessage authMessage = (AuthMessage) message;
        final AuthMessageData data = (AuthMessageData) authMessage.getData();
        this.tmpId = data.id;
        semaphore.release();
    };

    // endregion

    // region Public methods

    /**
     * Přihlásí uživatele do aplikace
     *
     * @param username Uživatelské jméno
     * @param password Uživatelské heslo
     */
    public CompletableFuture<User> loginAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            final byte[] usernameRaw = cryptoService.encrypt(username.getBytes());
            final byte[] passwordRaw = cryptoService.encrypt(password.getBytes());
            final IMessage loginMessage = new AuthMessage(MessageSource.CLIENT, AuthAction.LOGIN,
                new AuthMessageData(usernameRaw, passwordRaw));
            this.communicator.sendMessage(loginMessage);

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            if (!success) {
                throw new RuntimeException("Přihlášení se nezdařilo.");
            }

            final User u = new User(username, password);
            u.setId(tmpId);
            tmpId = null;
            return u;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(user -> {
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
     *  @param username Uživatelské jméno
     * @param password Uživatelské heslo
     */
    public CompletableFuture<Void> registerAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            final byte[] usernameRaw = cryptoService.encrypt(username.getBytes());
            final byte[] passwordRaw = cryptoService.encrypt(password.getBytes());
            final IMessage message = new AuthMessage(MessageSource.CLIENT, AuthAction.REGISTER,
                new AuthMessageData(usernameRaw, passwordRaw));
            this.communicator.sendMessage(message);

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            return success;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(success -> {
                if (!success) {
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
        this.communicator.connectionStateProperty()
            .addListener((observable, oldValue, newValue) -> {
                switch (newValue) {
                    case CONNECTED:
                        this.communicator.registerMessageObserver(AuthMessage.MESSAGE_TYPE, this.authListener);
                        break;
                    case CONNECTING:
                        break;
                    case DISCONNECTED:
                        this.communicator.unregisterMessageObserver(AuthMessage.MESSAGE_TYPE, this.authListener);
                        break;
                }
            });
    }

    // endregion
}
