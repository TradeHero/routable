package com.tradehero.route.internal;

import com.tradehero.route.RouteProperty;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static com.tradehero.route.internal.Utils.stackTraceToString;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

public class RouterProcessor extends AbstractProcessor {
  public static final String SUFFIX = "$$Routable";

  private TypeToBundleMethodMap typeToBundleMethodMap;

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    typeToBundleMethodMap = new TypeToBundleMethodMap(elementUtils(), typeUtils());
  }

  @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
    Map<TypeElement, RouteInjector> routeInjectorMap = findAndParseRouteTargets(env);
    for (Map.Entry<TypeElement, RouteInjector> entry : routeInjectorMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      RouteInjector routeInjector = entry.getValue();

      try {
        JavaFileObject jfo = filer().createSourceFile(routeInjector.getFqcn(), typeElement);
        Writer writer = jfo.openWriter();
        writer.write(routeInjector.brewJava());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        error(typeElement, "Unable to write injector for type %s: %s", typeElement, e.getMessage());
      }
    }
    return true;
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportTypes = new LinkedHashSet<String>();
    supportTypes.add(RouteProperty.class.getCanonicalName());

    return supportTypes;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private Map<TypeElement, RouteInjector> findAndParseRouteTargets(RoundEnvironment env) {
    Map<TypeElement, RouteInjector> targetClassMap = new LinkedHashMap<TypeElement, RouteInjector>();
    Set<String> injectableTargetClasses = new LinkedHashSet<String>();

    // Process each @RouteProperty element.
    for (Element element : env.getElementsAnnotatedWith(RouteProperty.class)) {
      try {
        if (element.getKind() != CLASS) {
          parseRouteProperty(element, targetClassMap, injectableTargetClasses);
          parseInjectRoute(element, targetClassMap, injectableTargetClasses);
        }
      } catch (Exception e) {
        error(element, "Unable to generate injector for @RouteProperty\n%s", stackTraceToString(e));
      }
    }

    // Try to find a parent injector for each injector.
    for (Map.Entry<TypeElement, RouteInjector> entry : targetClassMap.entrySet()) {
      String parentClassFqcn = findParentFqcn(entry.getKey(), injectableTargetClasses);
      if (parentClassFqcn != null) {
        entry.getValue().setParentInjector(parentClassFqcn + SUFFIX);
      }
    }

    return targetClassMap;
  }

  /**
   * Find the first parent of class that have typeElement that is injectable
   * @param typeElement typeElement of current injectable class
   * @param parents list of possible parent
   * @return parent fully qualified class name
   */
  private String findParentFqcn(TypeElement typeElement, Set<String> parents) {
    TypeMirror type;
    while (true) {
      type = typeElement.getSuperclass();
      if (type.getKind() == TypeKind.NONE) {
        return null;
      }
      typeElement = (TypeElement) ((DeclaredType) type).asElement();
      if (parents.contains(typeElement.toString())) {
        String packageName = getPackageName(typeElement);
        return (packageName.length() == 0) ?
            getClassName(typeElement, packageName) :
            packageName + "." + getClassName(typeElement, packageName);
      }
    }
  }

  private void parseRouteProperty(Element element, Map<TypeElement, RouteInjector> targetClassMap, Set<String> injectableTargetClasses) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type extends from Serializable.
    TypeMirror elementType = element.asType();
    if (elementType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }

    // Verify common generated code restrictions.
    if (isValidForGeneratedCode(RouteProperty.class, "fields", element)) {
      return;
    }

    boolean isMethod = element.getKind() == METHOD;
    if (!isMethod && typeToBundleMethodMap.convert(elementType) == null) {
      return;
    }
    // Assemble information on the injection point.
    String bundleMethod = getBundleMethod(element, elementType);

    String bundleKey = element.getAnnotation(RouteProperty.class).value();
    String name = element.getSimpleName().toString();
    if (isMethod) {
      if (bundleKey == null || bundleKey.length() == 0) {
        // extract getter name from getter method, example: getNumber ---> number
        for (int i = 0; i < name.length(); ++i) {
          if (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') {
            bundleKey = Character.toLowerCase(name.charAt(i)) + name.substring(i + 1);
            break;
          }
        }
      }
    }

    RouteInjector routeInjector =
        getOrCreateTargetRoutePropertyClass(targetClassMap, enclosingElement);
    RoutePropertyBinding binding = new RoutePropertyBinding(name, bundleMethod, bundleKey, isMethod);
    routeInjector.addBinding(binding);

    injectableTargetClasses.add(enclosingElement.toString());
  }

  private String getBundleMethod(Element element, TypeMirror elementType) {
    boolean isMethod = element.getKind() == METHOD;
    String name = element.getSimpleName().toString();
    if (isMethod) {
      // check this method is set method and for which property
      ExecutableElement executableElement = (ExecutableElement) element;
      List<? extends VariableElement> methodParameters = executableElement.getParameters();
      if (name.startsWith("set")) {
        if (methodParameters.size() != 1) {
          throw new IllegalStateException(String.format(
              "Setter method %s that annotated with @RouteProperty "
                  + "can have exactly one parameter", executableElement.getSimpleName()
          ));
        }

        VariableElement val = methodParameters.get(0);
        return typeToBundleMethodMap.convert(val.asType());
      }

      if (name.startsWith("get") || name.startsWith("is") || name.startsWith("has")) {
        if (methodParameters.size() != 0) {
          throw new IllegalStateException(String.format(
              "Getter method %s that annotated with @RouteProperty can not have any parameter",
              executableElement.getSimpleName()));
        }

        return typeToBundleMethodMap.convert(executableElement.getReturnType());
      }
    } else {
      return typeToBundleMethodMap.convert(elementType);
    }
    return null;
  }

  private RouteInjector getOrCreateTargetRoutePropertyClass(
      Map<TypeElement, RouteInjector> targetClassMap, TypeElement enclosingElement) {
    RouteInjector routeInjector = targetClassMap.get(enclosingElement);
    if (routeInjector == null) {
      String targetType = enclosingElement.getQualifiedName().toString();
      String classPackage = getPackageName(enclosingElement);
      String className = getClassName(enclosingElement, classPackage) + SUFFIX;

      String bundleKey = targetType;
      RouteProperty classRouteProperty = enclosingElement.getAnnotation(RouteProperty.class);
      if (classRouteProperty != null && classRouteProperty.value() != null
          && classRouteProperty.value().length() > 0) {
        bundleKey = classRouteProperty.value();
      }
      routeInjector = new RouteInjector(classPackage, className, targetType, bundleKey);
      targetClassMap.put(enclosingElement, routeInjector);
    }
    return routeInjector;
  }

  private void parseInjectRoute(Element element, Map<TypeElement, RouteInjector> targetClassMap,
      Set<String> erasedTargetNames) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type extends from Serializable.
    TypeMirror elementType = element.asType();
    if (elementType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }

    // Verify common generated code restrictions.
    if (isValidForGeneratedCode(RouteProperty.class, "fields", element)) {
      return;
    }

    if (element.getKind() == METHOD || typeToBundleMethodMap.convert(elementType) != null) {
      return;
    }

    // Assemble information on the injection point.
    String name = element.getSimpleName().toString();
    String type = elementType.toString();

    RouteInjector injectRouteInjector = getOrCreateTargetRoutePropertyClass(targetClassMap, enclosingElement);
    FieldBinding binding = new FieldBinding(name, type);
    injectRouteInjector.addBinding(binding);

    // Add the type-erased version to the valid injection targets set.
    erasedTargetNames.add(enclosingElement.toString());
  }

  private static String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() == 0 ? 0 : packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  private String getPackageName(TypeElement type) {
    return elementUtils().getPackageOf(type).getQualifiedName().toString();
  }

  private boolean isValidForGeneratedCode(Class<? extends Annotation> annotationClass,
      String targetThing, Element element) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@%s %s must not be private or static. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    return hasError;
  }

  private void error(Element element, String message, Object... args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }
    processingEnv.getMessager().printMessage(ERROR, message, element);
  }

  private Elements elementUtils() {
    return processingEnv.getElementUtils();
  }

  private Types typeUtils() {
    return processingEnv.getTypeUtils();
  }

  private Filer filer() {
    return processingEnv.getFiler();
  }
}
