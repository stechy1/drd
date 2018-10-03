package cz.stechy.drd.service.dice;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.hero.Hero;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiceService implements IDiceService {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(DiceService.class);

    // endregion

    // region Variables

    private final ObservableList<DiceAddition> additions = FXCollections.observableArrayList();
    private final ObservableList<Integer> rollResults = FXCollections.observableArrayList();
    private final Hero hero;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou servisu kostky pro zadaného hrdinu
     *
     * @param hero {@link Hero}
     */
    DiceService(Hero hero) {
        this.hero = hero;
    }

    // endregion

    // region Public methods

    @Override
    public void roll(int sides, int count) {
        rollResults.clear();
        Dice dice = new Dice(sides);
        for (int i = 0; i < count; i++) {
            int result = dice.roll();
            for (DiceAddition addition : additions) {
                int value = 0;
                switch (addition.additionType.get()) {
                    case STRENGTH:
                        value = addition.useRepair.get()
                            ? hero.getStrength().getRepair()
                            : hero.getStrength().getValue();
                        break;
                    case DEXTERITY:
                        value = addition.useRepair.get()
                            ? hero.getDexterity().getRepair()
                            : hero.getDexterity().getValue();
                        break;
                    case IMMUNITY:
                        value = addition.useRepair.get()
                            ? hero.getImmunity().getRepair()
                            : hero.getImmunity().getValue();
                        break;
                    case INTELLIGENCE:
                        value = addition.useRepair.get()
                            ? hero.getIntelligence().getRepair()
                            : hero.getIntelligence().getValue();
                        break;
                    case CHARISMA:
                        value = addition.useRepair.get()
                            ? hero.getCharisma().getRepair()
                            : hero.getCharisma().getValue();
                        break;
                }
                if (addition.useSubtract.get()) {
                    value *= -1;
                }
                result += value;
            }
            rollResults.add(result);
        }
    }

    @Override
    public void addAddition(DiceAddition addition) {
        this.additions.add(addition);
    }

    @Override
    public void removeAdditions(List<DiceAddition> additions) {
        this.additions.removeAll(additions);
    }

    // endregion

    // region Getters & Setters

    @Override
    public ObservableList<Integer> getRollResulsts() {
        return FXCollections.unmodifiableObservableList(rollResults);
    }

    @Override
    public ObservableList<DiceAddition> getAdditions() {
        return FXCollections.unmodifiableObservableList(additions);
    }

    // endregion
}
