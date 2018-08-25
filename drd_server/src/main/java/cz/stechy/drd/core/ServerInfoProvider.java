package cz.stechy.drd.core;

import cz.stechy.drd.net.message.ServerStatusMessage;

public interface ServerInfoProvider {
    ServerStatusMessage getServerStatusMessage();
}
