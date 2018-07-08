package cz.stechy.drd.widget;

import cz.stechy.drd.ThreadPool;
import java.util.concurrent.CompletableFuture;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

public class ChatTabContent {

    // region Variables

    // region FXML

    @FXML
    Label lblFrom;
    @FXML
    TextArea areaMessage;
    @FXML
    private ImageView imgLoading;

    // endregion

    // endregion

    // region Getters & Setters

    public void setContactName(String name) {
        lblFrom.setText(name);
    }

    public void setMessage(String message) {
        areaMessage.setText(message);
    }

    /**
     * Pokusí se změnit velikost oblasti se zprávou tak, aby se vešla celá do okna
     */
    public void askForResizeTextArea() {
        // Toto je trošku čuňárna
        CompletableFuture.runAsync(() -> {
            // Nejdříve chvíli počkám v jiném vlákně
            // Čekám, protože musím mít jistotu, že se vložený text již nastavil a vypočítaly
            // se veškeré vlastnosti
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }
        }, ThreadPool.COMMON_EXECUTOR)
            .thenAcceptAsync(aVoid -> {
                // Nyní můžu najít jiř přiřazený text
                final Node text = areaMessage.lookup(".text");
                if (text == null) {
                    return;
                }
                // A nabindovat správnou výšku textArea
                areaMessage.prefHeightProperty().bind(Bindings.createDoubleBinding(
                    () -> text.getBoundsInLocal().getHeight(), text.boundsInLocalProperty()).add(20));
                imgLoading.setVisible(false);
                areaMessage.setDisable(false);
            }, ThreadPool.JAVAFX_EXECUTOR);
    }

    // endregion

}
