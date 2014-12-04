package com.tradehero.route.internal;

import java.util.LinkedHashMap;
import java.util.Map;

class IndirectBinding extends FieldBinding {
  final Map<String, BundleType> typeMap = new LinkedHashMap<String, BundleType>();
  final String creatorName;

  /**
   * Binding for custom type, also see {@link com.tradehero.route.internal.BundleableBinding}
   * Example: {@code @RouteProperty com.example.SomeType val } has
   *  - name: val
   *  - creatorName: com.example.SomeType
   */
  public IndirectBinding(String name, String creatorName) {
    super(name);
    this.creatorName = creatorName;
  }
}
