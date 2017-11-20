package pt.ipleiria.dei.iair.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import pt.ipleiria.dei.iair.R;

/**
 * Created by kxtreme on 06-11-2017.
 */

public class GPSActivity extends AppCompatActivity {
    protected GoogleApiClient googleApiClient;
    public  Menu menu;
    protected void enableGPS() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext()) .addApi(LocationServices.API) .build(); googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create(); locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); locationRequest.setInterval(30 * 1000); locationRequest.setFastestInterval(5 * 1000); LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder() .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult result = LocationServices.SettingsApi .checkLocationSettings(googleApiClient, builder.build()); result.setResultCallback(

                    new ResultCallback()
                    {

                    @Override
                    public void onResult(Result result) {
                        Intent intent;

                        final Status status = result.getStatus();

                        final LocationSettingsStates state = ((LocationSettingsResult) result).getLocationSettingsStates();

                        switch (status.getStatusCode())
                        {

                            case LocationSettingsStatusCodes.SUCCESS:

                                break;

                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {

                                    status.startResolutionForResult(GPSActivity.this, 1000);



                                } catch (IntentSender.SendIntentException e)

                                {
                                    System.out.println(e.getMessage());
                                }

                                break;

                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                break;

                        }

                    }

                    });

                    googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( menu.findItem(R.id.menu_gps) != null && manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                menu.findItem(R.id.menu_gps).setVisible(false);
        }


        return true;
    }

}
