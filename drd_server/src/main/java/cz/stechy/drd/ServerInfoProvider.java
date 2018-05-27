package cz.stechy.drd;

import cz.stechy.drd.net.message.ServerStatusMessage;

@FunctionalInterface
public interface ServerInfoProvider {
    ServerStatusMessage getServerStatusMessage();
}
