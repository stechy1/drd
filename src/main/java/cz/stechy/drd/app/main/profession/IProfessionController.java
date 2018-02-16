package cz.stechy.drd.app.main.profession;

import cz.stechy.drd.model.entity.hero.Hero;

/**
 * Pomocné značkovací rozhraní pro identifikaci kontroleru, obsluhující schopnosti dle profese hrdiny
 */
interface IProfessionController {

    /**
     * Injektuje hrdinu do kontroleru s profesí
     *
     * @param hero {@link Hero}
     */
    void setHero(Hero hero);

}
