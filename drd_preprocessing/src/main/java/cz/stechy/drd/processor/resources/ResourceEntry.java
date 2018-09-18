package cz.stechy.drd.processor.resources;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ResourceEntry {

    // region Constants

    private static final FilenameFilter DEFAULT_FILTER = (dir, name) -> true;

    private static FilenameFilter filter = DEFAULT_FILTER;

    // endregion

    // region Variables

    private final File file;
    private final ResourceEntry parent;
    private final List<ResourceEntry> childs = new ArrayList<>();
    private final int depth;

    // endregion

    // region Constructors

    ResourceEntry(File file) {
        this(null, file, 0);
    }

    private ResourceEntry(ResourceEntry parent, File file, int depth) {
        this.parent = parent;
        this.file = file;
        this.depth = depth;
        loadChilds();
    }

    // endregion

    // region Static methods

    static void resetFilenameFilter() {
        filter = DEFAULT_FILTER;
    }

    static void setFilenameFilter(FilenameFilter filter) {
        ResourceEntry.filter = filter;
    }

    // endregion

    // region Private methods

    private void loadChilds() {
        if (file.isDirectory()) {
            final File[] files = file.listFiles(filter);
            for (File entry : Objects.requireNonNull(files)) {
                childs.add(new ResourceEntry(this, entry, (depth + 1)));
            }
        }
    }

    // endregion

    // region Getters & Setters

    public List<ResourceEntry> getChilds() {
        return Collections.unmodifiableList(childs);
    }

    public String getRecursivePath() {
        if (parent != null) {
            return parent.getRecursivePath() + "/" + file.getName();
        }

        return getNameWithoutExtention();
    }

    public String getNameWithoutExtention() {
        final String name = file.getName().replace("-", "_");
        if (file.isDirectory()) {
            return name;
        }

        return name.substring(0, name.indexOf("."));
    }

    public boolean hasChilds() {
        return !childs.isEmpty();
    }

    // endregion

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Entry: " + getRecursivePath() + "\n");
        for (ResourceEntry child : childs) {
            for (int i = 0; i < depth; i++) {
                sb.append("\t");
            }
            sb.append(child.toString());
        }
        return sb.toString();
    }
}

