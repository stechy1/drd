package cz.stechy.drd.service.hero;

import static cz.stechy.drd.db.BaseOfflineTable.ID_FILTER;

import com.google.inject.Inject;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.annotation.Service;
import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.db.base.ITableFactory;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import cz.stechy.drd.service.inventory.IInventoryService;
import cz.stechy.drd.service.inventory.InventoryService;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * Služba pro přístup ke správě hrdinů
 */
@Service
public class HeroService implements IHeroService {

    // region Variables

    private final ObjectProperty<Hero> hero = new SimpleObjectProperty<>(this, "hero", null);
    private final ITableDefinitionsFactory tableDefinitionsFactory;
    private final BaseOfflineTable<Hero> heroTable;
    private final Database db;

    private IInventoryService inventoryService;

    // endregion

    // region Constructors

    @Inject
    public HeroService(ITableDefinitionsFactory tableDefinitionsFactory, ITableFactory tableFactory, Database db) {
        this.tableDefinitionsFactory = tableDefinitionsFactory;
        this.db = db;
        this.heroTable = tableFactory.getOfflineTable(Hero.class);
    }

    // endregion

    // region Public methods

    // region CRUD operations

    @Override
    public CompletableFuture<ObservableList<Hero>> getAll() {
        return heroTable.selectAllAsync();
    }

    @Override
    public CompletableFuture<Void> insertWithItems(Hero hero, ObservableList<InventoryHelper.ItemRecord> items) {
        return CompletableFuture.completedFuture(null);
//        return heroTable.insertAsync(hero)
//            .thenCompose(hero1 ->
//                heroTable.getInventoryAsync(hero1)
//                    .thenCompose(inventoryService ->
//                        InventoryHelper.insertItemsToInventoryAsync(inventoryService, items)));
    }

    @Override
    public CompletableFuture<Hero> updateAsync(Hero hero) {
        return heroTable.updateAsync(hero);
    }

    @Override
    public CompletableFuture<Hero> deleteAsync(Hero hero) {
        return heroTable.deleteAsync(hero);
    }

    // endregion

    @Override
    public synchronized Optional<IInventoryService> getInventoryService() {
        if (getHero() == null) {
            return Optional.empty();
        }

        if (inventoryService == null) {
            inventoryService = new InventoryService(tableDefinitionsFactory, db, getHero());
        }

        return Optional.of(inventoryService);
    }

    @Override
    public CompletableFuture<Hero> loadAsync(String heroId) {
        return heroTable.selectAsync(ID_FILTER(heroId)).thenApplyAsync(hero -> {
            this.hero.setValue(hero);

            return hero;
        }, ThreadPool.JAVAFX_EXECUTOR);
    }
//        return CompletableFuture.supplyAsync(() -> {
//            final Hero hero = heroTable.selectAsync(ID_FILTER(heroId)).orElse(null);
//            this.hero.setValue(hero);
//
//            return hero;
//        }, ThreadPool.JAVAFX_EXECUTOR);
//    }

    public synchronized void resetHero() {
        hero.setValue(null);

        inventoryService = null;
        //heroTable.resetInventory();
    }

    // endregion

    // region Getters & Setters

    @Override
    public final ReadOnlyObjectProperty<Hero> heroProperty() {
        return hero;
    }

    @Override
    public final Hero getHero() {
        return hero.get();
    }

    // endregion

}
