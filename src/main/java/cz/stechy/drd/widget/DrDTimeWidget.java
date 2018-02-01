package cz.stechy.drd.widget;

import cz.stechy.drd.R;
import cz.stechy.drd.model.DrDTime;
import cz.stechy.drd.util.Translator;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DrDTimeWidget extends VBox {

    // region Constants

    private static final String DEFAULT_TEXT_FORMAT = " %d %s";

    private static final int YEAR = 0;
    private static final int MONTH = 1;
    private static final int DAY = 2;
    private static final int INNING = 3;
    private static final int CYCLE = 4;

    private static final String[][] TRANSLATING_KEYS = {
        {
            R.Translate.TIME_YEAR_1,
            R.Translate.TIME_YEAR_2,
            R.Translate.TIME_YEAR_3},
        {
            R.Translate.TIME_MONTH_1,
            R.Translate.TIME_MONTH_2,
            R.Translate.TIME_MONTH_3},
        {
            R.Translate.TIME_DAY_1,
            R.Translate.TIME_DAY_2,
            R.Translate.TIME_DAY_3},
        {
            R.Translate.TIME_INNING_1,
            R.Translate.TIME_INNING_2,
            R.Translate.TIME_INNING_3},
        {
            R.Translate.TIME_CYCLE_1,
            R.Translate.TIME_CYCLE_2,
            R.Translate.TIME_CYCLE_3}
    };

    // endregion

    // region Variables

    private final Label title = new Label();

    private final Text yearText = new Text();
    private final Text monthText = new Text();
    private final Text dayText = new Text();
    private final Text inningText = new Text();
    private final Text cycleText = new Text();

    private DrDTime time = new DrDTime();
    private Translator translator;

    // endregion

    {
        TextFlow textContainer = new TextFlow(yearText, monthText, dayText, inningText, cycleText);
        getChildren().setAll(title, textContainer);
    }

    // region Private methods

    private void init() {
        title.setText(translator.translate(R.Translate.SPELL_DURATION));

        yearText.visibleProperty().bind(
            time.year.isNotEqualTo(0));
        monthText.visibleProperty().bind(
            time.month.isNotEqualTo(0));
        dayText.visibleProperty().bind(
            time.day.isNotEqualTo(0));
        inningText.visibleProperty().bind(
            time.inning.isNotEqualTo(0));
        cycleText.visibleProperty().bind(
            time.cycle.isNotEqualTo(0));
        yearText.managedProperty().bind(yearText.visibleProperty());
        monthText.managedProperty().bind(monthText.visibleProperty());
        dayText.managedProperty().bind(dayText.visibleProperty());
        inningText.managedProperty().bind(inningText.visibleProperty());
        cycleText.managedProperty().bind(cycleText.visibleProperty());

        yearText.textProperty().bind(Bindings.createStringBinding(() -> {
            final int value = time.getYear();
            return String.format(DEFAULT_TEXT_FORMAT, value, translator.translate(TRANSLATING_KEYS[YEAR][getIndex(value)]));
        }, time.year));
        monthText.textProperty().bind(Bindings.createStringBinding(() -> {
            final int value = time.getMonth();
            return String.format(DEFAULT_TEXT_FORMAT, value, translator.translate(TRANSLATING_KEYS[MONTH][getIndex(value)]));
        }, time.month));
        dayText.textProperty().bind(Bindings.createStringBinding(() -> {
            final int value = time.getDay();
            return String.format(DEFAULT_TEXT_FORMAT, value, translator.translate(TRANSLATING_KEYS[DAY][getIndex(value)]));
        }, time.day));
        inningText.textProperty().bind(Bindings.createStringBinding(() -> {
            final int value = time.getInning();
            return String.format(DEFAULT_TEXT_FORMAT, value, translator.translate(TRANSLATING_KEYS[INNING][getIndex(value)]));
        }, time.inning));
        cycleText.textProperty().bind(Bindings.createStringBinding(() -> {
            final int value = time.getCycle();
            return String.format(DEFAULT_TEXT_FORMAT, value, translator.translate(TRANSLATING_KEYS[CYCLE][getIndex(value)]));
        }, time.cycle));
    }

    /**
     * Vrátí správny index dle hodnoty
     *
     * @param value Hodnota
     * @return Index do pole
     */
    private int getIndex(int value) {
        switch (value) {
            case 1:
                return 0;
            case 2:
            case 3:
            case 4:
                return 1;
            default:
                return 2;
        }
    }

    // endregion

    // region Public methods

    /**
     * Nastaví listenery pro správné zobrazení textu
     *
     * @param time {@link DrDTime}
     * @param translator {@link Translator}
     */
    public void bind(DrDTime time, Translator translator) {
        this.time = time;
        this.translator = translator;
        unbind();
        init();
    }

    /**
     * Odebere listenery
     */
    public void unbind() {
        yearText.visibleProperty().unbind();
        monthText.visibleProperty().unbind();
        dayText.visibleProperty().unbind();
        inningText.visibleProperty().unbind();
        cycleText.visibleProperty().unbind();
        yearText.textProperty().unbind();
        monthText.textProperty().unbind();
        dayText.textProperty().unbind();
        inningText.textProperty().unbind();
        cycleText.textProperty().unbind();
        yearText.managedProperty().unbind();
        monthText.managedProperty().unbind();
        dayText.managedProperty().unbind();
        inningText.managedProperty().unbind();
        cycleText.managedProperty().unbind();
    }

    // endregion
}
