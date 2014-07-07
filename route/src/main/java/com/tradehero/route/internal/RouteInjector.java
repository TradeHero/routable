package com.tradehero.route.internal;

import java.util.LinkedHashSet;
import java.util.Set;

final class RouteInjector {
  private final String classPackage;
  private final String targetClass;
  private final String className;
  private Set<FieldBinding> fieldBinding = new LinkedHashSet<FieldBinding>();

  RouteInjector(String classPackage, String className, String targetClass) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
  }

  String getFqcn() {
    return classPackage + "." + className;
  }

  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code by Route. Do not modify!\n");
    builder.append("package ").append(classPackage).append(";\n\n");
    builder.append("import android.os.Bundle;\n\n");
    builder.append("public class ").append(className).append(" {\n");
    emitInject(builder);
    builder.append('\n');
    emitReset(builder);
    builder.append("}\n");
    return builder.toString();
  }

  private void emitReset(StringBuilder builder) {
    // TODO what to reset when activity/fragment is destroyed?
  }

  private void emitInject(StringBuilder builder) {
    builder.append("  public static void inject(final ")
        .append(targetClass)
        .append(" target, Bundle source) {\n");

    for (FieldBinding binding: fieldBinding) {
      emitBinding(builder, binding);
    }

    builder.append("  }");
  }

  private void emitBinding(StringBuilder builder, FieldBinding binding) {
    String fieldPath = "target." + binding.getName();

    builder.append("    ")
        .append("if (")
        .append(fieldPath)
        .append(" == null) ")
        .append(fieldPath)
        .append(" = new ")
        .append(binding.getType())
        .append("();\n");

    builder.append("    ")
        .append(binding.getType())
        .append(RouterProcessor.SUFFIX)
        .append(".inject(target.")
        .append(binding.getName())
        .append(", source);\n");
  }

  public void addBinding(FieldBinding binding) {
    fieldBinding.add(binding);
  }
}
