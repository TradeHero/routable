package com.tradehero.route.sample;

import android.app.Application;
import com.tradehero.route.Router;

public class App extends Application {
  private Router router;

  @Override public void onCreate() {
    super.onCreate();

    router = Router.of(this).registerRoutes(UserActivity.class);
  }

  public Router getRouter() {
    return router;
  }
}
