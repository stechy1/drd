package cz.stechy.drd.model.persistent;

import cz.stechy.drd.model.db.BaseDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Služba spravující CRUD operace nad třídou {@link Inventory}
 *
 */
public final class InventoryService extends BaseDatabaseService<Inventory> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

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
    private final Map<Inventory, InventoryContent> inventoryContentMap = new HashMap<>();

    // endregion

    // region Constructors

    /**
     * Inicializuje správce inventářů pro vybraného hrdinu
     *
     * @param db {@link Database}
     * @param hero {@link Hero} Hrdina, pro kterého se má vybrat inventář
     */
    public InventoryService(Database db, Hero hero) {
        super(db);

        this.hero = hero;

        try {
            createTable();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
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
    public void createTable() throws DatabaseException {
        if (tableInitialized) {
            return;
        }

        super.createTable();
        tableInitialized = true;
    }

    @Override
    public void delete(String id) throws DatabaseException {
        super.delete(id);
        for (Inventory inventory : items) {
            InventoryContent inventoryContent = new InventoryContent(db, inventory);
            inventoryContent.delete(inventory.getId());
        }
    }

    public InventoryContent getInventoryContentById(final String inventoryId)
        throws DatabaseException {
        final Inventory inventory = new Inventory.Builder().id(inventoryId).build();
        return getInventoryContent(inventory, ID_FILTER(inventory));
    }

    public InventoryContent getInventoryContent(final Inventory inventory)
        throws DatabaseException {
        return getInventoryContent(inventory, SIMPLE_FILTER(inventory));
    }

    /**
     * Vytvoří přistup k obsahu inventáře podle zadaného Id
     *
     * @param inventory {@link Inventory} Inventář pro který se hledá obsah
     * @param filter Filter, podle kterého probíhá hledání
     * @return {@link InventoryContent}
     */
    public InventoryContent getInventoryContent(final Inventory inventory,
        final Predicate<? super Inventory> filter) throws DatabaseException {
        final Optional<Inventory> result = inventoryContentMap.keySet()
            .stream().filter(Predicate.isEqual(inventory))
            .findFirst();
        InventoryContent inventoryContent;
        if (result.isPresent()) {
            inventoryContent = inventoryContentMap.get(result.get());
        } else {
            Inventory invnetoryResult = select(filter);
            inventoryContent = new InventoryContent(db, invnetoryResult);
            inventoryContent.selectAll();
            inventoryContentMap.put(invnetoryResult, inventoryContent);
        }

        return inventoryContent;
    }

    /**
     * Inicializuje nový inventář
     *
     * @param capacity Kapacita inventáře
     * @return Id inventáře
     * @throws DatabaseException Pokud se inicializace inventáře nezdaří
     */
    public String initSubInventory(final int capacity)
        throws DatabaseException {
        Inventory subInventory = new Inventory.Builder()
            .heroId(hero.getId())
            .inventoryType(InventoryType.BACKPACK)
            .capacity(capacity)
            .build();
        insert(subInventory);
        return subInventory.getId();
    }

    // endregion

    // region Getters & Setters

    public Hero getHero() {
        return hero;
    }

    // endregion

}
