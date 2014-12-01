package com.tradehero.route.internal;

import com.tradehero.route.Routable;
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
import static javax.tools.Diagnostic.Kind;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.WARNING;

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
    supportTypes.add(Routable.class.getCanonicalName());
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
        }
      } catch (Exception e) {
        error(element, "Unable to generate injector for @RouteProperty\n%s", stackTraceToString(e));
      }
    }

    // Process each @RouteProperty element.
    for (Element element : env.getElementsAnnotatedWith(Routable.class)) {
      try {
        if (element.getKind() == CLASS) {
          parseRoutable(element, targetClassMap, injectableTargetClasses);
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
    if (isInaccessibleViaGeneratedCode(RouteProperty.class, "fields", element)) {
      return;
    }

    // Verify annotated element to be a method or a field with bundle-able type
    boolean isMethod = element.getKind() == METHOD;
    String bundleMethod = typeToBundleMethodMap.convert(isMethod ? getMethodBundleType(element) :
        elementType);

    // Verification completed, generation process started
    // Add current class to list of class for code generation
    injectableTargetClasses.add(enclosingElement.toString());
    RouteInjector routeInjector = getOrCreateTargetRoutePropertyClass(targetClassMap, enclosingElement);
    String name = element.getSimpleName().toString();
    if (bundleMethod == null) {
      message(NOTE, element, (isMethod ? "Method" : "Field") + " %s type is not bundle-able, "
              + "indirect injection will be taking place!",
          element.getSimpleName());
      FieldBinding binding = new FieldBinding(name, elementType.toString());
      routeInjector.addFieldBinding(binding);
    } else {
      String bundleKey = element.getAnnotation(RouteProperty.class).value();
      if (isMethod && Utils.isNullOrEmpty(bundleKey)) {
        bundleKey = nameFromMutator(name);
      }
      RoutePropertyBinding binding = new RoutePropertyBinding(name, bundleMethod, bundleKey, isMethod);
      routeInjector.addFieldBinding(binding);
    }
  }

  private void parseRoutable(Element element, Map<TypeElement, RouteInjector> targetClassMap,
      Set<String> injectableTargetClasses) {
    TypeElement classElement = (TypeElement) element;

    // Verify common generated code restrictions.
    if (isRoutableIncorrectlyAnnotated(Routable.class, element)) {
      return;
    }

    String[] routes = element.getAnnotation(Routable.class).value();
    // Verify common generated code restrictions.
    RouteInjector routeInjector = getOrCreateTargetRoutePropertyClass(targetClassMap, classElement);
    routeInjector.addRoutableBinding(RoutableBinding.parse(routes));
    injectableTargetClasses.add(classElement.toString());
  }

  /** extract name from setter/getter method, example: getNumber ---> number */
  private String nameFromMutator(String name) {
    String resultName = name;
    for (int i = 0; i < name.length(); ++i) {
      if (Character.isUpperCase(name.charAt(i))) {
        resultName = Character.toLowerCase(name.charAt(i)) + name.substring(i + 1);
        break;
      }
    }
    return resultName;
  }

  /**
   * Get the bundle-able type of setter/getter method. For example:
   *  - Integer getId() will return Int, corresponding to {@link android.os.Bundle#putInt(String, int)}
   *  - void setName(String name) will return String, corresponding to{@link android.os.Bundle#getString(String)}
   *
   * @param element method element
   * @return Injection bundle type
   */
  private TypeMirror getMethodBundleType(Element element) {
    if (element.getKind() != METHOD) {
      throw new IllegalAccessError("Method is designed to call on a method element!");
    }

    String name = element.getSimpleName().toString();
    ExecutableElement executableElement = (ExecutableElement) element;
    List<? extends VariableElement> methodParameters = executableElement.getParameters();

    int parametersSize = methodParameters.size();
    // setter: bundle type is the type of the argument
    if (parametersSize == 1) {
      if (!verifySetterConvention(name)) {
        message(WARNING, element, "Route property setter [%s] should started with \"set\"", name);
      }
      VariableElement val = methodParameters.get(0);
      return val.asType();
    }

    // getter: bundle type is the method return type
    if (parametersSize == 0) {
      if (!verifyGetterConvention(name)) {
        message(WARNING, element, "Route property getter [%s] should started with either "
                + "\"get\", \"is\" or \"has\"", name);
      }
      return executableElement.getReturnType();
    }

    throw new IllegalStateException(String.format(
        "Method that annotated with @RouteProperty [%s] can only be a setter or getter, "
            + "which have no more than 1 argument",
        executableElement.getSimpleName()
    ));
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

  /**
   * Get relative class name to a package, for instance {@link android.view.View.OnClickListener}
   * will have name View$OnClickListener relatively to package {@link android.view.View}
  */
  private static String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() == 0 ? 0 : packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  private String getPackageName(TypeElement type) {
    return elementUtils().getPackageOf(type).getQualifiedName().toString();
  }

  /** Make sure that @RouteProperty annotated element is valid for code generation */
  private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
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

  private boolean isRoutableIncorrectlyAnnotated(Class<? extends Annotation> annotationClass, Element element) {
    boolean hasError = false;
    // Verify containing type.
    if (element.getKind() != CLASS) {
      error(element, "@%s %s has to be a class.",
          annotationClass.getSimpleName(), element.getSimpleName());
      hasError = true;
    }

    // Verify containing class visibility is not private.
    if (element.getModifiers().contains(PRIVATE)) {
      error(element, "@%s %s may not be a private class.",
          annotationClass.getSimpleName(), element.getSimpleName());
      hasError = true;
    }

    return hasError;
  }

  private void error(Element element, String message, Object... args) {
    message(ERROR, element, message, args);
  }

  private void message(Kind kind, Element element, String message, Object... args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }
    processingEnv.getMessager().printMessage(kind, message, element);
  }

  private boolean verifyGetterConvention(String name) {
    assert name != null;
    return name.startsWith("get") || name.startsWith("is") || name.startsWith("has");
  }

  private boolean verifySetterConvention(String name) {
    assert name != null;
    return name.startsWith("set");
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
