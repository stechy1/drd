package cz.stechy.drd.app.fight;

import cz.stechy.drd.model.entity.IAggressive;

abstract class AggresiveEntityDecorator implements IAggressive {

    // region Variables

    private final IAggressive aggressiveEntity;

    // endregion

    // region Constructors

    AggresiveEntityDecorator(IAggressive aggressiveEntity) {
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
