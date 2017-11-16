package pt.ipleiria.dei.iair.US8;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ipleiria.dei.iair.MasterTest;
import pt.ipleiria.dei.iair.Utils.InternetUtils;
import pt.ipleiria.dei.iair.view.DashboardActivity;

import static junit.framework.Assert.fail;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US8_AT2 extends MasterTest{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    @Test
    public void uS8_AT2() {
        if(!InternetUtils.isNetworkConnected(getCurrentActivity())) {fail("No Internet"); }
    }

}
