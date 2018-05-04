package cz.stechy.drd.ui.pages.shop;

import cz.stechy.drd.R.Translate;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.item.GeneralItem;
import cz.stechy.drd.ui.APage;
import cz.stechy.drd.ui.pages.MoneyPage;
import cz.stechy.drd.util.ImageUtils;
import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.testfx.api.FxRobot;

/**
 * Třída sloužící k interakci s kontrolerem {@link cz.stechy.drd.app.shop.ItemGeneralController}
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

    public static final GeneralItem ITEM_1;
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

    static {
        byte[] image;
        try {
            image = ImageUtils
                .readImage(GeneralShopPage.class.getResourceAsStream("/images/icon/add.png"));
        } catch (Exception e) {
            image = new byte[0];
        }

        ITEM_1 = new GeneralItem.Builder()
            .name("Item1")
            .description("Item created by testFX framework")
            .stackSize(1)
            .price(new Money().setCopper(20).getRaw())
            .image(image)
            .build();
    }

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
        robot.clickOn(LBL_PRICE);
        return new MoneyPage(robot);
    }

    public GeneralShopPage fillValues(GeneralItem item) {
        robot.doubleClickOn(TXT_NAME).write(item.getName());
        robot.doubleClickOn(TXT_WEIGHT).write(String.valueOf(item.getWeight()));
        showMoneyPage().setMoney(item.getPrice()).confirm();
        robot.doubleClickOn(TXT_STACKSIZE).write(String.valueOf(item.getStackSize()));
        robot.clickOn(TXT_DESCRIPTION).write(item.getDescription());
        ((ImageView) find(IMAGE_VIEW))
            .setImage(new Image(new ByteArrayInputStream(item.getImage())));
        return this;
    }

    public GeneralShopPage confirm() {
        robot.clickOn(BTN_FINISH);

        return this;
    }

    // endregion
}
