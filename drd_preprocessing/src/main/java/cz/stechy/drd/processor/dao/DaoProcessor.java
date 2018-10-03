package cz.stechy.drd.processor.dao;

import com.google.auto.service.AutoService;
import cz.stechy.drd.annotation.Dao;
import cz.stechy.drd.annotation.Service;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
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
import javax.tools.JavaFileObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

@AutoService(Processor.class)
public class DaoProcessor extends AbstractProcessor {

    private static final String INTERFACE_FORMAT = "I%s";

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new LinkedHashSet<>();
        supportedAnnotations.add(Dao.class.getCanonicalName());
        supportedAnnotations.add(Service.class.getCanonicalName());
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private List<Entry> getEntries(final Set<? extends Element> annotatedElements) {
        final List<Entry> entries = new LinkedList<>();
        if (annotatedElements.isEmpty()) {
            return Collections.emptyList();
        }

        for (Element element : annotatedElements) {
            final TypeElement packageElement = (TypeElement) element;
            final String packageName = ((PackageElement) packageElement.getEnclosingElement()).getQualifiedName().toString();
            final String className = packageElement.getSimpleName().toString();
            final String interfaceName = String.format(INTERFACE_FORMAT, className);
            entries.add(new Entry(packageName, interfaceName, className));
        }

        return entries;
    }

    private void write(List<Entry> entries, VelocityEngine ve, String fileName) {
        final VelocityContext vc = new VelocityContext();
        vc.put("classes", entries);
        vc.put("className", fileName);

        final Template vt = ve.getTemplate("dao.vm");

        try {
            final JavaFileObject jfo = filer.createSourceFile("cz.stechy.drd." + fileName);
            final Writer writer = jfo.openWriter();
            vt.merge(vc, writer);
            writer.close();
        } catch (IOException ignored) {}
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (new File("./drd_client/src/generated/java/cz/stechy/drd/DaoModule.java").exists()) {
            return true;
        }

        if (new File("./drd_client/src/generated/java/cz/stechy/drd/ServiceModule.java").exists()) {
            return true;
        }

//        final List<Entry> daoEntries = getEntries(roundEnv.getElementsAnnotatedWith(Dao.class));
        final List<Entry> serviceEntries = getEntries(roundEnv.getElementsAnnotatedWith(Service.class));

        final Properties props = new Properties();
        final URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        try {
            props.load(Objects.requireNonNull(url).openStream());
        } catch (IOException | NullPointerException e) {
            return false;
        }

        final VelocityEngine ve = new VelocityEngine(props);
        ve.init();

//        write(daoEntries, ve, "DaoModule");
        write(serviceEntries, ve, "ServiceModule");

        return true;
    }
}
