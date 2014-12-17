package com.tradehero.route.sample;

import android.app.Application;
import android.content.Context;
import com.tradehero.route.Router;

public class App extends Application {
  private Router router;

  @Override public void onCreate() {
    super.onCreate();

    router = TestRouter.create();
  }

  public Router getRouter() {
    return router;
  }

  public static App get(Context context) {
    return (App) context.getApplicationContext();
  }
}
