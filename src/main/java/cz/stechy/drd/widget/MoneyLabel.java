package cz.stechy.drd.widget;

import cz.stechy.drd.Money;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Kontrolka vizualizující peníze
 */
public class MoneyLabel extends VBox {

    // region Variables

    private final Label label = new Label();
    private final Label lblMoney = new Label();

    // endregion

    /**
     * Vytvoří novou kontrolku pro vizualizaci peněz
     */
    public MoneyLabel() {
        label.setFont(Font.font(10));
        getChildren().addAll(label, lblMoney);
    }

    /**
     * Nastaví model peněz, který se bude vizualizovat
     *
     * @param money {@link Money}
     */
    public void forMoney(Money money) {
        this.lblMoney.textProperty().bind(Bindings.concat(
            money.gold.asString(), "zl ",
            money.silver.asString(), "st ",
            money.copper.asString(), "md"));
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
