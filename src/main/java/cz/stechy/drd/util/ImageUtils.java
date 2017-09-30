package cz.stechy.drd.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
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

    /**
     * Načte obrázek a vrátí jeho bytovou reprezentaci
     *
     * @param inputStream Stream s obrázkem
     * @return bytová reprezentace obrázku
     * @throws IOException Pokud se obrázek nepodaří zpracovat
     */
    public static byte[] readImage(InputStream inputStream) throws Exception {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BufferedImage bufferedImage = ImageIO.read(inputStream);
        ImageIO.write(bufferedImage, "png", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Změní velikost obrázku
     *
     * @param imageRaw Surová data obrázku
     * @param width Nová šířka obrázku
     * @param height Nová výška obrázku
     * @return Obrázek s novou velikostí v podobě surových dat
     * @throws IOException Pokud se nepodaří změnit velikost obrázku
     */
    public static byte[] resizeImageRaw(byte[] imageRaw, int width, int height) throws IOException {
        return imageToRaw(resizeImage(imageRaw, width, height));
    }

    /**
     * Změní velikost obrázku
     *
     * @param imageRaw Surová data obrázku
     * @param width Nová šířka obrázku
     * @param height Nová výška obrázku
     * @return Obrázek s novou velikostí
     * @throws IOException Pokud se nepodaří změnit velikost obrázku
     */
    public static Image resizeImage(byte[] imageRaw, int width, int height) {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(imageRaw);
        Image original = new Image(inputStream);
        return resizeImage(original, width, height);
    }

    /**
     * Změní velikost obrázku
     *
     * @param image Obrázek, u kterého se má změnit velikost
     * @param width Nová šířka obrázku
     * @param height Nová výška obrázku
     * @return Obrázek s novou velikostí
     * @throws IOException Pokud se nepodaří změnit velikost obrázku
     */
    public static Image resizeImage(Image image, int width, int height) {
        final BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        final BufferedImage resizedImage = createResizedCopy(bufferedImage, width, height);
        return SwingFXUtils.toFXImage(resizedImage, null);
    }

    /**
     * Samotná matoda pro změnu velikosti obrázku
     *
     * @param src Zdrojový obrázek
     * @param width Nová šířka obrázku
     * @param height Nová výška obrázku
     * @return Obrázek s novou velikostí
     */
    private static BufferedImage createResizedCopy(BufferedImage src,
        int width, int height) {
            final int original_width = src.getWidth();
            final int original_height = src.getHeight();
            final int bound_width = width;
            final int bound_height = height;
            int new_width = original_width;
            int new_height = original_height;
            boolean needResizeWidth = true;
            boolean needResizeHeight = true;

        // zkontrola, zda-lie je opravdu potřeba změnit velikost šířky obrázku
            if (original_width > bound_width) {
                new_width = bound_width;
                new_height = (new_width * original_height) / original_width;
            } else {
                needResizeWidth = false;
            }

            // zkontrola, zda-lie je opravdu potřeba změnit velikost výšky obrázku
            if (new_height > bound_height) {
                new_height = bound_height;
                new_width = (new_height * original_width) / original_height;
            } else {
                needResizeHeight = false;
            }

            // Pokud nepotřebuji nic měnit, tak vrázím původní obrázek
            if (!needResizeWidth && !needResizeHeight) {
                return src;
            }

            BufferedImage resizedImg = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = resizedImg.createGraphics();
            g2.clearRect(0,0,new_width, new_height);
            g2.drawImage(src, 0, 0, new_width, new_height, null);
            g2.dispose();
            return resizedImg;
        }

    /**
     * Pomocná metoda pro převod {@link Image} na surová data
     *
     * @param image {@link Image} Obrázek, který chci překonvertovat
     * @return Surová data obrázku
     * @throws IOException Pokud se převod nepovede
     */
    public static byte[] imageToRaw(Image image) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", s);
        byte[] res = s.toByteArray();
        s.close();
        return res;
    }

}
