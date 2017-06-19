package cz.stechy.drd.model.entity.mob;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.EntityBase;
import cz.stechy.drd.model.entity.EntityProperty;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.entity.SimpleEntityProperty;
import cz.stechy.drd.model.entity.Vulnerability;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Třída představuje všechna zvířata a nestvůry žijící ve světě Dračího doupěte
 */
public final class Mob extends EntityBase {

    // region Variables

    // Útočné číslo
    private final EntityProperty attackNumber = new SimpleEntityProperty();
    // Obranné číslo
    private final IntegerProperty defenceNumber = new SimpleIntegerProperty();
    // Životaschpnost
    private final IntegerProperty viability = new SimpleIntegerProperty();
    // Odolnost
    private final EntityProperty immunity = new SimpleEntityProperty();
    // Bojovnost
    private final IntegerProperty mettle = new SimpleIntegerProperty();
    // Zranitenost
    private final Vulnerability vulnerability;
    // Pohyblivost
    private final IntegerProperty mobility = new SimpleIntegerProperty();
    // Vytrvalost
    private final IntegerProperty perservance = new SimpleIntegerProperty();
    // Manévrovací schopnosti
    private final IntegerProperty controlAbility = new SimpleIntegerProperty();
    // Inteligence
    private final EntityProperty intelligence = new SimpleEntityProperty();
    // Charisma
    private final EntityProperty charisma = new SimpleEntityProperty();
    // Základní síla mysli
    private final IntegerProperty basicPowerOfMind = new SimpleIntegerProperty();
    // TODO vymyslet, jak implementovat poklad
    // Zkušenosti
    private final IntegerProperty experience = new SimpleIntegerProperty();
    // Ochočení
    private final IntegerProperty domestication = new SimpleIntegerProperty();

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param mob Mob, který se má zkopírovat
     */
    private Mob(Mob mob) {
        this(mob.getId(), mob.getAuthor(), mob.getName(), mob.getDescription(),
            mob.getLive().getActValue().intValue(), mob.getLive().getMaxValue().intValue(),
            mob.getMag().getActValue().intValue(), mob.getMag().getMaxValue().intValue(),
            mob.getConviction(), mob.getHeight(), mob.getAttackNumber().getValue(),
            mob.getDefenceNumber(), mob.getViability(), mob.getImmunity().getValue(),
            mob.getMettle(), mob.getVulnerability(), mob.getMobility(), mob.getPerservance(),
            mob.getControlAbility(), mob.getIntelligence().getValue(), mob.getCharisma().getValue(),
            mob.getBasicPowerOfMind(), mob.getExperience(), mob.getDomestication(),
            mob.isDownloaded(), mob.isUploaded());
    }

    /**
     * Vytvoří novou nestvůru
     *
     * @param id Id nestvůry
     * @param author Autor nestvůry
     * @param name Název nestvůry
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
    private Mob(String id, String author, String name, String description, int live, int maxLive,
        int mag, int maxMag, Conviction conviction, Height height, int attackNumber,
        int defenceNumber, int viability, int immunity, int mettle, Vulnerability vulnerability,
        int mobility, int perservance, int controlAbility, int intelligence, int charisma,
        int basicPowerOfMind, int experience, int domestication, boolean downloaded,
        boolean uploaded) {
        super(id, author, name, description, live, maxLive, mag, maxMag, conviction, height,
            downloaded, uploaded);

        this.attackNumber.setValue(attackNumber);
        this.defenceNumber.setValue(defenceNumber);
        this.viability.setValue(viability);
        this.immunity.setValue(immunity);
        this.mettle.setValue(mettle);
        this.vulnerability = vulnerability;
        this.mobility.setValue(mobility);
        this.perservance.setValue(perservance);
        this.controlAbility.setValue(controlAbility);
        this.intelligence.setValue(intelligence);
        this.charisma.setValue(charisma);
        this.basicPowerOfMind.setValue(basicPowerOfMind);
        this.experience.setValue(experience);
        this.domestication.setValue(domestication);
    }

    // endregion

    // region Public methods


    @Override
    public void update(DatabaseItem other) {
        super.update(other);
        Mob mob = (Mob) other;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IClonable> T duplicate() {
        return (T) new Mob(this);
    }

    // endregion

    // region Getters & Setters

    public EntityProperty getAttackNumber() {
        return attackNumber;
    }

    public int getDefenceNumber() {
        return defenceNumber.get();
    }

    public IntegerProperty defenceNumberProperty() {
        return defenceNumber;
    }

    public void setDefenceNumber(int defenceNumber) {
        this.defenceNumber.set(defenceNumber);
    }

    public int getViability() {
        return viability.get();
    }

    public IntegerProperty viabilityProperty() {
        return viability;
    }

    public void setViability(int viability) {
        this.viability.set(viability);
    }

    public EntityProperty getImmunity() {
        return immunity;
    }

    public int getMettle() {
        return mettle.get();
    }

    public IntegerProperty mettleProperty() {
        return mettle;
    }

    public void setMettle(int mettle) {
        this.mettle.set(mettle);
    }

    public Vulnerability getVulnerability() {
        return vulnerability;
    }

    public int getMobility() {
        return mobility.get();
    }

    public IntegerProperty mobilityProperty() {
        return mobility;
    }

    public void setMobility(int mobility) {
        this.mobility.set(mobility);
    }

    public int getPerservance() {
        return perservance.get();
    }

    public IntegerProperty perservanceProperty() {
        return perservance;
    }

    public void setPerservance(int perservance) {
        this.perservance.set(perservance);
    }

    public int getControlAbility() {
        return controlAbility.get();
    }

    public IntegerProperty controlAbilityProperty() {
        return controlAbility;
    }

    public void setControlAbility(int controlAbility) {
        this.controlAbility.set(controlAbility);
    }

    public EntityProperty getIntelligence() {
        return intelligence;
    }

    public EntityProperty getCharisma() {
        return charisma;
    }

    public int getBasicPowerOfMind() {
        return basicPowerOfMind.get();
    }

    public IntegerProperty basicPowerOfMindProperty() {
        return basicPowerOfMind;
    }

    public void setBasicPowerOfMind(int basicPowerOfMind) {
        this.basicPowerOfMind.set(basicPowerOfMind);
    }

    public int getExperience() {
        return experience.get();
    }

    public IntegerProperty experienceProperty() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience.set(experience);
    }

    public int getDomestication() {
        return domestication.get();
    }

    public IntegerProperty domesticationProperty() {
        return domestication;
    }

    public void setDomestication(int domestication) {
        this.domestication.set(domestication);
    }

    // endregion
}
