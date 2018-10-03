package cz.stechy.drd.service.user;

import cz.stechy.drd.model.User;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface IUserService {

    /**
     * Zaregistruje uživatele
     *
     * @param username Uživatelské jméno
     * @param password Uživatelské heslo
     * @return {@link CompletableFuture<Void>} Budoucnost s výsledkem registrace
     */
    CompletableFuture<Void> registerAsync(String username, String password);

    /**
     * Přihlásí uživatele
     *
     * @param username Uživatelské jméno
     * @param password Uživatelské heslo
     * @return {@link CompletableFuture<Void>} Budoucnost s výsledkem přihlášení
     */
    CompletableFuture<User> loginAsync(String username, String password);

    /**
     * Odhláší uživatele
     *
     * @return {@link CompletableFuture<Void>} Budoucnost s výsledkem odhlášení
     */
    CompletableFuture<Void> logoutAsync();

    /**
     * Vrátí pozorovatelnou referenci na uživatele pouze pro čtení
     *
     * @return {@link ReadOnlyObjectProperty<User>}
     */
    ReadOnlyObjectProperty<User> userProperty();

    /**
     * Vrátí referenci na uživatele
     *
     * @return {@link User}
     */
    User getUser();
}
