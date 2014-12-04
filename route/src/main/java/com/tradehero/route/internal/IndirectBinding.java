package com.tradehero.route.internal;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class IndirectBinding extends FieldBinding {
  private final Set<FieldBinding> childBinding = new LinkedHashSet<FieldBinding>();
  private final String className;

  /**
   * Binding for custom type, also see {@link com.tradehero.route.internal.BundleableBinding}
   * Example: {@code @RouteProperty com.example.SomeType val } has
   *  - name: val
   *  - className: com.example.SomeType
   */
  public IndirectBinding(String name, String className) {
    super(name);
    this.className = className;
  }

  public String getClassName() {
    return className;
  }

  public void addFieldBinding(FieldBinding fieldBinding) {
    childBinding.add(fieldBinding);
  }

  public Set<FieldBinding> bindings() {
    return childBinding;
  }

  // TODO be lazy
  public Map<String, BundleType> typeMap() {
    Map<String, BundleType> result = new LinkedHashMap<String, BundleType>();
    for (FieldBinding binding: childBinding) {
      result.putAll(binding.typeMap());
    }
    return result;
  }
}
