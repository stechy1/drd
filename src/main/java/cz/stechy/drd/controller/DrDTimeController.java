package cz.stechy.drd.controller;

import com.jfoenix.controls.JFXButton;
import cz.stechy.drd.R;
import cz.stechy.drd.model.DrDTime;
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

public class DrDTimeController extends BaseController implements Initializable {

    // region Constants

    public static final String TIME = "time";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TextField txtYear;
    @FXML
    private TextField txtMonth;
    @FXML
    private TextField txtDay;
    @FXML
    private TextField txtInning;
    @FXML
    private TextField txtCycle;
    @FXML
    private JFXButton btnFinish;

    // endregion

    private final MaxActValue yearValue = new MaxActValue(0, DrDTime.MAX_YEAR, 0);
    private final MaxActValue monthValue = new MaxActValue(0, DrDTime.MAX_MONTH, 0);
    private final MaxActValue dayValue = new MaxActValue(0, DrDTime.MAX_DAY, 0);
    private final MaxActValue inningValue = new MaxActValue(0, DrDTime.MAX_INNING, 0);
    private final MaxActValue cycleValue = new MaxActValue(0, DrDTime.MAX_CYCLE, 0);

    private String title;

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title = resources.getString(R.Translate.MONEY_TITLE);

        FormUtils.initTextFormater(txtYear, yearValue);
        FormUtils.initTextFormater(txtMonth, monthValue);
        FormUtils.initTextFormater(txtDay, dayValue);
        FormUtils.initTextFormater(txtInning, inningValue);
        FormUtils.initTextFormater(txtCycle, cycleValue);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        DrDTime time = new DrDTime(bundle.getInt(TIME));
        yearValue.setActValue(time.getYear());
        monthValue.setActValue(time.getMonth());
        dayValue.setActValue(time.getDay());
        inningValue.setActValue(time.getInning());
        cycleValue.setActValue(time.getCycle());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(250, 236);
    }

    @FXML
    private void handleFinish(ActionEvent actionEvent) {
        setResult(RESULT_SUCCESS);
        final DrDTime time = new DrDTime();
        time.setYear(yearValue.getActValue().intValue());
        time.setMonth(monthValue.getActValue().intValue());
        time.setDay(dayValue.getActValue().intValue());
        time.setInning(inningValue.getActValue().intValue());
        time.setCycle(cycleValue.getActValue().intValue());
        finish(new Bundle().putInt(TIME, time.getRaw()));
    }
}
