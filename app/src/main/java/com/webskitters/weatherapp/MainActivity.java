package com.webskitters.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    EditText et;
    Button btn;
    TextView wea, weades, city, wspeed, wdir, tem, tem_min, tem_max, hum, press, res2;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = findViewById(R.id.city);
        wea = findViewById(R.id.weather);
        weades = findViewById(R.id.wdes);
        wspeed = findViewById(R.id.wspeed);
        wdir = findViewById(R.id.wdir);
        tem = findViewById(R.id.temp);
        tem_min = findViewById(R.id.min_temp);
        tem_max = findViewById(R.id.max_temp);
        hum = findViewById(R.id.humidity);
        press = findViewById(R.id.pressure);
        res2 =  findViewById(R.id.result);
        res2.setVisibility(View.GONE);


        sv = findViewById(R.id.sview);
        sv.setVisibility(View.GONE);


        et = findViewById(R.id.editText);
        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(et.getWindowToken(),0);

                if (et.getText().toString().equals("") || et.getText().toString().contains("  ")){
                    Toast.makeText(MainActivity.this, "Enter a valid City Name", Toast.LENGTH_SHORT).show();
                } else {
                    String s = null;
                    try {
                        s = "http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(et.getText().toString(),"UTF-8") +
                                "&appid={your API id}";
                        new DownloadData().execute(s);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error in City Name!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private class DownloadData extends AsyncTask<String,Void,String>{
        String res;
        ProgressDialog pd;

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data != -1){
                    char s = (char) data;
                    res += s;
                    data = reader.read();
                }

                res = res.replace("null","");

                return res;


            } catch (MalformedURLException e) {
                return "x";
            } catch (IOException e) {
                return "x";
            } catch (Exception e){
                return "x";
            }
        }


        @Override
        protected void onPreExecute() {
            et.setText("");
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Fetching info...");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            String t, tmin, tmax;
            if (result.equals("x") || result.equals("")){
                Toast.makeText(MainActivity.this, "No such City in the Database!", Toast.LENGTH_SHORT).show();
            } else {
                res2.setVisibility(View.VISIBLE);
                sv.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String name = jsonObject.getString("name");

                    String wind = jsonObject.getString("wind");
                    JSONObject jsonObject1 = new JSONObject(wind);
                    String windSpeed = jsonObject1.getString("speed");
                    String windDirection = jsonObject1.getString("deg");

                    String main = jsonObject.getString("main");
                    JSONObject jsonObject2 = new JSONObject(main);
                    String temp = jsonObject2.getString("temp");
                    String pressure  = jsonObject2.getString("pressure");
                    String humidity  = jsonObject2.getString("humidity");
                    String temp_min  = jsonObject2.getString("temp_min");
                    String temp_max  = jsonObject2.getString("temp_max");

                    String weather = jsonObject.getString("weather");
                    JSONArray jsonArray = new JSONArray(weather);


                    JSONObject jsonObject3 = jsonArray.getJSONObject(0);
                    String wmain = jsonObject3.getString("main");
                    String wdes = jsonObject3.getString("description");

                    windSpeed += " meter/sec";

                    double tem2 = Double.parseDouble(temp);
                    tem2 = tem2 - 273.15;

                    double tem3 = Double.parseDouble(temp_max);
                    tem3 = tem3 - 273.15;

                    double tem4 = Double.parseDouble(temp_min);
                    tem4 = tem4 - 273.15;

                    humidity += " %";

                    windDirection += "°";

                    pressure += " hPa";

                    if (String.valueOf(tem2).length() > 5){
                        t = String.valueOf(tem2).substring(0,4);
                    } else {
                        t = String.valueOf(tem2);
                    }

                    if (String.valueOf(tem3).length()>5){
                        tmax = String.valueOf(tem3).substring(0,4);
                    } else {
                        tmax = String.valueOf(tem3);
                    }

                    if (String.valueOf(tem3).length()>5) {
                        tmin = String.valueOf(tem4).substring(0,4);
                    } else {
                        tmin = String.valueOf(tem4);
                    }




                    city.setText(name);
                    wea.setText(wmain);
                    weades.setText(wdes);
                    wspeed.setText(windSpeed);
                    wdir.setText(windDirection);
                    tem.setText(t + " °C");
                    tem_min.setText(tmin + " °C");
                    tem_max.setText(tmax + " °C");
                    hum.setText(humidity);
                    press.setText(pressure);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error in parsing data!", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }


}
