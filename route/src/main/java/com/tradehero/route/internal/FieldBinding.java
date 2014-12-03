package com.tradehero.route.internal;

class FieldBinding {
  private String name;

  protected FieldBinding(String name) {
    this.name = name;
  }

  public final String getName() {
    return name;
  }
}
