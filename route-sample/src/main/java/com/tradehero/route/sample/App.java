package com.tradehero.route.sample;

import android.app.Application;
import com.tradehero.route.Router;

public class App extends Application {
  private Router router;

  @Override public void onCreate() {
    super.onCreate();

    router = Router.getInstance();
    router.setContext(this);
    router.registerRoutes(UserActivity.class);
  }

  public Router getRouter() {
    return router;
  }
}
