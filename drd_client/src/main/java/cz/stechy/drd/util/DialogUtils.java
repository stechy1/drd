package cz.stechy.drd.util;

import cz.stechy.drd.model.WithImage;
import cz.stechy.drd.model.WithSameProperties;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.spell.Spell;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * Pomocná knihovní třída pro generování systémových dialogů pro práci se soubory
 */
public final class DialogUtils {

    // region Constants

    private static final ExtensionFilter FILTER_PNG = new ExtensionFilter("PNG", "*.png");
    private static final int ITEM_IMAGE_SIZE = 150;

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor k zabránění vytvoření instance
     */
    private DialogUtils() {

    }

    // endregion

    // region Public static methods

    /**
     * Zobrazí dialog pro výběr obrázku s předpřipravenými konstantami pro editor předmětů
     *
     * @param window Vlastník dialogu, může být i null
     * @param title Titulek pro dialog
     * @return Binární podoba obrázku
     * @throws IOException Pokud se nepodaří správně zpracovat obrázek
     */
    public static byte[] openImageForItemEditor(Window window, String title) throws IOException {
        return openImage(window, title, FILTER_PNG, ITEM_IMAGE_SIZE, ITEM_IMAGE_SIZE);
    }

    /**
     * Zobrazí dialog pro výběr obrázku. Pokud bude obrázek vybrán, tak se vrátí ve formě byte[]
     *
     * @param owner Vlastník dialogu, může být i null
     * @param title Titulek pro dialog
     * @param filter {@link ExtensionFilter}
     * @param width Šířka obrázku
     * @param height Výška obrázku
     * @return Binární podoba obrázku
     * @throws IOException Pokud se nepodaří správně zpracovat obrázek
     */
    public static byte[] openImage(Window owner, String title, ExtensionFilter filter,
        int width, int height)
        throws IOException {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File file = fileChooser
            .showOpenDialog(owner);
        if (file == null) {
            return new byte[0];
        }

        final byte[] image = ImageUtils.readImage(file);
        return ImageUtils.resizeImageRaw(image, width, height);
    }

//    public static List<ChoiceEntry> getItemRegistryChoices() {
//        return ItemRegistry.getINSTANCE().getRegistry().entrySet()
//            .stream()
//            .map(entry -> new ChoiceEntry((WithSameProperties) entry.getValue()))
//            .collect(Collectors.toList());
//    }
    public static List<ChoiceEntry> getItemChoices(Collection<ItemBase> items) {
        return items.stream()
            .map(ChoiceEntry::new)
            .collect(Collectors.toList());
    }

    public static List<ChoiceEntry> getMobsChoices(Collection<Mob> mobs) {
        return mobs.stream()
            .map(ChoiceEntry::new)
            .collect(Collectors.toList());
    }

    public static List<ChoiceEntry> getSpellChoices(Collection<Spell> spells) {
        return spells.stream()
            .map(ChoiceEntry::new)
            .collect(Collectors.toList());
    }

    public static Optional<ChoiceEntry> selectItem(List<ChoiceEntry> items) {
        final ChoiceDialog<ChoiceEntry> dialog = new ChoiceDialog<>(null, items);
        dialog.setTitle("Výběr záznamu");
        dialog.setHeaderText("Vyberte ze seznamu");
        dialog.setContentText("Vyberte...");
        // Trocha čarování k získání reference na combobox abych ho mohl upravit
        @SuppressWarnings("unchecked") final ComboBox<ChoiceEntry> comboBox = (ComboBox) (((GridPane) dialog
            .getDialogPane()
            .getContent())
            .getChildren().get(1));
        comboBox.setPrefWidth(100);
        comboBox.setButtonCell(new CellUtils.RawImageListCell());
        comboBox.setCellFactory(param -> new CellUtils.RawImageListCell());
        comboBox.setMinWidth(200);
        comboBox.setMinHeight(40);
        return dialog.showAndWait();
    }

    // endregion

    public static final class ChoiceEntry implements WithImage {

        final StringProperty id = new SimpleStringProperty();
        final StringProperty name = new SimpleStringProperty();
        final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>();
        final WithSameProperties entry;

        public ChoiceEntry(WithSameProperties entry) {
            this.entry = entry;
            this.id.setValue(entry.getId());
            this.name.setValue(entry.getName());
            imageRaw.set(entry.getImage());
        }

        @Override
        public String toString() {
            return name.get();
        }

        @Override
        public byte[] getImage() {
            return imageRaw.get();
        }

        public String getId() {
            return id.get();
        }

        public StringProperty idProperty() {
            return id;
        }

        public void setId(String id) {
            this.id.set(id);
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public byte[] getImageRaw() {
            return imageRaw.get();
        }

        public ObjectProperty<byte[]> imageRawProperty() {
            return imageRaw;
        }

        public void setImageRaw(byte[] imageRaw) {
            this.imageRaw.set(imageRaw);
        }

        public WithSameProperties getBase() {
            return entry;
        }
    }

}
