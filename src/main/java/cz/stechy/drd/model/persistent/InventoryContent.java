package cz.stechy.drd.model.persistent;

import cz.stechy.drd.model.db.BaseDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryException;
import cz.stechy.drd.model.inventory.InventoryRecord;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ListChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída představující obsah jednoho inventáře
 */
public final class InventoryContent extends BaseDatabaseService<InventoryRecord> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(InventoryContent.class);

    // Název tabulky
    private static final String TABLE = "inventory_content";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_INVENTORY_ID = TABLE + "_inventory_id";
    private static final String COLUMN_ITEM_ID = TABLE + "_item_id";
    private static final String COLUMN_ITEM_AMMOUNT = TABLE + "_ammount";
    private static final String COLUMN_SLOT = TABLE + "_slot";
    private static final String COLUMN_METADATA = TABLE + "_metadata";
    // TODO poškození itemu?
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_INVENTORY_ID,
        COLUMN_ITEM_ID, COLUMN_ITEM_AMMOUNT, COLUMN_SLOT, COLUMN_METADATA};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,"    // id
            + "%s VARCHAR(255) NOT NULL,"                       // inventory id
            + "%s VARCHAR(255) NOT NULL,"                       // item id
            + "%S INT NOT NULL,"                                // ammount
            + "%s INT NOT NULL,"                                // slot index
            + "%s BLOB"                                         // image
            + ");", TABLE, COLUMN_ID, COLUMN_INVENTORY_ID, COLUMN_ITEM_ID, COLUMN_ITEM_AMMOUNT,
        COLUMN_SLOT, COLUMN_METADATA);

    // endregion

    // region Variables

    /* Celková hmotnost VŠECH inventáře
    Normálně to nedělám, ale zde mi to příjde jako vhodné
    Statickou finální metodou získám globální hmotnost všech inventářů, které má postava
    u sebe */
    private static final ReadOnlyIntegerWrapper WEIGHT = new ReadOnlyIntegerWrapper();

    private static boolean tableInitialized = false;
    // Inventář
    private final Inventory inventory;

    // endregion

    // region Constructors

    /**
     * Inicializuje správce inventářů pro jednoho hrdinu
     *
     * @param db Databáze, do které se ukládají informace
     * @param inventory Inventář, ke kterému je přidružen obsah
     */
    InventoryContent(Database db, Inventory inventory) {
        super(db);

        this.inventory = inventory;
        try {
            createTable();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        items.addListener(itemsListener);
    }

    // endregion

    // region Public static methods

    /**
     * Vynuluje váhu inventářů.
     * Mělo by se volat před změnou hrdiny
     */
    public static void clearWeight() {
        WEIGHT.set(0);
    }

    // endregion

    // region Private methods

    @Override
    protected InventoryRecord parseResultSet(ResultSet resultSet) throws SQLException {
        return new InventoryRecord.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .inventoryId(resultSet.getString(COLUMN_INVENTORY_ID))
            .itemId(resultSet.getString(COLUMN_ITEM_ID))
            .ammount(resultSet.getInt(COLUMN_ITEM_AMMOUNT))
            .slotId(resultSet.getInt(COLUMN_SLOT))
            .metadata(Metadata.deserialize(readBlob(resultSet, COLUMN_METADATA)))
            .build();
    }

    @Override
    protected List<Object> itemToParams(InventoryRecord record) {
        return new ArrayList<>(Arrays.asList(
            record.getId(),
            record.getInventoryId(),
            record.getItemId(),
            record.getAmmount(),
            record.getSlotId(),
            Metadata.serialize(record.getMetadata())
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
        return String.format("SELECT * FROM %s WHERE %s=?", getTable(), COLUMN_INVENTORY_ID);
    }

    @Override
    protected Object[] getParamsForSelectAll() {
        return new Object[]{inventory.getId()};
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
    public void update(InventoryRecord inventoryRecord) throws DatabaseException {
        final Optional<InventoryRecord> inventoryRecordOptional = items.stream()
            .filter(record -> Objects.equals(record, inventoryRecord))
            .findFirst();

        final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
            .getItemById(inventoryRecord.getItemId());
        if (!itemOptional.isPresent()) {
            return;
        }
        final ItemBase item = itemOptional.get();
        int oldAmmount = 0;
        if (inventoryRecordOptional.isPresent()) {
            oldAmmount = inventoryRecordOptional.get().getAmmount() * item.getWeight();
        }

        super.update(inventoryRecord);
        final int w = WEIGHT.get();
        WEIGHT.set(w - oldAmmount + inventoryRecord.getAmmount() * item.getWeight());
    }

    /**
     * Najde slot, ve kterém se nachází hledaný item
     *
     * @param item Item, který se hledá
     * @return Id slotu, ve kterém se nachází item
     * @throws InventoryException Pokud item není přítomný
     */
    public int getItemSlotIndexById(final ItemBase item) throws InventoryException {
        return getItemSlotIndex(record -> item.getId().equals(record.getItemId()));
    }

    /**
     * Najde slot, ve kterém se nachází hledaný item
     *
     * @param filter Filter, podle kterého se vyhledává item
     * @return Id slotu, ve kterém se nachází item
     * @throws InventoryException Pokud item není přítomný
     */
    public int getItemSlotIndex(final Predicate<InventoryRecord> filter) throws InventoryException {
        Optional<InventoryRecord> result = items.stream()
            .filter(filter)
            .findFirst();
        if (!result.isPresent()) {
            throw new InventoryException("Item not found");
        }

        return result.get().getSlotId();
    }

    /**
     * Najde index volného slotu
     *
     * @return Index volného slotu
     * @throws InventoryException Pokud volný slot neexistuje
     */
    public int getFreeSlot() throws InventoryException {
        final int size = items.size();
        // Pokud mám maximální počet itemů v inventáři, vyhodím vyjímku
        if (size == inventory.getCapacity()) {
            throw new InventoryException("No free slot");
        }
        // Pokud nemám žádné itemy v inventáři, vrátím první index
        if (size == 0) {
            return 0;
        }
        // Namapuji všechny sloty podle ID slotu a seřadím vzestupně
        final List<Integer> ids = items.stream()
            .sorted(Comparator.comparingInt(InventoryRecord::getSlotId))
            .mapToInt(InventoryRecord::getSlotId).boxed().collect(Collectors.toList());

        // Získám nejmenší index
        final int min = ids.get(0);
        // Pokud minimum není 0. index, tak vrátím 0
        if (min > 0) {
            return 0;
        }

        // Záskám nejvyšší index
        final int max = ids.get(size - 1);
        // Pokud je nejvyšší index menší než kapacita inventáře, vrátím index zvětšený o jedničku
        if (max < inventory.getCapacity()) {
            return max + 1;
        }

        // Kdyz jsou hraniční indexy obsazeny, budu hledat uvnitř otevřeného intervalu (min; max)
        // Inicializuji výsledek jaku 0. index
        int result = 0;
        for (int i = 0; i < inventory.getCapacity(); i++) {
            // Získám index podle I
            int index = ids.get(i);
            // Pokud index neodpovídá proměnné result, tak jsem nalezl odpověď a vracím I
            if (result != index) {
                return i;
            }
            // Jinak inkrementují proměnnou result o jedničku
            result++;
        }

        return 0;
    }

    // endregion

    // region Getters & Setters

    public Inventory getInventory() {
        return inventory;
    }

    public static ReadOnlyIntegerProperty getWeight() {
        return WEIGHT.getReadOnlyProperty();
    }

    // endregion

    // Pomocná mapovací funkce pro získání váhy předmětu podle počtu
    private static final ToIntFunction<InventoryRecord> mapper = value -> {
        final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
            .getItemById(value.getItemId());
        return itemOptional.map(itemBase -> value.getAmmount() * itemBase.getWeight()).orElse(0);
    };

    private final ListChangeListener<? super InventoryRecord> itemsListener = c -> {
        while (c.next()) {
            if (c.wasAdded()) {
                final int w = WEIGHT.get();
                WEIGHT.set(w + c.getAddedSubList().stream().mapToInt(mapper).sum());
            }
            if (c.wasRemoved()) {
                final int w = WEIGHT.get();
                WEIGHT.set(w - c.getRemoved().stream().mapToInt(mapper).sum());
            }
        }
    };
}
