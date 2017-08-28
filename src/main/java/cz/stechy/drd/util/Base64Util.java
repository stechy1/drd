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

    public static String encode(byte[] data) {
        String result = encoder.encodeToString(data);
        return result.replaceAll("/", SLASH_REPLACE);
    }

    public static byte[] decode(String data) {
        String toDecode = data.replaceAll(SLASH_REPLACE, "/");
        return decoder.decode(toDecode);
    }

    // endregion

    // endregion

}
