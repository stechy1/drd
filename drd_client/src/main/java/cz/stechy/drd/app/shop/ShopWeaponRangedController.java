package cz.stechy.drd.app.shop;

import cz.stechy.drd.R;
import cz.stechy.drd.app.shop.entry.RangedWeaponEntry;
import cz.stechy.drd.dao.RangedWeaponDao;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.RangedWeapon;
import cz.stechy.drd.model.item.RangedWeapon.RangedWeaponType;
import cz.stechy.drd.service.UserService;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
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

    public ShopWeaponRangedController(UserService userService, RangedWeaponDao rangedWeaponDao, Translator translator) {
        super(rangedWeaponDao, translator, userService);
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
