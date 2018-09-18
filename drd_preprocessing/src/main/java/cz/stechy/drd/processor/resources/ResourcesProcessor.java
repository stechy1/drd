package cz.stechy.drd.processor.resources;

import com.google.auto.service.AutoService;
import cz.stechy.drd.annotation.resources.ResourcesRoot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.util.StringUtils;

@AutoService(Processor.class)
public class ResourcesProcessor extends AbstractProcessor {

    // region Constants

    private static final String RESOURCES_PATH = "./drd_client/src/main/resources";
    private static final String FXML_PATH = "fxml";
    private static final String TRANSLATIONS_PATH = "lang";
    private static final String IMAGES_PATH = "images";
    private static final String TABLE_COLUMNS_PATH = "other/table_columns.txt";

    // endregion

    // region Variables

    private Filer filer;
    private Messager messager;

    // endregion

    // region Private methods

    /**
     * Vrátí rekurzivní strukturu všech FXML souborů
     *
     * @param parent {@link File} Kořenová složka resources
     * @return {@link ResourceEntry}
     */
    private Object getFxmls(File parent) {
        return new ResourceEntry(new File(parent, FXML_PATH));
    }

    /**
     * Vrátí kolekci všech klíčů použitých v překládacích souborech
     *
     * @param parent {@link File} Kořenová složka resources
     * @return {@link List<TranslationEntry>} Kolekci klíčů
     */
    private Object getTranslationEntries(File parent) {
        final List<TranslationEntry> translationEntries = new ArrayList<>();
        File translationFolder = new File(parent, TRANSLATIONS_PATH);
        if (translationFolder.isDirectory()) {
            final File[] translations = translationFolder.listFiles((dir, name) -> name.contains(".properties"));
            if (translations != null && translations.length > 0) {
                File translateFile = translations[0];
                try (BufferedReader br = new BufferedReader(new FileReader(translateFile))) {
                    String line;
                    while((line = br.readLine()) != null) {
                        if (!line.contains("=")) {
                            continue;
                        }

                        translationEntries.add(new TranslationEntry(line));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return translationEntries;
    }

    /**
     * Vrátí rekurzivní strukturu všech obrázků
     *
     * @param parent {@link File} Kořenová složka resources
     * @return {@link ResourceEntry}
     */
    private Object getImages(File parent) {
        ResourceEntry.setFilenameFilter((dir, name) -> (dir.isDirectory() && !name.contains(".")) || name.endsWith("png") || name.endsWith("gif"));
        final ResourceEntry imagesRootEntry = new ResourceEntry(new File(parent, IMAGES_PATH));
        ResourceEntry.resetFilenameFilter();

        return imagesRootEntry;
    }

    /**
     * Vrátí kolekci všech tabulek
     *
     * @param parent {@link File} Kořenová složka resources
     * @return {@link List<TableEntry>}
     */
    private Object getTables(File parent) {
        final List<TableEntry> tables = new ArrayList<>();
        File tableColumnsDefinitions = new File(parent, TABLE_COLUMNS_PATH);
        if (tableColumnsDefinitions.exists() && tableColumnsDefinitions.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(tableColumnsDefinitions))) {
                String line;
                while((line = br.readLine()) != null) {
                    tables.add(new TableEntry(line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tables;
    }

    // endregion

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> supportedAnnotations = new LinkedHashSet<>();
        supportedAnnotations.add(ResourcesRoot.class.getCanonicalName());
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (new File("./drd_share/src/generated/java/cz/stechy/drd/R.java").exists()) {
            return true;
        }

        String packageName = null;

        for (TypeElement annotation : annotations) {
            final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            if (annotatedElements.isEmpty()) {
                continue;
            }

            for (Element element : annotatedElements) {
                packageName = ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString();
            }
        }

        if (packageName == null) {
            return false;
        }

        messager.printMessage(Kind.NOTE, "Creating resources class in package: " + packageName);

        final File resources = new File(RESOURCES_PATH);
        if (!resources.isDirectory()) {
            return false;
        }

        final Properties props = new Properties();
        final URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        try {
            props.load(Objects.requireNonNull(url).openStream());
        } catch (IOException | NullPointerException e) {
            return false;
        }

        final VelocityEngine ve = new VelocityEngine(props);
        ve.init();
        final VelocityContext vc = new VelocityContext();
        vc.put("StringUtils", StringUtils.class);
        vc.put("packageName", packageName);
        vc.put("fxmls", getFxmls(resources));
        vc.put("translations", getTranslationEntries(resources));
        vc.put("images", getImages(resources));
        vc.put("tables", getTables(resources));

        final Template vt = ve.getTemplate("r.vm");

        try {
            final JavaFileObject jfo = filer.createSourceFile(packageName + ".R");
            final Writer writer = jfo.openWriter();
            vt.merge(vc, writer);
            writer.close();
        } catch (IOException ignored) {}

        return true;
    }
}
