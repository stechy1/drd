package cz.stechy.drd;

import cz.stechy.drd.app.PreloaderController;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SuppressWarnings("all")
public class GuicePreloader extends Preloader {

    // region Constants

    private static final String PRELOADER_FXML = "/fxml/preloader.fxml";

    // endregion

    // region Variables

    private PreloaderController controller;
    private Stage stage;

    // endregion

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(PRELOADER_FXML));
        final Parent parent = loader.load();
        this.controller = loader.getController();

        final Scene scene = new Scene(parent, 1280, 750);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Dračí doupě - načítání...");
//        stage.setAlwaysOnTop(true);
        stage.show();
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        final double progress = pn.getProgress();
        controller.updateProgress(pn.getProgress(), "");
    }

//    @Override
//    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
//        if (stateChangeNotification.getType() == Type.BEFORE_START) {
//            stage.hide();
//        }
//    }
}
