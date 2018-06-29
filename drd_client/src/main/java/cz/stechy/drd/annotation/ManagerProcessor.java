package cz.stechy.drd.annotation;

import static cz.stechy.drd.annotation.ProcessorUtils.error;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

/**
 * Processor pro generování manažerských tříd
 */
@SupportedAnnotationTypes("Table")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ManagerProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();
        Filer filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Zpracovavam annotace");
        messager.printMessage(Kind.WARNING, "Zpracovavam annotace");
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Table.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(messager, annotatedElement, "Only classes can be annotated with @%s",
                    Table.class.getSimpleName());
                return true; // Exit processing
            }
            TypeElement typeElement = (TypeElement) annotatedElement;
            TableAnnotatedClass tableAnnotatedClass = new TableAnnotatedClass(typeElement);
            messager.printMessage(Kind.NOTE,
                "Zpracovávám třídu: " + tableAnnotatedClass.getSimpleTypeName());

            final List<ColumnInfo> columnInfos = new ArrayList<>();
            final Field[] fields = annotatedElement.getClass().getDeclaredFields();
            for (Field field : fields) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation == null) {
                    continue;
                }

                messager.printMessage(Kind.NOTE, "Přidávám field: " + field.getName());
                columnInfos.add(new ColumnInfo(columnAnnotation.name(), field.getType()));
            }

            System.out.println("Test processor");

            try {
                final String className = typeElement.getSimpleName() + "Manager";
                final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(
                    typeElement.getQualifiedName() + "." + className);

                try (Writer writter = fileObject.openWriter()) {
                    writter.append("package ")
                        .append(String.valueOf(typeElement.getQualifiedName())).append(";");
                    writter.append("\\n\\n");
                    writter.append("public class ").append(className).append(" {");
                    writter.append("\\n");
                    writter.append("}");
                }
            } catch (final IOException ex) {
                processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage());
            }

        }
        return true;
    }
}
