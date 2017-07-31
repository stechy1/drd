package cz.stechy.drd.widget;

import cz.stechy.drd.model.Money;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Kontrolka vizualizující peníze
 */
public class LabeledMoney extends VBox {

    // region Variables

    private final Label label = new Label();
    private final MoneyWidget moneyWidget = new MoneyWidget();

    // endregion

    /**
     * Vytvoří novou kontrolku pro vizualizaci peněz
     */
    public LabeledMoney() {
        label.setFont(Font.font(10));
        getChildren().addAll(label, moneyWidget);
    }

    /**
     * Nastaví model peněz, který se bude vizualizovat
     *
     * @param money {@link Money}
     */
    public void forMoney(Money money) {
        this.moneyWidget.bind(money);
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
