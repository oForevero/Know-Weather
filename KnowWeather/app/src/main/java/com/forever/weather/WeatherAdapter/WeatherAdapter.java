package com.forever.weather.WeatherAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.forever.weather.R;

import java.util.List;

/**
 * Copyright (C), 2015-2019
 * FileName: WeatherAdapter
 * Author: Forever
 * Date: 2019/3/20 17:39
 * Description: ${DESCRIPTION}
 */
public class WeatherAdapter extends ArrayAdapter {
    int resourceid;
    public WeatherAdapter(Context context, int resource, List<Weather> objects) {
        super(context, resource, objects);
        resourceid = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Weather weather = (Weather) getItem(position);
        View view = null;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceid, parent, false);
        }else {
            view = convertView;
        }
        TextView WeatherDay = view.findViewById(R.id.weather_days);
        TextView WeatherNowValue = view.findViewById(R.id.weather_now_value);
        ImageView WeatherImage = view.findViewById(R.id.weather_image);
        WeatherDay.setText(weather.getWeatherDays());
        WeatherNowValue.setText(weather.getWeatherValue());
        WeatherImage.setImageResource(weather.getWeatherImage());
        return view;
    }
}
