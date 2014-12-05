package com.tradehero.route.internal;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

final class ClassBinding extends FieldBinding {
  private final Set<FieldBinding> childBinding = new LinkedHashSet<FieldBinding>();

  protected ClassBinding(String name) {
    super(name);
  }

  // TODO be lazy
  public Map<String, BundleType> typeMap() {
    Map<String, BundleType> result = new LinkedHashMap<String, BundleType>();
    for (FieldBinding binding: childBinding) {
      result.putAll(binding.typeMap());
    }
    return result;
  }

  public void addFieldBinding(FieldBinding fieldBinding) {
    childBinding.add(fieldBinding);
  }

  public Set<FieldBinding> bindings() {
    return childBinding;
  }
}
