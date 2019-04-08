package com.tilted.magicfame.cookeat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra("id")){
                id = intent.getStringExtra("id");
            }

            TextView textView = (TextView) findViewById(R.id.tv1);
            textView.setText(id);
        }


    }


}
