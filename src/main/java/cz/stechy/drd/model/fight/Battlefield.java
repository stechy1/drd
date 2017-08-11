package cz.stechy.drd.model.fight;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.IAggressive;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Třída reprezentující bojiště pro souboje
 */
public final class Battlefield {

    // region Constants

    private static final Duration DELAY = Duration.seconds(2);

    // endregion

    // region Variables

    final Timeline timeline = new Timeline();

    private OnFightFinishListener fightFinishListener;
    private OnActionVisualizeListener onActionVisualizeListener;

    // endregion

    // region Constructors

    /**
     * Vytvoří nové bojiště pro dvě entity
     * @param aggressiveEntity1 První bojovník
     * @param aggressiveEntity2 Druhý bojovník
     */
    public Battlefield(IAggressive aggressiveEntity1, IAggressive aggressiveEntity2) {
        timeline.getKeyFrames().setAll(
            new KeyFrame(DELAY, event -> { // Kontrola že jsou všichni živí a zdraví
                if (!aggressiveEntity1.isAlive() || !aggressiveEntity2.isAlive()) {
                    timeline.stop();
                    callEndHandler();
                }
                visualizeAction("Útočí: " + aggressiveEntity1.toString());
            }),
            new KeyFrame(DELAY, event -> { // Útočí první entita
                attack(aggressiveEntity1, aggressiveEntity2);
            }), // Utočí první
            new KeyFrame(DELAY, event -> { // Kontrola, že druhá entita je stále na živu
                if (!aggressiveEntity2.isAlive()) {
                    timeline.stop();
                    callEndHandler();
                }
                visualizeAction("Útočí: " + aggressiveEntity2.toString());
            }),
            new KeyFrame(DELAY, event -> { // Útočí druhá entita
                attack(aggressiveEntity2, aggressiveEntity1);
            })  // Útočí druhý
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // endregion

    // region Private methods

    private void visualizeAction(String action) {
        if (onActionVisualizeListener != null) {
            onActionVisualizeListener.onActionVisualize(action);
        }
        System.out.println(action);
    }

    private void attack(IAggressive attacker, IAggressive defender) {
        final int attackNumber = Math.max(Dice.K6.roll() + attacker.getAttackNumber(), 0);
        visualizeAction(attacker.toString() + " útočí s útočným číslem: " + attackNumber);
        final int defnenceNumber = Math.max(Dice.K6.roll() + defender.getDefenceNumber(), 0);
        visualizeAction(defender.toString() + " se brání s obranným číslem: " + defnenceNumber);
        final boolean attackSuccess = attackNumber > defnenceNumber;

        if (attackSuccess) { // Pokud byl útok úspěšný
            final int subtractLive = Math.max(attackNumber - defnenceNumber, 1);
            defender.subtractLive(subtractLive);
            visualizeAction(attacker.toString() + " ubírá celkem: " + subtractLive + " protivníkovi: " + defender.toString());
        }

    }

    /**
     * Zavolá handler na obsluhu události
     */
    private void callEndHandler() {
        if (fightFinishListener != null) {
            fightFinishListener.onFightFinish();
        }
    }

    // endregion

    // region Public methods

    /**
     * Spustí souboj
     */
    public void fight() {
        timeline.play();
    }

    /**
     * Zastaví souboj
     */
    public void stopFight() {
        timeline.stop();
    }

    // endregion

    // region Getters & Setters

    public OnFightFinishListener getFightFinishListener() {
        return fightFinishListener;
    }

    public Battlefield setFightFinishListener(OnFightFinishListener fightFinishListener) {
        this.fightFinishListener = fightFinishListener;
        return this;
    }

    public OnActionVisualizeListener getOnActionVisualizeListener() {
        return onActionVisualizeListener;
    }

    public void setOnActionVisualizeListener(OnActionVisualizeListener onActionVisualizeListener) {
        this.onActionVisualizeListener = onActionVisualizeListener;
    }

    // endregion

    public interface OnFightFinishListener {

        /**
         * Metoda, která se zavolá po skončení souboje
         */
        void onFightFinish();
    }

    public interface OnActionVisualizeListener {

        /**
         * Metoda je zavolána po každe, když se provede nějaká akce
         *
         * @param description
         */
        void onActionVisualize(String description);

    }
}
