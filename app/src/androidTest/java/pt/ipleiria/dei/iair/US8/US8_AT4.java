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
public class US8_AT4 extends MasterTest{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    @Test
    public void uS8_AT4() {

        ThinkSpeak.sendData(getCurrentActivity(), 39.039463, 125.763378, "23", "900", "0");
        //get Data Pyongyang

        try {
            onView(isRoot()).perform(waitId(R.id.menu_dashboard, TimeUnit.SECONDS.toMillis(10)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //fail(e.toString());
        }
ThinkSpeak.getData(new HttpCallBack() {

    @Override
    public void onResult(JSONObject response) throws JSONException {
        JSONArray feeds = response.getJSONArray("feeds");
        if(feeds.length() == 0)
            fail();
        JSONObject elem = (JSONObject) feeds.get(feeds.length()-1);
        if(!(elem.getString("field1").equals("23") && elem.getString("field2").equals("900") && elem.getString("field3").equals("0"))) {
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
