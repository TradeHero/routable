package com.tradehero.route;

import com.google.auto.value.AutoValue;

/**
 * A static part of the path, which is matched as is.
 */
@AutoValue
public abstract class StaticPart implements PathPart {
  public static StaticPart create(String value) {
    return new AutoValue_StaticPart(value);
  }

  abstract String value();
}
