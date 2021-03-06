package pt.ipleiria.dei.iair.US8;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import pt.ipleiria.dei.iair.MasterTest;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.view.DashboardActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US8_AT5 extends MasterTest{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    @Test
    public void uS8_AT5() {
        final DashboardActivity activity = mActivityTestRule.getActivity();
        try {
            onView(isRoot()).perform(waitId(R.id.menu_dashboard, TimeUnit.SECONDS.toMillis(10)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        onView(withText(R.string.No_data_message)).inRoot(withDecorView(not(is(getCurrentActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

}
