package cz.stechy.drd.model.item;

import cz.stechy.drd.R;
import cz.stechy.drd.db.base.DatabaseItem;
import cz.stechy.drd.model.IClonable;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Třída představující batoh
 */
public final class Backpack extends ItemBase {

    // region Constants

    public static final String CHILD_INVENTORY_ID = "child_inventory_id";

    // endregion

    // region Variables

    // Maximální nosnost baťohu
    private final IntegerProperty maxLoad = new SimpleIntegerProperty(this, "maxLoad");
    // Počet slotů v batohu
    private final ObjectProperty<Size> size = new SimpleObjectProperty<>(this, "size");

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param backpack Kopírovaný batoh
     */
    private Backpack(Backpack backpack) {
        this(backpack.getId(), backpack.getAuthor(), backpack.getName(), backpack.getDescription(),
            backpack.getWeight(), backpack.getPrice().getRaw(), backpack.getMaxLoad(),
            backpack.getSize(), backpack.getImage(), backpack.getStackSize(),
            backpack.isDownloaded(), backpack.isUploaded());
    }

    /**
     * Konstruktor batohu
     *
     * @param id Id batohu
     * @param author Autor batohu
     * @param name Název batohu
     * @param description Popis batohu
     * @param weight Váha batohu
     * @param maxLoad Maximální nosnost batohu
     * @param price Cena batohu
     * @param size Velikost batohu
     * @param image Obrázek batohu
     * @param stackSize Maximální počet batohů, který může být v jednom stacku ve slotu inventáře
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    private Backpack(String id, String author, String name, String description, int weight,
        int price, int maxLoad, Size size, byte[] image,
        int stackSize, boolean downloaded, boolean uploaded) {
        super(id, author, name, description, weight, price, image, stackSize, downloaded, uploaded);

        setMaxLoad(maxLoad);
        setSize(size);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        Backpack backpack = (Backpack) other;
        setMaxLoad(backpack.getMaxLoad());
        setSize(backpack.getSize());
    }

    @Override
    public Map<String, String> getMapDescription() {
        final Map<String, String> map = super.getMapDescription();
        map.put(R.Translate.ITEM_BACKPACK_SIZE, getSize().name());
        map.put(R.Translate.ITEM_MAX_LOAD, String.valueOf(getMaxLoad()));

        return map;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.BACKPACK;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new Backpack(this);
    }

    // endregion

    // region Getters & Setters

    public final Integer getMaxLoad() {
        return maxLoad.get();
    }

    public final ReadOnlyIntegerProperty maxLoadProperty() {
        return maxLoad;
    }

    private void setMaxLoad(Integer max_load) {
        this.maxLoad.set(max_load);
    }

    public final Size getSize() {
        return size.get();
    }

    public final ReadOnlyObjectProperty<Size> sizeProperty() {
        return size;
    }

    private void setSize(Size size) {
        this.size.set(size);
    }

    // endregion

    public static class Builder {

        private String id;
        private String author;
        private String name;
        private String description;
        private int weight;
        private int price;
        private int maxLoad;
        private byte[] image;
        private int stackSize;
        private boolean downloaded;
        private boolean uploaded;
        private Size size;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder maxLoad(int maxLoad) {
            this.maxLoad = maxLoad;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder size(int size) {
            this.size = Size.values()[size];
            return this;
        }

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder stackSize(int stackSize) {
            this.stackSize = stackSize;
            return this;
        }

        public Builder downloaded(boolean downloaded) {
            this.downloaded = downloaded;
            return this;
        }

        public Builder uploaded(boolean uploaded) {
            this.uploaded = uploaded;
            return this;
        }

        public Backpack build() {
            return new Backpack(id, author, name, description, weight, price, maxLoad, size,
                image, stackSize, downloaded, uploaded);
        }
    }

    public enum Size {
        SMALL(10), MEDIUM(20), LARGE(40);

        public final int size;

        Size(int size) {
            this.size = size;
        }
    }

}
