package cz.stechy.drd.model.shop.entry;

import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.item.ItemBase;
import java.io.ByteArrayInputStream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Položka v obchodě
 */
public abstract class ShopEntry {

    // region Variables

    protected final ItemBase itemBase;
    protected final StringProperty id = new SimpleStringProperty();
    protected final StringProperty name = new SimpleStringProperty();
    protected final StringProperty author = new SimpleStringProperty();
    protected final StringProperty description = new SimpleStringProperty();
    protected final Money price;
    protected final MaxActValue ammount = new MaxActValue(Integer.MAX_VALUE);
    protected final IntegerProperty weight = new SimpleIntegerProperty();
    protected final BooleanProperty inShoppingCart = new SimpleBooleanProperty();
    protected final BooleanProperty downloaded = new SimpleBooleanProperty();
    protected final BooleanProperty uploaded = new SimpleBooleanProperty();
    protected final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

    // endregion

    // region Constructors

    /**
     * Vytvoří novou abstrakní nákupní položku
     */
    ShopEntry(ItemBase itemBase) {
        ammount.minValueProperty().bind(Bindings
            .when(inShoppingCart)
            .then(1)
            .otherwise(0));

        imageRaw.addListener((observable, oldValue, newValue) -> {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(newValue);
            image.set(new Image(inputStream));
        });

        this.itemBase = itemBase;
        this.id.bind(itemBase.idProperty());
        this.name.bind(itemBase.nameProperty());
        this.description.bind(itemBase.descriptionProperty());
        this.price = new Money(itemBase.getPrice());
        this.weight.bind(itemBase.weightProperty());
        this.author.bind(itemBase.authorProperty());
        this.downloaded.bind(itemBase.downloadedProperty());
        this.uploaded.bind(itemBase.uploadedProperty());
        this.imageRaw.bind(itemBase.imageProperty());
    }

    // endregion

    // region Public methods

    public void update(DatabaseItem other) {
        itemBase.update(other);
    }

    // endregion

    // region Getters & Setters

    public ItemBase getItemBase() {
        return itemBase;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

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

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public MaxActValue getAmmount() {
        return ammount;
    }

    public Money getPrice() {
        return price;
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

    public boolean isInShoppingCart() {
        return inShoppingCart.get();
    }

    public BooleanProperty inShoppingCartProperty() {
        return inShoppingCart;
    }

    public void setInShoppingCart(boolean inShoppingCart) {
        this.inShoppingCart.set(inShoppingCart);
    }

    public boolean isDownloaded() {
        return downloaded.get();
    }

    public BooleanProperty downloadedProperty() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded.set(downloaded);
    }

    public boolean isUploaded() {
        return uploaded.get();
    }

    public BooleanProperty uploadedProperty() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded.set(uploaded);
    }

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
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

        ShopEntry shopEntry = (ShopEntry) o;

        return itemBase.equals(shopEntry.itemBase);
    }

    @Override
    public int hashCode() {
        return itemBase.hashCode();
    }
}
