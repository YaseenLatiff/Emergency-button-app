package com.said.emergency;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;


import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.database.sqlite.SQLiteDatabase;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link panic_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class panic_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public panic_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment panic_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static panic_Fragment newInstance(String param1, String param2) {
        panic_Fragment fragment = new panic_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    String panics;
    boolean loc;//location permission is true
    boolean phone;//call permission is true
    boolean sm;//sms permission is true
    //The last 2 boolean variables are used to confirm if the user is active
    boolean num ;//Used to check if there are phone numbers connected to the account
    TextView mess;


    private static final int CALL_CODE = 101;
    private static final int SMS_CODE = 103;
    private static final int COARSE_LOCATION_CODE = 102;
    private static final int FINE_LOCATION_CODE = 104;
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    TextView mes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View vue = inflater.inflate(R.layout.fragment_panic_, container, false);
        mes = vue.findViewById(R.id.txtMes);
        ImageButton panic = (ImageButton) vue.findViewById(R.id.btnPanic);
        panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    loc = false;

                }
                else{
                    loc = true;
                }

                // check if the coarse and fine location permissions were given
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    sm = true;//check if the SEND SMS permission was given
                }
                else{
                    sm = false;
                }


                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    phone = true;//check if the CALL PHONE PERMISSION was given
                }
                else{
                    phone = false;
                }
                if(loc){
                    if(sm){
                        if(phone){
                            startActivity(new Intent(getActivity(), SMS.class));
                            mes.setText("");
                        }
                        else{
                            checkPermission(Manifest.permission.CALL_PHONE, CALL_CODE);
                            Toast.makeText(getActivity().getBaseContext(), "Please give us the permission to make calls", Toast.LENGTH_SHORT).show();
                            mes.setText("Please press the button again as we could not send a message before because the call permission was not given");
                        }
                    }
                    else{
                        checkPermission(Manifest.permission.SEND_SMS, SMS_CODE);
                        Toast.makeText(getActivity().getBaseContext(), "Please give us the permission to send SMS's", Toast.LENGTH_SHORT).show();
                        mes.setText("Please press the button again as we could not send a message before because the SMS permission was not given");
                    }

                }
                else{
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_CODE);
                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
                    Toast.makeText(getActivity().getBaseContext(), "Please give us the permission to access your location", Toast.LENGTH_SHORT).show();
                    mes.setText("Please press the button again as we could not send a message before because the location permission was not given");
                }
            }
        });


        return vue;
    }



}