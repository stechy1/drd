package cz.stechy.drd.model.entity.mob;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.WithImage;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.EntityBase;
import cz.stechy.drd.model.entity.EntityProperty;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.entity.SimpleEntityProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Třída představuje všechna zvířata a nestvůry žijící ve světě Dračího doupěte
 */
public final class Mob extends EntityBase implements WithImage {

    // region Variables

    private final ObjectProperty<byte[]> image = new SimpleObjectProperty<>();
    // Třída nestvůry
    private final ObjectProperty<MobClass> mobClass = new SimpleObjectProperty<>();
    // Typ pravidel, do kterých nestvůra patří
    private final ObjectProperty<Rule> rulesType = new SimpleObjectProperty<>();
    // Životaschpnost
    private final IntegerProperty viability = new SimpleIntegerProperty(this, 
        "viability");
    // Odolnost
    private final EntityProperty immunity = new SimpleEntityProperty();
    // Bojovnost
    private final IntegerProperty mettle = new SimpleIntegerProperty(this, "mettle");
    // Zranitenost
    private final IntegerProperty vulnerability = new SimpleIntegerProperty();
    // Pohyblivost
    private final IntegerProperty mobility = new SimpleIntegerProperty(this, 
        "mobility");
    // Vytrvalost
    private final IntegerProperty perservance = new SimpleIntegerProperty(this, 
        "perservance");
    // Manévrovací schopnosti
    private final IntegerProperty controlAbility = new SimpleIntegerProperty(this, 
        "controlAbility");
    // Inteligence
    private final EntityProperty intelligence = new SimpleEntityProperty();
    // Charisma
    private final EntityProperty charisma = new SimpleEntityProperty();
    // Základní síla mysli
    private final IntegerProperty basicPowerOfMind = new SimpleIntegerProperty(this, 
        "basicPowerOfMind");
    // TODO vymyslet, jak implementovat poklad
    // Zkušenosti
    private final IntegerProperty experience = new SimpleIntegerProperty(this, 
        "experience");
    // Ochočení
    private final IntegerProperty domestication = new SimpleIntegerProperty(this, 
        "domestication");

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param mob Mob, který se má zkopírovat
     */
    private Mob(Mob mob) {
        this(mob.getId(), mob.getName(), mob.getDescription(), mob.getAuthor(), mob.getImage(),
            mob.getMobClass(), mob.getRulesType(),
            mob.getLive().getActValue().intValue(),
            mob.getLive().getMaxValue().intValue(), mob.getMag().getActValue().intValue(),
            mob.getMag().getMaxValue().intValue(),
            mob.getConviction(), mob.getHeight(), mob.getAttackNumber(),
            mob.getDefenceNumber(), mob.getViability(), mob.getImmunity().getValue(),
            mob.getMettle(),
            mob.getVulnerability(), mob.getMobility(), mob.getPerservance(),
            mob.getControlAbility(), mob.getIntelligence().getValue(), mob.getCharisma().getValue(),
            mob.getBasicPowerOfMind(), mob.getExperience(), mob.getDomestication(),
            mob.isDownloaded(), mob.isUploaded());
    }

    /**
     * Vytvoří novou nestvůru
     * @param id Id nestvůry
     * @param name Název nestvůry
     * @param author Autor nestvůry
     * @param image
     * @param mobClass
     * @param rulesType
     * @param live Aktuální pčet životů
     * @param maxLive Maximální počet životů
     * @param mag Aktuální počet magů
     * @param maxMag Maximální počet magů
     * @param conviction Přesvědčení nestvůry
     * @param height Výška nstvůry
     * @param attackNumber Útočné číslo
     * @param defenceNumber Obranné číslo
     * @param viability Životaschopnost
     * @param immunity Odolnost
     * @param mettle Bojovnost
     * @param vulnerability Zranitelnost
     * @param mobility Pohyblivost
     * @param perservance Vytrvalost
     * @param controlAbility Manévrovací schopnosti
     * @param intelligence Inteligence
     * @param charisma Charisma
     * @param basicPowerOfMind Základní síla mysli
     * @param experience Zkušenosti
     * @param domestication Ochočení
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    private Mob(String id, String name, String description, String author,
        byte[] image, MobClass mobClass, Rule rulesType, int live,
        int maxLive,
        int mag, int maxMag, Conviction conviction, Height height, int attackNumber,
        int defenceNumber, int viability, int immunity, int mettle, int vulnerability,
        int mobility, int perservance, int controlAbility, int intelligence, int charisma,
        int basicPowerOfMind, int experience, int domestication, boolean downloaded,
        boolean uploaded) {
        super(id, author, name, description, live, maxLive, mag, maxMag, conviction, height,
            downloaded, uploaded);

        setImage(image);
        setMobClass(mobClass);
        setRulesType(rulesType);
        this.attackNumber.setValue(attackNumber);
        setDefenceNumber(defenceNumber);
        setViability(viability);
        this.immunity.setValue(immunity);
        setMettle(mettle);
        setVulnerability(vulnerability);
        setMobility(mobility);
        setPerservance(perservance);
        setControlAbility(controlAbility);
        this.intelligence.setValue(intelligence);
        this.charisma.setValue(charisma);
        setBasicPowerOfMind(basicPowerOfMind);
        setExperience(experience);
        setDomestication(domestication);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        Mob mob = (Mob) other;
        setImage(mob.getImage());
        setMobClass(mob.getMobClass());
        setRulesType(mob.getRulesType());
        setViability(mob.getViability());
        this.immunity.update(mob.getImmunity());
        setMettle(mob.getMettle());
        setVulnerability(mob.getVulnerability());
        setMobility(mob.getMobility());
        setPerservance(mob.getPerservance());
        setControlAbility(mob.getControlAbility());
        this.intelligence.update(mob.getIntelligence());
        this.charisma.update(mob.getCharisma());
        setBasicPowerOfMind(mob.getBasicPowerOfMind());
        setExperience(mob.getExperience());
        setDomestication(mob.getDomestication());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IClonable> T duplicate() {
        return (T) new Mob(this);
    }

    // endregion

    // region Getters & Setters


    public final byte[] getImage() {
        return image.get();
    }

    public final ReadOnlyObjectProperty<byte[]> imageProperty() {
        return image;
    }

    private void setImage(byte[] image) {
        this.image.set(image);
    }

    public final MobClass getMobClass() {
        return mobClass.get();
    }

    public final ReadOnlyObjectProperty<MobClass> mobClassProperty() {
        return mobClass;
    }

    private void setMobClass(MobClass mobClass) {
        this.mobClass.set(mobClass);
    }

    public final Rule getRulesType() {
        return rulesType.get();
    }

    public final ReadOnlyObjectProperty<Rule> rulesTypeProperty() {
        return rulesType;
    }

    private void setRulesType(Rule rulesType) {
        this.rulesType.set(rulesType);
    }

    public final int getViability() {
        return viability.get();
    }

    public final ReadOnlyIntegerProperty viabilityProperty() {
        return viability;
    }

    private void setViability(int viability) {
        this.viability.set(viability);
    }

    public final EntityProperty getImmunity() {
        return immunity;
    }

    public final int getMettle() {
        return mettle.get();
    }

    public final ReadOnlyIntegerProperty mettleProperty() {
        return mettle;
    }

    private void setMettle(int mettle) {
        this.mettle.set(mettle);
    }

    public final int getVulnerability() {
        return vulnerability.get();
    }

    public final ReadOnlyIntegerProperty vulnerabilityProperty() {
        return vulnerability;
    }

    private void setVulnerability(int vulnerability) {
        this.vulnerability.set(vulnerability);
    }

    public final int getMobility() {
        return mobility.get();
    }

    public final ReadOnlyIntegerProperty mobilityProperty() {
        return mobility;
    }

    private void setMobility(int mobility) {
        this.mobility.set(mobility);
    }

    public final int getPerservance() {
        return perservance.get();
    }

    public final ReadOnlyIntegerProperty perservanceProperty() {
        return perservance;
    }

    private void setPerservance(int perservance) {
        this.perservance.set(perservance);
    }

    public final int getControlAbility() {
        return controlAbility.get();
    }

    public final ReadOnlyIntegerProperty controlAbilityProperty() {
        return controlAbility;
    }

    private void setControlAbility(int controlAbility) {
        this.controlAbility.set(controlAbility);
    }

    public final EntityProperty getIntelligence() {
        return intelligence;
    }

    public final EntityProperty getCharisma() {
        return charisma;
    }

    public final int getBasicPowerOfMind() {
        return basicPowerOfMind.get();
    }

    public final ReadOnlyIntegerProperty basicPowerOfMindProperty() {
        return basicPowerOfMind;
    }

    private void setBasicPowerOfMind(int basicPowerOfMind) {
        this.basicPowerOfMind.set(basicPowerOfMind);
    }

    public final int getExperience() {
        return experience.get();
    }

    public final ReadOnlyIntegerProperty experienceProperty() {
        return experience;
    }

    private void setExperience(int experience) {
        this.experience.set(experience);
    }

    public final int getDomestication() {
        return domestication.get();
    }

    public final ReadOnlyIntegerProperty domesticationProperty() {
        return domestication;
    }

    private void setDomestication(int domestication) {
        this.domestication.set(domestication);
    }

    // endregion

    public enum MobClass {
        DRAGON, SNAKE, LYCANTROP, UNDEATH, INVISIBLE, STATUE, SPIDER, INSECT, OTHER
    }

    public static class Builder {

        private String id;
        private String author;
        private String name;
        private String description;
        private byte[] image;
        private MobClass mobClass;
        private Rule rulesType;
        private int live;
        private int maxLive;
        private int mag;
        private int maxMag;
        private Conviction conviction;
        private Height height;
        private int attackNumber;
        private int defenceNumber;
        private int viability;
        private int immunity;
        private int mettle;
        private int vulnerability;
        private int mobility;
        private int perservance;
        private int controlAbility;
        private int intelligence;
        private int charisma;
        private int basicPowerOfMind;
        private int experience;
        private int domestication;
        private boolean downloaded;
        private boolean uploaded;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
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

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder mobClass(int mobClass) {
            this.mobClass = MobClass.values()[mobClass];
            return this;
        }

        public Builder mobClass(MobClass mobClass) {
            this.mobClass = mobClass;
            return this;
        }

        public Builder rulesType(int rulesType) {
            this.rulesType = Rule.values()[rulesType];
            return this;
        }

        public Builder rulesType(Rule rulesType) {
            this.rulesType = rulesType;
            return this;
        }

        public Builder live(int live) {
            this.live = live;
            return this;
        }

        public Builder maxLive(int maxLive) {
            this.maxLive = maxLive;
            return this;
        }

        public Builder mag(int mag) {
            this.mag = mag;
            return this;
        }

        public Builder maxMag(int maxMag) {
            this.maxMag = maxMag;
            return this;
        }

        public Builder conviction(int conviction) {
            this.conviction = Conviction.values()[conviction];
            return this;
        }

        public Builder conviction(Conviction conviction) {
            this.conviction = conviction;
            return this;
        }

        public Builder height(int height) {
            this.height = Height.values()[height];
            return this;
        }

        public Builder height(Height height) {
            this.height = height;
            return this;
        }

        public Builder attackNumber(int attackNumber) {
            this.attackNumber = attackNumber;
            return this;
        }

        public Builder defenceNumber(int defenceNumber) {
            this.defenceNumber = defenceNumber;
            return this;
        }

        public Builder viability(int viability) {
            this.viability = viability;
            return this;
        }

        public Builder immunity(int immunity) {
            this.immunity = immunity;
            return this;
        }

        public Builder mettle(int mettle) {
            this.mettle = mettle;
            return this;
        }

        public Builder vulnerability(int vulnerability) {
            this.vulnerability = vulnerability;
            return this;
        }

        public Builder mobility(int mobility) {
            this.mobility = mobility;
            return this;
        }

        public Builder perservance(int perservance) {
            this.perservance = perservance;
            return this;
        }

        public Builder controlAbility(int controlAbility) {
            this.controlAbility = controlAbility;
            return this;
        }

        public Builder intelligence(int intelligence) {
            this.intelligence = intelligence;
            return this;
        }

        public Builder charisma(int charisma) {
            this.charisma = charisma;
            return this;
        }

        public Builder basicPowerOfMind(int basicPowerOfMind) {
            this.basicPowerOfMind = basicPowerOfMind;
            return this;
        }

        public Builder experience(int experience) {
            this.experience = experience;
            return this;
        }

        public Builder domestication(int domestication) {
            this.domestication = domestication;
            return this;
        }

        public Builder downloaded(boolean downloaded) {
            this.downloaded = downloaded;
            return this;
        }

        public Builder uploaded(boolean uploaded) {
            this.uploaded = uploaded;
            return this;
        }

        public Mob build() {
            return new Mob(id, name, description, author, image, mobClass, rulesType, live, maxLive,
                mag, maxMag, conviction, height, attackNumber, defenceNumber, viability,
                immunity, mettle, vulnerability, mobility, perservance, controlAbility,
                intelligence, charisma, basicPowerOfMind, experience, domestication, downloaded,
                uploaded);
        }
    }

}
