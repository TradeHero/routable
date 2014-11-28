package com.tradehero.route;

import com.google.auto.value.AutoValue;

/**
 * A dynamic part, which gets extracted into a parameter.
 */
@AutoValue
public abstract class DynamicPart implements PathPart {
  /**
   * @param name The name of the parameter that this part of the path gets extracted into.
   * @param constraint The regular expression used to match this part.
   */
  public static DynamicPart create(String name, String constraint) {
    return new AutoValue_DynamicPart(name, constraint);
  }

  public abstract String name();
  public abstract String constraint();
}
