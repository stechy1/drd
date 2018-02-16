package cz.stechy.drd.app.bestiary.edit;

import cz.stechy.drd.app.bestiary.BestiaryHelper;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.ValidatedModel;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import cz.stechy.drd.util.FormUtils;
import cz.stechy.drd.util.Translator;
import cz.stechy.drd.util.Translator.Key;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Kontroler pro editaci základních vlastností nestvůry
 */
public class BestiaryBestiaryEditGeneralController implements Initializable,
    IBestiaryEditController {

    // region Variables

    // region FXML

    @FXML
    private TextField txtName;
    @FXML
    private ComboBox<Rule> cmbRule;
    @FXML
    private ComboBox<MobClass> cmbMobClass;
    @FXML
    private ComboBox<Conviction> cmbConviction;
    @FXML
    private ComboBox<Height> cmbHeight;

    // endregion

    private final Model model = new Model();
    private final Translator translator;

    // endregion

    // region Constructors

    public BestiaryBestiaryEditGeneralController(Translator translator) {
        this.translator = translator;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbRule.setConverter(translator.getConvertor(Key.RULES));
        cmbMobClass.setConverter(translator.getConvertor(Key.MOB_CLASSES));
        cmbConviction.setConverter(translator.getConvertor(Key.CONVICTIONS));

        txtName.textProperty().bindBidirectional(model.name);
        cmbRule.valueProperty().bindBidirectional(model.rule);
        cmbMobClass.valueProperty().bindBidirectional(model.mobClass);
        cmbConviction.valueProperty().bindBidirectional(model.conviction);
        cmbHeight.valueProperty().bindBidirectional(model.height);
    }

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        model.name.setValue(bundle.getString(BestiaryHelper.NAME));
        model.rule.setValue(Rule.values()[bundle.getInt(BestiaryHelper.RULES_TYPE)]);
        model.mobClass.setValue(MobClass.values()[bundle.getInt(BestiaryHelper.MOB_CLASS)]);
        model.conviction.setValue(Conviction.values()[bundle.getInt(BestiaryHelper.CONVICTION)]);
        model.height.setValue(Height.values()[bundle.getInt(BestiaryHelper.HEIGHT)]);
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.putString(BestiaryHelper.NAME, model.name.getValue());
        bundle.putInt(BestiaryHelper.RULES_TYPE, model.rule.getValue().ordinal());
        bundle.putInt(BestiaryHelper.MOB_CLASS, model.mobClass.getValue().ordinal());
        bundle.putInt(BestiaryHelper.CONVICTION, model.conviction.getValue().ordinal());
        bundle.putInt(BestiaryHelper.HEIGHT, model.height.getValue().ordinal());
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return model.validProperty();
    }

    private static final class Model extends ValidatedModel {
        private static final int FLAG_NAME = 1 << 0;
        private static final int FLAG_RULE = 1 << 1;
        private static final int FLAG_MOB_CLASS = 1 << 2;
        private static final int FLAG_CONVICTION = 1 << 3;
        private static final int FLAG_HEIGHT = 1 << 4;

        private final StringProperty name = new SimpleStringProperty(this, "name", null);
        private final ObjectProperty<Rule> rule = new SimpleObjectProperty<>(this, "rule", null);
        private final ObjectProperty<MobClass> mobClass = new SimpleObjectProperty<>(this, "mobClass", null);
        private final ObjectProperty<Conviction> conviction = new SimpleObjectProperty<>(this, "conviction", null);
        private final ObjectProperty<Height> height = new SimpleObjectProperty<>(this, "height", null);

        {
            name.addListener(FormUtils.notEmptyCondition(this, FLAG_NAME));
            rule.addListener(FormUtils.notEmptyCondition(this, FLAG_RULE));
            mobClass.addListener(FormUtils.notEmptyCondition(this, FLAG_MOB_CLASS));
            conviction.addListener(FormUtils.notEmptyCondition(this, FLAG_CONVICTION));
            height.addListener(FormUtils.notEmptyCondition(this, FLAG_HEIGHT));

            // Nastavení validačních příznaků - žádné pole není vyplněno
            validityFlag.set(FLAG_NAME + FLAG_RULE + FLAG_MOB_CLASS + FLAG_CONVICTION + FLAG_HEIGHT);
        }
    }
}
