package pt.ipleiria.dei.iair.US8;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ipleiria.dei.iair.MasterTest;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.InternetUtils;
import pt.ipleiria.dei.iair.view.DashboardActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static junit.framework.Assert.fail;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
@LargeTest
@RunWith(AndroidJUnit4.class)
public class US8_AT2 extends MasterTest{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    @Test
    public void uS8_AT2() {
        try {
            mActivityTestRule.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    WifiManager wifi = (WifiManager) getCurrentActivity().getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(false);

                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        onView(withText(R.string.No_internet_message)).inRoot(withDecorView(not(is(getCurrentActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

}
