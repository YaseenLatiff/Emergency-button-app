package com.said.emergency;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Welcome_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Welcome_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Welcome_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Welcome_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Welcome_Fragment newInstance(String param1, String param2) {
        Welcome_Fragment fragment = new Welcome_Fragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        SQLiteDatabase mydb = getActivity().getBaseContext().openOrCreateDatabase("Panic.db", MODE_PRIVATE, null);;
        //creating the Active table
        mydb.execSQL("CREATE TABLE IF NOT EXISTS Active(Account_ID INTEGER PRIMARY KEY ,Is_Active Boolean);");
        //creating the Accounts table
        mydb.execSQL("CREATE TABLE IF NOT EXISTS Account(Name VARCHAR);");
        //creating the emergency numbers table
        mydb.execSQL("CREATE TABLE IF NOT EXISTS Emergency_Contact(Name VARCHAR ,Emergency_Number VARCHAR);");
        mydb.execSQL("CREATE TABLE IF NOT EXISTS PhoneNumbers(Name_ID INTEGER PRIMARY KEY ,Name VARCHAR, Number VARCHAR);");

        Cursor cuss = mydb.rawQuery("SELECT * FROM Account",null);
        cuss.moveToFirst();
        if(cuss.getCount()>0){
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
        View vue = inflater.inflate(R.layout.fragment_welcome_, container, false);
        Button start = vue.findViewById(R.id.btnStart);

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Login_Fragment log = new Login_Fragment();
                FragmentManager fragman = getActivity().getSupportFragmentManager();
                FragmentTransaction fragtranact = fragman.beginTransaction();
                fragtranact.replace(R.id.nav_fragment,log);
                fragtranact.commit();
            }
        });

        return vue;
    }

}