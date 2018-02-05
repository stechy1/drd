package cz.stechy.drd.model.persistent;

import cz.stechy.drd.model.db.BaseDatabaseService;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.InventoryException;
import cz.stechy.drd.model.inventory.InventoryRecord;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.service.ItemRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ListChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Služba spravující CRUD operace nad třídou {@link InventoryRecord}
 */
public final class InventoryContent extends BaseDatabaseService<InventoryRecord> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryContent.class);

    private static final int SLOT_OCCUPIED = 1;
    private static final int SLOT_NOT_OCCUPIED = 0;

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
    private static final ReadOnlyIntegerWrapper weight = new ReadOnlyIntegerWrapper();

    private static boolean tableInitialized = false;
    // Inventář
    private final Inventory inventory;
    private int[] occupiedSlots;

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
        this.occupiedSlots = new int[inventory.getCapacity()];
        try {
            createTable();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        items.addListener(this::itemsHandler);
    }

    // endregion

    // region Public static methods

    /**
     * Vynuluje váhu inventářů.
     * Mělo by se volat před změnou hrdiny
     */
    public static void clearWeight() {
        weight.set(0);
    }

    // endregion

    // region Private methods

    // region Method handlers

    private void itemsHandler(ListChangeListener.Change<? extends InventoryRecord> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                final int w = weight.get();
                weight.set(w + c.getAddedSubList().stream().mapToInt(this::mapper).sum());
            }
            if (c.wasRemoved()) {
                final int w = weight.get();
                weight.set(w - c.getRemoved().stream().mapToInt(this::mapper).sum());
            }
        }
    }

    /**
     * Pomocná funkce, která vypočítá celkovou cenu předmětů v zadaném záznamu
     *
     * @param value {@link InventoryRecord}
     * @return Cena předmětů v zadaném záznamu
     */
    private int mapper(InventoryRecord value) {
        final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
            .getItemById(value.getItemId());
        return itemOptional.map(itemBase -> value.getAmmount() * itemBase.getWeight()).orElse(0);
    }

    // endregion

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
        final int w = weight.get();
        weight.set(w - oldAmmount + inventoryRecord.getAmmount() * item.getWeight());
    }

    @Override
    public CompletableFuture<InventoryRecord> updateAsync(InventoryRecord inventoryRecord) {
        final Optional<InventoryRecord> inventoryRecordOptional = items.stream()
            .filter(record -> Objects.equals(record, inventoryRecord))
            .findFirst();

        final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
            .getItemById(inventoryRecord.getItemId());
        if (!itemOptional.isPresent()) {
            return CompletableFuture.completedFuture(null)
                .thenApply(o -> {
                throw new RuntimeException();
            });
        }
        final ItemBase item = itemOptional.get();
        final int oldAmmount;
        oldAmmount = inventoryRecordOptional
            .map(inventoryRecord1 -> inventoryRecord1.getAmmount() * item.getWeight()).orElse(0);
        assert inventoryRecord.getAmmount() != 0;

        return super.updateAsync(inventoryRecord)
            .thenApply(inventoryRecord1 -> {
                final int w = weight.get();
                weight.set(w - oldAmmount + inventoryRecord.getAmmount() * item.getWeight());
                return inventoryRecord1;
            });
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

    public synchronized CompletableFuture<Map<Integer, Integer>> getFreeSlotAsync(ItemBase item, int ammount) {
        return CompletableFuture.supplyAsync(() -> {
            final Map<Integer, Integer> mapSlots = new HashMap<>();
            final int stackSize = item.getStackSize();

            items.forEach(inventoryRecord -> occupiedSlots[inventoryRecord.getSlotId()] = SLOT_OCCUPIED);
            final Map<Integer, Integer> occupiedItemMap = new HashMap<>();
            for (InventoryRecord inventoryRecord : items) {
                if (inventoryRecord.getItemId().equals(item.getId())) {
                    if (occupiedItemMap.put(inventoryRecord.getSlotId(), stackSize - inventoryRecord.getAmmount()) != null) {
                        throw new IllegalStateException("Duplicate key");
                    }
                }
            }
            int remaining = ammount;
            for (Entry<Integer, Integer> entry : occupiedItemMap.entrySet()) {
                final int slotId = entry.getKey();
                final int freeSpace = entry.getValue();
                final int insertAmmount = Math.min(remaining, freeSpace);
                mapSlots.put(slotId, insertAmmount);
                remaining -= insertAmmount;
                if (remaining <= 0) {
                    break;
                }
            }

            if (remaining - stackSize * (inventory.getCapacity() - items.size()) > 0) {
                throw new RuntimeException("V inventáři není dostatek místa.");
            }

            final int capacity = inventory.getCapacity();
            int index = 0;
            while (remaining > 0) {
                if (occupiedSlots[index] != SLOT_NOT_OCCUPIED) {
                    index++;
                    continue;
                }

                final int insertAmmount = Math.min(remaining, stackSize);
                mapSlots.put(index, insertAmmount);
                remaining -= insertAmmount;
                occupiedSlots[index] = SLOT_OCCUPIED;
                index++;
                assert index != capacity;
            }

            return mapSlots;
        });
    }

    /**
     * Najde index volného slotu
     *
     * @param item Předmět, pro který se hledá volná slot
     * @param ammount Množství předmětů které se má vložit do inventáře
     * @return Index volného slotu
     * @throws InventoryException Pokud volný slot neexistuje
     */
    public synchronized Map<Integer, Integer> getFreeSlot(ItemBase item, int ammount) throws InventoryException {
        final Map<Integer, Integer> mapSlots = new HashMap<>();
        final int stackSize = item.getStackSize();
        items.stream().forEach(inventoryRecord -> occupiedSlots[inventoryRecord.getSlotId()] = SLOT_OCCUPIED);
        final Map<Integer, Integer> map = items.stream()
            .filter(inventoryRecord -> inventoryRecord.getItemId().equals(item.getId()))
            .collect(Collectors.toMap(o -> o.getSlotId(), t -> stackSize - t.getAmmount()));

        int remaining = ammount;
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            final int slotId = entry.getKey();
            final int freeSpace = entry.getValue();
            final int insertAmmount = Math.min(remaining, freeSpace);
            mapSlots.put(slotId, insertAmmount);
            remaining -= insertAmmount;
            if (remaining <= 0) {
                break;
            }
        }

        if (remaining - stackSize * (inventory.getCapacity() - items.size()) > 0) {
            throw new InventoryException("Not enought space for insert");
        }

        final int capacity = inventory.getCapacity();
        int index = 0;
        while (remaining > 0) {
            if (occupiedSlots[index] != SLOT_NOT_OCCUPIED) {
                index++;
                continue;
            }

            final int insertAmmount = Math.min(remaining, stackSize);
            mapSlots.put(index, insertAmmount);
            remaining -= insertAmmount;
            occupiedSlots[index] = SLOT_OCCUPIED;
            index++;
            assert index != capacity;
        }

        return mapSlots;
    }

    // endregion

    // region Getters & Setters

    public Inventory getInventory() {
        return inventory;
    }

    public static ReadOnlyIntegerProperty getWeight() {
        return weight.getReadOnlyProperty();
    }

    // endregion
}
