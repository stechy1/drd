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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Třída představuje všechna zvířata a nestvůry žijící ve světě Dračího doupěte
 */
public final class Mob extends EntityBase {

    // region Variables

    // Útočné číslo
    private final EntityProperty attackNumber = new SimpleEntityProperty();
    // Obranné číslo
    private final IntegerProperty defenceNumber = new SimpleIntegerProperty(this, 
        "defenceNumber");
    // Životaschpnost
    private final IntegerProperty viability = new SimpleIntegerProperty(this, 
        "viability");
    // Odolnost
    private final EntityProperty immunity = new SimpleEntityProperty();
    // Bojovnost
    private final IntegerProperty mettle = new SimpleIntegerProperty(this, "mettle");
    // Zranitenost
    private final ObjectProperty<Vulnerability> vulnerability = new SimpleObjectProperty<>();
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
        this.attackNumber.update(mob.getAttackNumber());
        setDefenceNumber(mob.getDefenceNumber());
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

    public final EntityProperty getAttackNumber() {
        return attackNumber;
    }

    public final int getDefenceNumber() {
        return defenceNumber.get();
    }

    public final ReadOnlyIntegerProperty defenceNumberProperty() {
        return defenceNumber;
    }

    private void setDefenceNumber(int defenceNumber) {
        this.defenceNumber.set(defenceNumber);
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

    public final Vulnerability getVulnerability() {
        return vulnerability.get();
    }

    public final ReadOnlyObjectProperty<Vulnerability> vulnerabilityProperty() {
        return vulnerability;
    }

    private void setVulnerability(Vulnerability vulnerability) {
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
}
