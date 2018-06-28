package cz.stechy.drd.module;

import cz.stechy.drd.Client;
import cz.stechy.drd.auth.AuthService;
import cz.stechy.drd.net.message.AuthMessage;
import cz.stechy.drd.net.message.AuthMessage.AuthAction;
import cz.stechy.drd.net.message.AuthMessage.AuthMessageData;
import cz.stechy.drd.net.message.IMessage;

public class AuthModule implements IModule {

    // region Variables

    private final AuthService authService;

    // endregion

    // region Constructors

    public AuthModule(AuthService authService) {
        this.authService = authService;
    }

    // endregion

    @Override
    public void init() {
        authService.init();
    }

    @Override
    public void handleMessage(IMessage message, Client client) {
        final AuthMessage authMessage = (AuthMessage) message;
        final AuthMessageData data = (AuthMessageData) message.getData();
        final AuthAction action = authMessage.getAction();
        switch (action) {
            case REGISTER:
                authService.register(data.name, data.password, client);
                break;
            case LOGIN:
                authService.login(data.name, data.password, client);
                break;
            case LOGOUT:
                break;
            default:
                throw new RuntimeException("Neplatný parametr");
        }
    }

}
