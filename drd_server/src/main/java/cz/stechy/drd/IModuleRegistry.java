package cz.stechy.drd;

import cz.stechy.drd.module.IModule;
import cz.stechy.drd.net.message.MessageType;

public interface IModuleRegistry {

    /**
     * Zaregistruje nový modul pro obsluhu zpráv
     *
     * @param messageType {@link MessageType} Typ zprávy, který bude daný modul zpracovávat
     * @param module {@link IModule} Obsluha pro zpracování zprávy
     */
    void registerModule(MessageType messageType, IModule module);

}
