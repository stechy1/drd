package cz.stechy.drd.plugins.auth.service;

import com.google.inject.ImplementedBy;
import cz.stechy.drd.plugins.auth.User;
import java.util.Optional;

@ImplementedBy(AuthService.class)
public interface IAuthService {

    String FIREBASE_CHILD = "users";

    String COLUMN_ID = "id";
    String COLUMN_NAME = "name";
    String COLUMN_PASSWORD = "password";

    void init();

    Optional<User> register(byte[] usernameRaw, byte[] passwordRaw);

    Optional<User> login(byte[] usernameRaw, byte[] passwordRaw);

}
