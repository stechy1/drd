package cz.stechy.drd.service;

import static cz.stechy.drd.db.BaseDatabaseService.ID_FILTER;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.dao.HeroDao;
import cz.stechy.drd.dao.InventoryDao;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * Služba pro přístup ke správě hrdinů
 */
@Singleton
public class HeroService {

    // region Variables

    private final ObjectProperty<Hero> hero = new SimpleObjectProperty<>(this, "hero", null);
    private final HeroDao heroDao;

    // endregion

    // region Constructors

    public HeroService(HeroDao heroDao) {
        this.heroDao = heroDao;
    }

    // endregion

    // region Public methods

    // region CRUD operations

    /**
     * Vrátí všechny uživatele
     *
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<ObservableList<Hero>> getAll() {
        return heroDao.selectAllAsync();
    }

    /**
     * Vloží nového hrdinu do databáze spolu s jeho výchozími předměty
     *
     * @param hero {@link Hero} Hrdina, který se má vložit do databáze
     * @param items {@link ObservableList < InventoryHelper.ItemRecord>} Výchozí seznam předmětů,
     * který hrdina u sebe bude mít od začátku
     * @return {@link CompletableFuture <Void>}
     */
    public CompletableFuture<Void> insertWithItems(Hero hero,
        ObservableList<InventoryHelper.ItemRecord> items) {
        return heroDao.insertAsync(hero)
            .thenCompose(hero1 ->
                heroDao.getInventoryAsync(hero1)
                    .thenCompose(inventoryService ->
                        InventoryHelper.insertItemsToInventoryAsync(inventoryService, items)));
    }

    /**
     * Aktualizuje údaje o hrdinovi
     *
     * @param hero {@link Hero} Hrdina s aktualizovanými údaji
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Hero> updateAsync(Hero hero) {
        return heroDao.updateAsync(hero);
    }

    /**
     * Odstraní hrdinu
     *
     * @param hero {@link Hero}
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Hero> deleteAsync(Hero hero) {
        return heroDao.deleteAsync(hero);
    }

    // endregion

    /**
     * Vrátí inventář aktuálně otevřeného hrdiny
     *
     * @return {@link CompletableFuture<InventoryDao>}
     */
    public CompletableFuture<InventoryDao> getInventoryAsync() {
        return heroDao.getInventoryAsync(getHero());
    }

    /**
     * Načte hrdinu podle ID
     *
     * @param heroId ID hrdiny, který se má načíst
     * @return {@link CompletableFuture<Hero>}
     */
    public CompletableFuture<Hero> loadAsync(String heroId) {
        return heroDao.selectAsync(ID_FILTER(heroId))
            .thenApplyAsync(hero -> {
                this.hero.setValue(hero);
                return hero;
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    public void resetHero() {
        hero.setValue(null);
        heroDao.resetInventory();
    }

    // endregion

    // region Getters & Setters

    public final ReadOnlyObjectProperty<Hero> heroProperty() {
        return hero;
    }

    public final Hero getHero() {
        return hero.get();
    }

    // endregion

}
