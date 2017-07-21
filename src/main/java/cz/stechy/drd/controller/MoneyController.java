package cz.stechy.drd.controller;

import cz.stechy.drd.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Kontroler pro popup okno obsahující nastavení peněz
 */
public class MoneyController extends BaseController {

    // region Constants

    public static final String MONEY = "money";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TextField txtGold;
    @FXML
    private TextField txtSilver;
    @FXML
    private TextField txtCopper;

    // endregion

    private final Money money = new Money();

    private final MaxActValue goldValue = new MaxActValue(0, Money.MAX_GOLD, money.getGold());
    private final MaxActValue silverValue = new MaxActValue(0, Money.MAX_SILVER, money.getSilver());
    private final MaxActValue copperValue = new MaxActValue(0, Money.MAX_COPPER, money.getCopper());

    {
        goldValue.actValueProperty().bindBidirectional(money.gold);
        silverValue.actValueProperty().bindBidirectional(money.silver);
        copperValue.actValueProperty().bindBidirectional(money.copper);
    }

    // endregion

    @Override
    protected void onCreate(Bundle bundle) {
        money.setRaw(bundle.getInt(MONEY));

        FormUtils.initTextFormater(txtGold, goldValue);
        FormUtils.initTextFormater(txtSilver, silverValue);
        FormUtils.initTextFormater(txtCopper, copperValue);
    }

    @Override
    protected void onResume() {
        setTitle(R.Translate.MONEY_TITLE);
        setScreenSize(250, 150);
    }

    @Override
    protected void onClose() {
        goldValue.actValueProperty().unbindBidirectional(money.gold);
        silverValue.actValueProperty().unbindBidirectional(money.silver);
        copperValue.actValueProperty().unbindBidirectional(money.copper);

        FormUtils.disposeTextFormater(txtGold, goldValue);
        FormUtils.disposeTextFormater(txtSilver, silverValue);
        FormUtils.disposeTextFormater(txtCopper, copperValue);
    }

    // region Button handles

    @FXML
    private void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        finish(new Bundle().putInt(MONEY, money.getRaw()));
    }

    // endregion
}
