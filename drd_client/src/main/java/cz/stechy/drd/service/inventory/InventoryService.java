package cz.stechy.drd.service.inventory;

import cz.stechy.drd.db.BaseOfflineTable;
import cz.stechy.drd.db.base.Database;
import cz.stechy.drd.db.base.ITableDefinitionsFactory;
import cz.stechy.drd.db.table.invnetory.InventoryOfflineTable;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryService implements IInventoryService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    public static final Predicate<Inventory> MAIN_INVENTORY_FILTER = inventory -> inventory.getInventoryType() == InventoryType.MAIN;
    public static final Predicate<Inventory> EQUIP_INVENTORY_FILTER = inventory -> inventory.getInventoryType() == InventoryType.EQUIP;

    // endregion

    // region Variables

    private final Map<Inventory, IInventoryContentService> inventoryContents = new HashMap<>();
    private final BaseOfflineTable<Inventory> inventoryTable;
    private final ITableDefinitionsFactory tableDefinitionsFactory;
    private final Database db;
    private final Hero hero;

    private ObservableList<Inventory> inventories;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou service, která bude spravovat inventář konkrétního hrdiny
     *
     * @param tableDefinitionsFactory {@link ITableDefinitionsFactory} Továrna na definice tabulky
     * @param hero {@link Hero} Hrdina, pro kterého je service určena
     */
    public InventoryService(ITableDefinitionsFactory tableDefinitionsFactory, Database db, Hero hero) {
        this.inventoryTable = new InventoryOfflineTable(tableDefinitionsFactory.getTableDefinitions(Inventory.class), db);
        this.tableDefinitionsFactory = tableDefinitionsFactory;
        this.db = db;
        this.hero = hero;

        // Načtení inventářů zadaného hrdiny
        this.inventories = this.inventoryTable.selectAllAsync(hero.getId()).join();
    }

    // endregion

    // region Public methods

    @Override
    public Optional<Inventory> getInventory(Predicate<Inventory> filter) {
        return inventories.stream().filter(filter).findFirst();
    }

//    @Override
//    public CompletableFuture<Inventory> insertInventory(Inventory inventory) {
//        return inventoryTable.insertAsync(inventory);
//    }

    @Override
    public CompletableFuture<Inventory> updateInventory(Inventory inventory) {
        return inventoryTable.updateAsync(inventory);
    }

    @Override
    public CompletableFuture<Inventory> deleteInventory(Inventory inventory) {
        return inventoryTable.deleteAsync(inventory);
    }

    @Override
    public IInventoryContentService getInventoryContentService(Inventory inventory) {
        return inventoryContents.putIfAbsent(inventory, new InventoryContentService(tableDefinitionsFactory, db, inventory));
    }

    @Override
    public CompletableFuture<Inventory> initSubInventoryAsync(final int capacity) {
        Inventory subInventory = new Inventory.Builder()
            .heroId(hero.getId())
            .inventoryType(InventoryType.BACKPACK)
            .capacity(capacity)
            .build();
        return inventoryTable.insertAsync(subInventory);
    }

    // endregion
}
