package cz.stechy.drd.app.shop;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.app.shop.entry.BackpackEntry;
import cz.stechy.drd.db.base.ITableWrapperFactory;
import cz.stechy.drd.model.item.Backpack;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.translator.ITranslatorService;
import cz.stechy.drd.service.user.IUserService;
import cz.stechy.drd.util.CellUtils;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod s batohy
 */
public class ShopBackpackController extends AShopItemController<Backpack, BackpackEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopGeneralController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableColumn<BackpackEntry, Integer> columnMaxLoad;

    // endregion

    // endregion

    // region Constrollers

    @Inject
    public ShopBackpackController(IUserService userService, ITableWrapperFactory tableFactory, ITranslatorService translator) {
        super(tableFactory.getTableWrapper(Backpack.class), translator, userService);
    }
    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        columnMaxLoad.setCellFactory(param -> CellUtils.forWeight());
    }

    @Override
    protected BackpackEntry getEntry(Backpack backpack) {
        return new BackpackEntry(backpack);
    }

    @Override
    public String getEditScreenName() {
        return R.Fxml.ITEM_BACKPACK;
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemBackpackController.toBundle(bundle, (Backpack) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemBackpackController.fromBundle(bundle);
    }

}
