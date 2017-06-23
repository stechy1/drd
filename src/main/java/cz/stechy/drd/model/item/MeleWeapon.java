package cz.stechy.drd.model.item;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.db.base.DatabaseItem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Třída představující zbraň pro boj na blízko
 */
public final class MeleWeapon extends WeaponBase {

    // region Variables

    // Obrana zbraně
    protected final IntegerProperty defence = new SimpleIntegerProperty(this, "defence");
    // Třída zbraně
    protected final ObjectProperty<MeleWeaponClass> weaponClass = new SimpleObjectProperty<>(this,
        "weaponClass", MeleWeaponClass.LIGHT);
    // Typ zbraně
    protected final ObjectProperty<MeleWeaponType> weaponType = new SimpleObjectProperty<>(this,
        "weaponType", MeleWeaponType.ONE_HAND);

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param weapon Kopírovaná zbraň
     */
    private MeleWeapon(MeleWeapon weapon) {
        this(weapon.getId(), weapon.getName(), weapon.getDescription(), weapon.getAuthor(),
            weapon.getWeight(), weapon.getPrice().getRaw(), weapon.getStrength(),
            weapon.getRampancy(), weapon.getDefence(), weapon.getWeaponClass(),
            weapon.getWeaponType(), weapon.getImage(), weapon.isDownloaded(), weapon.isUploaded());
    }

    /**
     * Konstruktor pro zbraně k boji z blízka
     *
     * @param id Id zbraně
     * @param name Název zbraně
     * @param description Popis zbraně
     * @param author Autor zbraně
     * @param weight Váha zbraně
     * @param price Cena zbraně
     * @param strength Síla zbraně
     * @param rampancy Útočnost zbraně
     * @param defence Obrana zbraně
     * @param weaponClass Třída zbraně
     * @param weaponType Typ zbraně
     * @param image Obrázek zbraně
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     * @param downloaded Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    private MeleWeapon(String id, String name, String description, String author, int weight,
        int price, int strength, int rampancy, int defence, MeleWeaponClass weaponClass,
        MeleWeaponType weaponType, byte[] image, boolean uploaded,
        boolean downloaded) {
        super(id, name, description, weight, price, strength, rampancy, author, image,
            downloaded, uploaded);

        setDefence(defence);
        setWeaponClass(weaponClass);
        setWeaponType(weaponType);
    }

    // endregion

    // region Getters & Setters

    public final int getDefence() {
        return defence.get();
    }

    public final ReadOnlyIntegerProperty defenceProperty() {
        return defence;
    }

    private void setDefence(int defence) {
        this.defence.set(defence);
    }

    public final MeleWeaponClass getWeaponClass() {
        return weaponClass.get();
    }

    public final ReadOnlyObjectProperty<MeleWeaponClass> weaponClassProperty() {
        return weaponClass;
    }

    private void setWeaponClass(MeleWeaponClass weaponClass) {
        this.weaponClass.set(weaponClass);
    }

    public final MeleWeaponType getWeaponType() {
        return weaponType.get();
    }

    public final ReadOnlyObjectProperty<MeleWeaponType> weaponTypeProperty() {
        return weaponType;
    }

    private void setWeaponType(MeleWeaponType weaponType) {
        this.weaponType.set(weaponType);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        MeleWeapon weapon = (MeleWeapon) other;
        setDefence(weapon.getDefence());
        setWeaponClass(weapon.getWeaponClass());
        setWeaponType(weapon.getWeaponType());
    }

    @Override
    public ItemType getItemType() {
        return ItemType.WEAPON_MELE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new MeleWeapon(this);
    }

    // endregion

    // Třída zbraně
    public enum MeleWeaponClass {
        LIGHT, MEDIUM, HEAVY;

        public static MeleWeaponClass valueOf(int index) {
            return MeleWeaponClass.values()[index];
        }
    }

    // Typ zbraně
    public enum MeleWeaponType {
        ONE_HAND, DOUBLE_HAND;

        public static MeleWeaponType valueOf(int index) {
            return MeleWeaponType.values()[index];
        }
    }

    // Builder
    public static class Builder {

        private String id;
        private String name;
        private String description;
        private int weight;
        private int price;
        private int strength;
        private int rampancy;
        private int defence;
        private MeleWeaponClass weaponClass = MeleWeaponClass.LIGHT;
        private MeleWeaponType weaponType = MeleWeaponType.ONE_HAND;
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

        public Builder defence(int defence) {
            this.defence = defence;
            return this;
        }

        public Builder weaponClass(int weaponClass) {
            this.weaponClass = MeleWeaponClass.valueOf(weaponClass);
            return this;
        }

        public Builder weaponType(int weaponType) {
            this.weaponType = MeleWeaponType.valueOf(weaponType);
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

        public MeleWeapon build() {
            return new MeleWeapon(id, name, description, author, weight, price, strength, rampancy,
                defence, weaponClass, weaponType, image, downloaded, uploaded);
        }
    }

}
