package cz.stechy.drd.model.inventory;

import com.google.inject.Inject;
import cz.stechy.drd.model.inventory.InventoryContent.Metadata;
import cz.stechy.drd.model.inventory.ItemSlot.ClickListener;
import cz.stechy.drd.model.inventory.ItemSlot.DragDropHandlers;
import cz.stechy.drd.model.inventory.ItemSlot.HighlightState;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.inventory.IInventoryContentService;
import cz.stechy.drd.service.inventory.IInventoryService;
import cz.stechy.drd.service.item.IItemRegistry;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída představující inventář
 */
public abstract class ItemContainer {

    // region Constsnts

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemContainer.class);

    public static final int SLOT_SPACING = 1;

    // Globální informace obsahující přesouvaný item
    private static final ObjectProperty<DragInformations> dragInformations = new SimpleObjectProperty<>();

    private static final Predicate<ItemStack> DEFAULT_INVENTORY_FILTER = itemStack -> true;
    // endregion

    // region Variables

    private final IItemRegistry itemRegistry;
    protected final int capacity;
    private final TooltipTranslator tooltipTranslator;

    private IInventoryService inventoryService;
    private IInventoryContentService inventoryContentService;
    private ObservableList<InventoryContent> oldRecords;
    private ItemClickListener itemClickListener;
    private Predicate<ItemStack> inventoryFilter = DEFAULT_INVENTORY_FILTER;
    // Kolekce slotů pro itemy v inventáři
    protected final ObservableList<ItemSlot> itemSlots = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    @Inject
    protected ItemContainer(IItemRegistry itemRegistry, TooltipTranslator tooltipTranslator, int capacity) {
        this.itemRegistry = itemRegistry;
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
     * Vloží item z {@link InventoryContent} do správného slotu
     *
     * @param record {@link InventoryContent}
     */
    private void insert(final InventoryContent record) {
        final Optional<ItemBase> itemOptional = itemRegistry.getItemById(record.getItemId());
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
     * @param record {@link InventoryContent}
     */
    private void remove(final InventoryContent record) {
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
     * @param sourceInventory {@link Inventory} Zdrojový inventář
     * @param sourceSlot {@link ItemSlot} Zdrojový slot, ze kterého je vyjmut předmět
     * @param destinationSlot {@link ItemSlot} Cílový slot, do kterého se má vložit item
     * @param transferAmmount Přenášené množství předmětů
     */
    private void handleDragEnd(final Inventory sourceInventory, final ItemSlot sourceSlot, final ItemSlot destinationSlot, final int transferAmmount) {
        final int sourceAmmount = sourceSlot.getItemStack().getAmmount();
        final int sourceAmmountResult = sourceAmmount - transferAmmount;

        final IInventoryContentService sourceInventoryContentService = inventoryService.getInventoryContentService(sourceInventory);
        sourceInventoryContentService.select(record -> record.getSlotId() == sourceSlot.getId())
            .thenAccept(sourceInventoryRecord -> inventoryContentService
                .select(record -> record.getSlotId() == destinationSlot.getId())
                // Vložení/aktualizace cílového slotu
                .handle((destinationInventoryRecord, throwable) -> {
                    if (throwable == null) { // Cílový slot již obsahuje stejný předmět
                        final InventoryContent destinationInventoryContentCopy = destinationInventoryRecord.duplicate();
                        destinationInventoryContentCopy.addAmmount(transferAmmount);
                        return inventoryContentService
                            .updateContent(destinationInventoryContentCopy)
                            .thenApply(inventoryRecord -> {
                                destinationSlot.getItemStack().addAmmount(transferAmmount);
                                return inventoryRecord;
                            });
                    } else { // Musím vytvořit nový cílový slot
                        final InventoryContent destinationInventoryRecord1 = new InventoryContent.Builder()
                            .inventoryId(inventoryContentService.getInventory().getId())
                            .ammount(transferAmmount)
                            .itemId(sourceInventoryRecord.getItemId())
                            .slotId(destinationSlot.getId())
                            .metadata(sourceInventoryRecord.getMetadata())
                            .build();
                        return inventoryContentService.insertContent(destinationInventoryRecord1);
                    }
                })
                // Smazání/aktualizace zdrojového slotu
                .thenCompose(future -> future.thenCompose(sourceInventoryRecord1 -> {
                    if (sourceAmmountResult > 0) {
                        final InventoryContent recordCopy = sourceInventoryRecord.duplicate();
                        recordCopy.subtractAmmount(transferAmmount);
                        return sourceInventoryContentService.updateContent(recordCopy)
                            .thenApply(inventoryRecord -> {
                                sourceSlot.getItemStack().subtractAmmount(transferAmmount);
                                return inventoryRecord;
                            });
                    } else {
                        return sourceInventoryContentService.deleteContent(sourceInventoryRecord);
                    }
                })))
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
     * Zjistí, zda-li tento inventář může přijmout item, či nikoliv
     *
     * @param itemStack {@link ItemStack} Poptávaný předmět
     * @return True, pokud inventář může přijmout item, jinak false
     */
    protected boolean canAccept(ItemStack itemStack) {
        return inventoryFilter.test(itemStack);
    }

    /**
     * Zjistí, zda-li vybraný slot může přijmout item, či nikoliv
     *
     * @param slot Testovaný slot
     * @param itemStack Testovaný item
     * @return True, pokud slot přijme item, jinak false
     */
    private boolean canSlotAcceptItem(ItemSlot slot, ItemStack itemStack) {
//        if (itemStack.getItem().getItemType() == ItemType.BACKPACK) {
//            boolean canSlotAcceptItem = false;
//            System.out.println("What: " + itemStack.toString());
//            for (ItemSlot itemSlot : itemSlots) {
//                System.out.println("compared to: " + itemSlot.toString());
//                if (itemSlot.getItemStack() == itemStack) {
//                    canSlotAcceptItem = true;
//                    break;
//                }
//            }
//            if (!canSlotAcceptItem) {
//                return false;
//            }
//        }

        return slot.acceptItem(itemStack);
    }

    /**
     * Zvýrazní sloty podle itemu
     *
     * @param item {@link ItemStack}
     */
    private void highlightInventory(final ItemStack item) {
        final boolean inventoryAccept = canAccept(item);

        itemSlots.forEach(slot ->
            slot.highlight(inventoryAccept && slot.acceptItem(item)
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
     * @param inventoryContentDao {@link IInventoryContentService}
     */
    private void initInventoryContent(IInventoryContentService inventoryContentDao) {
        clear();

        //IInventoryContentService.getWeight().addListener(weightListener);
        inventoryContentDao.selectAll().thenAccept(inventoryContents -> {
            inventoryContents.forEach(this::insert);
            inventoryContents.addListener(inventoryRecordListener);
            this.inventoryContentService = inventoryContentDao;
            this.oldRecords = inventoryContents;
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

    public void setInventoryManager(IInventoryService inventoryManager, Inventory inventory) {
        this.inventoryService = inventoryManager;
        this.initInventoryContent(inventoryManager.getInventoryContentService(inventory));
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

    /**
     * Nastavý filtr pro celý inventář.
     * Nesmí být null
     *
     * @param inventoryFilter {@link Predicate<ItemStack>} Filter celého inventáře
     */
    public void setInventoryFilter(Predicate<ItemStack> inventoryFilter) {
        if (inventoryFilter == null) {
            return;
        }

        this.inventoryFilter = inventoryFilter;
    }

    // endregion

    // Handler pro drag&drop operace
    protected final DragDropHandlers dragDropHandlers = new DragDropHandlers() {
        @Override
        public void onDragStart(ItemSlot sourceSlot, ItemStack itemStack) {
            dragInformations.setValue(new DragInformations(inventoryContentService.getInventory(), sourceSlot, itemStack));
        }

        @Override
        public boolean acceptDrop(ItemSlot destinationSlot) {
            final ItemStack draggedStack = dragInformations.get().draggedStack;
            return canAccept(draggedStack) && canSlotAcceptItem(destinationSlot, draggedStack);
        }

        @Override
        public void onDragDrop(ItemSlot destinationSlot) {
            final DragInformations dragInformations = ItemContainer.dragInformations.get();
            handleDragEnd(dragInformations.sourceInventory, dragInformations.sourceSlot, destinationSlot, dragInformations.draggedStack.getAmmount());
        }

        @Override
        public void onDragEnd() {
            dragInformations.setValue(null);
        }
    };

    // Listener pro změnu obsahu inventáře
    private final ListChangeListener<? super InventoryContent> inventoryRecordListener = (ListChangeListener<InventoryContent>) c -> {
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

        //final String sourceInventoryId;
        final Inventory sourceInventory;
        final ItemSlot sourceSlot;
        final ItemStack draggedStack;

        private DragInformations(Inventory sourceInventory, ItemSlot sourceSlot, ItemStack draggedStack) {
//            this.sourceInventoryId = sourceInventoryId;
            this.sourceInventory = sourceInventory;
            this.sourceSlot = sourceSlot;
            this.draggedStack = draggedStack;
        }
    }
}
