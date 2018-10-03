package cz.stechy.drd.app.fight;

import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.container.EquipItemContainer;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.WeaponBase;
import cz.stechy.drd.service.inventory.IInventoryContentService;
import cz.stechy.drd.service.item.IItemRegistry;

class HeroAggresiveEntity extends AggresiveEntityDecorator {

    // region Variables

    private final IItemRegistry itemRegistry;

    private int weaponStrength = 0;
    private int weaponDefence = 0;
    private int armorDefence = 0;

    // endregion

    // region Constructors

    /**
     * Vytvoří "agresivní" dekorátor pro {@link Hero}
     *
     * @param aggresiveEntity {@link Hero} Hrdina, který jde do boje
     * @param inventory {@link Inventory} Inventář hrdiny s jeho vybavením
     */
    protected HeroAggresiveEntity(Hero aggresiveEntity, IInventoryContentService inventory, IItemRegistry itemRegistry) {
        super(aggresiveEntity);

        this.itemRegistry = itemRegistry;
        initWeaponAddition(inventory);
        initArmorAddition(inventory);
    }

    // endregion

    // region Private methods

    private void initWeaponAddition(IInventoryContentService inventory) {
        inventory.select(EquipItemContainer.SLOT_SWORD)
            .thenAcceptAsync(inventoryRecord -> {
                final String itemId = inventoryRecord.getItemId();
                itemRegistry.getItemById(itemId).ifPresent(itemBase -> {
                    assert itemBase instanceof WeaponBase;
                    WeaponBase weapon = (WeaponBase) itemBase;
                    weaponStrength = weapon.getStrength();
                    if (weapon instanceof MeleWeapon) {
                        MeleWeapon meleWeapon = (MeleWeapon) weapon;
                        weaponDefence = meleWeapon.getDefence();
                    }
                });
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    private void initArmorAddition(IInventoryContentService inventory) {
        inventory.select(EquipItemContainer.SLOT_BODY)
            .thenAcceptAsync(inventoryRecord -> {
                final String itemId = inventoryRecord.getItemId();
                itemRegistry.getItemById(itemId).ifPresent(itemBase -> {
                    assert itemBase instanceof Armor;
                    Armor armor = (Armor) itemBase;
                    armorDefence = armor.getDefenceNumber();
                });
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    // endregion

    @Override
    public int getAttackNumber() {
        final int baseAttackNumber = super.getAttackNumber();
        return baseAttackNumber + weaponStrength;
    }

    @Override
    public int getDefenceNumber() {
        final int baseDefenceNumber = super.getDefenceNumber();
        return baseDefenceNumber + weaponDefence + armorDefence;
    }
}
