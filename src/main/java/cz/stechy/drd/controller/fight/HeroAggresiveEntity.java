package cz.stechy.drd.controller.fight;

import cz.stechy.drd.model.db.DatabaseException;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.Inventory;
import cz.stechy.drd.model.inventory.container.EquipItemContainer;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.WeaponBase;
import cz.stechy.drd.model.persistent.InventoryContent;

public class HeroAggresiveEntity extends AggresiveEntityDecorator {

    // region Variables

//    private final InventoryContent inventory;
    private int weaponStrength = 0;
    private int weaponDefence = 0;
    private int armorDefence = 0;

    // endregion

    // region Constructors

    /**
     * Vytvoří "agresivní" dekorátor pro {@link Hero}
     *  @param aggresiveEntity {@link Hero} Hrdina, který jde do boje
     * @param inventory {@link Inventory} Inventář hrdiny s jeho vybavením
     */
    protected HeroAggresiveEntity(Hero aggresiveEntity, InventoryContent inventory) {
        super(aggresiveEntity);

        try {
            initWeaponAddition(inventory);
            initArmorAddition(inventory);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        System.out.printf("Weapon strength/defence: %d/%d; armor defence: %d%n", weaponStrength,
            weaponDefence, armorDefence);
    }

    // endregion

    // region Private methods

    private void initWeaponAddition(InventoryContent inventory) throws DatabaseException {
        final String itemId = inventory.select(EquipItemContainer.SLOT_SWORD).getItemId();
        ItemRegistry.getINSTANCE().getItemById(itemId).ifPresent(itemBase -> {
            assert itemBase instanceof WeaponBase;
            WeaponBase weapon = (WeaponBase) itemBase;
            weaponStrength = weapon.getStrength();
            if (weapon instanceof MeleWeapon) {
                MeleWeapon meleWeapon = (MeleWeapon) weapon;
                weaponDefence = meleWeapon.getDefence();
            }
        });
    }

    private void initArmorAddition(InventoryContent inventory) throws DatabaseException {
        final String itemId = inventory.select(EquipItemContainer.SLOT_BODY).getItemId();
        ItemRegistry.getINSTANCE().getItemById(itemId).ifPresent(itemBase -> {
            assert itemBase instanceof Armor;
            Armor armor = (Armor) itemBase;
            armorDefence = armor.getDefenceNumber();
        });
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
