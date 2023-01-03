package com.said.emergency;

import static android.content.Context.MODE_PRIVATE;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.ContentValues;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Login_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Login_Fragment newInstance(String param1, String param2) {
        Login_Fragment fragment = new Login_Fragment();
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
    String careNumber;
    String careName;
    String sName,sLastName, sId, sCode, key;
    Boolean pat, id, code;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        SQLiteDatabase mydb = getActivity().getBaseContext().openOrCreateDatabase("Panic.db", MODE_PRIVATE, null);;
        //creating the Active table

        View vue = inflater.inflate(R.layout.fragment_login_, container, false);

        //mydb.execSQL("INSERT INTO Accounts(Account_ID,Username) VALUES(1,'Yas')");
        //mydb.execSQL("INSERT INTO Active(Account_ID) VALUES(1)");
        EditText edtUsername = (EditText) vue.findViewById(R.id.edtUsername);

        EditText edtLastName = vue.findViewById(R.id.edtLastName);
        EditText edtID = (EditText) vue.findViewById(R.id.edtNumber);

        EditText edtCode = vue.findViewById(R.id.edtNumber2);

        Button btnLogin = (Button) vue.findViewById(R.id.btnLogin2);
        boolean empty = false;
        if(edtUsername.getText().length() == 0)
            empty = true; //checking the the Username edit box is empty



        boolean is_not_Empty = empty;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pat = true;
                id = true;
                code = true;
                sName = edtUsername.getText().toString();
                sLastName = edtLastName.getText().toString();
                sId = edtID.getText().toString();
                sCode = edtCode.getText().toString();

                db.child("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot shot) {
                        for (DataSnapshot snap : shot.getChildren()) {
                            if (snap.getKey().toString().equals(edtCode.getText().toString())) {
                                code = true;
                                if (snap.child("Patient").getValue().toString().equals("None")) {
                                    pat = true;
                                    id = true;
                                    careNumber = snap.child("Number").getValue().toString();
                                    careName = snap.child("Name").getValue().toString();
                                    key = snap.getKey().toString();


                                }
                                else{
                                    if(snap.child("Patient").getValue().toString().toUpperCase().equals(edtUsername.getText().toString().toUpperCase()+" "+edtLastName.getText().toString().toUpperCase())){
                                        pat = true;
                                        if(snap.child("Patient ID").getValue().toString().equals(edtID.getText().toString())){
                                            id = true;
                                            careNumber = snap.child("Number").getValue().toString();
                                            careName = snap.child("Name").getValue().toString();
                                            key = snap.getKey().toString();

                                        }
                                        else{
                                            id = false;
                                        }
                                    }
                                    else{
                                        pat = false;
                                    }
                                }
                            }
                            else{
                                code = false;
                            }

                        }
                        if((pat) && (id) && (!code)){
                            Hashtable usr = new Hashtable();

                            usr.put("Patient", edtUsername.getText().toString() + " " + edtLastName.getText().toString());
                            usr.put("Patient ID", edtID.getText().toString());
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), key, Toast.LENGTH_SHORT);
                            toast.show();
                            db.child("User").child(key).updateChildren(usr);
                            mydb.execSQL("INSERT INTO Emergency_Contact(Name, Emergency_Number) VALUES('" + careName + "', '"+careNumber+"');");
                            mydb.execSQL("INSERT INTO Account(Name) VALUES('" + sName + "');");
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }
                        else if(code){
                            Toast.makeText(getActivity().getBaseContext(), "The unique code is incorrect", Toast.LENGTH_SHORT).show();
                            edtCode.setBackgroundColor(Color.RED);
                            edtUsername.setBackgroundColor(Color.WHITE);
                            edtLastName.setBackgroundColor(Color.WHITE);
                            edtID.setBackgroundColor(Color.WHITE);
                        }
                        else if(!pat){
                            edtCode.setBackgroundColor(Color.RED);
                            edtUsername.setBackgroundColor(Color.WHITE);
                            edtLastName.setBackgroundColor(Color.WHITE);
                            edtID.setBackgroundColor(Color.WHITE);
                            Toast.makeText(getActivity().getBaseContext(), "Name is wrong", Toast.LENGTH_SHORT).show();
                        }
                        else if(!id){
                            edtCode.setBackgroundColor(Color.WHITE);
                            edtUsername.setBackgroundColor(Color.WHITE);
                            edtLastName.setBackgroundColor(Color.WHITE);
                            edtID.setBackgroundColor(Color.RED);
                            Toast.makeText(getActivity().getBaseContext(), "ID number is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return vue;

    }
}