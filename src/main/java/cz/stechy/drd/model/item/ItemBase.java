package cz.stechy.drd.model.item;

import cz.stechy.drd.Money;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.db.base.OnlineItem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Základní třída pro všechny itemy ve světě dračího doupěte
 */
public abstract class ItemBase extends OnlineItem {

    // region Variables

    // ID předmětu
    // Název předmětu
    protected final StringProperty name = new SimpleStringProperty();
    // Popis předmětu
    protected final StringProperty description = new SimpleStringProperty();
    // Váha předmětu
    protected final IntegerProperty weight = new SimpleIntegerProperty();
    // Cena předmětu
    protected final Money price = new Money();
    // Obrázek předmětu v Base64
    protected final ObjectProperty<byte[]> image = new SimpleObjectProperty<>();
    // endregion

    // region Constructors

    /**
     * Konstruktor pro každý předmět
     *
     * @param id Id předmětu
     * @param author Autor předmětu
     * @param name Název předmětu
     * @param description Popis předmětu
     * @param weight Váha předmětu
     * @param price Cena předmětu
     * @param image Obrázek předmětu
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    ItemBase(String id, String author, String name, String description, int weight,
        int price, byte[] image, boolean downloaded, boolean uploaded) {
        super(id, author, downloaded, uploaded);
        this.name.setValue(name);
        this.description.setValue(description);
        this.weight.setValue(weight);
        this.price.setRaw(price);
        this.image.set(image);
    }

    // endregion

    // region Getters & Setters

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public int getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    public Money getPrice() {
        return price;
    }

    public byte[] getImage() {
        return image.get();
    }

    public ObjectProperty<byte[]> imageProperty() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image.set(image);
    }

    // endregion

    // region Public methods

    /**
     * Aktualizuje parametry
     *
     * @param other {@link ItemBase} Druhý předmět, ze kterého se aktualizují parametry
     */
    public void update(DatabaseItem other) {
        super.update(other);
        ItemBase item = (ItemBase) other;
        this.id.setValue(item.getId());
        this.name.setValue(item.getName());
        this.description.setValue(item.getDescription());
        this.weight.setValue(item.getWeight());
        this.price.setRaw(item.price.getRaw());
        this.author.setValue(item.getAuthor());
        this.image.setValue(item.getImage());
    }

    /**
     * @return Vrátí typ předmětu
     */
    public abstract ItemType getItemType();

    // endregion

    @Override
    public String toString() {
        return getName();
    }
}
