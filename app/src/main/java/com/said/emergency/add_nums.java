package com.said.emergency;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class add_nums extends AppCompatActivity {
    String phoneNo, name, id;
    String Contact = "";
    Boolean c;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase mydb = this.openOrCreateDatabase("Panic.db", MODE_PRIVATE, null);
        PackageManager manager = getPackageManager();
        int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.your.package");

        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> numbers = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            mydb.execSQL("DELETE FROM PhoneNumbers");
            while ((cur != null) && cur.moveToNext()) {
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mydb.execSQL("INSERT INTO PhoneNumbers(Name_ID,Name,Number) VALUES('" + id + "', '" + name + "', '" + phoneNo + "');'");
                        list.add(name);
                        numbers.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        setContentView(R.layout.activity_add_nums);
        ListView Group = findViewById(R.id.lstContacts);
        ArrayAdapter<String> adapt = new ArrayAdapter(add_nums.this, R.layout.listsize, list);
        Group.setAdapter(adapt);
        int count = list.size();
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        Group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact = Group.getItemAtPosition(position).toString();
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cuss = mydb.rawQuery("SELECT * FROM PhoneNumbers WHERE Name = '" + Contact + "';", null);
                cuss.moveToFirst();
                if (cuss.getCount() != 0) {
                    mydb.execSQL("INSERT INTO Emergency_Contact (Name, Emergency_Number) VALUES('" + cuss.getString(1) + "', '" + cuss.getString(2) + "');");
                    Toast.makeText(add_nums.this, "Contact added", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }





    public void onPause(){
        super.onPause();
        startActivity(new Intent(add_nums.this, Welcome.class));
        finish();

    }
}