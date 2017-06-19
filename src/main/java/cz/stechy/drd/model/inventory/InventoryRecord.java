package cz.stechy.drd.model.inventory;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.util.HashGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída představující jeden záznam o itemu v inventáři v daztabázi
 */
public class InventoryRecord extends DatabaseItem {

    // region Variables

    // Id inventáře
    private final StringProperty inventoryId = new SimpleStringProperty();
    // Id itemu, který je v slotu
    private final StringProperty itemId = new SimpleStringProperty();
    // Počet itemů ve slotu
    private final IntegerProperty ammount = new SimpleIntegerProperty();
    // Id slotu, ve kterém se item nacházi
    private final IntegerProperty slotId = new SimpleIntegerProperty();
    // Pomocná data o záznamu
    private final Metadata metadata = new Metadata();

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param record Kopírovaný záznam
     */
    private InventoryRecord(InventoryRecord record) {
        this(record.getId(), record.getInventoryId(), record.getSlotId(), record.getItemId(),
            record.getAmmount(), new Metadata(record.getMetadata()));
    }

    /**
     * Inicializuje nový záznam o itemu
     *  @param id Id záznamu
     * @param inventoryId Id inventáře, do kterého záznam patří
     * @param slotId Id slotu, který obsahuje item
     * @param itemId Id itemu, který je obsažen ve slozu
     * @param ammount Množství itemu ve slotu
     * @param metadata {@link Metadata}
     */
    private InventoryRecord(String id, String inventoryId, int slotId, String itemId, int ammount,
        Metadata metadata) {
        super(id);

        this.inventoryId.set(inventoryId);
        this.itemId.set(itemId);
        this.ammount.set(ammount);
        this.slotId.set(slotId);
        this.metadata.putAll(metadata);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        InventoryRecord record = (InventoryRecord) other;
        this.inventoryId.setValue(record.getInventoryId());
        this.itemId.setValue(record.getItemId());
        this.ammount.setValue(record.getAmmount());
        this.slotId.setValue(record.getSlotId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new InventoryRecord(this);
    }

    // endregion

    // region Getters & Setters

    public String getInventoryId() {
        return inventoryId.get();
    }

    public StringProperty inventoryIdProperty() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId.set(inventoryId);
    }

    public String getItemId() {
        return itemId.get();
    }

    public StringProperty itemIdProperty() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId.set(itemId);
    }

    public int getAmmount() {
        return ammount.get();
    }

    public IntegerProperty ammountProperty() {
        return ammount;
    }

    public void setAmmount(int ammount) {
        this.ammount.set(ammount);
    }

    public int getSlotId() {
        return slotId.get();
    }

    public IntegerProperty slotIdProperty() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId.set(slotId);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    // endregion

    @Override
    public String toString() {
        return String.format("InventoryRecord of %s with ammount: %d in slot: %d",
            getItemId(), getAmmount(), getSlotId());
    }

    public static class Builder {

        private String id = HashGenerator.createHash();
        private String inventoryId;
        private int slotId;
        private String itemId;
        private int ammount;
        private Metadata metadata = new Metadata();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder inventoryId(String inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public Builder slotId(int slotId) {
            this.slotId = slotId;
            return this;
        }

        public Builder itemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder ammount(int ammount) {
            this.ammount = ammount;
            return this;
        }

        public Builder metadata(Metadata metadata) {
            this.metadata = metadata;

            return this;
        }

        public InventoryRecord build() {
            return new InventoryRecord(id, inventoryId, slotId, itemId, ammount, metadata);
        }

    }

    public static class Metadata extends HashMap<String, Object> {

        public Metadata() {
            super();
        }

        public Metadata(Metadata metadata) {
            super(metadata);
        }

        public static byte[] serialize(Metadata metadata) {
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (final ObjectOutputStream out = new ObjectOutputStream(bout)) {
                out.writeObject(metadata);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bout.toByteArray();
        }

        public static Metadata deserialize(byte[] raw) {
            Metadata metadata = null;
            final ByteArrayInputStream bais = new ByteArrayInputStream(raw);
            try (final ObjectInputStream in = new ObjectInputStream(bais)) {
                metadata = (Metadata) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            return metadata;
        }
    }
}