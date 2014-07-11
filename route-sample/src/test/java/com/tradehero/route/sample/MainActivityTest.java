package com.tradehero.route.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.tradehero.route.sample.key.PortfolioId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
  @Test public void verifyOpenByRoute() {
    MainActivity activity = Robolectric.setupActivity(MainActivity.class);
    TextView openByRoute = (TextView) activity.findViewById(R.id.open_by_route);
    assertThat(openByRoute).isNotNull();

    openByRoute.performClick();
    Intent nextActivity = shadowOf(activity).getNextStartedActivity();
    assertThat(nextActivity).isNotNull();
    assertThat(nextActivity.getComponent()).isNotNull();
    assertThat(nextActivity.getComponent().getClassName()).isEqualTo(UserActivity.class.getName());
    assertThat(nextActivity.getExtras()).isNotNull();
    assertThat(nextActivity.getStringExtra("userId")).isEqualTo("16");

    UserActivity userActivity =
        Robolectric.buildActivity(UserActivity.class).withIntent(nextActivity).create()
            .visible().get();

    TextView userInfo = (TextView) userActivity.findViewById(R.id.user);
    assertThat(userInfo).isNotNull();
    assertThat(userInfo.getText()).isEqualTo(activity.getString(R.string.user_info, 16, 0));
  }

  @Test public void verifyOpenManually() {
    MainActivity activity = Robolectric.setupActivity(MainActivity.class);
    TextView openManually = (TextView) activity.findViewById(R.id.open_manually);
    assertThat(openManually).isNotNull();

    openManually.performClick();
    Intent nextActivity = shadowOf(activity).getNextStartedActivity();
    assertThat(nextActivity).isNotNull();
    assertThat(nextActivity.getComponent()).isNotNull();
    assertThat(nextActivity.getComponent().getClassName()).isEqualTo(UserActivity.class.getName());
    assertThat(nextActivity.getExtras()).isNotNull();

    Bundle userBundle = nextActivity.getBundleExtra("user");
    assertThat(userBundle).isNotNull();
    assertThat(userBundle.getInt("userId")).isEqualTo(10);

    Bundle portfolioBundle = nextActivity.getBundleExtra(PortfolioId.class.getName());
    assertThat(portfolioBundle).isNotNull();
    assertThat(portfolioBundle.getInt("portfolioId")).isEqualTo(20);

    UserActivity userActivity =
        Robolectric.buildActivity(UserActivity.class).withIntent(nextActivity).create()
            .visible().get();

    TextView userInfo = (TextView) userActivity.findViewById(R.id.user);
    assertThat(userInfo).isNotNull();
    assertThat(userInfo.getText()).isEqualTo(activity.getString(R.string.user_info, 10, 20));
  }
}