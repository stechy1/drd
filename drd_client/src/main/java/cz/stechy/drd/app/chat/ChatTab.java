package cz.stechy.drd.app.chat;

import cz.stechy.drd.R;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.model.chat.ChatContact;
import cz.stechy.drd.model.chat.ChatMessageEntry;
import cz.stechy.drd.widget.ChatTabContent;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Třída reprezentující jedenu konverzaci
 */
class ChatTab extends Tab {

    // region Constants

    private static final URL PATH_CONTENT_INCOMING = ChatTab.class
        .getResource("/fxml/chat/chat_tab_content_incoming.fxml");
    private static final URL PATH_CONTENT_OUTCOMING = ChatTab.class
        .getResource("/fxml/chat/chat_tab_content_outcoming.fxml");

    // endregion

    // region Variables

    private final ScrollPane container = new ScrollPane();
    private final VBox messagesContiainer = new VBox();
    private final ChatContact chatContact;

    // endregion

    // region Constructors

    ChatTab(ChatContact chatContact) {
        super(chatContact.getName());
        this.chatContact = chatContact;
        this.chatContact.getMessages().addListener(this.messagesListener);
        loadMessagesAsync();

        final ImageView loadingImage = new ImageView();
        loadingImage.setImage(new Image(getClass().getResourceAsStream(R.Images.Icon.CHAT)));
        container.setContent(loadingImage);
        container.setHbarPolicy(ScrollBarPolicy.NEVER);
        container.setFitToWidth(true);
        setContent(container);
        messagesContiainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            container.setVvalue(newValue.doubleValue());
        });
    }

    // endregion

    // region Private methods

    /**
     * Vrátí URL adresu FXML souboru podle kontaktu
     *
     * @param from {@link ChatContact} Kontakt, od kterého zpráva je
     * @return URL adresu FXML souboru pro načtení správného view
     */
    private URL getPath(ChatContact from) {
        return from == this.chatContact ? PATH_CONTENT_INCOMING : PATH_CONTENT_OUTCOMING;
    }

    /**
     * Přidá grafickou reprezentaci zprávy do kontejneru
     *
     * @param chatMessage {@link ChatMessageEntry} Záznam o zprávě
     */
    private ChatTabContent addMessage(ChatMessageEntry chatMessage) {
        final ChatContact contact = chatMessage.getChatContact();
        final String message = chatMessage.getMessage();
        final FXMLLoader loader = new FXMLLoader(getPath(contact));
        ChatTabContent controller = null;
        try {
            final Parent parent = loader.load();
            controller = loader.getController();
            controller.setColor(contact.getColor());
            controller.setContactName(contact.getName());
            controller.setMessage(message);
            parent.setUserData(controller);
            messagesContiainer.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    /**
     * Asynchroně zobrazí všechny dosud přijaté zprávy
     */
    private void loadMessagesAsync() {
        CompletableFuture.runAsync(() -> {
            // Chvilku počkám
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            // Projedu všechny záznamy o zprávách a přidám je do kontejneru na zprávy
            this.chatContact.getMessages().forEach(this::addMessage);
        }, ThreadPool.COMMON_EXECUTOR)
            .thenAcceptAsync(ignored -> {
                    // Přidám kontejner se zprávami do hlavního kontejneru
                    container.setContent(messagesContiainer);
                    // Nakonec proiteruji všechny zprávy a zažádám o automatické nastavení velikosti
                    messagesContiainer.getChildren()
                        .parallelStream()
                        .map(node -> (ChatTabContent) node.getUserData())
                        .filter(Objects::nonNull)
                        .forEach(ChatTabContent::askForResizeTextArea);
                },
                ThreadPool.JAVAFX_EXECUTOR);
    }

    // endregion

    // region Public methods

    // endregion

    // Jednoduchý mapper, který vizualizuje všechny zprávy
    private final ListChangeListener<? super ChatMessageEntry> messagesListener = c -> {
        while (c.next()) {
            if (c.wasAdded()) {
                for (ChatMessageEntry chatMessageEntry : c.getAddedSubList()) {
                    final ChatTabContent chatTabContent = addMessage(chatMessageEntry);
                    if (chatTabContent != null) {
                        chatTabContent.askForResizeTextArea();
                    }
                }
            }
        }
    };
}
