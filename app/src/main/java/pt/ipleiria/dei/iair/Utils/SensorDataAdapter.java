package pt.ipleiria.dei.iair.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.model.Channel;

/**
 * Created by kxtreme on 01-12-2017.
 */

public class SensorDataAdapter extends ArrayAdapter<Channel> {
    public SensorDataAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
    public SensorDataAdapter(Context context, int resource, List<Channel> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_sensors_data, null);
        }

        Channel p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.textView_temperature_sensors_data);
            TextView tt2 = (TextView) v.findViewById(R.id.textView_humidity_sensors_data);
            TextView tt3 = (TextView) v.findViewById(R.id.textView_pressure_sensors_data);

            if (tt1 != null) {
                tt1.setText(p.getTemperature());
            }

            if (tt2 != null) {
                tt2.setText(p.getHumity());
            }

            if (tt3 != null) {
                tt3.setText(p.getPressure());
            }
        }

        return v;
    }
}
