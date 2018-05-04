package cz.stechy.drd;

import cz.stechy.drd.app.PreloaderController;
import cz.stechy.drd.model.MyPreloaderNotification;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppPreloader extends Preloader {

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

        Scene scene = new Scene(parent, 1280, 750);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        // Handle application notification in this point (see MyApplication#init).
        if (info instanceof MyPreloaderNotification) {
            final MyPreloaderNotification notification = (MyPreloaderNotification) info;
            controller.updateProgress(notification.getProgress(), notification.getDescription());
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_LOAD:
                break;
            case BEFORE_INIT:
                break;
            case BEFORE_START:
                stage.hide();
                break;
        }
    }

}
