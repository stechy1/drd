package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.util.HashGenerator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída reprezentující záznam v databázi o jednom inventáři
 */
public class Inventory extends DatabaseItem {

    // region Variables
    // Id hrdiny, kterému patří inventář
    private final StringProperty heroId = new SimpleStringProperty();
    // Typ inventáře
    private final ObjectProperty<InventoryType> inventoryType = new SimpleObjectProperty<>();
    // Kapacita inventáře
    private final IntegerProperty capacity = new SimpleIntegerProperty();

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param inventory Kopírovaný inventář
     */
    public Inventory(Inventory inventory) {
        this(inventory.getId(), inventory.getHeroId(), inventory.getInventoryType(),
            inventory.getCapacity());
    }

    /**
     * Konstruktor pro každý databázový item
     *
     * @param id Id inventáře
     * @param heroId Id hrdiny, kterému inventář patři
     * @param inventoryType Typ inventáře
     * @param capacity Kapacita inventáře
     */
    public Inventory(String id, String heroId, InventoryType inventoryType, int capacity) {
        super(id);

        this.heroId.set(heroId);
        this.inventoryType.set(inventoryType);
        this.capacity.set(capacity);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        Inventory inventory = (Inventory) other;
        setHeroId(inventory.getHeroId());
        setInventoryType(inventory.getInventoryType());
        setCapacity(inventory.getCapacity());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new Inventory(this);
    }

    // endregion

    // region Getters & Setters

    public String getHeroId() {
        return heroId.get();
    }

    public StringProperty heroIdProperty() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId.set(heroId);
    }

    public InventoryType getInventoryType() {
        return inventoryType.get();
    }

    public ObjectProperty<InventoryType> inventoryTypeProperty() {
        return inventoryType;
    }

    public void setInventoryType(InventoryType inventoryType) {
        this.inventoryType.set(inventoryType);
    }

    public int getCapacity() {
        return capacity.get();
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity.set(capacity);
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (super.equals(o)) {
            return true;
        }

        Inventory inventory = (Inventory) o;

        if (getHeroId() != null ? !getHeroId().equals(inventory.getHeroId())
            : inventory.getHeroId() != null) {
            return false;
        }
        if (getInventoryType() != null ? !getInventoryType().equals(inventory.getInventoryType())
            : inventory.getInventoryType() != null) {
            return false;
        }
        return getCapacity() == 0 || getCapacity() == inventory.getCapacity();
    }

    @Override
    public String toString() {
        return String.format("Type: %s with size: %d", inventoryType.get().name(), getCapacity());
    }

    public static class Builder {

        private String id = HashGenerator.createHash();
        private String heroId;
        private InventoryType inventoryType;
        private int capacity;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder heroId(String heroId) {
            this.heroId = heroId;
            return this;
        }

        public Builder inventoryType(int inventoryType) {
            this.inventoryType = InventoryType.values()[inventoryType];
            return this;
        }

        public Builder inventoryType(InventoryType inventoryType) {
            this.inventoryType = inventoryType;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Inventory build() {
            return new Inventory(id, heroId, inventoryType, capacity);
        }
    }
}
