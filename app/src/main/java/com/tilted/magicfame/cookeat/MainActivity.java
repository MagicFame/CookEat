package com.tilted.magicfame.cookeat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

    private final static String API_KEY = "93e5b880b602c4df65119c7236a61fe7";
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
        private ArrayList<Recipe> recipes= new ArrayList<Recipe>();
        private ArrayAdapter<Recipe> adapter;

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... voids) {
            String research = ((EditText) (findViewById(R.id.editText2))).getText().toString();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                String APIURL = "https://api.edamam.com/search?q=" + research + "&app_id=" + API_ID +
                        "&app_key=" + API_KEY + "&from=0";

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
            ListView listview = findViewById(R.id.listview);

            try{
                JSONObject JasonObject = new JSONObject(result);
                int number = 0;
                try{
                    number = Integer.parseInt(JasonObject.getString("to"));
                }catch (JSONException js){
                    Toast.makeText(MainActivity.this, "No more request available with the API!",
                            Toast.LENGTH_LONG).show();
                }
                JSONArray jarray = JasonObject.getJSONArray("hits");

                if(number > 0) {
                    for (int i = 0; i < number; i++) {
                        JSONObject jobject = jarray.getJSONObject(i).getJSONObject("recipe");
                        Recipe r = new Recipe(jobject.getString("uri"), jobject.getString("label"), jobject.getString("image"), jobject.getString("calories"));
                        recipes.add(r);
                    }
                    adapter = new CustomListAdapter(MainActivity.this, R.layout.item_listview, recipes);
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            intent.putExtra("id", recipes.get(position).getId());
                            startActivity(intent);
                            System.out.println(recipes.get(position).getId());
                        }
                    });
                }
            }catch (JSONException js) {
                System.out.println(js.toString());
            }
        }
    }


}
