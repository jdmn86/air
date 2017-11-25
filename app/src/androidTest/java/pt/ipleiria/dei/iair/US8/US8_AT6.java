package pt.ipleiria.dei.iair.US8;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import pt.ipleiria.dei.iair.MasterTest;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.view.DashboardActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static junit.framework.Assert.fail;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US8_AT6 extends MasterTest{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    @Test
    public void uS8_AT6() {
        ThinkSpeak.INSTANCE.sendData(getCurrentActivity(), 39.039463, 125.763378, "80", null, null);
        try {
            onView(isRoot()).perform(waitId(R.id.menu_dashboard, TimeUnit.SECONDS.toMillis(10)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //fail(e.toString());
        }
        ThinkSpeak.INSTANCE.getData(new HttpCallBack() {

            @Override
            public void onResult(JSONObject response) throws JSONException {
                JSONArray feeds = response.getJSONArray("feeds");
                if(feeds.length() == 0)
                    fail();
                JSONObject elem = (JSONObject) feeds.get(feeds.length()-1);
                if(!(elem.getString("field1").equals("80") && elem.getString("field2").equals("N/A") && elem.getString("field3").equals("N/A"))) {
                    fail("not working because" + elem.toString());
                }
            }

            @Override
            public void onResult(String response) {

            }
        },  getCurrentActivity(),  39.039463, 125.763378);
        try {
            onView(isRoot()).perform(waitId(R.id.menu_dashboard, TimeUnit.SECONDS.toMillis(10)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

