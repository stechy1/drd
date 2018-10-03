package cz.stechy.drd.app.shop;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.app.shop.entry.MeleWeaponEntry;
import cz.stechy.drd.db.base.ITableWrapperFactory;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.MeleWeapon;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponClass;
import cz.stechy.drd.model.item.MeleWeapon.MeleWeaponType;
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
 * Pomocný kontroler pro obchod se zbraněmi na blízko
 */
public class ShopWeaponMeleController extends AShopItemController<MeleWeapon, MeleWeaponEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopWeaponMeleController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableColumn<MeleWeaponEntry, Integer> columnStrength;
    @FXML
    private TableColumn<MeleWeaponEntry, Integer> columnRampancy;
    @FXML
    private TableColumn<MeleWeaponEntry, Integer> columnDefence;
    @FXML
    private TableColumn<MeleWeaponEntry, MeleWeaponClass> columnClass;
    @FXML
    private TableColumn<MeleWeaponEntry, MeleWeaponType> columnType;

    // endregion

    // endregion

    // region Constructors

    @Inject
    public ShopWeaponMeleController(IUserService userService, ITableWrapperFactory tableFactory, ITranslatorService translator) {
        super(tableFactory.getTableWrapper(MeleWeapon.class), translator, userService);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        columnClass.setCellFactory(TextFieldTableCell.forTableColumn(translator.getConvertor(Key.WEAPON_MELE_CLASSES)));
        columnType.setCellFactory(TextFieldTableCell.forTableColumn(translator.getConvertor(Key.WEAPON_MELE_TYPES)));
    }

    @Override
    protected MeleWeaponEntry getEntry(MeleWeapon meleWeapon) {
        return new MeleWeaponEntry(meleWeapon);
    }

    @Override
    public String getEditScreenName() {
        return R.Fxml.ITEM_MELE_WEAPON;
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemWeaponMeleController.toBundle(bundle, (MeleWeapon) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemWeaponMeleController.fromBundle(bundle);
    }

}
