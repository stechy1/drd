package cz.stechy.drd.app;

import cz.stechy.drd.model.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * Kontroler pro popup okno obsahující nastavení peněz
 */
public class MoneyController extends BaseController implements Initializable {

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

    private final MaxActValue goldValue = new MaxActValue(0, Money.MAX_GOLD, 0);
    private final MaxActValue silverValue = new MaxActValue(0, Money.MAX_SILVER, 0);
    private final MaxActValue copperValue = new MaxActValue(0, Money.MAX_COPPER, 0);

    private String title;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.MONEY_TITLE);

        FormUtils.initTextFormater(txtGold, goldValue);
        FormUtils.initTextFormater(txtSilver, silverValue);
        FormUtils.initTextFormater(txtCopper, copperValue);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        Money money = new Money(bundle.getInt(MONEY));
        goldValue.setActValue(money.getGold());
        silverValue.setActValue(money.getSilver());
        copperValue.setActValue(money.getCopper());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(250, 150);
    }

    // region Button handles

    @FXML
    private void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        Money money = new Money();
        money.setGold(goldValue.getActValue().intValue());
        money.setSilver(silverValue.getActValue().intValue());
        money.setCopper(copperValue.getActValue().intValue());
        finish(new Bundle().putInt(MONEY, money.getRaw()));
    }

    // endregion
}
