package cz.stechy.drd.widget;

import cz.stechy.drd.model.Money;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Widget reprezenzující peníze s grafickou vizualizací
 */
public class MoneyWidget extends TextFlow {

    // region Constants

    private static final String COIN_FOLDER = "/images/coins/";
    private static final String COIN_GOLD = COIN_FOLDER + "gold_coin.png";
    private static final String COIN_SILVER = COIN_FOLDER + "silver_coin.png";
    private static final String COIN_COPPER = COIN_FOLDER + "copper_coin.png";

    private static final int COIN_SIZE = 20;
    private static final boolean PRESERVE_RATIO = false;
    private static final boolean SMOOTH = true;

    private static final int FONT_SIZE = 12;
    private static final Font textFont = Font.font("Roboto", FONT_SIZE);

    private static final Image goldImage;
    private static final Image silverImage;
    private static final Image copperImage;

    // endregion

    static {
        goldImage = new Image(MoneyWidget.class.getResourceAsStream(COIN_GOLD), COIN_SIZE, COIN_SIZE, PRESERVE_RATIO, SMOOTH);
        silverImage = new Image(MoneyWidget.class.getResourceAsStream(COIN_SILVER), COIN_SIZE, COIN_SIZE, PRESERVE_RATIO, SMOOTH);
        copperImage = new Image(MoneyWidget.class.getResourceAsStream(COIN_COPPER), COIN_SIZE, COIN_SIZE, PRESERVE_RATIO, SMOOTH);
    }

    private final Text goldText = new Text();
    private final ImageView goldView = new ImageView(goldImage);
    private final Text silverText = new Text();
    private final ImageView silverView = new ImageView(silverImage);
    private final Text copperText = new Text();
    private final ImageView copperView = new ImageView(copperImage);

    // region Constructors

    /**
     * Vytvoří nový widget reprezentující peníze
     */
    public MoneyWidget() { }

    /**
     * Vytvoří nový widget reprezentující peníze s definovanou hodnotou
     *
      * @param money {@link Money} Hodnota, který se má zobrazit
     */
    public MoneyWidget(final Money money) {
        bind(money);
    }

    {
        goldText.setFont(textFont);
        silverText.setFont(textFont);
        copperText.setFont(textFont);

        final IntegerBinding translateBinding = Bindings.createIntegerBinding(() ->
            -((int) getHeight() / 2 - FONT_SIZE / 2), heightProperty());
        goldText.translateYProperty().bind(translateBinding);
        silverText.translateYProperty().bind(translateBinding);
        copperText.translateYProperty().bind(translateBinding);

        getChildren().setAll(goldText, goldView, silverText, silverView, copperText, copperView);
    }

    // endregion

    // region Public methods

    /**
     * Propojí widget s penězi
     *
     * @param money {@link Money}
     */
    public void bind(Money money) {
        goldText.textProperty().bind(money.gold.asString());
        silverText.textProperty().bind(money.silver.asString());
        copperText.textProperty().bind(money.copper.asString());
    }

    // endregion

}
