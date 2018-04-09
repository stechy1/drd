package cz.stechy.drd.model.item;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.db.base.DatabaseItem;
import cz.stechy.drd.util.HashGenerator;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída reprezentující jednu kolekci předmětů
 */
public class ItemCollection extends DatabaseItem {

    // region Variables

    // Název kolekce
    private final StringProperty name = new SimpleStringProperty(this, "name");
    // Autor kolekce
    private final StringProperty author = new SimpleStringProperty(this, "author");

    // endregion

    // region Constructors

    private ItemCollection(String id, String name, String author) {
        super(id);
        setName(name);
        setAuthor(author);
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

    // endregion

    public static final class Builder {
        private String id = HashGenerator.createHash();
        private String name;
        private String author;

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

        public ItemCollection build() {
            return new ItemCollection(id, name, author);
        }
    }
}
