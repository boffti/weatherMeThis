package com.aneeshmelkot.weathermethis;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private CardView weatherCard;
    private EditText cityName;
    private TextView cityTitle;
    private ImageView iconView;
    private TextView descriptionView;
    private TextView tempView;

    public void getWeather(View view) {

        weatherCard.setVisibility(View.VISIBLE);
        String city = cityName.getText().toString();
        cityTitle.setText(city);

        new DownloadTask().execute("http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=9adaf9a99c4964df0d6ae829644bf8f8");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        cityTitle = (TextView) findViewById(R.id.cityTitle);
        tempView = (TextView)findViewById(R.id.temperatureView);
        iconView = (ImageView) findViewById(R.id.iconView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        weatherCard = (CardView)findViewById(R.id.weatherCard);
        weatherCard.setVisibility(View.INVISIBLE);

    }

    public class DownloadTask extends AsyncTask<String , Void , Void>{

        private String content;
        private String Error = null;
        private int finalTemp;
        private String description="";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... urls) {

            BufferedReader reader = null;

            try {

                //Defined URL to send data
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                //Read Server Response
                while ((line=reader.readLine())!=null) {
                    //Append Server Response to String
                    sb.append(line+"");
                }
                //Append Server Response to Content String
                content = sb.toString();

            } catch (Exception e) {
                Error = e.getMessage();
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather.", Toast.LENGTH_SHORT);
            }

            finally {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                JSONObject jsonObject = new JSONObject(content);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Content", weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0; i<arr.length();i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Log.i("main",jsonPart.getString("main"));

                    //Description to be displayed
                    description = jsonPart.getString("description");
                    String finalDesc = description.substring(0,1).toUpperCase()+description.substring(1).toLowerCase();
                    Log.i("description",jsonPart.getString("description"));
                    descriptionView.setText(finalDesc);
                }

                String temp = jsonObject.getString("main");
                JSONObject tempObj = new JSONObject(temp);
                float rawTemp = Float.parseFloat(tempObj.getString("temp"));
                double convTemp = rawTemp-273.15;

                //Final Temperature to be displayed
                finalTemp = (int) Math.round(convTemp);

                Log.i("Raw Temp", tempObj.getString("temp"));
                Log.i("Conv Temp", Integer.toString(finalTemp));
                tempView.setText(Integer.toString(finalTemp)+"°c");

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather.", Toast.LENGTH_SHORT);
            }
        }
    }





    @Override
    public boolean onTouchEvent (MotionEvent event){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
