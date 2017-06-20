package cz.stechy.drd.model.persistent;

import cz.stechy.drd.controller.hero.creator.HeroCreatorHelper;
import cz.stechy.drd.model.db.BaseDatabaseManager;
import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.db.base.Database;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * Správce postav
 * Umožňuje CRUD operace a mnohem víc
 */
public class HeroManager extends BaseDatabaseManager<Hero> {

    // region Constants

    // Název tabulky
    private static final String TABLE = "hero";

    // Názvy sloupců v databázi
    private static final String COLUMN_ID = TABLE + "_id";
    private static final String COLUMN_NAME = TABLE + "_name";
    private static final String COLUMN_DESCRIPTION = TABLE + "_description";
    private static final String COLUMN_CONVICTION = TABLE + "_conviction";
    private static final String COLUMN_RACE = TABLE + "_race";
    private static final String COLUMN_PROFESSION = TABLE + "_profession";
    private static final String COLUMN_LEVEL = TABLE + "_level";
    private static final String COLUMN_MONEY = TABLE + "_money";
    private static final String COLUMN_EXPERIENCES = TABLE + "_experiences";
    private static final String COLUMN_STRENGTH = TABLE + "_strength";
    private static final String COLUMN_DEXTERITY = TABLE + "_dexterity";
    private static final String COLUMN_IMMUNITY = TABLE + "_immunity";
    private static final String COLUMN_INTELLIGENCE = TABLE + "_intelligence";
    private static final String COLUMN_CHARISMA = TABLE + "_charisma";
    private static final String COLUMN_HEIGHT = TABLE + "_height";
    private static final String COLUMN_DEFENCE_NUMBER = TABLE + "_defence_number";
    private static final String COLUMN_LIVE = TABLE + "_live";
    private static final String COLUMN_MAX_LIVE = TABLE + "_max_live";
    private static final String COLUMN_MAG = TABLE + "_mag";
    private static final String COLUMN_MAX_MAG = TABLE + "_max_mag";
    private static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
        COLUMN_CONVICTION, COLUMN_RACE, COLUMN_PROFESSION, COLUMN_LEVEL, COLUMN_MONEY,
        COLUMN_EXPERIENCES, COLUMN_STRENGTH, COLUMN_DEXTERITY, COLUMN_IMMUNITY, COLUMN_INTELLIGENCE,
        COLUMN_CHARISMA, COLUMN_HEIGHT, COLUMN_DEFENCE_NUMBER, COLUMN_LIVE, COLUMN_MAX_LIVE,
        COLUMN_MAG, COLUMN_MAX_MAG};
    private static final String COLUMNS_KEYS = GENERATE_COLUMN_KEYS(COLUMNS);
    private static final String COLUMNS_VALUES = GENERATE_COLUMNS_VALUES(COLUMNS);
    private static final String COLUMNS_UPDATE = GENERATE_COLUMNS_UPDATE(COLUMNS);
    private static final String QUERY_CREATE = String.format("CREATE TABLE IF NOT EXISTS %s("
            + "%s VARCHAR PRIMARY KEY NOT NULL UNIQUE,"         // id
            + "%s VARCHAR(255) NOT NULL,"                       // name
            + "%s VARCHAR(255),"                                // description
            + "%s INT NOT NULL,"                                // conviction
            + "%s INT NOT NULL,"                                // race
            + "%s INT NOT NULL,"                                // profession
            + "%s INT NOT NULL,"                                // level
            + "%s INT NOT NULL,"                                // money
            + "%s INT NOT NULL,"                                // experiences
            + "%s INT NOT NULL,"                                // strength
            + "%s INT NOT NULL,"                                // dexterity
            + "%s INT NOT NULL,"                                // immunity
            + "%s INT NOT NULL,"                                // intelligence
            + "%s INT NOT NULL,"                                // charisma
            + "%s INT NOT NULL,"                                // height
            + "%s INT NOT NULL,"                                // defence number
            + "%s INT NOT NULL,"                                // live
            + "%s INT NOT NULL,"                                // max live
            + "%s INT NOT NULL,"                                // mag
            + "%s INT NOT NULL"                                 // max mag
            + ");", TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_CONVICTION,
        COLUMN_RACE, COLUMN_PROFESSION, COLUMN_LEVEL, COLUMN_MONEY, COLUMN_EXPERIENCES,
        COLUMN_STRENGTH, COLUMN_DEXTERITY, COLUMN_IMMUNITY, COLUMN_INTELLIGENCE, COLUMN_CHARISMA,
        COLUMN_HEIGHT, COLUMN_DEFENCE_NUMBER, COLUMN_LIVE, COLUMN_MAX_LIVE, COLUMN_MAG,
        COLUMN_MAX_MAG);

    // endregion

    // region Variables

    private static boolean tableInitialized = false;

    private final ObjectProperty<Hero> hero = new SimpleObjectProperty<>(
        new Hero.Builder().build());
    // Správce inventáře pro hrdinu
    private InventoryManager inventoryManager;

    // endregion

    // region Constructors

    /**
     * Vytvoří nového správce hrdinů
     *
     * @param db {@link Database} Databáze, která obsahuje data o hrdinech
     */
    public HeroManager(Database db) {
        super(db);
    }

    // endregion

    // region Private methods

    @Override
    protected Hero parseResultSet(ResultSet resultSet) throws SQLException {
        return new Hero.Builder()
            .id(resultSet.getString(COLUMN_ID))
            .name(resultSet.getString(COLUMN_NAME))
            .description(resultSet.getString(COLUMN_DESCRIPTION))
            .conviction(resultSet.getInt(COLUMN_CONVICTION))
            .race(resultSet.getInt(COLUMN_RACE))
            .profession(resultSet.getInt(COLUMN_PROFESSION))
            .level(resultSet.getInt(COLUMN_LEVEL))
            .money(resultSet.getInt(COLUMN_MONEY))
            .experiences(resultSet.getInt(COLUMN_EXPERIENCES))
            .strength(resultSet.getInt(COLUMN_STRENGTH))
            .dexterity(resultSet.getInt(COLUMN_DEXTERITY))
            .immunity(resultSet.getInt(COLUMN_IMMUNITY))
            .intelligence(resultSet.getInt(COLUMN_INTELLIGENCE))
            .charisma(resultSet.getInt(COLUMN_CHARISMA))
            .height(resultSet.getInt(COLUMN_HEIGHT))
            .defenceNumber(resultSet.getInt(COLUMN_DEFENCE_NUMBER))
            .live(resultSet.getInt(COLUMN_LIVE))
            .maxLive(resultSet.getInt(COLUMN_MAX_LIVE))
            .mag(resultSet.getInt(COLUMN_MAG))
            .maxMag(resultSet.getInt(COLUMN_MAX_MAG))
            .build();
    }

    @Override
    protected List<Object> itemToParams(Hero hero) {
        return new ArrayList<>(Arrays.asList(
            hero.getId(),
            hero.getName(),
            hero.getDescription(),
            hero.getConviction().ordinal(),
            hero.getRace().ordinal(),
            hero.getProfession().ordinal(),
            hero.getLevel(),
            hero.getMoney().getRaw(),
            hero.getExperiences().getActValue().intValue(),
            hero.getStrength().getValue(),
            hero.getDexterity().getValue(),
            hero.getImmunity().getValue(),
            hero.getIntelligence().getValue(),
            hero.getCharisma().getValue(),
            hero.getHeight().ordinal(),
            hero.getDefenceNumber(),
            hero.getLive().getActValue().intValue(),
            hero.getLive().getMaxValue().intValue(),
            hero.getMag().getActValue().intValue(),
            hero.getMag().getMaxValue().intValue()
        ));
    }

    @Override
    protected String getTable() {
        return TABLE;
    }

    @Override
    protected String getColumnWithId() {
        return COLUMN_ID;
    }

    @Override
    protected String getColumnsKeys() {
        return COLUMNS_KEYS;
    }

    @Override
    protected String getColumnValues() {
        return COLUMNS_VALUES;
    }

    @Override
    protected String getColumnsUpdate() {
        return COLUMNS_UPDATE;
    }

    @Override
    protected String getInitializationQuery() {
        return QUERY_CREATE;
    }

    // endregion

    // region Public methods

    @Override
    public void createTable() throws DatabaseException {
        if (tableInitialized) {
            return;
        }

        super.createTable();
        tableInitialized = true;
    }

    public void insert(Hero hero, ObservableList<HeroCreatorHelper.ItemEntry> items)
        throws DatabaseException {
        insert(hero);

        InventoryManager inventoryManager = getInventory(hero);
        InventoryHelper.insertItemsToInventory(inventoryManager, items);
    }

    @Override
    public void insert(Hero hero) throws DatabaseException {
        InventoryManager manager = getInventory();
        manager.insert(InventoryManager.standartInventory(hero));

        super.insert(hero);
    }

    @Override
    public void delete(String id) throws DatabaseException {
        super.delete(id);
        inventoryManager.delete(id);
    }

    /**
     * Vrátí inventář aktuálně otevřeného hrdiny
     *
     * @return {@link InventoryManager}
     */
    public InventoryManager getInventory() {
        return getInventory(hero.get());
    }

    /**
     * Vytvoří novou instanci správce itemů pro zadaného hrdinu
     *
     * @return {@link InventoryContent}
     */
    public InventoryManager getInventory(Hero hero) {
        if (inventoryManager == null) {
            inventoryManager = new InventoryManager(db, hero);
            inventoryManager.selectAll();
        } else if (inventoryManager.getHero() != hero) {
            inventoryManager = new InventoryManager(db, hero);
            inventoryManager.selectAll();
        }

        return inventoryManager;
    }

    // endregion

    // region Getters & Setters

    public ObjectProperty<Hero> getHero() {
        return hero;
    }

    // endregion
}
