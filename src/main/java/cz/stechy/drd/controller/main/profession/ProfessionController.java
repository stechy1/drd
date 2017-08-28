package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.Context;
import cz.stechy.drd.R;
import cz.stechy.drd.controller.main.MainScreen;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.Hero.Profession;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

public class ProfessionController implements MainScreen, Initializable {

    // region Variables

    // region FXMl

    @FXML
    private StackPane container;

    // endregion

    private final Context context;

    private IProfessionController professionController;
    private ResourceBundle resources;

    // endregion

    // region Constructors

    public ProfessionController(Context context) {
        this.context = context;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
    }

    @Override
    public void setHero(ReadOnlyObjectProperty<Hero> hero) {
        hero.addListener((observable, oldValue, newValue) -> {
            container.getChildren().clear();
            if (newValue == null || newValue.getName().isEmpty()) {
                return;
            }

            try {
                loadProfession(newValue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Načte správný kontroler podle profese hrdiny
     *
     * @param hero {@link Hero}
     */
    private void loadProfession(Hero hero) throws IOException {
        System.out.println("Načítám profesi");
//        final String fxml = getFxmlName(hero.getProfession());
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/fxml/main/profession/" + fxml + ".fxml"));
//        loader.setControllerFactory(new ControllerFactory());
//        loader.setResources(resources);
//        final Node node = loader.load();
//        professionController = loader.getController();
//        professionController.setHero(hero);
//        container.getChildren().setAll(node);
    }

    /**
     * Vytvoří instanci kontroleru a vrátí název FXML dokumentu, který se spáruje s touto instancí
     *
     * @param profession {@link Profession}
     * @return Název FXML dokumentu pro spárování s instanci kontroleru
     */
    private String getFxmlName(Profession profession) {
        String fxmlName = "";
        switch (profession) {
            case WARIOR:
                fxmlName = R.FXML.WARRIOR;
                break;
            case RANGER:
                fxmlName = R.FXML.RANGER;
                break;
            case ALCHEMIST:
                fxmlName = R.FXML.ALCHEMIST;
                break;
            case WIZARD:
                fxmlName = R.FXML.WIZARD;
                break;
            case THIEF:
                fxmlName = R.FXML.THIEF;
                break;
        }

        return fxmlName;
    }
}
