package cz.stechy.drd.service.dice;

import cz.stechy.drd.model.entity.hero.Hero;

public interface IDiceServiceFactory {

    IDiceService getService(Hero hero);

}
