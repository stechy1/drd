package cz.stechy.drd.model.item;

import cz.stechy.drd.model.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.model.WithImage;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.db.base.OnlineItem;
import java.util.LinkedHashMap;
import java.util.Map;
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
public abstract class ItemBase extends OnlineItem implements IDescriptable, WithImage {

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
    // Maximální počet předmětů, který může být v jednom stacku ve slotu inventáře
    protected final IntegerProperty stackSize = new SimpleIntegerProperty(this, "stackSize");
    // endregion

    // region Constructors

    /**
     * Konstruktor pro každý předmět
     *  @param id Id předmětu
     * @param author Autor předmětu
     * @param name Název předmětu
     * @param description Popis předmětu
     * @param weight Váha předmětu
     * @param price Cena předmětu
     * @param image Obrázek předmětu
     * @param stackSize Maximální počet předmětů, který může být v jednom stacku ve slotu inventáře
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    ItemBase(String id, String author, String name, String description, int weight,
        int price, byte[] image, int stackSize, boolean downloaded, boolean uploaded) {
        super(id, author, downloaded, uploaded);

        setName(name);
        setDescription(description);
        setWeight(weight);
        this.price.setRaw(price);
        setImage(image);
        setStackSize(stackSize);
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

    public final int getStackSize() {
        return stackSize.get();
    }

    public final ReadOnlyIntegerProperty stackSizeProperty() {
        return stackSize;
    }

    private void setStackSize(int stackSize) {
        this.stackSize.set(stackSize);
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
        this.price.setRaw(item.getPrice().getRaw());
        setAuthor(item.getAuthor());
        setImage(item.getImage());
    }

    @Override
    public Map<String, String> getMapDescription() {
        final Map<String, String> map = new LinkedHashMap<>();

        map.put(R.Translate.ITEM_NAME, name.getValue());
        map.put(R.Translate.ITEM_WEIGHT, weight.getValue().toString());
        map.put(R.Translate.ITEM_PRICE, price.toString());

        return map;
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
