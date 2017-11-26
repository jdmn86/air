package pt.ipleiria.dei.iair.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;

public class LocationDetailsActivity extends AppCompatActivity {
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        bindLayoutElements();
        populateGraphs();
    }

    private void populateGraphs() {
        ThinkSpeak.INSTANCE.getData(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                System.out.println(response.toString());
                JSONArray feeds = response.getJSONArray("feeds");
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{

                });

                for (int i = 0; i < feeds.length(); i++) {
                    JSONObject feed = feeds.getJSONObject(i);
                    series.appendData(new DataPoint(Integer.getInteger(feed.getString("created_at").split("-", 0)[2].split("T", 2)[0]), feed.getDouble("field1")) ,true, 0);
                }
                graph.addSeries(series);

            }

            @Override
            public void onResult(String response) {

            }
        }, this, getIntent().getStringExtra("locationName"));
    }


    private void bindLayoutElements() {
        graph = (GraphView) findViewById(R.id.graphAirQuality);
    }
}
