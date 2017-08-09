package cz.stechy.drd.controller.fight;

import cz.stechy.drd.model.entity.hero.Hero;

/**
 * Rozhraní pro vložené kontrolery
 */
public interface IFightChild {

    /**
     * Nastaví hrdinu do kontroleru
     *
     * @param hero {@link Hero}
     */
    void setHero(Hero hero);
}
