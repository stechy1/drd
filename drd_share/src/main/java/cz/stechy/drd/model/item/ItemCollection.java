package cz.stechy.drd.model.item;

import cz.stechy.drd.util.HashGenerator;
import java.util.Collection;
import java.util.Collections;
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
    // Kolekce záznamů předmětů
    private final ObservableList<String> items = FXCollections.observableArrayList();
    // Kolekce záznamů nestvůr
    private final ObservableList<String> bestiary = FXCollections.observableArrayList();
    // Kolekce záznamů kouzel
    private final ObservableList<String> spells = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    private ItemCollection(String id, String name, String author,
        Collection<String> items, Collection<String> bestiary, Collection<String> spells) {
        setId(id);
        setName(name);
        setAuthor(author);
        this.items.setAll(items);
        this.bestiary.setAll(bestiary);
        this.spells.setAll(spells);
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

    public ObservableList<String> getItems() {
        return items;
    }

    public ObservableList<String> getBestiary() {
        return bestiary;
    }

    public ObservableList<String> getSpells() {
        return spells;
    }

    // endregion

    public static final class Builder {

        private String id = HashGenerator.createHash();
        private String name;
        private String author;
        private Collection<String> items = Collections.EMPTY_LIST;
        private Collection<String> bestiary = Collections.EMPTY_LIST;
        private Collection<String> spells = Collections.EMPTY_LIST;

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

        public Builder items(Collection<String> items) {
            this.items = items;
            return this;
        }

        public Builder bestiary(Collection<String> bestiary) {
            this.bestiary = bestiary;
            return this;
        }

        public Builder spells(Collection<String> spells) {
            this.spells = spells;
            return this;
        }

        public ItemCollection build() {
            return new ItemCollection(id, name, author, items, bestiary, spells);
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
