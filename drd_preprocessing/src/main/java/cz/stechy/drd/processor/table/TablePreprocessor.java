package cz.stechy.drd.processor.table;

import com.google.auto.service.AutoService;
import cz.stechy.drd.annotation.Table;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.JavaFileObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

@AutoService(Processor.class)
public class TablePreprocessor extends AbstractProcessor {

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new LinkedHashSet<>();
        supportedAnnotations.add(Table.class.getCanonicalName());
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (new File("./drd_client/src/generated/java/cz/stechy/drd/TableModule.java").exists()) {
            return true;
        }

        final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Table.class);
        final List<Entry> definitionsEntries = new LinkedList<>();
        final List<Entry> offlineEntries = new LinkedList<>();
        final List<Entry> onlineEntries = new LinkedList<>();
        final List<Entry> mergedEntries = new LinkedList<>();
        final Set<String> entities = new HashSet<>();

        for (Element element : annotatedElements) {
            final TypeElement packageElement = (TypeElement) element;
            final String tablePackage = ((PackageElement) packageElement.getEnclosingElement()).getQualifiedName().toString();
            final String tableName = packageElement.getSimpleName().toString();
            final Table table = packageElement.getAnnotation(Table.class);
            String entityPackage;
            try {
                entityPackage = table.clazz().getCanonicalName();
            } catch (MirroredTypeException mte) {
                entityPackage = mte.getTypeMirror().toString();
            }
            final String entityName = entityPackage.substring(entityPackage.lastIndexOf(".")+1);
            final Entry entry = new Entry(tablePackage, tableName, entityPackage, entityName);

            switch (table.type()) {
                case DEFINITION:
                    definitionsEntries.add(entry);
                    break;
                case OFFLINE:
                    offlineEntries.add(entry);
                    break;
                case ONLINE:
                    onlineEntries.add(entry);
                    break;
                case WRAPPER:
                    mergedEntries.add(entry);
            }
            entities.add(entityPackage);
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
        vc.put("entities", entities);
        vc.put("tableDefinitions", definitionsEntries);
        vc.put("offlineTables", offlineEntries);
        vc.put("onlineTables", onlineEntries);
        vc.put("offlineOnlineTables", mergedEntries);

        final Template vt = ve.getTemplate("table.vm");

        try {
            final JavaFileObject jfo = filer.createSourceFile("cz.stechy.drd.TableModule");
            final Writer writer = jfo.openWriter();
            vt.merge(vc, writer);
            writer.close();
        } catch (IOException ignored) {}

        return true;
    }
}
