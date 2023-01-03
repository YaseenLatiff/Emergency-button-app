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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link edit_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class edit_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public edit_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment edit_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static edit_Fragment newInstance(String param1, String param2) {
        edit_Fragment fragment = new edit_Fragment();
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
    String Contact;
    ArrayAdapter<String> adapt;
    ListView Group;
    private static final int READ_PERMISSION_CODE = 100;
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getActivity(), add_nums.class));
                getActivity().finish();

            }
            else
                Toast.makeText(getActivity().getApplicationContext(), "Please provide us with the appropriate permissions to read your contacts list in order to be able to add contacts to your emergency contacts list", Toast.LENGTH_SHORT).show();

        }
        else {
            startActivity(new Intent(getActivity(), add_nums.class));
            getActivity().finish();
            Toast.makeText(getActivity().getApplicationContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SQLiteDatabase mydb = getActivity().getBaseContext().openOrCreateDatabase("Panic.db", MODE_PRIVATE, null);
        ArrayList<String> list = new ArrayList<>();
        Cursor curse = mydb.rawQuery("SELECT * FROM Emergency_Contact",null);
        curse.moveToFirst();



        int k = curse.getCount();
        if(k > 0) {
            for (int i = 0; i < k; i++) {
                list.add(curse.getString(0));
                curse.moveToNext();
            }
        }

        View vue = inflater.inflate(R.layout.fragment_edit_, container, false);
            Group = vue.findViewById(R.id.lstPanic);
            adapt = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.listsize, list);
            Group.setAdapter(adapt);



        FloatingActionButton add = vue.findViewById(R.id.btnAdd);
        FloatingActionButton delete = vue.findViewById(R.id.btnDelete);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PackageManager manager = getActivity().getPackageManager();
                checkPermission(Manifest.permission.READ_CONTACTS, READ_PERMISSION_CODE);
            }
        });
        Group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact = Group.getItemAtPosition(position).toString();
            }
        });
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                adapt.remove(Contact);
                Group.setAdapter(adapt);
                mydb.execSQL("DELETE FROM Emergency_Contact WHERE Name = '"+Contact+"';");

            }
        });

        return vue;
    }

}