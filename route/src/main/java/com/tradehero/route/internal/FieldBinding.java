package com.tradehero.route.internal;

import java.util.Map;

abstract class FieldBinding {
  private String name;

  protected FieldBinding(String name) {
    this.name = name;
  }

  public final String getName() {
    return name;
  }
  
  public abstract Map<String, BundleType> typeMap();
}
