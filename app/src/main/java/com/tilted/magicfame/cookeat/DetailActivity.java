package com.tilted.magicfame.cookeat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private String id = "";
    private TextView textView = null;
    private ImageView imageView = null;
    private TextView ingredientText = null;
    private TextView calText = null;
    private TextView fatText = null;
    private TextView fiberText = null;
    private TextView sugarText = null;
    private TextView proteinText = null;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        // get the parameter id for the good recipe
        if(intent != null){
            if(intent.hasExtra("id")){
                id = intent.getStringExtra("id");
            }
        }
        new FetchDetail().execute();
    }

    // call API to get all details
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
           // System.out.println(APIURL);
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

        // Parse JSON data and print in the good textview
        @Override
        protected void onPostExecute(String s){
            imageView = (ImageView) findViewById(R.id.picture);
            try{
                JSONArray jarray = new JSONArray(result);
                JSONObject jobject = jarray.getJSONObject(0);
                textView = findViewById(R.id.tv1);
                textView.setText(jobject.getString("label"));
                Picasso.with(DetailActivity.this).load(jobject.getString("image")).into(imageView);
                ingredientText = findViewById(R.id.ingredient);
                JSONArray jarrayingredient = new JSONArray(jobject.getString("ingredientLines"));
                String ingredient = "Ingredients :\n";
                for(int i = 0; i < jarrayingredient.length(); i++){
                    ingredient = ingredient + jarrayingredient.getString(i) + "\n";
                }
                ingredientText.setText(ingredient);
                ingredientText.setMovementMethod(new ScrollingMovementMethod());
                JSONObject jobjectApport = new JSONObject(jobject.getString("totalNutrients"));
                String calories = "";
                String fat = "";
                String fiber = "";
                String sugar = "";
                String protein = "";
                calories = new JSONObject(jobjectApport.getString("ENERC_KCAL")).getString("quantity");
                fat = new JSONObject(jobjectApport.getString("FAT")).getString("quantity");
                fiber = new JSONObject(jobjectApport.getString("FIBTG")).getString("quantity");
                sugar = new JSONObject(jobjectApport.getString("SUGAR")).getString("quantity");
                protein = new JSONObject(jobjectApport.getString("PROCNT")).getString("quantity");
                calText = findViewById(R.id.cal);
                fatText = findViewById(R.id.fat);
                fiberText = findViewById(R.id.fib);
                sugarText = findViewById(R.id.sugar);
                proteinText = findViewById(R.id.prot);
                modifyContent(calories, calText);
                modifyContent(fat, fatText);
                modifyContent(fiber, fiberText);
                modifyContent(sugar, sugarText);
                modifyContent(protein, proteinText);


            }catch (JSONException js){
                System.out.println("Error parsing");
                Toast.makeText(DetailActivity.this, "Error in data in the API call..",
                        Toast.LENGTH_LONG).show();
            }
        }

        // To reduce the number of decimal
        public void modifyContent(String value, TextView textView){
            try{
                String new_value = String.format("%.2f", Float.parseFloat(value));
                textView.setText(new_value);
            }catch(Exception e){
                System.out.println("Error converting" + e.getMessage());
            }
        }

    }


}
