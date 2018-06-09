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
import cz.stechy.drd.net.message.MessageType;
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
    private ClientCommunicator communicator;
    private String tmpId;

    // endregion

    // region Constructors

    public UserService() {

    }

    // endregion

    // region Private methods

    private final OnDataReceivedListener authListener = message -> {
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
            final IMessage loginMessage = new AuthMessage(MessageSource.CLIENT, AuthAction.LOGIN,
                new AuthMessageData(username, password));
            this.communicator.sendMessage(loginMessage);

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            if ("".equals(tmpId)) {
                throw new RuntimeException("Uživatel nenalezen");
            }

            final User u = new User(username, password);
            u.setId(tmpId);
            tmpId = null;
            return u;
//            final Optional<User> result = userDao.getUsers().stream()
//                .filter(user -> user.getName().equals(username) && HashGenerator
//                    .checkSame(user.getPassword(), password))
//                .findFirst();
//            if (!result.isPresent()) {
//                throw new RuntimeException();
//            }
//
//            return result.get();
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
            final IMessage message = new AuthMessage(MessageSource.CLIENT, AuthAction.REGISTER,
                new AuthMessageData(username, password));
            this.communicator.sendMessage(message);

            try {
                semaphore.acquire();
            } catch (InterruptedException ignored) {}

            return null;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(ignored -> {
                return null;
            }, ThreadPool.JAVAFX_EXECUTOR);
//        final Optional<User> result = userDao.getUsers().stream()
//            .filter(user -> user.getName().equals(username))
//            .findFirst();
//        if (result.isPresent()) {
//            //listener.onComplete(DatabaseError.fromCode(DatabaseError.USER_CODE_EXCEPTION), null);
//        }
//
//        final User user = new User(username, password);
//        userDao.uploadAsync(user);
    }

    // endregion

    // region Gettsrs & Setters

    public final ReadOnlyObjectProperty<User> userProperty() {
        return user;
    }

    public final User getUser() {
        return user.get();
    }

    @Inject
    public void setCommunicator(ClientCommunicator communicator) {
        this.communicator = communicator;
        this.communicator.connectionStateProperty()
            .addListener((observable, oldValue, newValue) -> {
                switch (newValue) {
                    case CONNECTED:
                        this.communicator.registerMessageObserver(MessageType.AUTH, this.authListener);
                        break;
                    case CONNECTING:
                        break;
                    case DISCONNECTED:
                        this.communicator.unregisterMessageObserver(MessageType.AUTH, this.authListener);
                        break;
                }
            });
    }

    // endregion
}
