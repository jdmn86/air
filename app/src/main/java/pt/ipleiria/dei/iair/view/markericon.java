package pt.ipleiria.dei.iair.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.view.MapActivity;

/**
 * TODO: document your custom view class.
 */
public class markericon extends RelativeLayout {

    private String temp;
    private String press;
    private String hum;

    private TextView texVtemp;
    private TextView textVpress;
    private TextView textvhum;

    public markericon(Context context, String temp, String press, String hum) {
        super(context);
        this.temp = temp;
        this.press = press;
        this.hum = hum;

        initViews(context);
    }


    private void initViews(Context context) {

        //TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
          //      R.styleable.markericon, 0, 0);

        try {
            // get the text and colors specified using the names in attrs.xml
                    //temp = a.getString(R.styleable.markericon_temp);
                    //press = a.getString(R.styleable.markericon_press);
                    //hum = a.getString(R.styleable.markericon_hum);
            //leftStyle = a.getResourceId(R.styleable.markericon_press, android.R.style.TextAppearance_DeviceDefault);
            //rightStyle = a.getResourceId(R.styleable.markericon_hum, android.R.style.TextAppearance_DeviceDefault);
            inflate(getContext(), R.layout.sample_markericon, this);

            this.texVtemp = (TextView)findViewById(R.id.textViewtemp);
            this.texVtemp.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            this.textVpress = (TextView)findViewById(R.id.textViewpress);
            this.textVpress.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            this.textvhum = (TextView) findViewById(R.id.textViewhum);
            this.textvhum.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            //this.icon = (ImageView)findViewById(R.id.icon);

            texVtemp.setText(temp);
            textVpress.setText(press);
            textvhum.setText(hum);


        } finally {

        }

    }


}
