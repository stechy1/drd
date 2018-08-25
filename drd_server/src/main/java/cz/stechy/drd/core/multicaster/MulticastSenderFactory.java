package cz.stechy.drd.core.multicaster;

import cz.stechy.drd.core.ServerInfoProvider;

public class MulticastSenderFactory implements IMulticastSenderFactory {

    @Override
    public IMulticastSender getMulticastSender(ServerInfoProvider infoProvider) {
        return new MulticastSender(infoProvider);
    }
}
