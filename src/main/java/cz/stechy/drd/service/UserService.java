package cz.stechy.drd.service;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.dao.UserDao;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.User;
import cz.stechy.drd.util.HashGenerator;
import java.util.Optional;
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
    private final UserDao userDao;

    // endregion

    // region Constructors

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

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
            final Optional<User> result = userDao.getUsers().stream()
                .filter(user -> user.getName().equals(username) && HashGenerator
                    .checkSame(user.getPassword(), password))
                .findFirst();
            if (!result.isPresent()) {
                throw new RuntimeException();
            }

            return result.get();
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
    public void registerAsync(String username, String password, CompletionListener listener) {
        final Optional<User> result = userDao.getUsers().stream()
            .filter(user -> user.getName().equals(username))
            .findFirst();
        if (result.isPresent()) {
            listener.onComplete(DatabaseError.fromCode(DatabaseError.USER_CODE_EXCEPTION), null);
        }

        final User user = new User(username, password);
        userDao.uploadAsync(user, listener);
    }

    // endregion

    // region Gettsrs & Setters

    public final ReadOnlyObjectProperty<User> userProperty() {
        return user;
    }

    public final User getUser() {
        return user.get();
    }

    // endregion
}
