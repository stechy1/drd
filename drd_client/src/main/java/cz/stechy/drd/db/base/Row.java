package cz.stechy.drd.db.base;

import static cz.stechy.drd.annotation.dao.Column.Specification.NOT_NULL;
import static cz.stechy.drd.annotation.dao.Column.Specification.PRIMARY_KEY;
import static cz.stechy.drd.annotation.dao.Column.Specification.UNIQUE;

import cz.stechy.drd.R.Translate;
import cz.stechy.drd.annotation.TranslateEntry;
import cz.stechy.drd.annotation.dao.Column;
import cz.stechy.drd.model.IClonable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Základní třída pro všecny itemy, které se budou ukládat do databáze
 */
public abstract class Row implements IClonable {

    // region Variables

    // Id
    @Column(columnName = "id", clazz = "String", specifications = {PRIMARY_KEY, NOT_NULL, UNIQUE})
    @TranslateEntry(key = Translate.ENTRY_ID)
    protected final StringProperty id = new SimpleStringProperty(this, "id");

    // endregion

    // region Constructors

    /**
     * Konstruktor pro každý databázový item
     *
     * @param id Id předmětu
     */
    protected Row(String id) {
        setId(id);
    }

    // endregion

    // region Public methods

    /**
     * Aktualizuje parametry objektu z jiného objektu
     *
     * @param other Jiný objekt, ze kterého se převezmou parametry
     */
    public void update(Row other) {
        this.id.setValue(other.getId());
    }

    /**
     * Vrátí kolekci, která obsahuje názvy vlastností, které jsou rozdílné s porovnávaným záznamem
     *
     * @param other {@link Row} Porovnávaný záznam
     * @return {@link List<String>} Kolekci vlastností s různými hodnotami
     */
    public List<String> getDiffList(Row other) {
        final List<String> diffList = new LinkedList<>();

        if (!Objects.equals(this.getId(), other.getId())) {
            diffList.add(id.getName());
        }

        return diffList;
    }

    // endregion

    // region Getters & Setters

    public String getId() {
        return id.get();
    }

    public ReadOnlyStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
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

        Row that = (Row) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getId();
    }
}
