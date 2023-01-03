package com.said.emergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SMS extends AppCompatActivity implements LocationListener {
    protected LocationManager locmanager;
    Location location;
    String locale;
    double Lat;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean loc = true;//location permission is true
        boolean phone = true;//call permission is true
        boolean sm = true;//sms permission is true
        //The last 2 boolean variables are used to confirm if the user is active
        boolean num = true;//Used to check if there are phone numbers connected to the account
        boolean act = true;//Checks if the account is active

        //instantiates android location service
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //opens the database
        SQLiteDatabase mydb = openOrCreateDatabase("Panic.db", MODE_PRIVATE, null);

        //checks if any accounts are active in the active table
        Cursor active = mydb.rawQuery("SELECT * FROM Account;", null);
        active.moveToFirst();

        if (active.getCount() == 0)
            act = false;

        if(act) {
            String id = active.getString(0);//We are storing the account ID that is active

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                loc = false;
                // check if the coarse and fine location permissions were given

            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                sm = false;//check if the SEND SMS permission was given

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                phone = false;//check if the CALL PHONE PERMISSION was given


            Cursor numbers = mydb.rawQuery("SELECT * FROM Emergency_Contact;", null);
            numbers.moveToFirst();


            if (numbers.getCount() == 0)
                num = false;//check if there are numbers in the emergency numbers table


            SmsManager smsManager = SmsManager.getDefault();//instantiates the sms manager to prepare to send an sms

            setContentView(R.layout.activity_sms);
            String result = "";
            TextView display = (TextView) findViewById(R.id.textView3);
            if (loc) {
                locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);//This piece of code calls the onlocationchanged method in order to change the given location
                location = locmanager.getLastKnownLocation(locmanager.GPS_PROVIDER);// this gets the last known location
                Geocoder geo = new Geocoder(getBaseContext(), Locale.getDefault());//This instantiates the geocoder in order to perform reverse geocoding

                try {
                    if(location != null){
                        List<Address> address = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);//This translates the latitude and longitude of the location into a physical address
                        //and stores it in the Array List address
                        if (address.size() > 0) {
                            Address add = address.get(0);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < add.getMaxAddressLineIndex(); i++) {
                                sb.append(add.getAddressLine(i)).append("\n");
                            }
                            //translates the given address into a string format only extract the necessary information

                            sb.append(add.getFeatureName()).append("\n");
                            sb.append(add.getThoroughfare()).append("\n");
                            sb.append(add.getSubAdminArea()).append("\n");
                            sb.append(add.getAdminArea()).append("\n");
                            sb.append(add.getSubLocality()).append("\n");
                            sb.append(add.getLocality()).append("\n");
                            sb.append(add.getPostalCode()).append("\n");
                            sb.append(add.getCountryName());
                            result = sb.toString() +"\n"+ locale;
                        }
                    }
                    else
                        result = "no location";


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //displays an error message if the location permission was not provided
                Toast toast = Toast.makeText(getApplicationContext(), "Please provide the application with permission to access your phones location", Toast.LENGTH_SHORT);
                toast.show();
                result = "no location";
            }

            if (!sm) {
                //displays an error message if the SMS permission was not provided
                Toast toast = Toast.makeText(getApplicationContext(), "Please provide the application with permission to access your phones SMS application", Toast.LENGTH_SHORT);
                toast.show();
            } else if(!num) {
                //displays an error message if there are no numbers associated with the active account
                Toast toast = Toast.makeText(getApplicationContext(), "There are no numbers assiciated with this account", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                for (int i = 0; i < numbers.getCount(); i++){
                    smsManager.sendTextMessage(numbers.getString(1), null, "SOS " + result, null, null);
                    numbers.moveToNext();

                }
                                    //sends a SMS with the smsManager to the 5 provided emergency numbers
            }

            if (phone) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:1011"));//change the number
                startActivity(callIntent);
                //sends a call to the provided number
            } else {
                //displays an error message if the call permission was not provided
                Toast toast = Toast.makeText(getApplicationContext(), "Please provide the application with permission to access your phones calling application", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
            //places a call if there is no account actively logged in when the button is pressed
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                phone = false;
            if (phone) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:1011"));//change the number
                startActivity(callIntent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please provide the application with permission to access your phones calling application", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }



    //this is an abstract class of the LocationListener interface and is used to change the location when the users location changes
    @Override
    public void onLocationChanged(Location location){
        locale = "" + location.getLatitude() + " " + location.getLongitude();

    }
    //the below pieces of code ensure the program doesn't error out when using the onlocationChanged method
    //in case the location provider was disabled or the status was changed
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


}