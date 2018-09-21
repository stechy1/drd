package cz.stechy.drd.model;

import cz.stechy.drd.util.HashGenerator;
import java.util.regex.Pattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída představuje jednho uživatele
 */
public class User {

    // region Constants

    private static final Pattern LOGIN_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]{0,31}");

    // endregion

    // region Variables

    // ID uživatele
    private final StringProperty id = new SimpleStringProperty();
    // Jméno uživatele
    private final StringProperty name = new SimpleStringProperty();
    // Hash hesla uživatele
    private final StringProperty password = new SimpleStringProperty();
    // Přiznak určující, zda-li je tento uživatel přihlášení v aplikaci, či nikoliv
    private final BooleanProperty logged = new SimpleBooleanProperty();

    // endregion

    // region Constructors

    /**
     * Vytvoří nového uživatele bez hesla se jménem podle přihlášeného uživatele k systému
     */
    public User() {
        this(System.getProperty("user.name") + "-" + System.currentTimeMillis(), "");
    }

    /**
     * Vytvoří nového uživatele
     *
     * @param name Uživatelské jméno
     * @param password Surové heslo uživatele
     */
    public User(String name, String password) {
        this(HashGenerator.createHash(), name, HashGenerator.createHash(password));
    }

    /**
     * Vytvoří nového uživatele
     *
     * @param id Id uživatele
     * @param name Uživatelské jméno
     * @param password Hash hesla uživatele
     */
    public User(String id, String name, String password) {
        this.id.set(id);
        this.name.set(name);
        this.password.set(password);
    }

    // endregion

    // region Public static methods

    /**
     * Zvaliduje jméno uživatele
     *
     * @param name Jméno uživatele
     * @return True, pokud je jméno validní, jinak false
     */
    public static boolean isNameValid(String name) {
        return LOGIN_PATTERN.matcher(name).matches();
    }

    // endregion

    // region Public methods

    public void set(User other) {
        this.id.set(other.getId());
        this.name.set(other.getName());
        this.password.set(other.getPassword());
    }

    // endregion

    // region Getters & Setters

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(HashGenerator.createHash(password));
    }

    public boolean isLogged() {
        return logged.get();
    }

    public BooleanProperty loggedProperty() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged.set(logged);
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    public static class Builder {

        private String id;
        private String name;
        private String password;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            return new User(id, name, password);
        }
    }
}
