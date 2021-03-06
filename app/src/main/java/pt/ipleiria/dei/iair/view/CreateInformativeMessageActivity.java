package pt.ipleiria.dei.iair.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.CityAssociation;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.model.InformativeMessageType;
import pt.ipleiria.dei.iair.model.Location;

public class CreateInformativeMessageActivity extends GetVinicityActivity {

    private EditText editTextTimestampCreateInformativeMessage;
    int mYear, mMonth, mDay, mHour, mMinute;
    static final int PICK_LOCATION_REQUEST = 1;  // The request code
    private Spinner spinnerLocations;
    private TextView textViewLocation;
    private TextView textViewTimestamp;
    private TextView textViewcharacterCount;
    private Button buttonCancel;
    private ImageView imageGetMyLocation;
    private Location currentLocation;
    private ArrayAdapter<String> adapter;
    private Spinner spinner;
    private Spinner spinner1;
    private ArrayAdapter<String> adapter1;
    private EditText editTextDescriptionCreateInformativeMessage;
    private Location auxLocation;

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
        spinnerLocations = findViewById(R.id.spinnerLocations);
        imageGetMyLocation = findViewById(R.id.imageGetMyLocationCreateInformativeMessage);
        textViewcharacterCount = (TextView) findViewById(R.id.textViewCreateInformativeMessageCharacterCount);

        if (!getMyLocation()) {
            Toast.makeText(this, "Error Getting Actual Location", Toast.LENGTH_SHORT);
        }

        editTextTimestampCreateInformativeMessage = findViewById(R.id.editTextTimestampCreateInformativeMessage);
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
                boolean flag = true;
                if (editTextDescription.getText().length() < 5) {
                    textViewDescription.setTextColor(Color.RED);
                    textViewDescription.setText("Description: Please insert a description (at least 20 characters)");
                    flag = false;
                }
                if (spinnerLocations.getAdapter().getCount() == 0 || spinnerLocations.getSelectedItem().toString().length()==0){
                    textViewLocation.setTextColor(Color.RED);
                    textViewLocation.setText("Location: Please Insert A Valid Location");
                    flag = false;
                }
                if (editTextTimestampCreateInformativeMessage.getText().length() == 0) {
                    textViewTimestamp.setTextColor(Color.RED);
                    textViewTimestamp.setText("Timestamp: Please Insert A Valid Timestamp");
                    flag = false;
                }
                if (flag) {
                    Alerts alert = new Alerts(spinner.getSelectedItem().toString(), spinner1.getSelectedItem().toString(),
                            editTextDescription.getText().toString(), editTextTimestampCreateInformativeMessage.getText().toString());
                    ThinkSpeak.INSTANCE.insertInAlerts(alert, getApplicationContext());

                    Toast.makeText(CreateInformativeMessageActivity.this, "THE alert was send", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent pickLocation = new Intent(getApplicationContext(), MapActivity.class);
                pickLocation.putExtra("SEND_LOCATION_REQUEST", 2);
                startActivityForResult(pickLocation, PICK_LOCATION_REQUEST);
            }
        });
        editTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewcharacterCount.setText(count + " Characters");
                if (count < 5 || count > 160)
                    textViewcharacterCount.setTextColor(Color.RED);
                else
                    textViewcharacterCount.setTextColor(Color.GREEN);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinner = (Spinner) findViewById(R.id.spinnerLocations);
        java.util.ArrayList<String> strings = new java.util.ArrayList<>();
        System.out.println("tamanho:" + IAirManager.INSTANCE.getAllCityAssociations().size());

        for (CityAssociation city : IAirManager.INSTANCE.getAllCityAssociations()) {
            strings.add(city.getREGION_NAME());
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, strings);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner1 = (Spinner) findViewById(R.id.spinnerInformativeMesageTypes);
        java.util.ArrayList<String> strings1 = new java.util.ArrayList<>();
        strings1.add("fire");
        strings1.add("rain");
        strings1.add("wind");
        strings1.add("gas_leak");
        strings1.add("other");

        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, strings1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageGetMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!getMyLocation()) {
                    Toast.makeText(getApplicationContext(), "Error Getting Actual Location", Toast.LENGTH_SHORT);
                } else {
                    if (IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getCurrentLocationName()) == null) {

                        adapter.add(IAirManager.INSTANCE.getCurrentLocationName());
                        for (int position = 0; position < adapter.getCount(); position++) {
                            if (adapter.getItem(position).equalsIgnoreCase(IAirManager.INSTANCE.getCurrentLocationName())) {

                                ThinkSpeak.INSTANCE.createNewChannel(IAirManager.INSTANCE.getCurrentLocationName(),
                                        String.valueOf(IAirManager.INSTANCE.getCurrentLocation().latitude),
                                        String.valueOf(IAirManager.INSTANCE.getCurrentLocation().longitude), getApplicationContext());
                                spinner.setSelection(position);

                                return;
                            }
                        }
                    } else {
                        for (int position = 0; position < adapter.getCount(); position++) {
                            if (adapter.getItem(position).equalsIgnoreCase(IAirManager.INSTANCE.getCurrentLocationName())) {
                                spinner.setSelection(position);

                                return;
                            }

                        }
                    }
                }
            }
        });
        Intent intent = getIntent();
        spinnerLocations.setSelection(intent.getIntExtra("listPosition", 0));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                auxLocation = new Location();
                auxLocation.setLatitude(data.getDoubleExtra("latitude", 0.0));
                auxLocation.setLongitude(data.getDoubleExtra("longitude", 0.0));
                auxLocation.setLocationName(data.getStringExtra("locationName"));
                String locationName = data.getStringExtra("locationName");

                if (IAirManager.INSTANCE.getCityAssociation(locationName) == null) {

                    ThinkSpeak.INSTANCE.createNewChannel(locationName, auxLocation.getLatitude().toString(),
                            auxLocation.getLongitude().toString(), this);

                    adapter.add(locationName);
                    for (int position = 0; position < adapter.getCount(); position++) {
                        if (adapter.getItem(position).equalsIgnoreCase(locationName)) {
                            spinner.setSelection(position);
                            return;
                        }
                    }
                } else {
                    for (int position = 0; position < adapter.getCount(); position++) {
                        if (adapter.getItem(position).equalsIgnoreCase(auxLocation.getLocationName())) {
                            spinner.setSelection(position);

                            return;
                        }

                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("fail  ");
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

        } else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);

        } else if (id == R.id.menu_gps) {
            enableGPS();

        }else if (id == R.id.menu_send_data) {
            CityAssociation city = IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getCurrentLocationName().toString());

            String temp = IAirManager.INSTANCE.getTemperature();
            String press = IAirManager.INSTANCE.getPresure();
            String hum = IAirManager.INSTANCE.getHumity();

            if (city != null) {

                pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(temp, press, hum, city.getREGION_NAME(),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().latitude),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().longitude));
                //channel=IAirManager.INSTANCE.getChannel(local);
                ThinkSpeak.INSTANCE.insertInChannel(channel, this);
                Toast.makeText(this, "The sensors data was send", Toast.LENGTH_LONG).show();
            }
        }
        if (intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mMinute = minute;
                        mHour = hourOfDay;

                        GregorianCalendar calendar = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute);
                        String month = String.format("%02d", calendar.get(Calendar.MONTH));
                        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
                        String year = String.format("%02d", calendar.get(Calendar.YEAR));
                        String hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
                        String minutes = String.format("%02d", calendar.get(Calendar.MINUTE));

                        editTextTimestampCreateInformativeMessage.setText(day + "/" + month + "/" + year
                                + " " + hours + ":" + minutes);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public boolean getMyLocation() {

        GPSUtils locationTrack = new GPSUtils(getApplicationContext());
        if (locationTrack.getLocation() != null) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            LatLng latLng = new LatLng(latitude, longitude);

            getVicinity(latLng, 4000);
            return true;
        }
        return false;
    }

}
