package com.example.kerbe.findd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Vol extends AppCompatActivity {
    ListView list_vol;
    ArrayList<String> a = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol);
        a.add("man");
        a.add("man age: 28");
        a.add("man");
        a.add("man");

        list_vol = (ListView)   findViewById(R.id.list_vol);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.vol_item, R.id.text_vol, a);
        list_vol.setAdapter(aa);
        list_vol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(Vol.this, MainActivity.class);
                startActivity(in);
            }
        });
    }
}
