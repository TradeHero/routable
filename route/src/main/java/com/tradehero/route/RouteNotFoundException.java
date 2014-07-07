package com.tradehero.route;

/**
 * Thrown if a given route is not found.
 */
public class RouteNotFoundException extends RuntimeException {
  public RouteNotFoundException(String message) {
    super(message);
  }
}