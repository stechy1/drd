package cz.stechy.drd.model.item;

import cz.stechy.drd.Money;
import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.entity.Height;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Třída představující brnění
 */
public class Armor extends ItemBase {

    // region Variables

    private final IntegerProperty defenceNumber = new SimpleIntegerProperty();
    private final IntegerProperty weightA = new SimpleIntegerProperty();
    private final IntegerProperty weightB = new SimpleIntegerProperty();
    private final IntegerProperty weightC = new SimpleIntegerProperty();
    private final Money priceA = new Money();
    private final Money priceB = new Money();
    private final Money priceC = new Money();
    private final IntegerProperty minimumStrength = new SimpleIntegerProperty();

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param armor Brnění, které se má nakopírovat
     */
    public Armor(Armor armor) {
        this(armor.getId(), armor.getName(), armor.getDescription(), armor.getAuthor(),
            armor.getDefenceNumber(), armor.getMinimumStrength(), armor.getWeightA(),
            armor.getWeightB(), armor.getWeightC(), armor.getPriceA().getRaw(),
            armor.getPriceB().getRaw(), armor.getPriceC().getRaw(), armor.getImage(),
            armor.isDownloaded(), armor.isUploaded());
    }

    /**
     * Konstruktor zbroje
     *
     * @param id Id zbroje
     * @param author Autor brnění
     * @param name Název zbroje
     * @param description Popis zbroje
     * @param defenceNumber Obranné číslo brnění
     * @param weightA Váha pro bytosti velikosti A
     * @param weightB Váha pro bytosti velikosti B
     * @param weightC Váha pro bytosti velikosti C
     * @param priceA Cena pro bytosti velikosti A
     * @param priceB Cena pro bytosti velikosti B
     * @param priceC Cena pro bytosti velikosti C
     * @param image Obrázek zbroje
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    public Armor(String id, String author, String name, String description, int defenceNumber,
        int weightA, int weightB, int weightC, int priceA, int priceB, int priceC,
        int minimumStrength, byte[] image, boolean downloaded, boolean uploaded) {
        super(id, author, name, description, weightB, priceB, image, downloaded, uploaded);

        this.defenceNumber.setValue(defenceNumber);
        this.weightA.setValue(weightA);
        this.weightB.setValue(weightB);
        this.weightC.setValue(weightC);
        this.priceA.setRaw(priceA);
        this.priceB.setRaw(priceB);
        this.priceC.setRaw(priceC);
        this.minimumStrength.setValue(minimumStrength);
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

    public int getDefenceNumber() {
        return defenceNumber.get();
    }

    public IntegerProperty defenceNumberProperty() {
        return defenceNumber;
    }

    public void setDefenceNumber(int defenceNumber) {
        this.defenceNumber.set(defenceNumber);
    }

    public int getWeightA() {
        return weightA.get();
    }

    public IntegerProperty weightAProperty() {
        return weightA;
    }

    public void setWeightA(int weightA) {
        this.weightA.set(weightA);
    }

    public int getWeightB() {
        return weightB.get();
    }

    public IntegerProperty weightBProperty() {
        return weightB;
    }

    public void setWeightB(int weightB) {
        this.weightB.set(weightB);
    }

    public int getWeightC() {
        return weightC.get();
    }

    public IntegerProperty weightCProperty() {
        return weightC;
    }

    public void setWeightC(int weightC) {
        this.weightC.set(weightC);
    }

    public int getMinimumStrength() {
        return minimumStrength.get();
    }

    public IntegerProperty minimumStrengthProperty() {
        return minimumStrength;
    }

    public void setMinimumStrength(int minimumStrength) {
        this.minimumStrength.set(minimumStrength);
    }

    public Money getPriceA() {
        return priceA;
    }

    public Money getPriceB() {
        return priceB;
    }

    public Money getPriceC() {
        return priceC;
    }

    // endregion

    public static class Builder {

        private String id;
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

        public Armor build() {
            return new Armor(id, author, name, description, defenceNumber, weightA, weightB,
                weightC,
                priceA, priceB, priceC, minimumStrength, image, downloaded, uploaded);
        }
    }
}
