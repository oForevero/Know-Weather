package com.forever.weather.WeatherAdapter;

/**
 * Copyright (C), 2015-2019
 * FileName: Weather
 * Author: Forever
 * Date: 2019/3/20 17:38
 * Description: 用于显示天气值的自定义adapter
 */
public class Weather {
    String weatherDays;
    int weatherImage;
    String weatherValue;
    public Weather(String weatherDays, int weatherImage, String weatherValue){
        this.weatherDays = weatherDays;
        this.weatherImage = weatherImage;
        this.weatherValue = weatherValue;
    }

    public int getWeatherImage() {
        return weatherImage;
    }

    public String getWeatherDays() {
        return weatherDays;
    }

    public String getWeatherValue() {
        return weatherValue;
    }
}
