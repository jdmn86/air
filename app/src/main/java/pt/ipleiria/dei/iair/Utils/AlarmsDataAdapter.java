package pt.ipleiria.dei.iair.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.model.Alerts;

/**
 * Created by kxtreme on 01-12-2017.
 */

public class AlarmsDataAdapter extends ArrayAdapter<Alerts> {
    public AlarmsDataAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
    public AlarmsDataAdapter(Context context, int resource, List<Alerts> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_alerts_data, null);
        }

        Alerts p = getItem(position);

        if (p != null) {
            ImageView tt1 = (ImageView) v.findViewById(R.id.imageView_alarm_type);
            TextView tt3 = (TextView) v.findViewById(R.id.textView_alarmsData_Date);
            TextView tt2 = (TextView) v.findViewById(R.id.textView_alarms_description_data);

            if (tt1 != null) {
                if(p.getType().equals("fire"))
                tt1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.fire_icon));
                else if(p.getType().equals("rain"))
                    tt1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rain_icon));
                else if(p.getType().equals("wind"))
                    tt1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.wind_icon));
                else if(p.getType().equals("gas_leak"))
                    tt1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.gas_leak_icon));
                else if(p.getType().equals("other"))
                    tt1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.others_icon));

            }

            if (tt2 != null) {
                tt2.setText(p.getMessage());
            }

            if (tt3 != null) {
                tt3.setText(p.getTimeStamp());
            }
        }

        return v;
    }
}
