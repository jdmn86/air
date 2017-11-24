package pt.ipleiria.dei.iair.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.InformativeMessageType;
import pt.ipleiria.dei.iair.model.Location;

public class CreateInformativeMessageActivity extends GetVinicityActivity {

    private EditText editTextTimestampCreateInformativeMessage;
    int mYear,mMonth,mDay,mHour,mMinute;
    static final int PICK_LOCATION_REQUEST = 1;  // The request code
    private EditText editTextLocation;
    private TextView textViewLocation;
    private TextView textViewTimestamp;
    private Button buttonCancel;
    private ImageView imageGetMyLocation;
    private Location currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_informative_message);
        IAirManager.INSTANCE.setCreateInformativeMessageActivity(this);
        buttonCancel = findViewById(R.id.buttonCancelCreateInformativeMessage);
        textViewLocation = findViewById(R.id.textViewLocationCreateInformativeMessage);
        textViewTimestamp = findViewById(R.id.textViewTimestamp);
        final Button buttonSelectDateTime = findViewById(R.id.buttonSelectDateTimeCreateInformativeMessage);
        final SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        final String format = s.format(new Date());
        final Button buttonMap = findViewById(R.id.buttonMapCreateInformativeMessage);
        final Button buttonSave = findViewById(R.id.buttonSaveCreateInformativeMessage);
        final Spinner spinnerTypes = findViewById(R.id.spinnerInformativeMesageTypes);
        final EditText editTextDescription = findViewById(R.id.editTextDescriptionCreateInformativeMessage);
        final TextView textViewDescription = findViewById(R.id.textViewDescription);
        editTextLocation = findViewById(R.id.editTextLocationCreateInformativeMessage);
        imageGetMyLocation = findViewById(R.id.imageGetMyLocationCreateInformativeMessage);


        if(!getMyLocation()){
            Toast.makeText(this,"Error Getting Actual Location", Toast.LENGTH_SHORT);
        }

        editTextTimestampCreateInformativeMessage= findViewById(R.id.editTextTimestampCreateInformativeMessage);
        editTextTimestampCreateInformativeMessage.setText(format);
        spinnerTypes.setAdapter(new ArrayAdapter<InformativeMessageType>(this, android.R.layout.simple_spinner_item, InformativeMessageType.values()));
        buttonSelectDateTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePicker();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextDescription.getText().length()<20){
                    textViewDescription.setTextColor(Color.RED);
                    textViewDescription.setText("Description: Please insert a description (at least 20 characters)");
                }
                if (editTextLocation.getText().length()==0 || currentLocation.getLatitude() == null || currentLocation.getLongitude() == null){
                    textViewLocation.setTextColor(Color.RED);
                    textViewLocation.setText("Location: Please Insert A Valid Location");
                }
                if (editTextTimestampCreateInformativeMessage.getText().length()==0){
                    textViewTimestamp.setTextColor(Color.RED);
                    textViewTimestamp.setText("Timestamp: Please Insert A Valid Timestamp");
                }
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent pickLocation = new Intent( getApplicationContext() , MapActivity.class );
                //pickLocation.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                pickLocation.putExtra("SEND_LOCATION_REQUEST", 2);
                startActivityForResult(pickLocation, PICK_LOCATION_REQUEST);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageGetMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!getMyLocation()){
                    Toast.makeText(getApplicationContext(),"Error Getting Actual Location", Toast.LENGTH_SHORT);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                currentLocation.setLatitude(data.getDoubleExtra("latitude",0.0));
                currentLocation.setLongitude(data.getDoubleExtra("longitude",0.0));
                currentLocation.setLocationName(data.getStringExtra("locationName"));
                editTextLocation.setText(currentLocation.getLocationName());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("fail");
            }
        }
    }//onActivityResult

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.menu_dashboard) {
            intent = new Intent(this, DashboardActivity.class);

        } else if (id == R.id.menu_my_sensors) {
            intent = new Intent(this, MySensorsActivity.class);

        } else if (id == R.id.menu_create_message) {
            intent = new Intent(this, CreateInformativeMessageActivity.class);

        } else if (id == R.id.menu_map) {
            intent = new Intent(this, MapActivity.class);

        } else if (id == R.id.menu_locations) {
            intent = new Intent(this, LocationActivity.class);

        }else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);

        } else if (id == R.id.menu_gps) {
            enableGPS();

        }

        if (intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mYear=year;
                        mMonth=monthOfYear+1;
                        mDay=dayOfMonth;
                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                        mMinute=minute;
                        mHour=hourOfDay;

                        GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
                        String month=String.format("%02d",calendar.get(Calendar.MONTH));
                        String day=String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));
                        String year=String.format("%02d",calendar.get(Calendar.YEAR));
                        String hours=String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
                        String minutes=String.format("%02d",calendar.get(Calendar.MINUTE));

                        editTextTimestampCreateInformativeMessage.setText(day + "/" + month + "/" + year
                                + " " + hours + ":" + minutes);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }


    public boolean getMyLocation() {
        enableGPS();
        GPSUtils locationTrack = new GPSUtils(getApplicationContext());;
        if (locationTrack.getLocation()!= null) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            LatLng latLng = new LatLng(latitude, longitude);

            getVicinity(latLng, 4000);
            return true;
        }
        return false;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        editTextLocation.setText(currentLocation.getLocationName());
    }
}
