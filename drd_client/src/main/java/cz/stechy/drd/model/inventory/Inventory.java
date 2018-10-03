package cz.stechy.drd.model.inventory;

import cz.stechy.drd.db.base.Row;
import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.util.HashGenerator;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída reprezentující záznam v databázi o jednom inventáři
 */
public final class Inventory extends Row {

    // region Variables
    // Id hrdiny, kterému patří inventář
    private final StringProperty heroId = new SimpleStringProperty(this, "heroId");
    // Typ inventáře
    private final ObjectProperty<InventoryType> inventoryType = new SimpleObjectProperty<>(this, "inventoryType");
    // Kapacita inventáře
    private final IntegerProperty capacity = new SimpleIntegerProperty(this, "capacity");

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param inventory Kopírovaný inventář
     */
    private Inventory(Inventory inventory) {
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
    private Inventory(String id, String heroId, InventoryType inventoryType, int capacity) {
        super(id);

        this.heroId.set(heroId);
        this.inventoryType.set(inventoryType);
        this.capacity.set(capacity);
    }

    // endregion

    // region Public methods

    @Override
    public void update(Row other) {
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

    public final String getHeroId() {
        return heroId.get();
    }

    public final ReadOnlyStringProperty heroIdProperty() {
        return heroId;
    }

    private void setHeroId(String heroId) {
        this.heroId.set(heroId);
    }

    public final InventoryType getInventoryType() {
        return inventoryType.get();
    }

    public final ReadOnlyObjectProperty<InventoryType> inventoryTypeProperty() {
        return inventoryType;
    }

    private void setInventoryType(InventoryType inventoryType) {
        this.inventoryType.set(inventoryType);
    }

    public final int getCapacity() {
        return capacity.get();
    }

    public final ReadOnlyIntegerProperty capacityProperty() {
        return capacity;
    }

    private void setCapacity(int capacity) {
        this.capacity.set(capacity);
    }

    // endregion

    @Override
    public List<String> getDiffList(Row other) {
        final List<String> diffList = super.getDiffList(other);
        final Inventory inventory = (Inventory) other;

        if (!Objects.equals(this.getHeroId(), inventory.getHeroId())) {
            diffList.add(heroId.getName());
        }

        if (!Objects.equals(this.getInventoryType(), inventory.getInventoryType())) {
            diffList.add(inventoryType.getName());
        }

        if (!Objects.equals(this.getCapacity(), inventory.getCapacity())) {
            diffList.add(capacity.getName());
        }

        return diffList;
    }

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
