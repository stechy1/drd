package cz.stechy.drd.service.dice;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public interface IDiceService {

    /**
     * Provede hod kostkou
     *
     * @param sides Počet stěn kostky
     * @param count Počet hodů
     */
    void roll(int sides, int count);

    void addAddition(DiceAddition addition);

    void removeAdditions(List<DiceAddition> additions);

    ObservableList<Integer> getRollResulsts();

    ObservableList<DiceAddition> getAdditions();

    enum DiceType {
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

    enum AdditionType {
        // TODO vymyslet, jak to bude když budu chtít přidat vlastní konstantu
        STRENGTH, DEXTERITY, IMMUNITY, INTELLIGENCE, CHARISMA;

        public static AdditionType valueOf(int value) {
            return AdditionType.values()[value];
        }
    }

    class DiceAddition {

        final ObjectProperty<AdditionType> additionType = new SimpleObjectProperty<>(AdditionType.STRENGTH);
        final BooleanProperty useRepair = new SimpleBooleanProperty();
        final BooleanProperty useSubtract = new SimpleBooleanProperty();

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
