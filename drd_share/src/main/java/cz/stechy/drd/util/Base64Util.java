package cz.stechy.drd.util;

import java.util.Base64;

/**
 * Pomocní knihovní třída pro práci s Base64 řetězci
 */
public final class Base64Util {

    // region Constants

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    private static final String SLASH_REPLACE = "-";

    // endregion

    // region Public static methods

    /**
     * Zakóduje vstupní data do Base64
     *
     * @param data Vstupní data
     * @return Textová reprezentace dat v podobě Base64
     */
    public static String encode(byte[] data) {
        String result = encoder.encodeToString(data);
        return result.replaceAll("/", SLASH_REPLACE);
    }

    /**
     * Dekóduje vstupní data v podobě Base64 do binární podoby
     *
     * @param data Vstupní data
     * @return Binární reprezentace dat
     */
    public static byte[] decode(String data) {
        String toDecode = data.replaceAll(SLASH_REPLACE, "/");
        return decoder.decode(toDecode);
    }

    // endregion

}
