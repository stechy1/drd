package cz.stechy.drd.old;

import cz.stechy.drd.net.message.ServerStatusMessage;

@FunctionalInterface
public interface ServerInfoProvider {
    ServerStatusMessage getServerStatusMessage();
}
