package cz.stechy.drd.app.chat;

import cz.stechy.drd.widget.ChatTabContent;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 * Třída reprezentující jedenu konverzaci
 */
class ChatTab extends Tab {

    // region Constants

    private static final URL PATH_CONTENT_INCOMING = ChatTab.class.getResource("/fxml/chat/chat_tab_content_incoming.fxml");
    private static final URL PATH_CONTENT_OUTCOMING = ChatTab.class.getResource("/fxml/chat/chat_tab_content_outcoming.fxml");

    // endregion

    // region Variables

    private final VBox container = new VBox();

    // endregion

    // region Constructors

    ChatTab(String text) {
        super(text);

        final ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        setContent(scrollPane);
    }

    // endregion

    // region Private methods

    private URL getPath(boolean fromMe) {
        return fromMe ? PATH_CONTENT_OUTCOMING : PATH_CONTENT_INCOMING;
    }

    // endregion

    // region Public methods

    /**
     * Přidá text do konverzace
     *
     * @param text Obsah zprávy
     * @param fromMe True, pokud je zpráva odeslaná, False, pokud je zpráva přijatá
     */
    void appendText(String text, boolean fromMe, String from) {
        final FXMLLoader loader = new FXMLLoader(getPath(fromMe));
        ChatTabContent controller = null;
        try {
            final Parent parent = loader.load();
            controller = loader.getController();
            controller.setContactName(from);
            controller.setMessage(text);
            container.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (controller != null) {
                controller.askForResizeTextArea();
            }
        }
    }

    // endregion
}
