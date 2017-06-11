package cz.stechy.drd.controller;

import cz.stechy.drd.Money;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;

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
    private Spinner<Integer> spinnerGold;
    @FXML
    private Spinner<Integer> spinnerSilver;
    @FXML
    private Spinner<Integer> spinnerCopper;

    // endregion

    // endregion

    @Override
    protected void onCreate(Bundle bundle) {
        Money money = bundle.get(MONEY);

        spinnerGold.getValueFactory().valueProperty().bindBidirectional(money.gold);
        spinnerSilver.getValueFactory().valueProperty().bindBidirectional(money.silver);
        spinnerCopper.getValueFactory().valueProperty().bindBidirectional(money.copper);

        spinnerGold.valueProperty()
            .addListener((observable, oldValue, newValue) -> money.setGold(newValue));
        spinnerSilver.valueProperty()
            .addListener((observable, oldValue, newValue) -> money.setSilver(newValue));
        spinnerCopper.valueProperty()
            .addListener((observable, oldValue, newValue) -> money.setCopper(newValue));
    }

    @Override
    protected void onResume() {
        setScreenSize(250, 150);
    }

    // region Button handles

    public void handleFinish(ActionEvent actionEvent) {
        finish();
    }

    // endregion
}
