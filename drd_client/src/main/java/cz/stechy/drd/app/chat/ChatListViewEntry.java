package cz.stechy.drd.app.chat;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXListCell;
import cz.stechy.drd.model.chat.ChatContact;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

class ChatListViewEntry extends JFXListCell<ChatContact> {

    private final Circle circle = new Circle();
    private final Label lblName = new Label();
    private final Region spacer = new Region();
    private final Label lblUnreadedMessages = new Label();
    private final JFXBadge badgeUnreadedMessages = new JFXBadge(lblUnreadedMessages);
    private final HBox container = new HBox(circle, lblName, spacer, badgeUnreadedMessages);

    {
        circle.setRadius(15);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        container.setAlignment(Pos.CENTER_LEFT);
        badgeUnreadedMessages.setStyle("-fx-border-radius: 50; -fx-border-color: orange;");
        badgeUnreadedMessages.setPadding(new Insets(4));
        container.setSpacing(8);
    }

    private void bind(ChatContact item) {
        circle.fillProperty().bind(item.contactColorProperty());
        lblName.textProperty().bind(item.nameProperty());
        lblUnreadedMessages.textProperty().bind(item.unreadedMessagesProperty().asString());
        badgeUnreadedMessages.visibleProperty().bind(item.unreadedMessagesProperty().greaterThan(0));
    }

    private void unbind() {
        circle.fillProperty().unbind();
        lblName.textProperty().unbind();
        badgeUnreadedMessages.textProperty().unbind();
    }

    @Override
    protected void updateItem(ChatContact item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (empty) {
            unbind();
            setGraphic(null);
        } else {
            bind(item);

            setGraphic(container);
        }
    }
}
