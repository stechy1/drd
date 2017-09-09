package cz.stechy.drd.widget;

import java.io.IOException;
import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

@DefaultProperty("content")
public final class Card extends Group {

    // region Variables

    // region FXML

    @FXML
    private Label lblCaption;

    @FXML
    private StackPane container;
    // endregion

    // endregion

    // region Constructors

    public Card(@NamedArg("caption") String caption) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/card.fxml"));
            loader.setController(this);
            AnchorPane pane = loader.load();
            getChildren().setAll(pane);
            lblCaption.setText(caption);
            lblCaption.setTooltip(new Tooltip(caption));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // endregion

    // region Public methods

    public Node getContent() {
        return container.getChildren().get(0);
    }

    public void setContent(Node content) {
        container.getChildren().setAll(content);
    }

    public Label getCaption() {
        return lblCaption;
    }

    public StringProperty captionTextProperty() {
        return lblCaption.textProperty();
    }

    // endregion
}
