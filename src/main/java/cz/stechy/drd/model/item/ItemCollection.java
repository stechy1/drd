package cz.stechy.drd.model.item;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.WithImage;
import cz.stechy.drd.model.db.base.DatabaseItem;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Třída reprezentující jednu kolekci předmětů
 */
public class ItemCollection extends DatabaseItem {

    // region Variables

    // Název kolekce
    private final StringProperty name = new SimpleStringProperty(this, "name");
    // Autor kolekce
    private final StringProperty author = new SimpleStringProperty(this, "author");
    // Seznam předmětů, které jsou v kolekci
    private final ObservableList<ItemEntry> items = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    public ItemCollection(String id, String name, String author, List<ItemEntry> entries) {
        super(id);
        setName(name);
        setAuthor(author);
        items.setAll(entries);
    }

    // endregion

    // region Public methods

    @Override
    public <T extends IClonable> T duplicate() {
        return null;
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

    public final String getAuthor() {
        return author.get();
    }

    public final ReadOnlyStringProperty authorProperty() {
        return author;
    }

    private void setAuthor(String author) {
        this.author.set(author);
    }

    public final ObservableList<ItemEntry> getItems() {
        return items;
    }

    // endregion

    public static final class ItemEntry implements WithImage {
        private final StringProperty id = new SimpleStringProperty(this, "id");
        private final StringProperty name = new SimpleStringProperty(this, "name");
        private final ObjectProperty<byte[]> image = new SimpleObjectProperty<>(this, "image");

        public ItemEntry(String id, String name, byte[] image) {
            setId(id);
            setName(name);
            setImage(image);
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

        public byte[] getImage() {
            return image.get();
        }

        public ObjectProperty<byte[]> imageProperty() {
            return image;
        }

        public void setImage(byte[] image) {
            this.image.set(image);
        }
    }

    public static final class Builder {
        private String id;
        private String name;
        private String author;
        private List<ItemEntry> entries = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder entry(ItemEntry entry) {
            entries.add(entry);
            return this;
        }

        public ItemCollection build() {
            return new ItemCollection(id, name, author, entries);
        }
    }
}
