package cz.stechy.drd.app.spellbook;

import cz.stechy.drd.model.DiffEntry.DiffEntryTuple;
import cz.stechy.drd.model.spell.Spell;
import cz.stechy.drd.model.spell.Spell.SpellProfessionType;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import java.io.ByteArrayInputStream;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;

/**
 * Třída reprezentující jeden záznam v tabulce
 */
public final class SpellEntry {

    // region Variables

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final StringProperty magicName = new SimpleStringProperty(this, "magicName");
    private final StringProperty author = new SimpleStringProperty(this, "author");
    private final ObjectProperty<SpellProfessionType> type = new SimpleObjectProperty<>(this,
        "type");
    private final ObjectProperty<ISpellPrice> price = new SimpleObjectProperty<>(this, "price");
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
    private final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>(this, "imageRaw");
    private final BooleanProperty downloaded = new SimpleBooleanProperty(this, "downloaded");
    private final BooleanProperty uploaded = new SimpleBooleanProperty(this, "uploaded");
    protected final ReadOnlyBooleanWrapper hasDiff = new ReadOnlyBooleanWrapper();
    protected final ObservableMap<String, DiffEntryTuple> diffMap = FXCollections.observableHashMap();

    private final Spell spellBase;

    // endregion

    // region Constructors

    public SpellEntry(Spell spell) {
        this.spellBase = spell;

        this.id.bind(spell.idProperty());
        this.name.bind(spell.nameProperty());
        this.magicName.bind(spell.magicNameProperty());
        this.author.bind(spell.authorProperty());
        this.type.bind(spell.typeProperty());
        this.price.bind(spell.priceProperty());
        this.downloaded.bind(spell.downloadedProperty());
        this.uploaded.bind(spell.uploadedProperty());

        image.bind(Bindings.createObjectBinding(() -> {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(spell.getImage());
            return new Image(inputStream);
        }, imageRaw));

        hasDiff.bind(Bindings.createBooleanBinding(() -> !diffMap.isEmpty(), diffMap));
    }

    // endregion

    // region Getters & Setters

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

    public String getMagicName() {
        return magicName.get();
    }

    public StringProperty magicNameProperty() {
        return magicName;
    }

    public void setMagicName(String magicName) {
        this.magicName.set(magicName);
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

    public SpellProfessionType getType() {
        return type.get();
    }

    public ObjectProperty<SpellProfessionType> typeProperty() {
        return type;
    }

    public void setType(SpellProfessionType type) {
        this.type.set(type);
    }

    public ISpellPrice getPrice() {
        return price.get();
    }

    public ObjectProperty<ISpellPrice> priceProperty() {
        return price;
    }

    public void setPrice(ISpellPrice price) {
        this.price.set(price);
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

    public byte[] getImageRaw() {
        return imageRaw.get();
    }

    public ObjectProperty<byte[]> imageRawProperty() {
        return imageRaw;
    }

    public void setImageRaw(byte[] imageRaw) {
        this.imageRaw.set(imageRaw);
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

    public boolean hasDiff() {
        return hasDiff.get();
    }

    public ReadOnlyBooleanProperty hasDiffProperty() {
        return hasDiff;
    }

    public ObservableMap<String, DiffEntryTuple> getDiffMap() {
        return diffMap;
    }

    public void setDiffMap(Map<String, DiffEntryTuple> diffMap) {
        clearDiffMap();
        this.diffMap.putAll(diffMap);
    }

    public void clearDiffMap() {
        this.diffMap.clear();
    }

    public Spell getSpellBase() {
        return spellBase;
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

        SpellEntry spell = (SpellEntry) o;

        return spellBase.equals(spell.spellBase);
    }

    @Override
    public int hashCode() {
        return spellBase.hashCode();
    }

    @Override
    public String toString() {
        return spellBase.toString();
    }
}
