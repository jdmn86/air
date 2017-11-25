package pt.ipleiria.dei.iair.US8;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import pt.ipleiria.dei.iair.MasterTest;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.view.DashboardActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static junit.framework.Assert.fail;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US8_AT1  extends MasterTest{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    @Test
    public void uS8_AT1() {
        ArrayList<Pair<String, String>> data = new ArrayList<>();
        data.add(new Pair<>("api_key", "DWVBH01AMQV6OCLR"));
        data.add(new Pair<>("name", "test_proposes_only"));
        data.add(new Pair<>("field1", "12"));
        data.add(new Pair<>("field2", "24"));
        data.add(new Pair<>("field3", "36"));

        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                HttpUtils.Get(new HttpCallBack() {
                    @Override
                    public void onResult(JSONObject response) throws JSONException {
                        JSONArray feeds = response.getJSONArray("feeds");
                        if(feeds.length() == 0)
                            fail();
                        JSONObject elem = (JSONObject) feeds.get(feeds.length()-1);
                        if(!(elem.getString("field1").equals("12") && elem.getString("field2").equals("24") && elem.getString("field3").equals("36"))) {
                            fail("not working because" + elem.toString());
                        }
                    }

                    @Override
                    public void onResult(String response) {

                    }
                }, "https://api.thingspeak.com/channels/365362/feeds.json?api_key=XE5Z8ARPRY2WY62U&results=2", getCurrentActivity());


            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/update.json?api_key=DWVBH01AMQV6OCLR&field1=12", data, getCurrentActivity());
        try {
            onView(isRoot()).perform(waitId(R.id.menu_dashboard, TimeUnit.SECONDS.toMillis(10)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
