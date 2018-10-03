package cz.stechy.drd.service.dice;

import cz.stechy.drd.model.entity.hero.Hero;

public class DiceServiceFactory implements IDiceServiceFactory {

    @Override
    public IDiceService getService(Hero hero) {
        return new DiceService(hero);
    }
}
