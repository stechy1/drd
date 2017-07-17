package cz.stechy.drd.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Pomocná třída pro úpravu obrázků
 */
public final class ImageUtils {

    /**
     * Načte obrázek a vrátí jeho bytovou reprezentaci
     *
     * @param file Soubor s obrázkem
     * @return bytová reprezentace obrázku
     * @throws IOException Pokud se obrázek nepodaří zpracovat
     */
    public static byte[] readImage(File file) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BufferedImage bufferedImage = ImageIO.read(file);
        ImageIO.write(bufferedImage, "png", outputStream);

        return outputStream.toByteArray();
    }

}
