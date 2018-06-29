package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.KeyboardService;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;

/**
 * Třída představující jeden slot v inventáři
 */
public class ItemSlot {

    // region Constants

    private static final DataFormat MOVE_ITEM = new DataFormat("move_item");
    private static final int LABEL_TRANSLATE_Y = 15;
    private static final int LABEL_SIZE = 10;
    private static final int LABEL_AMMOUNT_HEGHT = 15;

    private static final String STYLE_CLASS_ACCEPTING = "accepting";
    private static final String STYLE_CLASS_DECLINE = "decline";

    private static final Predicate<ItemBase> DEFAULT_FILTER = itemBase -> true;

    public static final int SLOT_SIZE = 40;
    // endregion

    // region Variables
    // Kontejner s obrázkem
    private final StackPane container = new StackPane();
    // Obrázek itemu
    private final ImageView imgItem = new ImageView();
    // Text zobrazující počet itemů
    private final Label lblAmmount = new Label();
    // Tooltip, který se zobrazí při najetí myši nad slot s itemem
    private final Tooltip tooltip = new Tooltip();

    // Id slotu
    private int id;
    private DragDropHandlers dragDropHandlers;
    private ClickListener clickListener;
    private TooltipTranslator tooltipTranslator;
    private ItemStack itemStack;
    // Výchozí item filster, který přijímá vše
    private Predicate<ItemBase> filter = DEFAULT_FILTER;
    // Kontejner pro tooltip
    private GridPane tooltipContainer;
    private Image backgroundImage;

    // endregion

    // region Constructors

    /**
     * Inicializuje nový slot
     *
     * @param id Id slotu
     * @param dragDropHandlers {@link DragDropHandlers}
     */
    public ItemSlot(int id, DragDropHandlers dragDropHandlers) {
        this.id = id;
        this.dragDropHandlers = dragDropHandlers;

        init();
    }

    // endregion

    // region Drag & Drop
    // Source slot drag events
    private final EventHandler<? super MouseEvent> onDragDetected = event -> {
        if (itemStack == null || dragDropHandlers == null) {
            event.consume();
            return;
        }

        final KeyboardService keyboardService = KeyboardService.getINSTANCE();
        final Dragboard db = imgItem.startDragAndDrop(TransferMode.ANY);
        final ClipboardContent content = new ClipboardContent();
        int ammount = 1;
        if (keyboardService.isShiftDown()) {
            ammount = itemStack.getAmmount();
        } else if (keyboardService.isCtrlDown()) {
            ammount = itemStack.getAmmount() / 2;
        }

        if (ammount == 0) {
            event.consume();
            return;
        }

        content.put(MOVE_ITEM, "");

        db.setDragView(imgItem.getImage());
        db.setContent(content);
        dragDropHandlers.onDragStart(this, new ItemStack(itemStack.getItem(), ammount,
            itemStack.getMetadata()));
        event.consume();
    };
    private final EventHandler<? super DragEvent> onDragDone = event -> {
        dragDropHandlers.onDragEnd();
        event.consume();
    };

    // Destination slot drag events
    private final EventHandler<? super DragEvent> onDragOver = event -> {
        if (event.getGestureSource() != imgItem &&
            event.getDragboard().hasContent(MOVE_ITEM)) {
            /* allow for moving */
            event.acceptTransferModes(TransferMode.MOVE);
        }

        event.consume();
    };
    private final EventHandler<? super DragEvent> onDragDropped = event -> {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasContent(MOVE_ITEM) && dragDropHandlers.acceptDrop(this)) {
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

    // Handler na změnu viditelnosti tooltipu
    private final EventHandler<WindowEvent> tooltipShowing = event -> {
        if (tooltipContainer == null) {
            final Map<String, String> tooltipMap = itemStack.getItem().getMapDescription();
            if (tooltipTranslator != null) {
                tooltipTranslator.onTooltipTranslateRequest(tooltipMap);
            }
            initTooltipContainer(tooltipMap);
        }
        tooltip.setGraphic(tooltipContainer);
    };

    // endregion

    // region Private methods

    private void init() {
        container.setPrefWidth(SLOT_SIZE);
        container.setPrefHeight(SLOT_SIZE);

        imgItem.setFitWidth(SLOT_SIZE);
        imgItem.setFitHeight(SLOT_SIZE);
        imgItem.setCursor(Cursor.HAND);

        addImageHandlers();
        tooltip.setOnShowing(tooltipShowing);

        lblAmmount.setMaxSize(SLOT_SIZE, LABEL_AMMOUNT_HEGHT);
        lblAmmount.setTranslateY(LABEL_TRANSLATE_Y);
        lblAmmount.setAlignment(Pos.BASELINE_RIGHT);
        lblAmmount.setFont(new Font(LABEL_SIZE));

        container.getStyleClass().add("item-slot");
        container.getChildren().setAll(imgItem, lblAmmount);
        container.setOnDragOver(onDragOver);
        container.setOnDragDropped(onDragDropped);
    }

    private void addImageHandlers() {
        imgItem.setOnMouseClicked(onMouseClicked);

        // Source slot drag events
        imgItem.setOnDragDetected(onDragDetected);
        imgItem.setOnDragDone(onDragDone);

        // Destination slot drag events
        imgItem.setOnDragOver(onDragOver);
        imgItem.setOnDragDropped(onDragDropped);

        Tooltip.install(imgItem, tooltip);
    }

    private void setImage(byte[] image) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        imgItem.setImage(new Image(inputStream));
    }

    private void initTooltipContainer(Map<String, String> tooltipMap) {
        tooltipContainer = new GridPane();
        tooltipContainer.getColumnConstraints().setAll(
            new ColumnConstraints(70),
            new ColumnConstraints(50)
        );
        final int[] rowIndex = {0};
        tooltipMap.forEach((key1, value1) -> {
            final Text key = new Text(key1);
            final Text value = new Text(value1);
            key.setFill(Color.WHITE);
            value.setFill(Color.WHITE);
            tooltipContainer.add(key, 0, rowIndex[0]);
            tooltipContainer.add(value, 1, rowIndex[0]);
            rowIndex[0]++;
        });
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
        lblAmmount.textProperty().unbind();
        imgItem.setImage(backgroundImage);
        lblAmmount.setText(null);
        clickListener = null;
        //removeImageHandlers();
    }

    /**
     * Pokusí se přidat item do slotu.
     * Pokud slot neobsahuje žádný item, tak je vložen celý.
     * Pokud slot obsahuje jiný item, tak se nic nevloží, jinak se pouze přičte počet
     *
     * @param itemStack {@link ItemStack} Item zabalený do stacku, který se má přidat
     */
    public void addItem(ItemStack itemStack) {
        final ItemBase item = itemStack.getItem();
        final int ammount = itemStack.getAmmount();
        if (this.itemStack == null) {
            this.itemStack = new ItemStack(itemStack);
            lblAmmount.textProperty().bind(this.itemStack.ammountProperty().asString());
            setImage(item.getImage());
            this.itemStack.getItem().imageProperty().addListener(imageChangeListener);
            this.filter = itemBase -> itemBase.getItemType() == item.getItemType();
//            addImageHandlers();
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
     * Zjistí, zda-li je slot prázdný
     *
     * @return True, pokud je slot prázdný, jinak False
     */
    public boolean isEmpty() {
        return itemStack == null;
    }

    /**
     * Zjistí, zda-li tento slot přijme požadovaný itemStack
     * Záleží na dvou faktorech:
     * 1. Jak je nastavený filtr cílového ItemStacku
     * 2. Jestli je v cílovém ItemStacku dostatečné místo pro vložení požadovaného počtu
     *
     * @param itemStack {@link ItemStack} ItemStack, který chci vložit do slotu
     * @return True, pokud lze item vložit, jinak false
     */
    public boolean acceptItem(ItemStack itemStack) {
        final boolean filterTest = filter.test(itemStack.getItem());
        if (!filterTest) {
            return false;
        }

        // Pokud je slot prázdný a prošel filtrem, tak lze předmět vložit
        if (isEmpty()) {
            return true;
        }

        // Předmět prošel testem a slot není prázdný -> lze do stacku vložit požadované množství?
        return this.itemStack.canInsertAmmount(itemStack.getAmmount());
    }

    /**
     * Zvrazní slot podle stavu
     *
     * @param state {@link HighlightState} Stav zvýraznění
     */
    public void highlight(HighlightState state) {
        switch (state) {
            case ACCEPT:
                container.getStyleClass().add(STYLE_CLASS_ACCEPTING);
                container.getStyleClass().remove(STYLE_CLASS_DECLINE);
                break;
            case DECLINE:
                container.getStyleClass().add(STYLE_CLASS_DECLINE);
                container.getStyleClass().remove(STYLE_CLASS_ACCEPTING);
                break;
            default:
                container.getStyleClass().remove(STYLE_CLASS_ACCEPTING);
                container.getStyleClass().remove(STYLE_CLASS_DECLINE);
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

    /**
     * Nastaví překladač klíčů pro tooltip
     *
     * @param tooltipTranslator {@link TooltipTranslator}
     */
    public void setTooltipTranslator(TooltipTranslator tooltipTranslator) {
        if (this.tooltipTranslator == null) {
            this.tooltipTranslator = tooltipTranslator;
        }
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        if (isEmpty()) {
            imgItem.setImage(backgroundImage);
        }
    }

    // endregion


    @Override
    public String toString() {
        String descr = "Item slot - id: " + id + " contains: ";
        return descr + (itemStack == null ? "empty" : itemStack.toString());
    }

    private final ChangeListener<byte[]> imageChangeListener = (observable, oldValue, newValue) -> setImage(
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
         * Rozhodne, zda-li je možné vložit item na cílový slot
         *
         * @param destinationSlot {@link ItemSlot} Cílový slot
         * @return True, pokud je možné vložit item na cílový slot, jinak False
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
