package cz.stechy.drd.controller.main;

import cz.stechy.drd.model.entity.hero.Hero;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Rozhraní pro komunikaci s kontrolery z třídy {@link MainController}
 */
interface MainScreen {

    /**
     * Nastaví referenci na hrdinu
     *
     * @param hero {@link Hero}
     */
    void setHero(ReadOnlyObjectProperty<Hero> hero);

}
