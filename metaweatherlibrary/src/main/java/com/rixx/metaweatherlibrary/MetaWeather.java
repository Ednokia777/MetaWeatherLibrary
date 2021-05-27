package com.rixx.metaweatherlibrary;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;

public abstract class MetaWeather {
    static String temper = "";

    public static String getCurrentWeather(String location, Context context) {
        DecimalFormat df = new DecimalFormat("#.##");
        final String[] urlCityGetTemperature = {"https://www.metaweather.com/api/location/"};
        String urlCityGetId = "https://www.metaweather.com/api/location/search/?query=";
        final String[] finishUrl = new String[1];
        String tempUrl = "";
        String city = "";
        city = location;
        if (city.equals("")) {
            Toast.makeText(context.getApplicationContext(), "Введите город!", Toast.LENGTH_SHORT).show();
        } else {
            tempUrl = urlCityGetId + city;
            String finalCity = city;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONArray jsonResponce = new JSONArray(response);
                        if (response.equals("[]")) {
                            Toast.makeText(context, "Неверно указан город. Укажите его на английском.", Toast.LENGTH_SHORT).show();
                            Log.d("badcity", "Неверно указан город. Укажите его на английском.");
                        }
                        JSONObject jsonObjectZeroIndex = jsonResponce.getJSONObject(0);
                        int woeid = jsonObjectZeroIndex.getInt("woeid");
                        output = df.format(woeid);
                        //tvRes.setText(output);
                        finishUrl[0] = urlCityGetTemperature[0] + output;
                        //тут мы будем получат температуру по id города.
                        StringRequest stringRequestGetTeperatureWithId = new StringRequest(Request.Method.GET, finishUrl[0], new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Log.d("finalLink", response);
                                String outputTemp = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("consolidated_weather");
                                    JSONObject jsonObectFromArray = jsonArray.getJSONObject(0);
                                    int woeid = jsonObectFromArray.getInt("the_temp");
                                    temper = df.format(woeid);
                                    //openDialog(outputTemp, finalCity);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("errors", "вы ввели неккоректное название");
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext().getApplicationContext());
                        requestQueue.add(stringRequestGetTeperatureWithId);
                        //мы получили id города в переменную output и теперь нам надо передать это в другую ссылку
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("errors", "вы ввели неккоректное название");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext().getApplicationContext());
            requestQueue.add(stringRequest);

        }
        return temper;
    }
}