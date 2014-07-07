package com.tradehero.route.internal;

class FieldBinding {
  private String name;
  private String type;

  public FieldBinding(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
