package cz.stechy.drd.model.item;

import cz.stechy.drd.util.HashGenerator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Třída reprezentující jednu kolekci předmětů
 */
public class ItemCollection {

    // region Variables

    // Id kolekce
    private final StringProperty id = new SimpleStringProperty(this, "id");
    // Název kolekce
    private final StringProperty name = new SimpleStringProperty(this, "name");
    // Autor kolekce
    private final StringProperty author = new SimpleStringProperty(this, "author");
    // Kolekce záznamů
    private final ObservableList<String> records = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    private ItemCollection(String id, String name, String author, Collection<String> records) {
        setId(id);
        setName(name);
        setAuthor(author);
        this.records.setAll(records);
    }

    // endregion

    // region Public methods
    // endregion

    // region Getters & Setters

    public final String getId() {
        return id.get();
    }

    public final ReadOnlyStringProperty idProperty() {
        return id;
    }

    public final void setId(String id) {
        this.id.set(id);
    }

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

    public ObservableList<String> getRecords() {
        return records;
    }

    // endregion

    public static final class Builder {

        private String id = HashGenerator.createHash();
        private String name;
        private String author;
        private Collection<String> records = Arrays.asList();

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

        public Builder records(Collection<String> records) {
            this.records = records;
            return this;
        }

        public ItemCollection build() {
            return new ItemCollection(id, name, author, records);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemCollection that = (ItemCollection) o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getName(), that.getName()) &&
            Objects.equals(getAuthor(), that.getAuthor());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName(), getAuthor());
    }

    @Override
    public String toString() {
        return getName();
    }
}
