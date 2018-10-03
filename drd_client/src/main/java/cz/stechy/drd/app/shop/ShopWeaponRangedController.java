package cz.stechy.drd.app.shop;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.app.shop.entry.RangedWeaponEntry;
import cz.stechy.drd.db.base.ITableWrapperFactory;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.item.RangedWeapon.RangedWeaponType;
import cz.stechy.drd.service.translator.ITranslatorService;
import cz.stechy.drd.service.translator.TranslatorService.Key;
import cz.stechy.drd.service.user.IUserService;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se zbraněmi na dálku
 */
public class ShopWeaponRangedController extends AShopItemController<RangedWeapon, RangedWeaponEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopWeaponRangedController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableColumn<RangedWeaponEntry, RangedWeaponType> columnType;

    // endregion

    // endregion

    // region Constructors

    @Inject
    public ShopWeaponRangedController(IUserService userService, ITableWrapperFactory tableFactory, ITranslatorService translator) {
        super(tableFactory.getTableWrapper(RangedWeapon.class), translator, userService);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        columnType.setCellFactory(TextFieldTableCell.forTableColumn(translator.getConvertor(Key.WEAPON_RANGED_TYPES)));
    }

    @Override
    protected RangedWeaponEntry getEntry(RangedWeapon rangedWeapon) {
        return new RangedWeaponEntry(rangedWeapon);
    }

    @Override
    public String getEditScreenName() {
        return R.Fxml.ITEM_RANGED_WEAPON;
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemWeaponRangedController.toBundle(bundle, (RangedWeapon) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemWeaponRangedController.fromBundle(bundle);
    }

}
