package cz.stechy.drd.model.entity;

import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.db.base.OnlineItem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Základní společná třída pro všechny entity ve světě Dračího doupěte
 */
public abstract class EntityBase extends OnlineItem implements IAggressive {

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
    // Útočné číslo
    protected final IntegerProperty attackNumber = new SimpleIntegerProperty();
    // Obranné číslo
    protected final IntegerProperty defenceNumber = new SimpleIntegerProperty(this,
        "defenceNumber");
    protected final BooleanProperty alive = new SimpleBooleanProperty();

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

        initBindings();

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

    // region Private methods

    private void initBindings() {
        alive.bind(Bindings.createBooleanBinding(() -> getLive().getActValue().intValue() > 0,
            live.actValueProperty()));
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        EntityBase entity = (EntityBase) other;

        setName(entity.getName());
        setDescription(entity.getDescription());
        this.live.update(entity.live);
        this.mag.update(entity.mag);
        setConviction(entity.getConviction());
        setHeight(entity.getHeight());
        if (!attackNumber.isBound()) {
            setAttackNumber(entity.getAttackNumber());
        }
        if (!defenceNumber.isBound()) {
            setDefenceNumber(entity.getDefenceNumber());
        }
    }

    @Override
    public void subtractLive(int live) {
        this.live.subtract(live);
        System.out.println(getLive().getActValue());
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

    public final String getDescription() {
        return description.get();
    }

    public final ReadOnlyStringProperty descriptionProperty() {
        return description;
    }

    private void setDescription(String description) {
        this.description.set(description);
    }

    public final MaxActValue getLive() {
        return live;
    }

    public final MaxActValue getMag() {
        return mag;
    }

    public final Conviction getConviction() {
        return conviction.get();
    }

    public final ReadOnlyObjectProperty<Conviction> convictionProperty() {
        return conviction;
    }

    private void setConviction(Conviction conviction) {
        this.conviction.set(conviction);
    }

    public final Height getHeight() {
        return height.get();
    }

    public final ReadOnlyObjectProperty<Height> heightProperty() {
        return height;
    }

    private void setHeight(Height height) {
        this.height.set(height);
    }

    public final int getAttackNumber() {
        return attackNumber.get();
    }

    public final ReadOnlyIntegerProperty attackNumberProperty() {
        return attackNumber;
    }

    protected void setAttackNumber(int attackNumber) {
        this.attackNumber.set(attackNumber);
    }

    public final int getDefenceNumber() {
        return defenceNumber.get();
    }

    public final ReadOnlyIntegerProperty defenceNumberProperty() {
        return defenceNumber;
    }

    protected void setDefenceNumber(int defenceNumber) {
        this.defenceNumber.set(defenceNumber);
    }

    public boolean isAlive() {
        return alive.get();
    }

    public BooleanProperty aliveProperty() {
        return alive;
    }

    // endregion

    @Override
    public String toString() {
        return getName();
    }
}
