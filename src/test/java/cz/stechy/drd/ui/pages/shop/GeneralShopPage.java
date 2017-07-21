package cz.stechy.drd.ui.pages.shop;

import cz.stechy.drd.Money;
import cz.stechy.drd.R.Translate;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.ui.APage;
import cz.stechy.drd.ui.pages.MoneyPage;
import org.testfx.api.FxRobot;

/**
 * Třída sloužící k interakci s kontrolerem {@link cz.stechy.drd.controller.shop.ItemGeneralController}
 */
public class GeneralShopPage extends APage {

    // region Constants

    private static final String TXT_NAME = "#txtName";
    private static final String TXT_WEIGHT = "#txtWeight";
    private static final String LBL_PRICE = "#lblPrice";
    private static final String TXT_STACKSIZE = "#txtStackSize";
    private static final String IMAGE_VIEW = "#imageView";
    private static final String TXT_DESCRIPTION = "#txtDescription";
    private static final String BTN_FINISH = "#btnFinish";

    public static final GeneralItem ITEM_1 = new GeneralItem.Builder()
        .name("Item1")
        .description("Item created by testFX framework")
        .stackSize(1)
        .price(new Money().setCopper(20).getRaw())
        .build();
    public static final GeneralItem ITEM_2 = new GeneralItem.Builder()
        .name("Item2")
        .description("Item created by testFX framework")
        .stackSize(10)
        .price(new Money().setSilver(20).getRaw())
        .build();
    public static final GeneralItem ITEM_3 = new GeneralItem.Builder()
        .name("Item3")
        .description("Item created by testFX framework")
        .stackSize(5)
        .price(new Money().setGold(20).getRaw())
        .build();

    // endregion

    // region Constructors

    public GeneralShopPage(FxRobot parentRobot) {
        super(parentRobot);
    }

    // endregion

    // region Private methods

    @Override
    protected String getTag() {
        return Translate.ITEM_TYPE_GENERAL;
    }

    // endregion

    // region Public methods

    public MoneyPage showMoneyPage() {
        clickOn(LBL_PRICE);
        return new MoneyPage(robot);
    }

    public GeneralShopPage fillValues(GeneralItem item) {
        robot.doubleClickOn(TXT_NAME).write(item.getName());
        robot.doubleClickOn(TXT_WEIGHT).write(String.valueOf(item.getWeight()));
        showMoneyPage().setMoney(item.getPrice()).confirm();
        robot.doubleClickOn(TXT_STACKSIZE).write(String.valueOf(item.getStackSize()));
        robot.clickOn(TXT_DESCRIPTION).write(item.getDescription());

        return this;
    }

    public GeneralShopPage confirm() {
        robot.clickOn(BTN_FINISH);

        return this;
    }

    // endregion
}
