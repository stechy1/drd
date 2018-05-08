package cz.stechy.drd;

import java.util.Random;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Zajištění, že prvky ve scrollPanu budou vždy vydět
 */
public class Temp extends Application {

    private static final Random random = new Random();

    private static void ensureVisible(ScrollPane pane, Node node) {
        double width = pane.getContent().getBoundsInLocal().getWidth();
        double height = pane.getContent().getBoundsInLocal().getHeight();

        double x = node.getBoundsInParent().getMaxX();
        double y = node.getBoundsInParent().getMaxY();

        // scrolling values range from 0 to 1
        pane.setVvalue(y/height);
        pane.setHvalue(x/width);

        // just for usability
        node.requestFocus();
    }

    @Override
    public void start(Stage primaryStage) {

        final ScrollPane root = new ScrollPane();
        final Pane content = new Pane();
        root.setContent(content);

        // put 10 buttons at random places with same handler
        final EventHandler<ActionEvent> handler = event -> {
            int index = random.nextInt(10);
            System.out.println("Moving to button " + index);
            ensureVisible(root, content.getChildren().get(index));
        };

        for (int i = 0; i < 10; i++) {
            Button btn = new Button("next " + i);
            btn.setOnAction(handler);
            content.getChildren().add(btn);
            btn.relocate(2000 * random.nextDouble(), 2000 * random.nextDouble());
        }

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

        // run once to don't search for a first button manually
        handler.handle(null);
    }

    public static void main(String[] args) { launch(); }
}