package cz.stechy.drd.model.db.base;

import cz.stechy.drd.model.IClonable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Základní třída pro všecny itemy, které se budou ukládat do databáze
 */
public abstract class DatabaseItem implements IClonable {

    // region Variables

    // Id
    protected final StringProperty id = new SimpleStringProperty();

    // endregion

    // region Constructors

    /**
     * Konstruktor pro každý databázový item
     *
     * @param id Id předmětu
     */
    protected DatabaseItem(String id) {
        this.id.set(id);
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

    // endregion

    // region Public methods

    /**
     * Aktualizuje parametry objektu z jiného objektu
     *
     * @param other Jiný objekt, ze kterého se převezmou parametry
     */
    public void update(DatabaseItem other) {
        this.id.setValue(other.getId());
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

        DatabaseItem that = (DatabaseItem) o;

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
