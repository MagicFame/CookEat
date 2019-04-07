package com.tilted.magicfame.cookeat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String API_KEY = "c784e0bb829e97e20b1731ef2bc263c1";
    private final static String API_ID = "71b510d1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button searchButton = findViewById(R.id.button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new FetchData().execute();
            }
        });
    }


    class FetchData extends AsyncTask<Void, Void, String> {

        String result = "";
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... voids) {
            String research = ((EditText) (findViewById(R.id.editText2))).getText().toString();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                String APIURL = "https://www.food2fork.com/api/search?key=" + API_KEY + "&sort=r&q=" + research ;

                URL url = new URL(APIURL);
                System.out.println(url);
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
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
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
        protected void onPostExecute(String s) {
            ListView listview = findViewById(R.id.lstview);

            try{
                JSONObject JasonObject = new JSONObject(result);
                int number = Integer.parseInt(JasonObject.getString("count"));
                System.out.println(number);
                JSONArray jarray = JasonObject.getJSONArray("recipes");
                List<String> l = new ArrayList<String>();
                if(number > 0) {
                    for (int i = 0; i < number; i++) {
                        l.add(jarray.getJSONObject(i).getString("title"));
                    }
                    ArrayAdapter<String> adapter = null;
                    adapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            l);

                    listview.setAdapter(adapter);
                }
            }catch (JSONException js) {
                System.out.println(js.toString());
            }
        }
    }


}
