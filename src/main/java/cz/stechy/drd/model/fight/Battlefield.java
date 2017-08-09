package cz.stechy.drd.model.fight;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.EntityBase;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Třída reprezentující bojiště pro souboje
 */
public final class Battlefield {

    // region Variables

    private final EntityBase entity1;
    private final EntityBase entity2;

    // endregion

    // region Constructors

    /**
     * Vytvoří nové bojiště pro dvě entity
     *
     * @param entity1 První bojovník
     * @param entity2 Druhý bojovník
     */
    public Battlefield(EntityBase entity1, EntityBase entity2) {
        this.entity1 = entity1;
        this.entity2 = entity2;
    }

    // endregion

    // region Private methods

    private void attack(EntityBase attacker, EntityBase defender) {
        final int attackNumber = Math.max(Dice.K6.roll() + attacker.getAttackNumber(), 0);
        final int defnenceNumber = Math.max(Dice.K6.roll() + defender.getDefenceNumber(), 0);
        final boolean attackSuccess = attackNumber > defnenceNumber;
        if (attackSuccess) {
            defender.getLive().subtract(Math.max(attackNumber - defnenceNumber, 1));
        }
        System.out.println("ÚČ: " + attackNumber + "; DEF: " + defnenceNumber + " delta: " + (attackNumber - defnenceNumber));

    }

    // endregion

    // region Public methods

    public void fight() {
        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().setAll(
            new KeyFrame(Duration.ZERO, event -> { // Kontrola že jsou všichni živí a zdraví
                if (!entity1.isAlive() || !entity2.isAlive()) {
                    timeline.stop();
                }
            }),
            new KeyFrame(Duration.millis(500), event -> { // Útočí první entita
                System.out.println("Útočí hrdina");
                attack(entity1, entity2);
            }), // Utočí první
            new KeyFrame(Duration.ZERO, event -> { // Kontrola, že druhá entita je stále na živu
                if (!entity2.isAlive()) {
                    timeline.stop();
                }
            }),
            new KeyFrame(Duration.millis(500), event -> { // Útočí druhá entita
                System.out.println("Útočí oponent");
                attack(entity2, entity1);
            })  // Útočí druhý
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
    }

    // endregion
}
