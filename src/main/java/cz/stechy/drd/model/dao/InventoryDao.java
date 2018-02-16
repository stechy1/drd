package cz.stechy.drd.model.dao;

import cz.stechy.drd.model.db.BaseDatabaseService;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryRecord;
import cz.stechy.drd.model.inventory.InventoryType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Služba spravující CRUD operace nad třídou {@link Inventory}
 */
public final class InventoryDao extends BaseDatabaseService<Inventory> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryDao.class);

    // Název tabulky
    private static final String TABLE = "inventory";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_HERO_ID = TABLE + "_hero_id";
    private static final String COLUMN_INVENTORY_TYPE = TABLE + "_inventory_type";
    private static final String COLUMN_CAPACITY = TABLE + "_capacity";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_HERO_ID,
        COLUMN_INVENTORY_TYPE, COLUMN_CAPACITY};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
        + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"     // id
        + "%s VARCHAR(255) NOT NULL,"                        // hero id
        + "%s INT NOT NULL,"                                 // inventory type
        + "%s INT NOT NULL"                                  // capacity
        + ");", TABLE, COLUMN_ID, COLUMN_HERO_ID, COLUMN_INVENTORY_TYPE, COLUMN_CAPACITY);

    public static final Predicate<? super Inventory> MAIN_INVENTORY_FILTER = inventory ->
        inventory.getInventoryType() == InventoryType.MAIN;
    public static final Predicate<? super Inventory> EQUIP_INVENTORY_FILTER = inventory ->
        inventory.getInventoryType() == InventoryType.EQUIP;
    // endregion

    // region Variables

    private static boolean tableInitialized = false;
    // Hrdina
    private final Hero hero;
    // Mapa referencí na správce obsahů jednotlivých inventářů
    private final Map<Inventory, InventoryContentDao> inventoryContentMap = new HashMap<>();

    // endregion

    // region Constructors

    /**
     * Inicializuje správce inventářů pro vybraného hrdinu
     *
     * @param db {@link Database}
     * @param hero {@link Hero} Hrdina, pro kterého se má vybrat inventář
     */
    public InventoryDao(Database db, Hero hero) {
        super(db);

        this.hero = hero;

        createTableAsync().join();
//        try {
//            createTable();
//        } catch (DatabaseException e) {
//            e.printStackTrace();
//        }
    }

    // endregion

    // region Public static methods

    /**
     * Vytvoří standartní inventář pro zadaného hrdinu
     *
     * @param hero {@link Hero}
     * @return {@link Inventory}
     */
    public static Inventory standartInventory(Hero hero) {
        return new Inventory.Builder().heroId(hero.getId()).capacity(20)
            .inventoryType(InventoryType.MAIN.ordinal()).build();
    }

    private static Predicate<? super Inventory> SIMPLE_FILTER(final Inventory inventory) {
        return inventory::equals;
    }

    private static Predicate<? super Inventory> ID_FILTER(final Inventory inventory) {
        return i -> inventory.getId().equals(i.getId());
    }

    // endregion

    // region Private methods

    @Override
    protected Inventory parseResultSet(ResultSet resultSet) throws SQLException {
        return new Inventory.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .heroId(resultSet.getString(COLUMN_HERO_ID))
            .inventoryType(resultSet.getInt(COLUMN_INVENTORY_TYPE))
            .capacity(resultSet.getInt(COLUMN_CAPACITY))
            .build();
    }

    @Override
    protected List<Object> itemToParams(Inventory inventory) {
        return new ArrayList<>(Arrays.asList(
            inventory.getId(),
            inventory.getHeroId(),
            inventory.getInventoryType().ordinal(),
            inventory.getCapacity()
        ));
    }

    @Override
    protected String getTable() {
        return TABLE;
    }

    @Override
    protected String getColumnWithId() {
        return COLUMN_ID;
    }

    @Override
    protected String getColumnsKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    protected String getColumnValues() {
        return COLUMNS_VALUES;
    }

    @Override
    protected String getColumnsUpdate() {
        return COLUMNS_UPDATE;
    }

    @Override
    protected String getInitializationQuery() {
        return QUERY_CREATE;
    }

    @Override
    protected String getQuerySelectAll() {
        return String.format("SELECT * FROM %s WHERE %s=?", getTable(), COLUMN_HERO_ID);
    }

    @Override
    protected Object[] getParamsForSelectAll() {
        return new Object[]{hero.getId()};
    }

    // endregion

    // region Public methods

    @Override
    public CompletableFuture<Void> createTableAsync() {
        if (tableInitialized) {
            return CompletableFuture.completedFuture(null);
        }

        return super.createTableAsync()
            .thenAccept(ignore -> tableInitialized = true);
    }
    @Override
    public CompletableFuture<Inventory> deleteAsync(Inventory item) {
        return getInventoryContentByIdAsync(item.getId())
            .thenCompose(inventoryContent ->
                inventoryContent.selectAllAsync().thenCompose(inventoryRecords -> {
                    List<CompletableFuture<InventoryRecord>> futureList = new ArrayList<>(
                        inventoryRecords.size());
                    inventoryRecords.forEach(inventoryRecord ->
                        futureList.add(inventoryContent.deleteAsync(inventoryRecord)));
                    return CompletableFuture
                        .allOf(futureList.toArray(new CompletableFuture[futureList.size()]))
                        .thenCompose(aVoid -> super.deleteAsync(item));
                }));
    }

    public CompletableFuture<InventoryContentDao> getInventoryContentByIdAsync(
        final String inventoryId) {
        final Inventory inventory = new Inventory.Builder().id(inventoryId).build();
        return getInventoryContentAsync(inventory, ID_FILTER(inventory));
    }

    public CompletableFuture<InventoryContentDao> getInventoryContentAsync(final Inventory inventory) {
        return getInventoryContentAsync(inventory, SIMPLE_FILTER(inventory));
    }

    /**
     * Vytvoří přistup k obsahu inventáře podle zadaného filtru
     *
     * @param inventory {@link Inventory} Inventář pro který se hledá obsah
     * @param filter Filter, podle kterého probíhá hledání
     * @return {@link CompletableFuture< InventoryContentDao >}
     */
    private CompletableFuture<InventoryContentDao> getInventoryContentAsync(final Inventory inventory,
        final Predicate<? super Inventory> filter) {
        final Optional<Inventory> result = inventoryContentMap.keySet()
            .stream()
            .filter(Predicate.isEqual(inventory))
            .findFirst();

        if (result.isPresent()) {
            final InventoryContentDao inventoryContentDao = inventoryContentMap.get(result.get());
            return CompletableFuture.completedFuture(inventoryContentDao);
        }

        return selectAsync(filter)
            .thenCompose(inventoryResult -> {
                final InventoryContentDao inventoryContentDao = new InventoryContentDao(db, inventoryResult);
                inventoryContentMap.put(inventoryResult, inventoryContentDao);
                return inventoryContentDao.selectAllAsync()
                    .thenApply(inventoryRecords -> inventoryContentDao);
            });
    }

    /**
     * Inicializuje nový inventář
     *
     * @param capacity Kapacita inventáře
     * @return {@link Inventory} instanci inicializovaného inventáře
     */
    public CompletableFuture<Inventory> initSubInventoryAsync(final int capacity) {
        Inventory subInventory = new Inventory.Builder()
            .heroId(hero.getId())
            .inventoryType(InventoryType.BACKPACK)
            .capacity(capacity)
            .build();
        return insertAsync(subInventory);
    }

    // endregion

    // region Getters & Setters

    public Hero getHero() {
        return hero;
    }

    // endregion

}
