package cz.stechy.drd.processor.resources;

public class TranslationEntry {

    // region Variables

    private final String name;
    private final String value;

    // endregion

    // region Constructors

    TranslationEntry(String row) {
        final String[] split = row.split("=");
        this.value = split[0];
        this.name = this.value.replace("drd_", "").toUpperCase();
    }

    // endregion

    // region Getters & Setters

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    // endregion

    @Override
    public String toString() {
        return "Name: " + name + " = " + value;
    }
}

