package cz.stechy.drd.util;

import java.io.File;
import java.io.IOException;
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

    // endregion
}
