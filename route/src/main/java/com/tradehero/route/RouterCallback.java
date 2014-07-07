package com.tradehero.route;

import java.util.Map;

/**
 * The class used when you want to map a function (given in `run`) to a Router URL.
 */
public interface RouterCallback {
  void run(Map<String, String> params);
}