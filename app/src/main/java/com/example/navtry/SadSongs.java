package com.example.navtry;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SadSongs extends AppCompatActivity {


    Context context;



    String[] ListElements = new String[]{};

    ListView listView;

    List<String> ListElementsArrayList;

    ArrayAdapter<String> adapter;

    ContentResolver contentResolver;

    Cursor cursor;

    Uri uri;
    public static final int RUNTIME_PERMISSION_CODE = 7;

    Button button;
    EditText s1;


    HashMap<String, String> SongD;

    DataBaseHelper db;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        s1 = (EditText)findViewById(R.id.srch);

        SongD = new HashMap<String, String>();

        listView = (ListView) findViewById(R.id.listView);


        context = getApplicationContext();

        ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));

        Collections.sort(ListElementsArrayList);

        adapter = new ArrayAdapter<String>
                (SadSongs.this, android.R.layout.simple_list_item_checked, ListElementsArrayList);


        GetAllMediaMp3Files();

        listView.setAdapter(adapter);

        db = new DataBaseHelper(this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Toast.makeText(Songs.this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();

                String SongMessage = SongD.get(parent.getAdapter().getItem(position).toString());
                Intent intent = new Intent(SadSongs.this, SongPlay.class);
                intent.putExtra("SongData", SongMessage);
                startActivity(intent);

            }
        });
        s1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SadSongs.this.adapter.getFilter().filter(charSequence);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void GetAllMediaMp3Files() {

        contentResolver = context.getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //String selection = MediaStore.Audio.Media +"like?";
        String[] STAR={"*"};
        cursor = contentResolver.query(
                uri,
                STAR,
                MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%Sad%"},
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(SadSongs.this, "Something Went Wrong.", Toast.LENGTH_SHORT);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(SadSongs.this, "No Music Found on5 SD Card.", Toast.LENGTH_SHORT);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int Data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);



            do {

                String SongTitle = cursor.getString(Title);
                String SongData = cursor.getString(Data);

                ListElementsArrayList.add(SongTitle);
                SongD.put(SongTitle , SongData);


            } while (cursor.moveToNext());
        }
    }





}













