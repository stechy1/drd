package cz.stechy.drd.model.item;

import cz.stechy.drd.Money;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.db.base.OnlineItem;
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
 * Základní třída pro všechny itemy ve světě dračího doupěte
 */
public abstract class ItemBase extends OnlineItem {

    // region Variables

    // ID předmětu
    // Název předmětu
    protected final StringProperty  name = new SimpleStringProperty(this, "name");
    // Popis předmětu
    protected final StringProperty description = new SimpleStringProperty(this, "description");
    // Váha předmětu
    protected final IntegerProperty weight = new SimpleIntegerProperty(this, "weight");
    // Cena předmětu
    protected final Money price = new Money();
    // Obrázek předmětu v Base64
    protected final ObjectProperty<byte[]> image = new SimpleObjectProperty<>(this, "image");
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

        setName(name);
        setDescription(description);
        setWeight(weight);
        this.price.setRaw(price);
        setImage(image);
    }

    // endregion

    // region Getters & Setters

    public final String getName() {
        return name.get();
    }

    public final ReadOnlyStringProperty nameProperty() {
        return name;
    }

    private void setName(String name) {
        this.name.set(name);
    }

    public final String getDescription() {
        return description.get();
    }

    public final ReadOnlyStringProperty descriptionProperty() {
        return description;
    }

    private void setDescription(String description) {
        this.description.set(description);
    }

    public final int getWeight() {
        return weight.get();
    }

    public final ReadOnlyIntegerProperty weightProperty() {
        return weight;
    }

    private void setWeight(int weight) {
        this.weight.set(weight);
    }

    public final Money getPrice() {
        return price;
    }

    public final byte[] getImage() {
        return image.get();
    }

    public final ReadOnlyObjectProperty<byte[]> imageProperty() {
        return image;
    }

    private void setImage(byte[] image) {
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
        setId(item.getId());
        setName(item.getName());
        setDescription(item.getDescription());
        setWeight(item.getWeight());
        this.price.setRaw(price.getRaw());
        setAuthor(item.getAuthor());
        setImage(item.getImage());
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
