package cz.stechy.drd.plugins.auth;

import com.google.inject.Inject;
import cz.stechy.drd.core.connection.IClient;
import cz.stechy.drd.core.connection.MessageReceivedEvent;
import cz.stechy.drd.core.event.IEvent;
import cz.stechy.drd.core.event.IEventBus;
import cz.stechy.drd.net.message.AuthMessage;
import cz.stechy.drd.net.message.AuthMessage.AuthAction;
import cz.stechy.drd.net.message.AuthMessage.AuthMessageData;
import cz.stechy.drd.net.message.MessageSource;
import cz.stechy.drd.plugins.IPlugin;
import cz.stechy.drd.plugins.auth.service.IAuthService;
import java.util.Optional;

public class AuthPlugin implements IPlugin {

    // region Constants

    public static final String PLUGIN_NAME = "auth";

    // endregion

    // region Variables

    private final IAuthService authService;

    // endregion

    // region Constructors

    @Inject
    public AuthPlugin(IAuthService authService) {
        this.authService = authService;
    }

    // endregion

    // region Private methods

    private void callRegister(byte[] usernameRaw, byte[] passwordRaw, IClient client) {
        final Optional<User> userOptional = authService.register(usernameRaw, passwordRaw);
        final boolean success = userOptional.isPresent();
        final String id = success ? userOptional.get().id : "";
        client.sendMessageAsync(new AuthMessage(MessageSource.SERVER, AuthAction.REGISTER,
            success, new AuthMessageData(id)));
    }

    private void callLogin(byte[] usernameRaw, byte[] passwordRaw, IClient client) {
        final Optional<User> userOptional = authService.login(usernameRaw, passwordRaw);
        final boolean success = userOptional.isPresent();
        final String id = success ? userOptional.get().id : "";
        client.sendMessageAsync(new AuthMessage(MessageSource.SERVER, AuthAction.LOGIN,
            success, new AuthMessageData(id)));
    }

    private void authMessageHandler(IEvent event) {
        assert event instanceof MessageReceivedEvent;
        final MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
        final AuthMessage authMessage = (AuthMessage) messageReceivedEvent.getReceivedMessage();
        final AuthMessageData data = (AuthMessageData) authMessage.getData();

        switch (authMessage.getAction()) {
            case REGISTER:
                callRegister(data.name, data.password, messageReceivedEvent.getClient());
                break;
            case LOGIN:
                callLogin(data.name, data.password, messageReceivedEvent.getClient());
                break;
            case LOGOUT:
                break;
            default:
                throw new RuntimeException("Neplatný parametr");
        }
    }

    // endregion

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() {
        authService.init();
    }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) {
        eventBus.registerEventHandler(AuthMessage.MESSAGE_TYPE, this::authMessageHandler);
    }

}
