package cz.stechy.drd.model.item;

import cz.stechy.drd.db.base.OnlineRecord;
import cz.stechy.drd.model.IClonable;
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
public class OnlineCollection extends OnlineRecord {

    // region Variables

    // Název kolekce
    private final StringProperty name = new SimpleStringProperty(this, "name");
    // Kolekce záznamů předmětů
    private final ObservableList<String> items = FXCollections.observableArrayList();
    // Kolekce záznamů nestvůr
    private final ObservableList<String> bestiary = FXCollections.observableArrayList();
    // Kolekce záznamů kouzel
    private final ObservableList<String> spells = FXCollections.observableArrayList();

    // endregion

    // region Constructors

    private OnlineCollection(String id, String name, String author, Collection<String> items, Collection<String> bestiary, Collection<String> spells) {
        super(id, author, false, true);
//        setId(id);
        setName(name);
//        setAuthor(author);
        this.items.setAll(items);
        this.bestiary.setAll(bestiary);
        this.spells.setAll(spells);
    }

    // endregion

    // region Public methods

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new OnlineCollection(getId(), getName(), getAuthor(), items, bestiary, spells);
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

    public ObservableList<String> getCollection(CollectionType type) {
        switch (type) {
            case ITEMS:
                return items;
            case BESTIARY:
                return bestiary;
            case SPELLS:
                return spells;
            default:
                throw new RuntimeException("Neplatný argument");
        }
    }

    // endregion

    public enum CollectionType {
        ITEMS, BESTIARY, SPELLS
    }

    public static final class Builder {

        private String id = HashGenerator.createHash();
        private String name;
        private String author;
        private Collection<String> items = Collections.emptyList();
        private Collection<String> bestiary = Collections.emptyList();
        private Collection<String> spells = Collections.emptyList();

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

        public OnlineCollection build() {
            return new OnlineCollection(id, name, author, items, bestiary, spells);
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
        OnlineCollection that = (OnlineCollection) o;
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
