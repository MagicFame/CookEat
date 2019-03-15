package com.tilted.magicfame.cookeat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final static String API_KEY = "93e5b880b602c4df65119c7236a61fe7";
    private final static String API_ID = "71b510d1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onClick(View v){
        String research = ((EditText)(findViewById(R.id.editText2))).getText().toString();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            String APIURL = "https://api.edamam.com/search?app_id=" + API_ID + "&app_key="
                    + API_KEY + "&from=0&to=9&q=" + research;

            URL url = new URL(APIURL);
            System.out.println(url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.getInputStream();


        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }


}
