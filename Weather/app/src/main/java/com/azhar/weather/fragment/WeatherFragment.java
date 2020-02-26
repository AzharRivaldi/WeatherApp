package com.azhar.weather.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.azhar.weather.R;
import com.azhar.weather.adapter.WeatherListAdapter;
import com.azhar.weather.api.ApiWeather;
import com.azhar.weather.model.WeatherList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Azhar Rivaldi on 26-12-2019.
 */

public class WeatherFragment extends Fragment {

    String API_URL = ApiWeather.API_URL;

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvCity)
    TextView tvCity;
    @BindView(R.id.tvDayDate)
    TextView tvDayDate;
    @BindView(R.id.tvMaxTemp)
    TextView tvMaxTemp;
    @BindView(R.id.tvMinTemp)
    TextView tvMinTemp;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.imgCuaca)
    GifImageView imgCuaca;

    ArrayList<WeatherList> mWeatherList = new ArrayList<>();
    WeatherListAdapter adapterWeather;
    ProgressDialog mProgress;

    public WeatherFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this, view);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle("Mohon tunggu");
        mProgress.setCancelable(false);
        mProgress.setMessage("Sedang menampilkan data...");
        mProgress.show();
        RecyclerView recyclerView = view.findViewById(R.id.recycleList);
        adapterWeather = new WeatherListAdapter(mWeatherList);
        recyclerView.setAdapter(adapterWeather);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL,
                null, new Response.Listener<JSONObject>() {

            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject cityObject = response.getJSONObject("city");
                    String country = cityObject.getString("country");
                    String city = cityObject.getString("name");

                    JSONArray listArray = response.getJSONArray("list");
                    JSONObject firstObject = listArray.getJSONObject(0);

                    JSONObject mainObject = firstObject.getJSONObject("main");
                    double tempMax = mainObject.getDouble("temp_max");
                    int intTempMax = (int) tempMax;

                    double tempMin = mainObject.getDouble("temp_min");
                    int intTempMin = (int) tempMin;

                    JSONArray weatherArray = firstObject.getJSONArray("weather");
                    JSONObject weatherFirstObject = weatherArray.getJSONObject(0);
                    String status = weatherFirstObject.getString("main");
                    String date = firstObject.getString("dt_txt");
                    /*String strIcon = firstObject.getString("icon");*/

                    tvCity.setText(city + ", " + country);
                    tvMaxTemp.setText(String.valueOf(intTempMax) + (char) 0x00B0 + "C");
                    tvMinTemp.setText(String.valueOf(intTempMin) + (char) 0x00B0 + "C");
                    tvStatus.setText(status);

                    if (status.equals("Rain")) {
                        imgCuaca.setImageResource(R.drawable.hujan);
                    } else if (status.equals("Clouds")) {
                        imgCuaca.setImageResource(R.drawable.awan);
                    } else {
                        imgCuaca.setImageResource(R.drawable.cerah);
                    }

                    /*if(!strIcon.equals(""))
                    {
                        String url="http://openweathermap.org/img/w/"+strIcon+".png";
                        ImageView condIcon = getView().findViewById(R.id.imgCuaca);
                        Glide.with(getActivity())
                                .load(url)
                                .into(condIcon);
                    }*/

                    SimpleDateFormat formatDefault = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatTimeCustom = new SimpleDateFormat("hh.mm");
                    SimpleDateFormat formatDateCustom = new SimpleDateFormat("EEEE, dd MMM");

                    String time = date;
                    String datecustom = date;
                    try {
                        Date timesFormat = formatDefault.parse(time);
                        time = formatTimeCustom.format(timesFormat);

                        Date dateFormat = formatDefault.parse(date);
                        datecustom = formatDateCustom.format(dateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    tvTime.setText(time);
                    tvDayDate.setText(datecustom);

                    for (int x = 1; x < listArray.length(); x++) {
                        JSONObject objectList = listArray.getJSONObject(x);
                        JSONObject mainObjectList = objectList.getJSONObject("main");
                        double tempMaxList = mainObjectList.getDouble("temp_max");
                        int intTempMaxList = (int) tempMaxList;

                        double tempMinList = mainObjectList.getDouble("temp_min");
                        int intTempMinList = (int) tempMinList;

                        JSONArray weatherArrayList = objectList.getJSONArray("weather");
                        JSONObject weatherObjectList = weatherArrayList.getJSONObject(0);
                        String statusList = weatherObjectList.getString("main");
                        String statusDetail = weatherObjectList.getString("description");
                        String dateList = objectList.getString("dt_txt");

                        WeatherList weatherListModel = new WeatherList(dateList, intTempMaxList,
                                intTempMinList, statusList, statusDetail);
                        mWeatherList.add(weatherListModel);
                        adapterWeather.notifyDataSetChanged();

                    }
                    mProgress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
        return view;
    }

}
