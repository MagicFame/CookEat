package com.tilted.magicfame.cookeat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    private final static String API_KEY = "93e5b880b602c4df65119c7236a61fe7";
    private final static String API_ID = "71b510d1";
    private FusedLocationProviderClient fusedLocationProviderClient;
    public final static int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button searchButton = findViewById(R.id.button);
        Button clearButton = findViewById(R.id.button_4);
        mDatabaseHelper = new DatabaseHelper(this);

        // Listener for the clear button
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.deleteData();
                updateData();
            }
        });

        // Listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) (findViewById(R.id.autoCompleteTextView))).getText().toString().length() != 0){
                    AddData(((EditText)(findViewById(R.id.autoCompleteTextView))).getText().toString());
                    updateData();
                }

                new FetchData().execute();
            }
        });

        locationFetch();
        updateData();
    }

    // Ask if the app don't have permission and print result (lat - long)
    public void locationFetch(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,  new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else{
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                TextView text = findViewById(R.id.location);
                                text.setText("Position: " + location.getLongitude() + "/" + location.getLatitude());
                            }
                        }
                    });
        }
    }

    //Update data history
    public void updateData(){
        Cursor data = mDatabaseHelper.getData();
        List<String> history = new ArrayList<>();
        while(data.moveToNext()){
            history.add(data.getString(1));
        }
        String arrayHistory[] = new String[history.size()];
        for (int i = 0; i<history.size(); i++){
            arrayHistory[i] = history.get(i);
        }
        ArrayAdapter<String> adapaterHistory = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, arrayHistory);
        AutoCompleteTextView searching = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        searching.setAdapter(adapaterHistory);
    }
    // Permissions asking for the location
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        TextView text = findViewById(R.id.location);
                                        text.setText("Position: " + location.getLongitude() + "/" + location.getLatitude());
                                    }
                                }
                            });
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    // Add Data to the DB
    public void AddData(String newValue){
        boolean insertingData = mDatabaseHelper.addData(newValue);
        if(insertingData){
            Toast.makeText(MainActivity.this, "Added to DB",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this, "Nop for the DB!",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Fetching data from the API
    class FetchData extends AsyncTask<Void, Void, String> {

        String result = "";
        private ArrayList<Recipe> recipes= new ArrayList<Recipe>();
        private ArrayAdapter<Recipe> adapter;

        // Call the good URL for the API
        @Override
        protected String doInBackground(Void... voids) {
            String research = ((EditText) (findViewById(R.id.autoCompleteTextView))).getText().toString();
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

        // Print data or error message
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
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "No results.",
                            Toast.LENGTH_LONG).show();
                }
            }catch (JSONException js) {
                Toast.makeText(MainActivity.this, "No results.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


}
