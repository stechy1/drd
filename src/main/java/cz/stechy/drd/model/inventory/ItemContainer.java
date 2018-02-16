package cz.stechy.drd.model.inventory;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.inventory.InventoryRecord.Metadata;
import cz.stechy.drd.model.inventory.ItemSlot.ClickListener;
import cz.stechy.drd.model.inventory.ItemSlot.DragDropHandlers;
import cz.stechy.drd.model.inventory.ItemSlot.HighlightState;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.dao.InventoryContentDao;
import cz.stechy.drd.model.dao.InventoryDao;
import cz.stechy.drd.model.service.ItemRegistry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    // Globální informace obsahující přesouvaný item
    private static final ObjectProperty<DragInformations> dragInformations = new SimpleObjectProperty<>();
    // endregion

    // region Variables

    protected final int capacity;
    private final TooltipTranslator tooltipTranslator;

    private InventoryDao inventoryManager;
    private InventoryContentDao inventoryContentDao;
    private ObservableList<InventoryRecord> oldRecords;
    private ItemClickListener itemClickListener;
    // Kolekce slotů pro itemy v inventáři
    protected final ObservableList<ItemSlot> itemSlots = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    protected ItemContainer(TooltipTranslator tooltipTranslator, int capacity) {
        this.capacity = capacity;
        this.tooltipTranslator = tooltipTranslator;

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
                itemSlot.setTooltipTranslator(tooltipTranslator);
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
     * @param sourceInventoryId Id zdrojového inventáře
     * @param sourceSlot {@link ItemSlot} Zdrojový slot, ze kterého je vyjmut předmět
     * @param destinationSlot {@link ItemSlot} Cílový slot, do kterého se má vložit item
     * @param transferAmmount Přenášené množství předmětů
     */
    private void handleDragEnd(final String sourceInventoryId, final ItemSlot sourceSlot,
        final ItemSlot destinationSlot, final int transferAmmount) {
        final int sourceAmmount = sourceSlot.getItemStack().getAmmount();
        final int sourceAmmountResult = sourceAmmount - transferAmmount;

        inventoryManager.getInventoryContentByIdAsync(sourceInventoryId)
            .thenAccept(sourceInventoryContent -> {
                sourceInventoryContent
                    .selectAsync(record -> record.getSlotId() == sourceSlot.getId())
                    .thenAccept(sourceInventoryRecord -> {
                        inventoryContentDao
                            .selectAsync(record -> record.getSlotId() == destinationSlot.getId())
                            .handle((destinationInventoryRecord, throwable) -> {
                                if (throwable == null) { // Cílový slot již obsahuje stejný předmět
                                    final InventoryRecord destinationInventoryRecordCopy = destinationInventoryRecord
                                    .duplicate();
                                destinationInventoryRecordCopy.addAmmount(transferAmmount);
                                return inventoryContentDao.updateAsync(destinationInventoryRecordCopy)
                                    .thenApply(inventoryRecord -> {
                                        destinationSlot.getItemStack().addAmmount(transferAmmount);
                                        return inventoryRecord;
                                    });
                                } else { // Musím vytvořit nový cílový slot
                                    final InventoryRecord destinationInventoryRecord1 = new InventoryRecord.Builder()
                                    .inventoryId(inventoryContentDao.getInventory().getId())
                                    .ammount(transferAmmount)
                                    .itemId(sourceInventoryRecord.getItemId())
                                    .slotId(destinationSlot.getId())
                                    .build();
                                    return inventoryContentDao.insertAsync(destinationInventoryRecord1);
                                }
                            })
                            .thenCompose(future -> {
                                return future.thenCompose(sourceInventoryRecord1 -> {
                                    if (sourceAmmountResult > 0) {
                                        final InventoryRecord recordCopy = sourceInventoryRecord.duplicate();
                                        recordCopy.subtractAmmount(transferAmmount);
                                        return sourceInventoryContent.updateAsync(recordCopy)
                                            .thenApply(inventoryRecord -> {
                                                sourceSlot.getItemStack().subtractAmmount(transferAmmount);
                                                return inventoryRecord;
                                            });
                                    } else {
                                        return sourceInventoryContent.deleteAsync(sourceInventoryRecord);
                                    }
                                });
                            });
                    })
                    .exceptionally(throwable -> {
                        throwable.printStackTrace();
                        throw new RuntimeException(throwable);
                    });
            })
        .exceptionally(throwable -> {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        });
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
     * @param inventoryContentDao {@link InventoryContentDao}
     */
    private void initInventoryContent(InventoryContentDao inventoryContentDao) {
        clear();

        InventoryContentDao.getWeight().addListener(weightListener);
        inventoryContentDao.selectAllAsync()
            .thenAccept(inventoryRecords -> {
                inventoryRecords.forEach(this::insert);
                inventoryRecords.addListener(inventoryRecordListener);
                this.inventoryContentDao = inventoryContentDao;
                this.oldRecords = inventoryRecords;
            });
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

    public CompletableFuture<Void> setInventoryManager(InventoryDao inventoryManager, Inventory inventory) {
        this.inventoryManager = inventoryManager;
        return inventoryManager.getInventoryContentAsync(inventory)
            .thenAcceptAsync(this::initInventoryContent, ThreadPool.JAVAFX_EXECUTOR);
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
            dragInformations.setValue(new DragInformations(inventoryContentDao.getInventory().getId(),
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
