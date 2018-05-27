package cz.stechy.drd;

import cz.stechy.drd.di.DiContainer;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.UTF8ResourceBundleControl;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CollectionsTest extends Application {

    private static final String LANG_FILE_CONVENTION = "lang.translate";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/collections/collections.fxml"));
        ResourceBundle resources = ResourceBundle.getBundle(
            LANG_FILE_CONVENTION, Locale.getDefault(), new UTF8ResourceBundleControl());
        loader.setResources(resources);
        DiContainer di = new DiContainer();
        di.addService(Translator.class, new Translator(resources));
        loader.setControllerFactory(new ControllerFactory(di));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

}
