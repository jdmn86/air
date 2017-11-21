package pt.ipleiria.dei.iair.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;

public class CreateInformativeMessageActivity extends GPSActivity {

    EditText editTextTimestampCreateInformativeMessage;
    int mYear,mMonth,mDay,mHour,mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_informative_message);

        final Button buttonSelectDateTime = findViewById(R.id.buttonSelectDateTimeCreateInformativeMessage);
        final SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        final String format = s.format(new Date());

        editTextTimestampCreateInformativeMessage= findViewById(R.id.editTextTimestampCreateInformativeMessage);
        editTextTimestampCreateInformativeMessage.setText(format);

        buttonSelectDateTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePicker();
            }
        });
    }

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
                        mMonth=monthOfYear;
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

}
