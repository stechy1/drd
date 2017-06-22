package cz.stechy.drd.model.entity;

import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.db.base.OnlineItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Základní společná třída pro všechny entity ve světě Dračího doupěte
 */
public abstract class EntityBase extends OnlineItem {

    // region Variales

    // Jméno
    protected final StringProperty name = new SimpleStringProperty();
    // Popis entity
    protected final StringProperty description = new SimpleStringProperty();
    // Aktuální počet životů
    protected final MaxActValue live = new MaxActValue();
    // Aktuální počet magů
    protected final MaxActValue mag = new MaxActValue();
    // Přesvědčení
    protected final ObjectProperty<Conviction> conviction = new SimpleObjectProperty<>(
        Conviction.NEUTRAL);
    // Velikost
    protected final ObjectProperty<Height> height = new SimpleObjectProperty<>(Height.B);

    // endregion

    // region Constructors

    /**
     * Vytvoří novou entitu
     *
     * @param id Id entity
     * @param author Autor entity
     * @param name Název entity
     * @param description Popis entity
     * @param live Aktuální pčet životů
     * @param maxLive Maximální počet životů
     * @param mag Aktuální počet magů
     * @param maxMag Maximální počet magů
     * @param conviction Přesvědčení
     * @param height Výška
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    protected EntityBase(String id, String author, String name, String description, int live,
        int maxLive, int mag, int maxMag, Conviction conviction, Height height, boolean downloaded,
        boolean uploaded) {
        super(id, author, downloaded, uploaded);

        setId(id);
        setName(name);
        setDescription(description);
        this.live.setMaxValue(maxLive);
        this.live.setActValue(live);
        this.mag.setMaxValue(maxMag);
        this.mag.setActValue(mag);
        setConviction(conviction);
        setHeight(height);
    }

    // endregion

    // region Getters & Setters

    public String getName() {
        return name.get();
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public ReadOnlyStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public MaxActValue getLive() {
        return live;
    }

    public MaxActValue getMag() {
        return mag;
    }

    public Conviction getConviction() {
        return conviction.get();
    }

    public ReadOnlyObjectProperty<Conviction> convictionProperty() {
        return conviction;
    }

    public void setConviction(Conviction conviction) {
        this.conviction.set(conviction);
    }

    public Height getHeight() {
        return height.get();
    }

    public ReadOnlyObjectProperty<Height> heightProperty() {
        return height;
    }

    public void setHeight(Height height) {
        this.height.set(height);
    }

    // endregion

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        EntityBase entity = (EntityBase) other;

        setName(entity.getName());
        setDescription(entity.getDescription());
        this.live.setMaxValue(entity.live.getMaxValue());
        this.live.setActValue(entity.live.getActValue());
        this.mag.setMaxValue(entity.mag.getMaxValue());
        this.mag.setActValue(entity.mag.getActValue());
        setConviction(entity.getConviction());
        setHeight(entity.getHeight());
    }

    @Override
    public String toString() {
        return getName();
    }
}
