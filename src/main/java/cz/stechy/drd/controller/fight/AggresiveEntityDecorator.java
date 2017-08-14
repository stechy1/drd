package cz.stechy.drd.controller.fight;

import cz.stechy.drd.model.entity.IAggressive;

public abstract class AggresiveEntityDecorator implements IAggressive {

    // region Variables

    private final IAggressive aggressiveEntity;

    // endregion

    // region Constructors

    protected AggresiveEntityDecorator(IAggressive aggressiveEntity) {
        this.aggressiveEntity = aggressiveEntity;
    }

    // endregion

    @Override
    public int getAttackNumber() {
        return aggressiveEntity.getAttackNumber();
    }

    @Override
    public int getDefenceNumber() {
        return aggressiveEntity.getDefenceNumber();
    }

    @Override
    public void subtractLive(int live) {
        aggressiveEntity.subtractLive(live);
    }

    @Override
    public boolean isAlive() {
        return aggressiveEntity.isAlive();
    }

    @Override
    public String toString() {
        return aggressiveEntity.toString();
    }
}
