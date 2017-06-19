package cz.stechy.drd.controller.main;

import cz.stechy.drd.controller.IScreenSupport;
import cz.stechy.drd.model.entity.hero.Hero;
import javafx.beans.property.ObjectProperty;

/**
 * Rozhraní pro komunikaci s kontrolery z třídy {@link MainController}
 */
public interface MainScreen {

    /**
     * Nastaví referenci na hrdinu
     *
     * @param hero {@link Hero}
     */
    void setHero(ObjectProperty<Hero> hero);

    void setScreenSupport(IScreenSupport screenSupport);

}
