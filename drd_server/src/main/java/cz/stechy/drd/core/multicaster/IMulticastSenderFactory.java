package cz.stechy.drd.core.multicaster;

import cz.stechy.drd.core.ServerInfoProvider;

public interface IMulticastSenderFactory {

    IMulticastSender getMulticastSender(ServerInfoProvider infoProvider);

}
