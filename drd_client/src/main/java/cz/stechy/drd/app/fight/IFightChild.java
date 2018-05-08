package cz.stechy.drd.app.fight;

import cz.stechy.drd.model.entity.hero.Hero;

/**
 * Rozhraní pro vložené kontrolery
 */
interface IFightChild {

    /**
     * Nastaví hrdinu do kontroleru
     *
     * @param hero {@link Hero}
     */
    void setHero(Hero hero);
}
