package cz.stechy.drd.app.shop;

import com.google.inject.Inject;
import cz.stechy.drd.R;
import cz.stechy.drd.app.shop.entry.ArmorEntry;
import cz.stechy.drd.db.base.ITableWrapperFactory;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.item.Armor;
import cz.stechy.drd.model.item.Armor.ArmorType;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.service.translator.ITranslatorService;
import cz.stechy.drd.service.translator.TranslatorService.Key;
import cz.stechy.drd.service.user.IUserService;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pomocný kontroler pro obchod se zbrojí
 */
public class ShopArmorController extends AShopItemController<Armor, ArmorEntry> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopArmorController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TableColumn<ArmorEntry, ArmorType> columnArmorType;

    // endregion

    private final ObjectProperty<Height> height = new SimpleObjectProperty<>(Height.B);

    // endregion

    // region Constructors

    @Inject
    public ShopArmorController(IUserService userService, ITableWrapperFactory tableFactory, ITranslatorService translator) {
        super(tableFactory.getTableWrapper(Armor.class), translator, userService);
    }

    // endregion

    // region Public methods

    /**
     * Nastaví velikost, od které se bude odrážet cena i váha zbroje
     *
     * @param height Výška postavy
     */
    void setHeroHeight(Height height) {
        this.height.setValue(height);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        columnArmorType.setCellFactory(TextFieldTableCell.forTableColumn(translator.getConvertor(Key.ARMOR_TYPES)));
    }

    @Override
    protected ArmorEntry getEntry(Armor armor) {
        return new ArmorEntry(armor, height);
    }

    @Override
    public String getEditScreenName() {
        return R.Fxml.ITEM_ARMOR;
    }

    @Override
    public void insertItemToBundle(Bundle bundle, int index) {
        ItemArmorController.toBundle(bundle, (Armor) sortedList.get(index).getItemBase());
    }

    @Override
    public ItemBase fromBundle(Bundle bundle) {
        return ItemArmorController.fromBundle(bundle);
    }

}
