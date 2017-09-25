package cz.stechy.drd.controller.dice;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.hero.Hero;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Pomocná knihovní třída pro kontroler s kostkou
 */
public final class DiceHelper {

    // region Variables

    public final ObservableList<DiceAddition> additions = FXCollections.observableArrayList();
    //public final IntegerProperty rollResult = new SimpleIntegerProperty();
    public final ObservableList<Integer> rollResults = FXCollections.observableArrayList();
    private final Hero hero;

    // endregion

    // region Constructors

    public DiceHelper(Hero hero) {
        this.hero = hero;
    }

    // endregion

    // region Public methods

    /**
     * Provede hod kostkou
     *
     * @param sides Počet stěn kostky
     * @param count Počet hodů
     */
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

    // endregion

    public enum DiceType {
        CUSTOM(0), TWO(2), SIX(6), TEN(10), TWENTY(20), HUNDRED(100);

        private final int sideCount;

        DiceType(int value) {
            this.sideCount = value;
        }

        public int getSideCount() {
            return sideCount;
        }

        @Override
        public String toString() {
            return String.valueOf(sideCount);
        }
    }

    public enum AdditionType {
        // TODO vymyslet, jak to bude když budu chtít přidat vlastní konstantu
        STRENGTH, DEXTERITY, IMMUNITY, INTELLIGENCE, CHARISMA;

        public static AdditionType valueOf(int value) {
            return AdditionType.values()[value];
        }
    }

    public static class DiceAddition {

        private final ObjectProperty<AdditionType> additionType = new SimpleObjectProperty<>(
            AdditionType.STRENGTH);
        private final BooleanProperty useRepair = new SimpleBooleanProperty();
        private final BooleanProperty useSubtract = new SimpleBooleanProperty();

        public AdditionType getAdditionType() {
            return additionType.get();
        }

        public ObjectProperty<AdditionType> additionTypeProperty() {
            return additionType;
        }

        public void setAdditionType(AdditionType additionType) {
            this.additionType.set(additionType);
        }

        public boolean isUseRepair() {
            return useRepair.get();
        }

        public BooleanProperty useRepairProperty() {
            return useRepair;
        }

        public void setUseRepair(boolean useRepair) {
            this.useRepair.set(useRepair);
        }

        public boolean getUseSubtract() {
            return useSubtract.get();
        }

        public BooleanProperty useSubtractProperty() {
            return useSubtract;
        }

        public void setUseSubtract(boolean useSubtract) {
            this.useSubtract.set(useSubtract);
        }
    }

}
