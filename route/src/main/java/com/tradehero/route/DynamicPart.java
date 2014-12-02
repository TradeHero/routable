package com.tradehero.route;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

/**
 * A dynamic part, which gets extracted into a parameter.
 */
@AutoValue
public abstract class DynamicPart implements PathPart {

  /**
   * @param name The name of the parameter that this part of the path gets extracted into.
   * @param constraint The regular expression used to match this part.
   * @param bundleType The bundle-able type of variable associated with the name
   */
  public static DynamicPart create(String name, String constraint, String bundleType) {
    return new AutoValue_DynamicPart(name, constraint, bundleType);
  }

  public static DynamicPart create(String name, String constraint) {
    return new AutoValue_DynamicPart(name, constraint, BundleType.STRING);
  }

  public static DynamicPart create(String name) {
    return new AutoValue_DynamicPart(name, null, BundleType.STRING);
  }

  public abstract String name();
  @Nullable public abstract String constraint();
  public abstract String bundleType();
}
