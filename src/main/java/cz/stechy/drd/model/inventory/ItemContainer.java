package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.db.BaseDatabaseManager.UpdateListener;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.inventory.ItemSlot.ClickListener;
import cz.stechy.drd.model.inventory.ItemSlot.DragDropHandlers;
import cz.stechy.drd.model.inventory.ItemSlot.HighlightState;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.drd.model.item.ItemRegistry.ItemException;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * Třída představující inventář
 */
public abstract class ItemContainer {

    // region Variables

    // Globální informace obsahující přesouvaný item
    private static ObjectProperty<DragInformations> dragInformations = new SimpleObjectProperty<>();

    private InventoryManager inventoryManager;
    protected InventoryContent inventoryContent;
    protected final int capacity;
    private ObservableList<InventoryRecord> oldRecords;
    private ItemClickListener itemClickListener;
    // Kolekce slotů pro itemy v inventáři
    public final ObservableList<ItemSlot> itemSlots = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    public ItemContainer(int capacity) {
        this.capacity = capacity;
    }

    // endregion

    {
        dragInformations.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                cancelSlotHighlight();
            } else {
                highlightInventory(newValue.draggedStack);
            }
        });
    }

    // region Private methods

    /**
     * Vloží item z {@link InventoryRecord} do správného slotu
     *
     * @param record {@link InventoryRecord}
     * @throws ItemException Pokud se item nepodaří vložit
     */
    private void insertItemToContainer(InventoryRecord record) throws ItemException {
        insert(ItemRegistry.getINSTANCE().getItem(
            databaseItem -> databaseItem.getId().equals(record.getItemId())),
            record.getAmmount(), record.getSlotId());
    }

    /**
     * Odebere item ze se slotu
     *
     * @param record {@link InventoryRecord}
     */
    private void removeItemFromContainer(InventoryRecord record) {
        try {
            remove(record.getSlotId(), record.getAmmount());
        } catch (ItemException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zajištění přesunu itemu
     *
     * @param destinationSlot {@link ItemSlot} Cílový slot, do kterého se má vložit item
     */
    private void handleDragEnd(final ItemSlot destinationSlot) {
        try {
            final InventoryContent sourceInventoryContent = inventoryManager
                .getInventoryContentById(dragInformations.get().sourceInventoryId);
            final InventoryRecord sourceInventoryRecord = sourceInventoryContent
                .select(record -> record.getSlotId() == dragInformations.get().sourceSlot.getId());
            final int sourceAmmount = dragInformations.get().sourceSlot.getItemStack().getAmmount();
            final int transferAmmount = dragInformations.get().draggedStack.getAmmount();
            final int sourceAmmountResult = sourceAmmount - transferAmmount;

            try {
                final InventoryRecord destinationInventoryRecord = inventoryContent
                    .select(record -> record.getSlotId() == destinationSlot.getId());
                final InventoryRecord destinationInventoryRecordCopy = destinationInventoryRecord
                    .duplicate();
                destinationInventoryRecordCopy
                    .setAmmount(destinationInventoryRecord.getAmmount() + transferAmmount);
                inventoryContent.update(destinationInventoryRecordCopy);
            } catch (DatabaseException e) {
                InventoryRecord destinationInventoryRecord = new InventoryRecord.Builder()
                    .inventoryId(inventoryContent.getInventory().getId())
                    .ammount(transferAmmount)
                    .itemId(sourceInventoryRecord.getItemId())
                    .slotId(destinationSlot.getId())
                    .build();
                inventoryContent.insert(destinationInventoryRecord);
            }

            if (sourceAmmountResult > 0) {
                InventoryRecord recordCopy = sourceInventoryRecord.duplicate();
                recordCopy.setAmmount(sourceAmmountResult);
                sourceInventoryContent.update(recordCopy);
            } else {
                sourceInventoryContent.delete(sourceInventoryRecord.getId());
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Přidá slot do inventáře
     *
     * @param slot {@link ItemSlot}
     */
    protected void addItemSlot(ItemSlot slot) {
        itemSlots.add(slot);
    }

    /**
     * Zjistí, zda-li vybraný slot může přijmout item, či nikoliv
     *
     * @param slot Testovaný slot
     * @param what Testovaný item
     * @return True, pokud slot přijme item, jinak false
     */
    private boolean canAccept(ItemSlot slot, ItemStack what) {
        return slot.acceptItem(what);
    }

    /**
     * Zvýrazní sloty podle itemu
     *
     * @param item {@link ItemStack}
     */
    private void highlightInventory(final ItemStack item) {
        itemSlots.forEach(slot ->
            slot.highlight(slot.acceptItem(item)
                ? HighlightState.ACCEPT
                : HighlightState.DECLINE));
    }

    /**
     * Zruší zvýraznění slotů
     */
    private void cancelSlotHighlight() {
        itemSlots.forEach(slot -> slot.highlight(HighlightState.NONE));
    }

    /**
     * Inicializuje obsah inventáře
     *
     * @param inventoryContent {@link InventoryContent}
     */
    private void initInventoryContent(InventoryContent inventoryContent) {
        clear();

        inventoryContent.setUpdateListener(inventoryUpdateListener);
        inventoryContent.getWeight().addListener(weightListener);
        final ObservableList<InventoryRecord> inventoryRecords = inventoryContent.selectAll();
        inventoryRecords.forEach(record -> insert(ItemRegistry.getINSTANCE()
                .getItem(databaseItem -> databaseItem.getId().equals(record.getItemId())),
            record.getAmmount(),
            record.getSlotId()));
        inventoryRecords.addListener(inventoryRecordListener);
        this.inventoryContent = inventoryContent;
        this.oldRecords = inventoryRecords;
    }

    // endregion

    // region Public methdos

    /**
     * Odebere všechny itemy ze slotů
     */
    public void clear() {
        itemSlots.forEach(ItemSlot::clearSlot);
        if (oldRecords != null) {
            oldRecords.removeListener(inventoryRecordListener);
            oldRecords = null;
        }
        if (inventoryContent != null) {
            inventoryContent.setUpdateListener(null);
        }
    }

    /**
     * Vloži item do slotu
     *
     * @param item {@link ItemBase}
     * @param ammount Počet itemů
     * @param slotIndex Index slotu, do kterého se má vložit item
     */
    public void insert(ItemBase item, int ammount, int slotIndex) {
        final ItemSlot itemSlot = itemSlots.get(slotIndex);
        if (!itemSlot.containsItem()) {
            itemSlot.setClickListener(clickListener);
        }
        itemSlot.addItem(item, ammount);
    }

    /**
     * Odebere ze slotu item
     *
     * @param slotIndex Index slotu, ze kterého se má odebrat item
     * @param ammount Počet itemů, který se má odebrat
     */
    public void remove(int slotIndex, int ammount) throws ItemException {
        final ItemSlot itemSlot = itemSlots.get(slotIndex);
        if (!itemSlot.containsItem()) {
            return;
        }
        itemSlot.removeItems(ammount);
        if (!itemSlot.containsItem()) {
            itemSlot.setClickListener(null);
        }
    }

    // endregion

    // region Getters & Setters

    public void setInventoryManager(InventoryManager inventoryManager, Inventory inventory)
        throws DatabaseException {
        this.inventoryManager = inventoryManager;
        initInventoryContent(inventoryManager.getInventoryContent(inventory));
    }

    /**
     * Vrátí grafickou reprezentaci inventáře
     *
     * @return {@link Node}
     */
    public abstract Node getGraphics();

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    // endregion

    // Handler pro drag&drop operace
    protected final DragDropHandlers dragDropHandlers = new DragDropHandlers() {
        @Override
        public void onDragStart(ItemSlot sourceSlot, ItemStack itemStack) {
            dragInformations.setValue(new DragInformations(inventoryContent.getInventory().getId(),
                sourceSlot, itemStack));
        }

        @Override
        public boolean acceptDrop(ItemSlot destinationSlot) {
            return canAccept(destinationSlot, dragInformations.get().draggedStack);
        }

        @Override
        public void onDragDrop(ItemSlot destinationSlot) {
            handleDragEnd(destinationSlot);
        }

        @Override
        public void onDragEnd() {
            dragInformations.setValue(null);
            //cancelSlotHighlight();
        }
    };

    // Listener pro změnu obsahu inventáře
    private final ListChangeListener<? super InventoryRecord> inventoryRecordListener = (ListChangeListener<InventoryRecord>) c -> {
        while (c.next()) {
            if (c.wasAdded()) {
                for (InventoryRecord record : c.getAddedSubList()) {
                    try {
                        insertItemToContainer(record);
                    } catch (ItemException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (c.wasRemoved()) {
                for (InventoryRecord record : c.getRemoved()) {
                    removeItemFromContainer(record);
                }
            }
        }
    };

    // Listener pro správnou vizualizaci počtu itemů ve slotu
    private final UpdateListener<InventoryRecord> inventoryUpdateListener = item ->
        itemSlots.get(item.getSlotId()).getItemStack().setAmmount(item.getAmmount());

    // Listener pro změnu váhy inventáře
    private final ChangeListener<? super Number> weightListener = (observable, oldValue, newValue) -> {

    };

    // Listener pro kliknutí na položku v inventáři
    private final ClickListener clickListener = itemSlot -> {
        if (itemClickListener != null) {
            itemClickListener.onClick(itemSlot);
        }
    };

    private static final class DragInformations {

        final String sourceInventoryId;
        final ItemSlot sourceSlot;
        final ItemStack draggedStack;

        private DragInformations(String sourceInventoryId, ItemSlot sourceSlot,
            ItemStack draggedStack) {
            this.sourceInventoryId = sourceInventoryId;
            this.sourceSlot = sourceSlot;
            this.draggedStack = draggedStack;
        }
    }
}
