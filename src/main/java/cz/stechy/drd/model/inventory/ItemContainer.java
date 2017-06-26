package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.inventory.ItemSlot.ClickListener;
import cz.stechy.drd.model.inventory.ItemSlot.DragDropHandlers;
import cz.stechy.drd.model.inventory.ItemSlot.HighlightState;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.drd.model.persistent.InventoryContent;
import cz.stechy.drd.model.persistent.InventoryService;
import java.util.Optional;
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

    // region Constsnts

    public static final int SLOT_SPACING = 1;

    // endregion

    // region Variables

    // Globální informace obsahující přesouvaný item
    private static final ObjectProperty<DragInformations> dragInformations = new SimpleObjectProperty<>();

    private InventoryService inventoryManager;
    private InventoryContent inventoryContent;
    protected final int capacity;
    private ObservableList<InventoryRecord> oldRecords;
    private ItemClickListener itemClickListener;
    // Kolekce slotů pro itemy v inventáři
    protected final ObservableList<ItemSlot> itemSlots = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    protected ItemContainer(int capacity) {
        this.capacity = capacity;

        dragInformations.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                cancelSlotHighlight();
            } else {
                highlightInventory(newValue.draggedStack);
            }
        });
    }

    // endregion

    // region Private methods

    /**
     * Vloží item z {@link InventoryRecord} do správného slotu
     *
     * @param record {@link InventoryRecord}
     */
    private void insert(final InventoryRecord record) {
        final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
            .getItemById(record.getItemId());
        itemOptional.ifPresent(itemBase -> {
            final ItemBase item = itemBase;
            final int ammount = record.getAmmount();
            final int slotIndex = record.getSlotId();
            final Metadata metadata = record.getMetadata();
            final ItemSlot itemSlot = itemSlots.get(slotIndex);
            if (itemSlot.isEmpty()) {
                itemSlot.setClickListener(clickListener);
            }

            itemSlot.addItem(new ItemStack(item, ammount, metadata));
        });
    }

    /**
     * Odebere item ze se slotu
     *
     * @param record {@link InventoryRecord}
     */
    private void remove(final InventoryRecord record) {
        final int ammount = record.getAmmount();
        final int slotIndex = record.getSlotId();
        final ItemSlot itemSlot = itemSlots.get(slotIndex);
        if (itemSlot.isEmpty()) {
            return;
        }

        itemSlot.removeItems(ammount);
    }

    /**
     * Zajištění přesunu itemu
     *
     * @param sourceInventoryId
     * @param sourceSlot
     * @param destinationSlot {@link ItemSlot} Cílový slot, do kterého se má vložit item
     * @param transferAmmount
     */
    private void handleDragEnd(final String sourceInventoryId, final ItemSlot sourceSlot,
        final ItemSlot destinationSlot, final int transferAmmount) {
        try {
            final InventoryContent sourceInventoryContent = inventoryManager
                .getInventoryContentById(sourceInventoryId);
            final InventoryRecord sourceInventoryRecord = sourceInventoryContent
                .select(record -> record.getSlotId() == sourceSlot.getId());
            final int sourceAmmount = sourceSlot.getItemStack().getAmmount();
            final int sourceAmmountResult = sourceAmmount - transferAmmount;

            try {
                final InventoryRecord destinationInventoryRecord = inventoryContent
                    .select(record -> record.getSlotId() == destinationSlot.getId());
                final InventoryRecord destinationInventoryRecordCopy = destinationInventoryRecord
                    .duplicate();
                destinationInventoryRecordCopy.addAmmount(transferAmmount);
                inventoryContent.update(destinationInventoryRecordCopy);
                destinationSlot.getItemStack().addAmmount(transferAmmount);
            } catch (DatabaseException e) {
                final InventoryRecord destinationInventoryRecord = new InventoryRecord.Builder()
                    .inventoryId(inventoryContent.getInventory().getId())
                    .ammount(transferAmmount)
                    .itemId(sourceInventoryRecord.getItemId())
                    .slotId(destinationSlot.getId())
                    .build();
                inventoryContent.insert(destinationInventoryRecord);
                // Zde nemusím volat insert nad destinationSlotem, protože se zavolá automaticky
            }

            if (sourceAmmountResult > 0) {
                final InventoryRecord recordCopy = sourceInventoryRecord.duplicate();
                recordCopy.subtractAmmount(transferAmmount);
                sourceInventoryContent.update(recordCopy);
                sourceSlot.getItemStack().subtractAmmount(transferAmmount);
            } else {
                sourceInventoryContent.delete(sourceInventoryRecord.getId());
                // Zde nemusím volat delete nad sourceSlotem, protože se zavolá automaticky
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

        InventoryContent.getWeight().addListener(weightListener);
        final ObservableList<InventoryRecord> inventoryRecords = inventoryContent.selectAll();
        inventoryRecords.forEach(this::insert);
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
    }

    // endregion

    // region Getters & Setters

    public void setInventoryManager(InventoryService inventoryManager, Inventory inventory)
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
            final DragInformations dragInformations = ItemContainer.dragInformations.get();
            handleDragEnd(dragInformations.sourceInventoryId, dragInformations.sourceSlot,
                destinationSlot, dragInformations.draggedStack.getAmmount());
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
            c.getAddedSubList().forEach(this::insert);
            c.getRemoved().forEach(this::remove);
        }
    };

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
