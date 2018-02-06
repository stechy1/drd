package cz.stechy.drd.model.service;

import cz.stechy.drd.di.Singleton;
import cz.stechy.drd.model.db.AdvancedDatabaseService;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemType;
import cz.stechy.drd.model.persistent.ArmorService;
import cz.stechy.drd.model.persistent.BackpackService;
import cz.stechy.drd.model.persistent.GeneralItemService;
import cz.stechy.drd.model.persistent.MeleWeaponService;
import cz.stechy.drd.model.persistent.RangedWeaponService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Třída pro stahování předmětů po kolekcích
 */
@Singleton
public class ItemResolver {

    // region Variables

    private final ArmorService armorService;
    private final BackpackService backpackService;
    private final GeneralItemService generalItemService;
    private final MeleWeaponService meleWeaponService;
    private final RangedWeaponService rangedWeaponService;

    // endregion

    // region Constructors

    public ItemResolver(ArmorService armorService, BackpackService backpackService,
        GeneralItemService generalItemService, MeleWeaponService meleWeaponService,
        RangedWeaponService rangedWeaponService) {
        this.armorService = armorService;
        this.backpackService = backpackService;
        this.generalItemService = generalItemService;
        this.meleWeaponService = meleWeaponService;
        this.rangedWeaponService = rangedWeaponService;
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
                return armorService;
            case BACKPACK:
                return backpackService;
            case GENERAL:
                return generalItemService;
            case WEAPON_MELE:
                return meleWeaponService;
            case WEAPON_RANGED:
                return rangedWeaponService;
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
        final ItemRegistry itemRegistry = ItemRegistry.getINSTANCE();
        final int[] merged = {0};

        return CompletableFuture.allOf(items
            .stream()
            .map(item -> itemRegistry.getItemById(item.getItemBase().getId()))
            .filter(optional -> !optional.isPresent())
            .map(Optional::get)
            .map(item -> {
                final AdvancedDatabaseService service = getService(item.getItemType());
                return service.insertAsync(item)
                    .thenAccept(o -> merged[0]++);
            }).toArray(CompletableFuture[]::new))
            .thenApply(aVoid -> merged[0]);
    }

    // endregion

    /**
     * Pomocné rozhraní pro všechny třídy, které obsahují {@link ItemBase}
     */
    public interface WithItemBase {

        ItemBase getItemBase();
    }

}
