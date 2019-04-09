package com.tilted.magicfame.cookeat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;

public class DetailActivity extends AppCompatActivity {

    private String id = "";
    private TextView textView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra("id")){
                id = intent.getStringExtra("id");
            }
            textView = (TextView) findViewById(R.id.tv1);
            textView.setText(id);
        }
        new FetchDetail().execute();
    }

    class FetchDetail extends AsyncTask<Void, Void, String>{
        private final static String API_KEY = "93e5b880b602c4df65119c7236a61fe7";
        private final static String API_ID = "71b510d1";
        String result = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void...voids){
            String APIURL = "https://api.edamam.com/search?r=" + id + "&app_id=" + API_ID +
                    "&app_key=" + API_KEY + "&from=0";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            System.out.println(APIURL);
            try{
                URL url = new URL(APIURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    result = stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }catch (IOException ie){
                System.out.println("Url error" + ie.getStackTrace());
            } finally {
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
            return null;
        }
        @Override
        protected void onPostExecute(String s){
            try{
                JSONArray jarray = new JSONArray(result);
                JSONObject jobject = jarray.getJSONObject(0);
                textView.setText(jobject.getString("label"));

            }catch (JSONException js){
                System.out.println("Error parsing");
            }
        }

    }


}
