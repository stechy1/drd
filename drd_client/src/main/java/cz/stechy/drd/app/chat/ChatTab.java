package cz.stechy.drd.app.chat;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

/**
 * Třída reprezentující jedenu konverzaci
 */
class ChatTab extends Tab {

    // Oblast, kam se zaznamenává konverzace
    private final TextArea messagesArea = new TextArea();

    ChatTab(String text) {
        super(text);

        setContent(messagesArea);
    }

    void appendText(String text) {
        messagesArea.appendText(text);
    }
}
