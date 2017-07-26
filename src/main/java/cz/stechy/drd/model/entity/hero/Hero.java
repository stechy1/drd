package cz.stechy.drd.model.entity.hero;

import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.db.base.DatabaseItem;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.EntityBase;
import cz.stechy.drd.model.entity.EntityProperty;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.entity.SimpleEntityProperty;
import cz.stechy.drd.util.HashGenerator;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Základní třída definující atributy pro všechny hrdiny
 */
public class Hero extends EntityBase {

    // region Constants

    public static final Pattern NAME_PATTERN = Pattern.compile(
        "^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");

    // Základ pohyblivosti podle rasy
    private static final int[] AGILITY_BY_RACE = {10, 9, 8, 12, 11, 12, 11};

    // Tabulka nosnosti
    private static final int[] CAPACITY_BY_STRENGTH = {
        210, 240, 270, 300, 330, 360, 390, 420, 450, 480, 510};

    // Násobící konstanty pro určení naložení
    public static final double WEIGHT_LOW_MULTIPLICATOR = .75;
    public static final double WEIGHT_MEDIUM_MULTIPLICATOR = .5;
    public static final double WEIGHT_HIGH_MULTIPLICATOR = .25;

    // endregion

    // region Variables

    // Rasa
    private final ObjectProperty<Race> race = new SimpleObjectProperty<>(this, "race");
    // Profece
    private final ObjectProperty<Profession> profession = new SimpleObjectProperty<>(this, "profession");
    // Úroveň
    private final IntegerProperty level = new SimpleIntegerProperty(this, "level");
    // Peníze
    private final Money money = new Money();
    // Zkušenosti
    private final MaxActValue experiences = new MaxActValue();
    // Síla
    private final EntityProperty strength = new SimpleEntityProperty();
    // Obratnost
    private final EntityProperty dexterity = new SimpleEntityProperty();
    // Odolnost
    private final EntityProperty immunity = new SimpleEntityProperty();
    // Inteligence
    private final EntityProperty intelligence = new SimpleEntityProperty();
    // Charisma
    private final EntityProperty charisma = new SimpleEntityProperty();
    // Obranné číslo
    private final IntegerProperty defenceNumber = new SimpleIntegerProperty(this, 
        "defenceNumber");
    // Nosnost
    private final IntegerProperty capacity = new SimpleIntegerProperty(this, "capacity");
    // Pohyblivost
    private final EntityProperty agility = new SimpleEntityProperty();
    // Mírné naložení
    private final EntityProperty lowLoad = new SimpleEntityProperty();
    // Střední naložení
    private final EntityProperty mediumLoad = new SimpleEntityProperty();
    // Velké naložení
    private final EntityProperty highLoad = new SimpleEntityProperty();
    // Postřeh
    private final EntityProperty observationObjects = new SimpleEntityProperty();
    // Postřeh na mechanické předměty
    private final EntityProperty observationMechanics = new SimpleEntityProperty();

    private final BooleanProperty levelUp = new SimpleBooleanProperty(this, "levelUp");

    // endregion

    // region Constructors

    private Hero(Hero hero) {
        this(hero.getId(),
            hero.getName(),
            hero.getDescription(),
            hero.getAuthor(),
            hero.getConviction(),
            hero.getRace(),
            hero.getProfession(),
            hero.getLevel(),
            hero.getMoney().getRaw(),
            hero.getExperiences().getActValue().intValue(),
            hero.getStrength().getValue(),
            hero.getDexterity().getValue(),
            hero.getImmunity().getValue(),
            hero.getIntelligence().getValue(),
            hero.getCharisma().getValue(),
            hero.getHeight(),
            hero.getDefenceNumber(),
            hero.getLive().getActValue().intValue(),
            hero.getLive().getMaxValue().intValue(),
            hero.getMag().getActValue().intValue(),
            hero.getMag().getMaxValue().intValue(),
            false, false);
    }

    /**
     * Vytvoří nového hrdinu
     *
     * @param id Id hrdiny
     * @param name Jméno hrdiny
     * @param description Popis hrdiny
     * @param author Autor hrdiny
     * @param conviction Přesvědčení
     * @param race Rasa
     * @param profession Povolání
     * @param level Úroveň
     * @param money Peníze
     * @param experiences Zkušenosti
     * @param strength Síla
     * @param dexterity Obratnost
     * @param immunity Odolnost
     * @param intelligence Inteligence
     * @param charisma Charisma
     * @param height Výška
     * @param defenceNumber Obranné číslo
     * @param live Aktuální počet životů
     * @param maxLive Maximální počet životů
     * @param mag Aktuální počet magů
     * @param maxMag Maximální počet magů
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    private Hero(String id, String name, String description, String author,
        Conviction conviction, Race race, Profession profession,
        int level, int money, int experiences, int strength, int dexterity, int immunity,
        int intelligence, int charisma, Height height, int defenceNumber, int live,
        int maxLive, int mag, int maxMag, boolean downloaded, boolean uploaded) {
        super(id, author, name, description, live, maxLive, mag, maxMag, conviction, height,
            downloaded, uploaded);

        initBindings();

        setRace(race);
        setProfession(profession);
        setLevel(level);
        this.money.setRaw(money);
        this.experiences.setActValue(experiences);
        this.strength.setValue(strength);
        this.dexterity.setValue(dexterity);
        this.immunity.setValue(immunity);
        this.intelligence.setValue(intelligence);
        this.charisma.setValue(charisma);
        this.defenceNumber.setValue(defenceNumber);


    }

    // endregion

    // region Private methods

    /**
     * Inicializuje závislosti mezi proměnnými
     */
    private void initBindings() {
        // Nastavení pohyblivosti
        this.agility.bindTo(
            Bindings
                .add(race.get() == null ? 0 : AGILITY_BY_RACE[race.get().ordinal()], Bindings.add(
                    dexterity.repairProperty(), Bindings.multiply(
                        2, strength.repairProperty()
                    ))
                ));

        // Nastavení nosnosti
        this.capacity.bind(Bindings.createIntegerBinding(() ->
            CAPACITY_BY_STRENGTH[this.strength.getRepair() + 5],
            this.strength.repairProperty()));

        // Nastavení postřehu na objekty
        this.observationObjects.bindTo(intelligence.valueProperty());
        // Nastavení postřehu na mechanismy
        this.observationMechanics.bindTo(Bindings.divide(intelligence.valueProperty(), 2));

        this.agility.valueProperty().addListener((observable, oldValue, newValue) -> {
            final double value = newValue.doubleValue();
            this.lowLoad.setValue(Math.round(Math.floor(value * WEIGHT_LOW_MULTIPLICATOR)));
            this.mediumLoad.setValue(Math.round(Math.floor(value * WEIGHT_MEDIUM_MULTIPLICATOR)));
            this.highLoad.setValue(Math.round(Math.floor(value * WEIGHT_HIGH_MULTIPLICATOR)));
        });

        this.levelProperty().addListener((observable, oldValue, newValue) ->
            this.experiences.setMaxValue(HeroGenerator.experience(getRace(), newValue.intValue())));

        this.levelUp.bind(Bindings.createBooleanBinding(() -> {
            final int experience = this.experiences.getActValue().intValue();
            final int maxExperience = this.experiences.getMaxValue().intValue();
            return experience >= maxExperience;
        }, this.experiences.actValueProperty(), this.experiences.maxValueProperty()));
    }

    // endregion

    // region Getters & Setters

    public final Race getRace() {
        return race.get();
    }

    public final ReadOnlyObjectProperty<Race> raceProperty() {
        return race;
    }

    private void setRace(Race race) {
        this.race.set(race);
    }

    public final Profession getProfession() {
        return profession.get();
    }

    public final ReadOnlyObjectProperty<Profession> professionProperty() {
        return profession;
    }

    private void setProfession(Profession profession) {
        this.profession.set(profession);
    }

    public final int getLevel() {
        return level.get();
    }

    public final ReadOnlyIntegerProperty levelProperty() {
        return level;
    }

    private void setLevel(int level) {
        this.level.set(level);
    }

    public final Money getMoney() {
        return money;
    }

    public final MaxActValue getExperiences() {
        return experiences;
    }

    public final EntityProperty getStrength() {
        return strength;
    }

    public final EntityProperty getDexterity() {
        return dexterity;
    }

    public final EntityProperty getImmunity() {
        return immunity;
    }

    public final EntityProperty getIntelligence() {
        return intelligence;
    }

    public final EntityProperty getCharisma() {
        return charisma;
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

    public final int getCapacity() {
        return capacity.get();
    }

    public final ReadOnlyIntegerProperty capacityProperty() {
        return capacity;
    }

    private void setCapacity(int capacity) {
        this.capacity.set(capacity);
    }

    public final EntityProperty getAgility() {
        return agility;
    }

    public final EntityProperty getLowLoad() {
        return lowLoad;
    }

    public final EntityProperty getMediumLoad() {
        return mediumLoad;
    }

    public final EntityProperty getHighLoad() {
        return highLoad;
    }

    public final EntityProperty getObservationObjects() {
        return observationObjects;
    }

    public final EntityProperty getObservationMechanics() {
        return observationMechanics;
    }

    public final boolean canLevelUp() {
        return levelUp.get();
    }

    public final ReadOnlyBooleanProperty levelUpProperty() {
        return levelUp;
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        Hero hero = (Hero) other;
        setRace(hero.getRace());
        setProfession(hero.getProfession());
        setLevel(hero.getLevel());
        this.money.setRaw(hero.getMoney().getRaw());
        this.experiences.update(hero.getExperiences());
        this.strength.setValue(hero.getStrength().getValue());
        this.dexterity.setValue(hero.getDexterity().getValue());
        this.immunity.setValue(hero.getImmunity().getValue());
        this.intelligence.setValue(hero.getIntelligence().getValue());
        this.charisma.setValue(hero.getCharisma().getValue());
        this.defenceNumber.setValue(hero.getDefenceNumber());
    }

    @Override
    @SuppressWarnings({"unused", "unchecked"})
    public <T extends IClonable> T duplicate() {
        return (T) new Hero(this);
    }

    // endregion

    // Výčet ras
    public enum Race {
        HOBIT, KUDUK, DWARF, ELF, HUMAN, BARBAR, KROLL;

        public static Race valueOf(int index) {
            if (index < 0) {
                return null;
            }

            return Race.values()[index];
        }
    }

    // Výčet povolání
    public enum Profession {
        WARIOR, RANGER, ALCHEMIST, MAGICIAN, THIEF;

        public static Profession valueOf(int index) {
            if (index < 0) {
                return null;
            }

            return Profession.values()[index];
        }
    }

    public static class Builder {

        private String id = HashGenerator.createHash();
        private String author = "";
        private String name = "";
        private String description = "";
        private Conviction conviction = Conviction.NEUTRAL;
        private Race race;
        private Profession profession;
        private int level;
        private int money;
        private int experiences;
        private int strength;
        private int dexterity;
        private int immunity;
        private int intelligence;
        private int charisma;
        private Height height = Height.B;
        private int defenceNumber;
        private int live;
        private int maxLive;
        private int mag;
        private int maxMag;
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

        public Builder conviction(int conviction) {
            this.conviction = Conviction.valueOf(conviction);
            return this;
        }

        public Builder race(int race) {
            this.race = Race.valueOf(race);
            return this;
        }

        public Builder profession(int profession) {
            this.profession = Profession.valueOf(profession);
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder money(int money) {
            this.money = money;
            return this;
        }

        public Builder experiences(int experiences) {
            this.experiences = experiences;
            return this;
        }

        public Builder strength(int strength) {
            this.strength = strength;
            return this;
        }

        public Builder dexterity(int dexterity) {
            this.dexterity = dexterity;
            return this;
        }

        public Builder immunity(int immunity) {
            this.immunity = immunity;
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

        public Builder height(int height) {
            this.height = Height.valueOf(height);
            return this;
        }

        public Builder defenceNumber(int defenceNumber) {
            this.defenceNumber = defenceNumber;
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

        public Builder description(String description) {
            this.description = description;
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

        public Hero build() {
            return new Hero(id, name, description, author, conviction, race, profession, level,
                money, experiences, strength, dexterity, immunity, intelligence, charisma,
                height, defenceNumber, live, maxLive, mag, maxMag, downloaded, uploaded);
        }
    }
}