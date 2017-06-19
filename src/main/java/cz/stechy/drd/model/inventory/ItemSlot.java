package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.item.ItemBase;
import java.io.ByteArrayInputStream;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.converter.NumberStringConverter;

/**
 * Třída představující jeden slot v inventáři
 */
public class ItemSlot {

    // region Constants

    public static final int SLOT_SIZE = 40;
    private static final int LABEL_TRANSLATE_Y = 15;
    private static final int LABEL_SIZE = 10;
    private static final int LABEL_AMMOUNT_HEGHT = 15;

    // endregion

    // region Variables
    // Kontainer s obrázkem
    private final StackPane container = new StackPane();
    // Obrázek itemu
    private final ImageView imgItem = new ImageView();
    // Text zobrazující počet itemů
    private final Label lblAmmount = new Label();

    // Id slotu
    private int id;
    private DragDropHandlers dragDropHandlers;
    private ClickListener clickListener;
    private ItemStack itemStack;
    // Výchozí item filster, který přijímá vše
    private Predicate<ItemBase> filter = itemBase -> true;

    // endregion

    // region Constructors

    /**
     * Inicializuje nový slot, ve kterém nebudou fungovat drag&drop operace
     *
     * @param id Id slotu
     */
    public ItemSlot(int id) {
        this(id, null);
    }

    /**
     * Inicializuje nový slot
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     */
    public ItemSlot(int id, DragDropHandlers dragDropHandlers) {
        this.id = id;
        this.dragDropHandlers = dragDropHandlers;
    }

    // endregion

    // region Drag & Drop
    // Source slot drag events
    private final EventHandler<? super MouseEvent> onDragDetected = event -> {
        if (itemStack == null || dragDropHandlers == null) {
            event.consume();
            return;
        }
        final Dragboard db = imgItem.startDragAndDrop(TransferMode.ANY);
        final ClipboardContent content = new ClipboardContent();
        final DragItemContainer dragItemContainer = new DragItemContainer(
            itemStack.getItem().getId(), itemStack.getAmmount());

        content.put(DragItemContainer.MOVE_ITEM, dragItemContainer);

        db.setDragView(imgItem.getImage());
        db.setContent(content);
        // TODO přidat moznosti přesunu různého množství itemů
        dragDropHandlers.onDragStart(this, itemStack);
        event.consume();
    };
    private final EventHandler<? super DragEvent> onDragDone = event -> {
        if (event.getTransferMode() == TransferMode.MOVE) {
            dragDropHandlers.onDragEnd();
        }
        event.consume();
    };

    // Destination slot drag events
    private final EventHandler<? super DragEvent> onDragOver = event -> {
        if (event.getGestureSource() != imgItem &&
            event.getDragboard().hasContent(DragItemContainer.MOVE_ITEM)) {
              /* allow for moving */
            event.acceptTransferModes(TransferMode.MOVE);
        }

        event.consume();
    };
    private final EventHandler<? super DragEvent> onDragDropped = event -> {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasContent(DragItemContainer.MOVE_ITEM) && dragDropHandlers.acceptDrop(this)) {
            DragItemContainer dragItemContainer = (DragItemContainer) db
                .getContent(DragItemContainer.MOVE_ITEM);
            success = true;
        }

        event.setDropCompleted(success);
        if (success) {
            dragDropHandlers.onDragDrop(this);
        }

        event.consume();
    };

    private final EventHandler<? super MouseEvent> onMouseClicked = event -> {
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    };

    // endregion

    {
        container.setPrefWidth(SLOT_SIZE);
        container.setPrefHeight(SLOT_SIZE);

        imgItem.setOnMouseClicked(onMouseClicked);

        // Source slot drag events
        imgItem.setOnDragDetected(onDragDetected);
        imgItem.setOnDragDone(onDragDone);

        // Destination slot drag events
        imgItem.setOnDragOver(onDragOver);
        imgItem.setOnDragDropped(onDragDropped);

        imgItem.setFitWidth(SLOT_SIZE);
        imgItem.setFitHeight(SLOT_SIZE);
        imgItem.setCursor(Cursor.HAND);

        lblAmmount.setMaxSize(SLOT_SIZE, LABEL_AMMOUNT_HEGHT);
        lblAmmount.setTranslateY(LABEL_TRANSLATE_Y);
        lblAmmount.setAlignment(Pos.BASELINE_RIGHT);
        lblAmmount.setFont(new Font(LABEL_SIZE));

        container.getStyleClass().add("item-slot");
        container.getChildren().setAll(imgItem, lblAmmount);
        container.setOnDragOver(onDragOver);
        container.setOnDragDropped(onDragDropped);
    }

    // region Private methods

    private void setImage(byte[] image) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        imgItem.setImage(new Image(inputStream));
    }

    // endregion

    // region Public methods

    /**
     * Smaže obsah slotu
     */
    public void clearSlot() {
        if (itemStack != null) {
            itemStack.getItem().imageProperty().removeListener(imageChangeListener);
        }
        itemStack = null;
        imgItem.setImage(null);
        lblAmmount.setText(null);
        clickListener = null;
    }

    /**
     * Pokusí se přidat item do slotu.
     * Pokud slot neobsahuje žádný item, tak je vložen celý.
     * Pokud slot obsahuje jiný item, tak se nic nevloží, jinak se pouze přičte počet
     *
     * @param itemStack
     */
    public void addItem(ItemStack itemStack) {
        final ItemBase item = itemStack.getItem();
        final int ammount = itemStack.getAmmount();
        if (this.itemStack == null) {
            this.itemStack = new ItemStack(itemStack);
            lblAmmount.textProperty()
                .bindBidirectional(this.itemStack.ammountProperty(), new NumberStringConverter());
            setImage(item.getImage());
            this.itemStack.getItem().imageProperty().addListener(imageChangeListener);
            return;
        }

        if (this.itemStack.containsItemType(item)) {
            this.itemStack.addAmmount(ammount);
        }
    }

    /**
     * Odebere požadovaný počet itemů ze stacku.
     * Pokud na aktuálním stacku nezůstane nic, tak je stack odebrán
     *
     * @param ammount Počet itemů, který se odebere ze stacku
     * @return {@link ItemStack}
     */
    public ItemStack removeItems(int ammount) {
        assert itemStack != null;
        final int totalAmmount = itemStack.getAmmount();
        assert totalAmmount >= ammount;
        final int remaining = totalAmmount - ammount;
        final int count = (remaining >= 0) ? ammount : totalAmmount;
        ItemStack stack = new ItemStack(itemStack.getItem(), count, itemStack.getMetadata());

        if (remaining == 0) {
            clearSlot();
        }

        return stack;
    }

    /**
     * Zjistí, zda-li slot obsahuje nějaký item
     *
     * @return True, pokud slot obsahuje item, jinak false
     */
    public boolean containsItem() {
        return itemStack != null;
    }

    /**
     * Zjistí, zda-li tento slot přijme požadovan itemStack
     *
     * @param itemStack {@link ItemStack} ItemStack, který chci vložit do slotu
     * @return True, pokud lze item vložit, jinak false
     */
    public boolean acceptItem(ItemStack itemStack) {
        return filter.test(itemStack.getItem());
    }

    /**
     * Zvrazní slot podle stavu
     *
     * @param state {@link HighlightState} Stav zvýraznění
     */
    public void highlight(HighlightState state) {
        switch (state) {
            case ACCEPT:
                container.getStyleClass().add("accepting");
                container.getStyleClass().remove("decline");
                break;
            case DECLINE:
                container.getStyleClass().add("decline");
                container.getStyleClass().remove("accepting");
                break;
            default:
                container.getStyleClass().remove("accepting");
                container.getStyleClass().remove("decline");
                break;
        }
    }

    // endregion

    // region Getters & Setters

    public Node getContainer() {
        return container;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getId() {
        return id;
    }

    /**
     * Nastaví filtr, podle kterého se bude zjišťovat, zda-li může slot přijmout item, či nikoliv
     *
     * @param filter Filtr pro příjem itemů
     */
    public void setFilter(Predicate<ItemBase> filter) {
        this.filter = filter;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // endregion

    private ChangeListener<byte[]> imageChangeListener = (observable, oldValue, newValue) -> setImage(
        newValue);

    public interface DragDropHandlers {

        /**
         * Metoda je zavolána, kdy začne proces přesunu
         *
         * @param sourceSlot {@link ItemSlot} Zdrojov slot
         * @param itemStack {@link ItemStack} Informace o přesouvaném itemu
         */
        void onDragStart(ItemSlot sourceSlot, ItemStack itemStack);

        /**
         * Rozhodne, zda-li je možné vloit item na cílový slot
         *
         * @param destinationSlot {@link ItemSlot} Cílový slot
         */
        boolean acceptDrop(ItemSlot destinationSlot);

        /**
         * Metoda je zavolána, když se má item vložit do nového slotu
         *
         * @param destinationSlot {@link ItemSlot} Cílový slot, kam se má přesunout item
         */
        void onDragDrop(ItemSlot destinationSlot);

        /**
         * Metoda je zavolána při úspěšném dokončená přesunu
         * Slouží pro vyčištění zdrojů
         */
        void onDragEnd();

    }

    interface ClickListener {

        /**
         * Handler na kliknutí myši na slot
         *
         * @param itemSlot {@link ItemSlot} Item slot, na který se kliklo
         */
        void onClick(ItemSlot itemSlot);
    }

    public enum HighlightState {
        NONE, ACCEPT, DECLINE
    }
}
