package cz.stechy.drd.widget;

import cz.stechy.drd.Money;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Kontrolka vizualizující peníze
 */
public class MoneyLabel extends VBox {

    // region Variables

    private final Label label = new Label("");
    private final Text goldText = new Text("0");
    private final Text silverText = new Text("0");
    private final Text copperText = new Text("0");
    private final TextFlow container = new TextFlow(goldText, silverText, copperText);

    // endregion

    /**
     * Vytvoří novou kontrolku pro vizualizaci peněz
     */
    public MoneyLabel() {
        label.setFont(Font.font(10));
        getChildren().addAll(label, container);
    }

    /**
     * Nastaví model peněz, který se bude vizualizovat
     *
     * @param money {@link Money}
     */
    public void forMoney(Money money) {
        goldText.textProperty().bind(Bindings.concat(money.silver.asString(), "zl "));
        silverText.textProperty().bind(Bindings.concat(money.silver.asString(), "st "));
        copperText.textProperty().bind(Bindings.concat(money.copper.asString(), "md"));
    }

    public StringProperty labelProperty() {
        return label.textProperty();
    }

    public String getLabel() {
        return label.getText();
    }

    public void setLabel(String text) {
        this.label.setText(text);
    }
}
