package cz.stechy.drd.plugins.auth.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.drd.plugins.auth.User;
import cz.stechy.drd.plugins.crypto.service.ICryptoService;
import cz.stechy.drd.plugins.firebase.FirebaseEntryEventListener;
import cz.stechy.drd.plugins.firebase.service.IFirebaseService;
import cz.stechy.drd.util.HashGenerator;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class AuthService implements IAuthService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    // endregion

    // region Variables

    private final List<User> users = new ArrayList<>();

    private final IFirebaseService firebaseService;
    private final ICryptoService cryptoService;
    private boolean initialized = false;

    // endregion

    // region Constructors

    @Inject
    public AuthService(IFirebaseService firebaseService, ICryptoService cryptoService) {
        this.firebaseService = firebaseService;
        this.cryptoService = cryptoService;
    }

    // endregion

    // region Private methods

    /**
     * Konvertuje {@link Map} na instanci třídy {@link User}
     *
     * @param map {@link Map}
     * @return {@link User}
     */
    private User mapToUser(Map<String, Object> map) {
        return new User(
            (String) map.get(COLUMN_ID),
            (String) map.get(COLUMN_NAME),
            (String) map.get(COLUMN_PASSWORD)
        );
    }

    /**
     * Konvertuje instanci třídy {@link User} na {@link Map}
     *
     * @param user {@link User}
     * @return {@link Map}
     */
    private Map<String, Object> userToMap(User user) {
        final Map<String, Object> map = new HashMap<>();
        map.put(COLUMN_ID, user.id);
        map.put(COLUMN_NAME, user.name);
        map.put(COLUMN_PASSWORD, user.password);
        return map;
    }

    // endregion

    @Override
    public void init() {
        if (initialized) {
            LOGGER.warn("Autentizační služba již byla inicializována.");
            return;
        }

        firebaseService.registerListener(FIREBASE_CHILD, this.userListener);
        initialized = true;
    }

    @Override
    public Optional<User> register(byte[] usernameRaw, byte[] passwordRaw) {
        final String username = new String(cryptoService.decrypt(usernameRaw), StandardCharsets.UTF_8);
        final String password = new String(cryptoService.decrypt(passwordRaw), StandardCharsets.UTF_8);
        final Optional<User> result = users.stream().filter(user -> user.name.equals(username)).findFirst();

        if (result.isPresent()) {
            return Optional.empty();
        }

        final User user = new User(username, password);
        final Map<String, Object> map = userToMap(user);
        this.firebaseService.performInsert(FIREBASE_CHILD, map, user.id);
        users.add(user);

        return Optional.of(user);
//        if (result.isPresent()) {
//            client.sendMessage(new AuthMessage(MessageSource.SERVER, AuthAction.REGISTER,
//                false, new AuthMessageData()));
//            return;
//        }
//
//        final User user = new User(username, password);
//        final Map<String, Object> map = userToMap(user);
//        this.repository.performInsert(FIREBASE_CHILD, map, user.id);
//        client.sendMessage(new AuthMessage(MessageSource.SERVER, AuthAction.REGISTER,
//            true, new AuthMessageData(user.id, usernameRaw, passwordRaw)));
//        return false;
    }

    @Override
    public Optional<User> login(byte[] usernameRaw, byte[] passwordRaw) {
        final String username = new String(cryptoService.decrypt(usernameRaw), StandardCharsets.UTF_8);
        final String password = new String(cryptoService.decrypt(passwordRaw), StandardCharsets.UTF_8);
        return users.stream().filter(user ->
            user.name.equals(username) && HashGenerator.checkSame(user.password, password))
            .findFirst();

//        if (!result.isPresent()) {
//            client.sendMessage(new AuthMessage(MessageSource.SERVER, AuthAction.LOGIN,
//                false, new AuthMessageData()));
//            return;
//        }
//        client.sendMessage(new AuthMessage(MessageSource.SERVER, AuthAction.LOGIN,
//            true, new AuthMessageData(result.get().id, usernameRaw, passwordRaw)));
//        return result;
    }

    private final FirebaseEntryEventListener userListener = event -> {

    };


}
