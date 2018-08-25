package cz.stechy.drd.plugins.auth;

import cz.stechy.drd.util.HashGenerator;
import java.util.Objects;

public final class User {
    public final String id;
    public final String name;
    public final String password;

    public User(String name, String password) {
        this(HashGenerator.createHash(), name, HashGenerator.createHash(password));
    }

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
            Objects.equals(name, user.name) &&
            Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, password);
    }
}