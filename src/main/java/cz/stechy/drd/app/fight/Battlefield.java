package cz.stechy.drd.app.fight;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.IAggressive;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Třída reprezentující bojiště pro souboje
 */
final class Battlefield {

    // region Constants

    private static final Duration DELAY = Duration.seconds(2);

    // endregion

    // region Variables

    final Timeline timeline = new Timeline();

    private OnFightFinishListener fightFinishListener;
    private OnActionVisualizeListener onActionVisualizeListener;

    private int turnCount;

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
                    IAggressive entity = aggressiveEntity1.isAlive() ? aggressiveEntity2 : aggressiveEntity1;
                    visualizeAction(BattlefieldAction.DEATH, entity.toString());
                    callEndHandler();
                } else {
                    visualizeAction(BattlefieldAction.TURN, ++turnCount);
                    visualizeAction(BattlefieldAction.SEPARATOR);
                    visualizeAction(BattlefieldAction.ATTACK_INFO, aggressiveEntity1.toString());
                }
            }),
            new KeyFrame(DELAY, event -> { // Útočí první entita
                attack(aggressiveEntity1, aggressiveEntity2);
            }), // Utočí první
            new KeyFrame(DELAY, event -> { // Kontrola, že druhá entita je stále na živu
                if (!aggressiveEntity2.isAlive()) {
                    timeline.stop();
                    visualizeAction(BattlefieldAction.DEATH, aggressiveEntity2.toString());
                    callEndHandler();
                } else {
                    visualizeAction(BattlefieldAction.ATTACK_INFO, aggressiveEntity2.toString());
                }
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

    private void visualizeAction(BattlefieldAction action, Object... params) {
        if (onActionVisualizeListener != null) {
            onActionVisualizeListener.onActionVisualize(action, params);
        }
    }

    private void attack(IAggressive attacker, IAggressive defender) {
        final int attackNumber = Math.max(Dice.K6.roll() + attacker.getAttackNumber(), 0);
        visualizeAction(BattlefieldAction.ATTACK, attacker.toString(), attackNumber);
        final int defnenceNumber = Math.max(Dice.K6.roll() + defender.getDefenceNumber(), 0);
        visualizeAction(BattlefieldAction.DEFENCE, defender.toString(), defnenceNumber);
        final boolean attackSuccess = attackNumber > defnenceNumber;

        if (attackSuccess) { // Pokud byl útok úspěšný
            final int subtractLive = Math.max(attackNumber - defnenceNumber, 1);
            defender.subtractLive(subtractLive);
            visualizeAction(BattlefieldAction.HEALTH, attacker.toString(), subtractLive);
        } else {
            visualizeAction(BattlefieldAction.BLOCK, defender.toString(), attacker.toString());
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

    public void setFightFinishListener(OnFightFinishListener fightFinishListener) {
        this.fightFinishListener = fightFinishListener;
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
         *  @param action
         * @param params*/
        void onActionVisualize(BattlefieldAction action, Object... params);

    }

    public enum BattlefieldAction {
        ATTACK, DEFENCE, HEALTH, BLOCK, DEATH, TURN, SEPARATOR, ATTACK_INFO
    }
}
