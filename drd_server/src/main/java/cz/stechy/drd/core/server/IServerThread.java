package cz.stechy.drd.core.server;

import cz.stechy.drd.core.IThreadControl;
import cz.stechy.drd.core.ServerInfoProvider;

/**
 * Značkovací rozhraní pro hlavní vlákno serveru
 */
public interface IServerThread extends IThreadControl, ServerInfoProvider {

}
