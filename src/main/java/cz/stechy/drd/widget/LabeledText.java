package cz.stechy.drd.widget;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LabeledText extends VBox {

    private final Label title = new Label();
    private final Label text = new Label();

    public LabeledText() {
        title.setFont(new Font(10));
        getChildren().addAll(title, text);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public String getTitle() {
        return title.getText();
    }

    public StringProperty TitleProperty() {
        return title.textProperty();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return text.getText();
    }

    public StringProperty textProperty() {
        return text.textProperty();
    }
}
