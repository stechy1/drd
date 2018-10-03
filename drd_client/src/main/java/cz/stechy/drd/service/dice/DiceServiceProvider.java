package cz.stechy.drd.service.dice;

import cz.stechy.drd.service.hero.IHeroService;
import javax.inject.Inject;
import javax.inject.Provider;

public class DiceServiceProvider implements Provider<IDiceService> {

    private final IHeroService heroService;

    @Inject
    public DiceServiceProvider(IHeroService heroService) {
        this.heroService = heroService;
    }

    @Override
    public IDiceService get() {
        return new DiceService(heroService.getHero());
    }
}
