package cz.stechy.drd.model.item;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.db.base.DatabaseItem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Třída představující střelné zbraně
 */
public class RangedWeapon extends WeaponBase {

    // region Variables

    // Typ střelné zbraně
    protected final ObjectProperty<RangedWeaponType> weaponType = new SimpleObjectProperty<>(
        RangedWeaponType.FIRE);
    // Dostřel - malý
    protected final IntegerProperty rangeLow = new SimpleIntegerProperty();
    // Dostřel - střední
    protected final IntegerProperty rangeMedium = new SimpleIntegerProperty();
    // Dostřel - velký
    protected final IntegerProperty rangeLong = new SimpleIntegerProperty();

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param weapon Kopírovaná zbraň
     */
    public RangedWeapon(RangedWeapon weapon) {
        this(weapon.getId(), weapon.getName(), weapon.getDescription(), weapon.getAuthor(),
            weapon.getWeight(), weapon.getPrice().getRaw(), weapon.getStrength(),
            weapon.getRampancy(), weapon.getWeaponType(), weapon.getRangeLow(),
            weapon.getRangeMedium(), weapon.getRangeLong(), weapon.getImage(),
            weapon.isDownloaded(), weapon.isUploaded());
    }

    /**
     * Konstruktor pro zbraně na dálku
     *
     * @param id Id zbraně
     * @param name Název zbraně
     * @param description Popis zbraně
     * @param author Autor zbraně
     * @param weight Váha zbraně
     * @param price Cena zbraně
     * @param strength Síla zbraně
     * @param rampancy Útočnost zbraně
     * @param weaponType Typ střelné zbraně
     * @param rangeLow Dostřel - malý
     * @param rangeMedium Dostřel - střední
     * @param rangeLong Dostřel - velký
     * @param image Obrázek zbraně
     * @param downloaded Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    public RangedWeapon(String id, String name, String description, String author, int weight,
        int price,
        int strength, int rampancy, RangedWeaponType weaponType, int rangeLow, int rangeMedium,
        int rangeLong, byte[] image, boolean downloaded, boolean uploaded) {
        super(id, name, description, weight, price, strength, rampancy, author, image,
            downloaded, uploaded);

        this.weaponType.setValue(weaponType);
        this.rangeLow.setValue(rangeLow);
        this.rangeMedium.setValue(rangeMedium);
        this.rangeLong.setValue(rangeLong);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);
        RangedWeapon otherWeapon = (RangedWeapon) other;
        this.weaponType.setValue(otherWeapon.getWeaponType());
        this.rangeLow.setValue(otherWeapon.getRangeLow());
        this.rangeMedium.setValue(otherWeapon.getRangeMedium());
        this.rangeLong.setValue(otherWeapon.getRangeLong());

    }

    @Override
    public ItemType getItemType() {
        return ItemType.WEAPON_RANGED;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new RangedWeapon(this);
    }

    // endregion

    // region Getters & Setters

    public RangedWeaponType getWeaponType() {
        return weaponType.get();
    }

    public ObjectProperty<RangedWeaponType> weaponTypeProperty() {
        return weaponType;
    }

    public void setWeaponType(RangedWeaponType weaponType) {
        this.weaponType.set(weaponType);
    }

    public int getRangeLow() {
        return rangeLow.get();
    }

    public IntegerProperty rangeLowProperty() {
        return rangeLow;
    }

    public void setRangeLow(int rangeLow) {
        this.rangeLow.set(rangeLow);
    }

    public int getRangeMedium() {
        return rangeMedium.get();
    }

    public IntegerProperty rangeMediumProperty() {
        return rangeMedium;
    }

    public void setRangeMedium(int rangeMedium) {
        this.rangeMedium.set(rangeMedium);
    }

    public int getRangeLong() {
        return rangeLong.get();
    }

    public IntegerProperty rangeLongProperty() {
        return rangeLong;
    }

    public void setRangeLong(int rangeLong) {
        this.rangeLong.set(rangeLong);
    }

    // endregion

    // Typ zbraně
    public enum RangedWeaponType {
        FIRE, PROJECTILE;

        public static RangedWeaponType valueOf(int index) {
            return RangedWeaponType.values()[index];
        }
    }

    public static class Builder {

        private String id;
        private String name;
        private String description;
        private int weight;
        private int price;
        private int strength;
        private int rampancy;
        private RangedWeaponType weaponType = RangedWeaponType.FIRE;
        private int rangeLow;
        private int rangeMedium;
        private int rangeHigh;
        private String author;
        private byte[] image;
        private boolean uploaded;
        private boolean downloaded;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder strength(int strength) {
            this.strength = strength;
            return this;
        }

        public Builder rampancy(int rampancy) {
            this.rampancy = rampancy;
            return this;
        }

        public Builder weaponType(int weaponType) {
            this.weaponType = RangedWeaponType.valueOf(weaponType);
            return this;
        }

        public Builder rangeLow(int rangeLow) {
            this.rangeLow = rangeLow;
            return this;
        }

        public Builder rangeMedium(int rangeMedium) {
            this.rangeMedium = rangeMedium;
            return this;
        }

        public Builder rangeLong(int rangeHigh) {
            this.rangeHigh = rangeHigh;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder uploaded(boolean uploaded) {
            this.uploaded = uploaded;
            return this;
        }

        public Builder downloaded(boolean downloaded) {
            this.downloaded = downloaded;
            return this;
        }

        public RangedWeapon build() {
            return new RangedWeapon(id, name, description, author, weight, price, strength,
                rampancy,
                weaponType, rangeLow, rangeMedium, rangeHigh, image, downloaded, uploaded);
        }
    }
}
