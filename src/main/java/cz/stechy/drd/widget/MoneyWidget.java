package cz.stechy.drd.widget;

import cz.stechy.drd.model.Money;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Widget reprezenzující peníze s grafickou vizualizací
 */
public class MoneyWidget extends VBox {

    // region Constants

    private static final String COIN_FOLDER = "/images/coins/";
    private static final String COIN_GOLD = COIN_FOLDER + "gold_coin.png";
    private static final String COIN_SILVER = COIN_FOLDER + "silver_coin.png";
    private static final String COIN_COPPER = COIN_FOLDER + "copper_coin.png";

    private static final int COIN_SIZE = 20;
    private static final boolean PRESERVE_RATIO = false;
    private static final boolean SMOOTH = true;

    private static final int FONT_SIZE = 20;
    private static final Font TEXT_FONT = Font.font("Roboto", FONT_SIZE);

    private static final Image GOLD_IMAGE;
    private static final Image SILVER_IMAGE;
    private static final Image COPPER_IMAGE;

    // endregion

    static {
        GOLD_IMAGE = new Image(MoneyWidget.class.getResourceAsStream(COIN_GOLD), COIN_SIZE, COIN_SIZE, PRESERVE_RATIO, SMOOTH);
        SILVER_IMAGE = new Image(MoneyWidget.class.getResourceAsStream(COIN_SILVER), COIN_SIZE, COIN_SIZE, PRESERVE_RATIO, SMOOTH);
        COPPER_IMAGE = new Image(MoneyWidget.class.getResourceAsStream(COIN_COPPER), COIN_SIZE, COIN_SIZE, PRESERVE_RATIO, SMOOTH);
    }

    // region Variables

    private final Text goldText = new Text();
    private final ImageView goldView = new ImageView(GOLD_IMAGE);
    private final Text silverText = new Text();
    private final ImageView silverView = new ImageView(SILVER_IMAGE);
    private final Text copperText = new Text();
    private final ImageView copperView = new ImageView(COPPER_IMAGE);
    private final TextFlow container = new TextFlow(goldText, goldView, silverText, silverView, copperText, copperView);

    // endregion

    // region Constructors

    /**
     * Vytvoří nový widget reprezentující peníze
     */
    public MoneyWidget() {
        reset();
    }

    /**
     * Vytvoří nový widget reprezentující peníze s definovanou hodnotou
     *
      * @param money {@link Money} Hodnota, který se má zobrazit
     */
    public MoneyWidget(final Money money) {
        bind(money);
    }

    {
        goldText.setFont(TEXT_FONT);
        silverText.setFont(TEXT_FONT);
        copperText.setFont(TEXT_FONT);

        getChildren().setAll(container);
        container.setTextAlignment(TextAlignment.CENTER);
        setAlignment(Pos.CENTER);
    }

    // endregion

    // region Private methods

    /**
     * Vyresetuje hodnoty na výchozí hodnoty
     */
    private void reset() {
        if (!goldText.textProperty().isBound()) {
            goldText.setText("0");
        }
        if (!silverText.textProperty().isBound()) {
            silverText.setText("0");
        }
        if (!copperText.textProperty().isBound()) {
            copperText.setText("0");
        }
    }

    // endregion

    // region Public methods

    /**
     * Začne pozorovat vybraný model.
     * Pokud se změní v modelu vybrané hdnoty, jsou tyto změny propagovány do widgetu.
     *
     * @param money {@link Money}
     */
    public void bind(Money money) {
        goldText.textProperty().bind(money.gold.asString());
        silverText.textProperty().bind(money.silver.asString());
        copperText.textProperty().bind(money.copper.asString());
    }

    /**
     * Přestane pozorovat dříve přiřazený model.
     */
    public void unbind() {
        goldText.textProperty().unbind();
        silverText.textProperty().unbind();
        copperText.textProperty().unbind();

        reset();
    }

    // endregion

}
