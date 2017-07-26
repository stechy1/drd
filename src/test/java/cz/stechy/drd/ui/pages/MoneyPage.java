package cz.stechy.drd.ui.pages;

import cz.stechy.drd.model.Money;
import cz.stechy.drd.R;
import cz.stechy.drd.ui.APage;
import org.testfx.api.FxRobot;

public class MoneyPage extends APage {

    // region Constsnts

    private static final String TXT_COPPER = "#txtCopper";
    private static final String TXT_SILVER = "#txtSilver";
    private static final String TXT_GOLD = "#txtGold";

    private static final String BTN_FINISH = "#btnFinish";

    // endregion

    // region Constructors

    public MoneyPage(FxRobot parentRobot) {
        super(parentRobot);
    }

    // endregion

    // region Private methods

    @Override
    protected String getTag() {
        return R.Translate.MONEY_TITLE;
    }

    // endregion

    // region Public methods

    public MoneyPage setMoney(Money money) {
        setCopper(money.getCopper());
        setSilver(money.getSilver());
        setGold(money.getGold());

        return this;
    }

    public MoneyPage setCopper(int copper) {
        robot.doubleClickOn(TXT_COPPER).write(String.valueOf(copper));

        return this;
    }

    public MoneyPage setSilver(int silver) {
        robot.doubleClickOn(TXT_SILVER).write(String.valueOf(silver));

        return this;
    }

    public MoneyPage setGold(int gold) {
        robot.doubleClickOn(TXT_GOLD).write(String.valueOf(gold));

        return this;
    }

    public MoneyPage confirm() {
        robot.clickOn(BTN_FINISH);

        return this;
    }

    // endregion
}
