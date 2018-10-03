package cz.stechy.drd.service.hero;

import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import cz.stechy.drd.service.inventory.IInventoryService;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public interface IHeroService {

    /**
     * Vrátí všechny uživatele
     *
     * @return {@link CompletableFuture}
     */
    CompletableFuture<ObservableList<Hero>> getAll();

    /**
     * Vloží nového hrdinu do databáze spolu s jeho výchozími předměty
     *
     * @param hero {@link Hero} Hrdina, který se má vložit do databáze
     * @param items {@link ObservableList < InventoryHelper.ItemRecord>} Výchozí seznam předmětů,
     * který hrdina u sebe bude mít od začátku
     * @return {@link CompletableFuture <Void>}
     */
    CompletableFuture<Void> insertWithItems(Hero hero, ObservableList<InventoryHelper.ItemRecord> items);

    /**
     * Načte hrdinu podle ID
     *
     * @param heroId ID hrdiny, který se má načíst
     * @return {@link CompletableFuture<Hero>}
     */
    CompletableFuture<Hero> loadAsync(String heroId);

    /**
     * Aktualizuje údaje o hrdinovi
     *
     * @param hero {@link Hero} Hrdina s aktualizovanými údaji
     * @return {@link CompletableFuture}
     */
    CompletableFuture<Hero> updateAsync(Hero hero);

    /**
     * Odstraní hrdinu
     *
     * @param hero {@link Hero}
     * @return {@link CompletableFuture}
     */
    CompletableFuture<Hero> deleteAsync(Hero hero);

//    /**
//     * Vrátí inventář aktuálně otevřeného hrdiny
//     *
//     * @return {@link CompletableFuture<InventoryDao>}
//     */
//    CompletableFuture<InventoryDao> getInventoryAsync();

    /**
     * Vrátí inventář, který vlastní aktuálně otevřený hrdina, nebo vyhodí vyjímku
     *
     * @return {@link IInventoryService} Servisu, která se stará o inventář zadaného hrdiny
     */
    Optional<IInventoryService> getInventoryService();

    /**
     * Vyresetuje aktuálně používaného hrdinu
     */
    void resetHero();

    ReadOnlyObjectProperty<Hero> heroProperty();

    /**
     * Vrátí referenci na {@link Hero}
     *
     * @return {@link Hero}
     */
    Hero getHero();
}
