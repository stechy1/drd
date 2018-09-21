package cz.stechy.drd.model.item;

import cz.stechy.drd.R;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.annotation.TranslateEntry;
import cz.stechy.drd.db.base.DatabaseItem;
import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.ITranslatedEnum;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.entity.Height;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Třída představující brnění
 */
public final class Armor extends ItemBase {

    // region Variables

    // Obranné číslo zbroje
    @TranslateEntry(key = Translate.ITEM_WEAPON_MELE_ARMOR_DEFENCE_NUMBER)
    private final IntegerProperty defenceNumber = new SimpleIntegerProperty(this, "defenceNumber");
    // Váha zbroje pro postavy velikosti A
    @TranslateEntry(key = Translate.ITEM_WEIGHT_A)
    private final IntegerProperty weightA = new SimpleIntegerProperty(this, "weightA");
    // Váha zbroje pro postavy velikosti B
    @TranslateEntry(key = Translate.ITEM_WEIGHT_B)
    private final IntegerProperty weightB = new SimpleIntegerProperty(this, "weightB");
    // Váha zbroje pro postavy velikosti C
    @TranslateEntry(key = Translate.ITEM_WEIGHT_C)
    private final IntegerProperty weightC = new SimpleIntegerProperty(this, "weightC");
    // Cena zbroje pro ostavy velikosti A
    @TranslateEntry(key = Translate.ITEM_PRICE_A)
    private final Money priceA = new Money();
    // Cena zbroje pro ostavy velikosti B
    @TranslateEntry(key = Translate.ITEM_PRICE_B)
    private final Money priceB = new Money();
    // Cena zbroje pro ostavy velikosti C
    @TranslateEntry(key = Translate.ITEM_PRICE_C)
    private final Money priceC = new Money();
    // Minimální sila, kterou musí postava mít, aby zbroj unesla
    @TranslateEntry(key = Translate.ITEM_ARMOR_MINIMUM_STRENGTH)
    private final IntegerProperty minimumStrength = new SimpleIntegerProperty(this, "minimumStrength");
    @TranslateEntry(key = Translate.ITEM_ARMOR_TYPE)
    private final ObjectProperty<ArmorType> type = new SimpleObjectProperty<>(this, "type");

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param armor Brnění, které se má nakopírovat
     */
    private Armor(Armor armor) {
        this(armor.getId(), armor.getName(), armor.getDescription(), armor.getAuthor(),
            armor.getDefenceNumber(), armor.getMinimumStrength(), armor.getWeightA(),
            armor.getWeightB(), armor.getWeightC(), armor.getPriceA().getRaw(),
            armor.getPriceB().getRaw(), armor.getPriceC().getRaw(), armor.getType(),
            armor.getImage(), armor.getStackSize(), armor.isDownloaded(), armor.isUploaded());
    }

    /**
     * Konstruktor zbroje
     *
     * @param id Id zbroje
     * @param name Název zbroje
     * @param description Popis zbroje
     * @param author Autor brnění
     * @param defenceNumber Obranné číslo brnění
     * @param weightA Váha pro bytosti velikosti A
     * @param weightB Váha pro bytosti velikosti B
     * @param weightC Váha pro bytosti velikosti C
     * @param priceA Cena pro bytosti velikosti A
     * @param priceB Cena pro bytosti velikosti B
     * @param priceC Cena pro bytosti velikosti C
     * @param minimumStrength Minimální potřebná síla k nošení brnění
     * @param type Typ části brnění
     * @param image Obrázek zbroje
     * @param stackSize Maximální počet předmětů, který může být v jednom stacku ve slotu inventáře
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    private Armor(String id, String name, String description, String author, int defenceNumber,
        int weightA, int weightB, int weightC, int priceA, int priceB, int priceC,
        int minimumStrength, ArmorType type, byte[] image,
        int stackSize, boolean downloaded, boolean uploaded) {
        super(id, author, name, description, weightB, priceB, image, stackSize, downloaded,
            uploaded);

        setDefenceNumber(defenceNumber);
        setWeightA(weightA);
        setWeightB(weightB);
        setWeightC(weightC);
        this.priceA.setRaw(priceA);
        this.priceB.setRaw(priceB);
        this.priceC.setRaw(priceC);
        setMinimumStrength(minimumStrength);
        setType(type);
    }

    // endregion

    // region Public methods

    /**
     * Nastaví vlastnosti brnění podle velikosti bytosti
     *
     * @param height Velikost bytosti
     */
    public void forHeight(Height height) {
        switch (height) {
            case A0:
            case A:
                weight.setValue(weightA.getValue());
                price.setRaw(priceA.getRaw());
                break;
            case B:
                weight.setValue(weightB.getValue());
                price.setRaw(priceB.getRaw());
                break;
            default:
                weight.setValue(weightC.getValue());
                price.setRaw(priceC.getRaw());
        }
    }

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        Armor armor = (Armor) other;
        setDefenceNumber(armor.getDefenceNumber());
        setWeightA(armor.getWeightA());
        setWeightB(armor.getWeightB());
        setWeightC(armor.getWeightC());
        this.priceA.setRaw(armor.getPriceA().getRaw());
        this.priceB.setRaw(armor.getPriceB().getRaw());
        this.priceC.setRaw(armor.getPriceC().getRaw());
        setMinimumStrength(armor.getMinimumStrength());
    }

    @Override
    public Map<String, String> getMapDescription() {
        final Map<String, String> map = super.getMapDescription();
        map.put(R.Translate.ITEM_WEAPON_MELE_ARMOR_DEFENCE_NUMBER,
            String.valueOf(getDefenceNumber()));
        map.put(R.Translate.ITEM_ARMOR_MINIMUM_STRENGTH, String.valueOf(getMinimumStrength()));

        return map;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.ARMOR;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new Armor(this);
    }

    // endregion

    // region Getters & Setters

    public final int getDefenceNumber() {
        return defenceNumber.get();
    }

    public final ReadOnlyIntegerProperty defenceNumberProperty() {
        return defenceNumber;
    }

    private void setDefenceNumber(int defenceNumber) {
        this.defenceNumber.set(defenceNumber);
    }

    public final int getWeightA() {
        return weightA.get();
    }

    public final ReadOnlyIntegerProperty weightAProperty() {
        return weightA;
    }

    private void setWeightA(int weightA) {
        this.weightA.set(weightA);
    }

    public final int getWeightB() {
        return weightB.get();
    }

    public final ReadOnlyIntegerProperty weightBProperty() {
        return weightB;
    }

    private void setWeightB(int weightB) {
        this.weightB.set(weightB);
    }

    public final int getWeightC() {
        return weightC.get();
    }

    public final ReadOnlyIntegerProperty weightCProperty() {
        return weightC;
    }

    private void setWeightC(int weightC) {
        this.weightC.set(weightC);
    }

    public final Money getPriceA() {
        return priceA;
    }

    public final Money getPriceB() {
        return priceB;
    }

    public final Money getPriceC() {
        return priceC;
    }

    public final int getMinimumStrength() {
        return minimumStrength.get();
    }

    public final ReadOnlyIntegerProperty minimumStrengthProperty() {
        return minimumStrength;
    }

    private void setMinimumStrength(int minimumStrength) {
        this.minimumStrength.set(minimumStrength);
    }

    public final ArmorType getType() {
        return type.get();
    }

    public final ReadOnlyObjectProperty<ArmorType> typeProperty() {
        return type;
    }

    private void setType(ArmorType type) {
        this.type.set(type);
    }

    // endregion

    @Override
    public List<String> getDiffList(DatabaseItem other) {
        final List<String> diffList = super.getDiffList(other);
        final Armor armor = (Armor) other;

        if (!Objects.equals(this.getDefenceNumber(), armor.getDefenceNumber())) {
            diffList.add(defenceNumber.getName());
        }

        if (!Objects.equals(this.getWeightA(), armor.getWeightA())) {
            diffList.add(weightA.getName());
        }

        if (!Objects.equals(this.getWeightB(), armor.getWeightB())) {
            diffList.add(weightB.getName());
        }

        if (!Objects.equals(this.getWeightC(), armor.getWeightC())) {
            diffList.add(weightC.getName());
        }

        if (!Objects.equals(this.getPriceA(), armor.getPriceA())) {
            diffList.add("priceA");
        }

        if (!Objects.equals(this.getPriceB(), armor.getPriceB())) {
            diffList.add("priceB");
        }

        if (!Objects.equals(this.getPriceC(), armor.getPriceC())) {
            diffList.add("priceC");
        }

        if (!Objects.equals(this.getMinimumStrength(), armor.getMinimumStrength())) {
            diffList.add(minimumStrength.getName());
        }

        if (!Objects.equals(this.getType(), armor.getType())) {
            diffList.add(type.getName());
        }

        return diffList;
    }

    // Typ části brnění
    public enum ArmorType implements ITranslatedEnum {
        HELM(R.Translate.ITEM_ARMOR_TYPE_HELM),
        BODY(R.Translate.ITEM_ARMOR_TYPE_BODY),
        LEGS(R.Translate.ITEM_ARMOR_TYPE_LEGS),
        BOTS(R.Translate.ITEM_ARMOR_TYPE_BOOTS),
        GLOVES(R.Translate.ITEM_ARMOR_TYPE_GLASES);

        private final String key;

        ArmorType(String key) {
            this.key = key;
        }

        @Override
        public String getKeyForTranslation() {
            return key;
        }
    }

    public static class Builder {

        private String id;
        private String author;
        private String name;
        private String description;
        private int defenceNumber;
        private int weightA;
        private int weightB;
        private int weightC;
        private int priceA;
        private int priceB;
        private int priceC;
        private int minimumStrength;
        private ArmorType type;
        private byte[] image;
        private int stackSize;
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

        public Builder defenceNumber(int defenceNumber) {
            this.defenceNumber = defenceNumber;
            return this;
        }

        public Builder weightA(int weightA) {
            this.weightA = weightA;
            return this;
        }

        public Builder weightB(int weightB) {
            this.weightB = weightB;
            return this;
        }

        public Builder weightC(int weightC) {
            this.weightC = weightC;
            return this;
        }

        public Builder priceA(int priceA) {
            this.priceA = priceA;
            return this;
        }

        public Builder priceB(int priceB) {
            this.priceB = priceB;
            return this;
        }

        public Builder priceC(int priceC) {
            this.priceC = priceC;
            return this;
        }

        public Builder minimumStrength(int minimumStrength) {
            this.minimumStrength = minimumStrength;
            return this;
        }

        public Builder type(ArmorType type) {
            this.type = type;
            return this;
        }

        public Builder type(int type) {
            this.type = ArmorType.values()[type];
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

        public Builder stackSize(int stackSize) {
            this.stackSize = stackSize;
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

        public Armor build() {
            return new Armor(id, name, description, author, defenceNumber, weightA, weightB,
                weightC, priceA, priceB, priceC, minimumStrength, type, image, stackSize,
                downloaded, uploaded);
        }
    }
}
