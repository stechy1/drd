package cz.stechy.drd.service;

import cz.stechy.drd.dao.ArmorDao;
import cz.stechy.drd.dao.BackpackDao;
import cz.stechy.drd.dao.GeneralItemDao;
import cz.stechy.drd.dao.MeleWeaponDao;
import cz.stechy.drd.dao.RangedWeaponDao;
import cz.stechy.drd.db.AdvancedDatabaseService;
import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Třída pro stahování předmětů po kolekcích
 */
@Singleton
public class ItemResolver {

    // region Variables

    private final ArmorDao armorDao;
    private final BackpackDao backpackDao;
    private final GeneralItemDao generalItemDao;
    private final MeleWeaponDao meleWeaponDao;
    private final RangedWeaponDao rangedWeaponDao;
    private final ItemRegistry itemRegistry;

    // endregion

    // region Constructors

    public ItemResolver(ArmorDao armorDao, BackpackDao backpackDao,
        GeneralItemDao generalItemDao, MeleWeaponDao meleWeaponDao,
        RangedWeaponDao rangedWeaponDao, ItemRegistry itemRegistry) {
        this.armorDao = armorDao;
        this.backpackDao = backpackDao;
        this.generalItemDao = generalItemDao;
        this.meleWeaponDao = meleWeaponDao;
        this.rangedWeaponDao = rangedWeaponDao;
        this.itemRegistry = itemRegistry;
    }

    // endregion

    // region Private methods

    /**
     * Podle typu předmětu vrátí odpovídající službu
     *
     * @param itemType {@link ItemType}
     * @return {@link AdvancedDatabaseService}
     */
    private AdvancedDatabaseService getService(ItemType itemType) {
        switch (itemType) {
            case ARMOR:
                return armorDao;
            case BACKPACK:
                return backpackDao;
            case GENERAL:
                return generalItemDao;
            case WEAPON_MELE:
                return meleWeaponDao;
            case WEAPON_RANGED:
                return rangedWeaponDao;
            default:
                throw new IllegalStateException();
        }
    }

    // endregion

    // region Public methods

    /**
     * Uloží kolekci online předmětů do offline databáze
     *
     * @param items Kolekce ID online předmětů
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<Integer> merge(List<? extends WithItemBase> items) {
        final int[] merged = {0};
        final Object lock = new Object();

        return CompletableFuture.supplyAsync(() -> {
            items.stream()
                .filter(item -> !itemRegistry.getItemById(item.getItemBase().getId()).isPresent())
                .map(WithItemBase::getItemBase)
                .forEach(item -> {
                    final AdvancedDatabaseService service = getService(item.getItemType());
                    service.insertAsync(item).thenAccept(o -> {
                        synchronized (lock) {
                            merged[0]++;
                        }
                    }).join();
                });
            return null;
        }).thenApply(aVoid -> merged[0]);
    }

    // endregion

    /**
     * Pomocné rozhraní pro všechny třídy, které obsahují {@link ItemBase}
     */
    public interface WithItemBase {

        ItemBase getItemBase();
    }

}
