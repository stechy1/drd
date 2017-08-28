package cz.stechy.drd.model.entity;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Zranitelnost entity
 */
public class Vulnerabilities {

    // region Constants

    public static final int A = 1 << 0;
    public static final int B = 1 << 1;
    public static final int C = 1 << 2;
    public static final int D = 1 << 3;
    public static final int E = 1 << 4;
    public static final int F = 1 << 5;
    public static final int G = 1 << 6;
    public static final int H = 1 << 7;
    public static final int I = 1 << 8;
    public static final int J = 1 << 9;
    public static final int K = 1 << 10;
    public static final int L = 1 << 11;
    public static final int M = 1 << 12;
    public static final int N = 1 << 13;
    public static final int O = 1 << 14;
    public static final int P = 1 << 15;
    public static final int Pplus = 1 << 16;

    // Všechny třídy zranitelnosti v jednom poli
    public static final Map<String, Integer> VALUES = Collections.unmodifiableMap(new HashMap<String, Integer>() {{
        put("A", A);
        put("B", B);
        put("C", C);
        put("D", D);
        put("E", E);
        put("F", F);
        put("G", G);
        put("H", H);
        put("I", I);
        put("J", J);
        put("K", K);
        put("L", L);
        put("M", M);
        put("N", N);
        put("O", O);
        put("P", P);
        put("Pplus", Pplus);
    }});

    // endregion

    private Vulnerabilities() {}

    public enum VulnerabilityType {
        ANIMAL(B | C | D | G | H | I | J | K | L),
        HUMANOID(A | B | C | D | G | H | I | J | K | L | M | O),
        DRAGON(B | C | D | G | H | I | J | K | P),
        LYCANTHROPE(A | B | C | D | F | G | H | I | J | K | L | M),
        UNDEATH(E | I | J | K | L | N | P),
        INVISIBLE(C | I | J | N | Pplus),
        CUSTOM(0);

        private final int vulnerability;

        VulnerabilityType(int vulnerability) {
            this.vulnerability = vulnerability;
        }

        public int getVulnerability() {
            return vulnerability;
        }
    }


}
