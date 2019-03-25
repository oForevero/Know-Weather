package com.forever.weather.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.forever.weather.AppConfig;
import com.forever.weather.BaseClass.BaseActivity;
import com.forever.weather.R;
import com.forever.weather.Utils.NetWorkUtils;
import com.forever.weather.WeatherAdapter.Weather;
import com.forever.weather.WeatherAdapter.WeatherAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.forever.weather.Utils.TimeUtils.belongCalendar;

public class MainActivity extends BaseActivity implements AMapLocationListener{
    SmartRefreshLayout srl;
    TextView tem_now_value,tem_cloud_value,tem_today_value,tem_weather_value;
    TextView Tem_air_suggestion,Tem_air_value,Tem_uv_suggestion,Tem_uv_value,Tem_drsg_suggestion,Tem_drsg_value,Tem_flu_suggestion,Tem_flu_value;
    TextView Tem_weather_air,Tem_air_fresh,Gps_Where;
    ListView tem_three_days;
    ScrollView Tem_live_view;
    LineChart mLineChart;
    Timer timer = new Timer();
    List<Weather> weatherList = new ArrayList<>();
    private Context context;
    private  Handler hd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what){
                case AppConfig.REFRESH_WEATHER_UI :
                    tem_now_value.setText(bundle.getString("TemNow")+"°");
                    tem_today_value.setText("今天："+bundle.getString("TemTodayMin")+" - "+bundle.getString("TemTodayMax")+"°C");
                    tem_weather_value.setText(bundle.getString("WeatherToday_Sun"));
                    tem_cloud_value.setText(bundle.getString("WindWhere")+" "+bundle.getString("WindStrong")+"级");
                    Tem_air_suggestion.setText(bundle.getString("Live_air_suggestion"));
                    Tem_air_value.setText(bundle.getString("Live_air_value"));
                    Tem_flu_suggestion.setText(bundle.getString("Live_flu_suggestion"));
                    Tem_flu_value.setText(bundle.getString("Live_flu_value"));
                    Tem_uv_suggestion.setText(bundle.getString("Live_sun_suggestion"));
                    Tem_uv_value.setText(bundle.getString("Live_sun_value"));
                    Tem_drsg_suggestion.setText(bundle.getString("Live_drsg_suggestion"));
                    Tem_drsg_value.setText(bundle.getString("Live_drsg_value"));
                    Tem_weather_air.setText("空气·"+bundle.getString("Live_air_value"));
                    Tem_air_fresh.setText(bundle.getString("air_qlty")+"（"+bundle.getString("air_aqi")+"）");
                    if(Province.equals(City)) {
                        Gps_Where.setText(Province + " " + District + " " + Street);
                    }else if(!Province.equals(City)){
                        Gps_Where.setText(Province + " " + City + " " + Street);
                    }
                    WeatherAdapter weatherAdapter = new WeatherAdapter(context, R.layout.weather_adapter_style, weatherList);
                    tem_three_days.setAdapter(weatherAdapter);
                    srl.finishRefresh();
                    break;
                case AppConfig.REFRESH_BACKGROUND_UI :
                    getWindow().setBackgroundDrawableResource(bundle.getInt("resourceid"));
                    timer.schedule(autogetTime(), 6*10000);
                    break;
            }
        }
    };
    String cityvalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = MainActivity.this;
        initControl();
        //setLineChartDate();
        firstrun();
        initWeatherValue("weather", "air", District);
        srl.autoRefresh();
        overridePendingTransition(R.anim.fade_out,R.anim.fade_in);
        timer.schedule(autogetTime(), 0);
    }
    //暂未启用，等1.12版本更新
    /*private void setLineChart(){
        mLineChart.getLegend().setEnabled(false);// 不显示图例
        mLineChart.getDescription().setEnabled(false);// 不显示描述
        mLineChart.setScaleEnabled(false);   // 取消缩放
        mLineChart.setNoDataText("暂无数据");// 没有数据的时候默认显示的文字
        mLineChart.setNoDataTextColor(Color.GRAY);//设置无数据颜色为灰色
        mLineChart.setBorderColor(Color.WHITE);//设置边距颜色为白色
        mLineChart.setTouchEnabled(true);//允许触摸
        mLineChart.setDragEnabled(true);
        // 如果x轴label文字比较大，可以设置边距
        mLineChart.setExtraRightOffset(25f);
        mLineChart.setExtraBottomOffset(10f);
        mLineChart.setExtraTopOffset(10f);
        XAxis xAxis = mLineChart.getXAxis();       //获取x轴线
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(true);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setTextSize(14f);//设置文字大小
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisMinimum(0f);//设置x轴的最小值 //`
        xAxis.setAxisMaximum(6f);//设置最大值 //
        xAxis.setLabelCount(6);  //设置X轴的显示个数
        xAxis.setAvoidFirstLastClipping(false);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAxisLineColor(Color.WHITE);//设置x轴线颜色
        xAxis.setAxisLineWidth(0.5f);//设置x轴线宽度
        YAxis leftAxis = mLineChart.getAxisLeft();
        YAxis axisRight = mLineChart.getAxisRight();
        leftAxis.enableGridDashedLine(10f, 10f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
        leftAxis.setGranularity(20f); // 网格线条间距
        axisRight.setEnabled(false);   //设置是否使用 Y轴右边的
        leftAxis.setEnabled(true);     //设置是否使用 Y轴左边的
        leftAxis.setGridColor(Color.parseColor("#7189a9"));  //网格线条的颜色
        leftAxis.setDrawLabels(true);        //是否显示Y轴刻度
        leftAxis.setStartAtZero(true);        //设置Y轴数值 从零开始
        leftAxis.setDrawGridLines(true);      //是否使用 Y轴网格线条
        leftAxis.setTextSize(12f);            //设置Y轴刻度字体
        leftAxis.setTextColor(Color.WHITE);   //设置字体颜色
        leftAxis.setAxisLineColor(Color.WHITE); //设置Y轴颜色
        leftAxis.setAxisLineWidth(0.5f);
        leftAxis.setDrawAxisLine(true);//是否绘制轴线
        leftAxis.setMinWidth(0f);
        leftAxis.setMaxWidth(200f);
        leftAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        Legend l = mLineChart.getLegend();//图例
        l.setEnabled(false);   //是否使用 图例
    }
    private void setLineChartDate() {
        setLineChart();
        List mValues = new ArrayList<>();
        mValues.add(new Entry(0, 10));
        mValues.add(new Entry(1, 15));
        mValues.add(new Entry(2, 25));
        mValues.add(new Entry(3, 19));
        mValues.add(new Entry(4, 25));
        mValues.add(new Entry(5, 16));
        mValues.add(new Entry(6, 40));
        mValues.add(new Entry(7, 27));
        if (mValues.size() == 0) return;
        LineDataSet set1;
        //判断图表中原来是否有数据
        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            //获取数据1
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(mValues);
            //刷新数据
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            set1 = new LineDataSet(mValues, "测试数据1");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.parseColor("#AAFFFFFF"));
            set1.setHighLightColor(Color.WHITE);//设置点击交点后显示交高亮线的颜色
            set1.setHighlightEnabled(true);//是否使用点击高亮线
            set1.setDrawCircles(true);
            set1.setValueTextColor(Color.WHITE);
            set1.setLineWidth(1f);//设置线宽
            set1.setCircleRadius(2f);//设置焦点圆心的大小
            set1.setHighlightLineWidth(0.5f);//设置点击交点后显示高亮线宽
            set1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
            set1.setValueTextSize(12f);//设置显示值的文字大小
            set1.setDrawFilled(true);//设置使用 范围背景填充

            set1.setDrawValues(false);
            //格式化显示数据
            final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return mFormat.format(value);
                }
            });
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.color.translucence);
                set1.setFillDrawable(drawable);//设置范围背景填充
            } else {
                set1.setFillColor(R.color.translucence);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets
            //创建LineData对象 属于LineChart折线图的数据集合
            LineData data = new LineData(dataSets);
            // 添加到图表中
            mLineChart.setData(data);
            //绘制图表
            mLineChart.invalidate();
        }
    }*/

    private void firstrun(){
        Intent it = getIntent();
        Province = it.getStringExtra("Province");
        City = it.getStringExtra("City");
        District = it.getStringExtra("District");
        Street = it.getStringExtra("Street");
        if(Province.equals(City)){
            cityvalue = District;
        }else if(!Province.equals(City)){
            cityvalue = City;
        }
        refreshTitle();
    }

    @Override
    protected void initControl() {
        initGaoDeMap();
        tem_now_value = findViewById(R.id.Tem_now_value);
        tem_today_value = findViewById(R.id.Tem_today_value);
        tem_weather_value = findViewById(R.id.Tem_weather_value);
        tem_cloud_value = findViewById(R.id.weather_cloud);
        tem_three_days = findViewById(R.id.Tem_weather_three_day);
        Tem_air_value = findViewById(R.id.Tem_air_value);
        Tem_air_suggestion = findViewById(R.id.Tem_air_suggestion);
        Tem_uv_value = findViewById(R.id.Tem_uv_value);
        Tem_uv_suggestion = findViewById(R.id.Tem_uv_suggestion);
        Tem_drsg_value = findViewById(R.id.Tem_drsg_value);
        Tem_drsg_suggestion = findViewById(R.id.Tem_drsg_suggestion);
        Tem_flu_value = findViewById(R.id.Tem_flu_value);
        Tem_flu_suggestion = findViewById(R.id.Tem_flu_suggestion);
        Tem_weather_air = findViewById(R.id.weather_air);
        Tem_air_fresh = findViewById(R.id.Tem_air_fresh);
        Tem_live_view = findViewById(R.id.Tem_live_view);
        Gps_Where = findViewById(R.id.Gps_Where);
        //mLineChart = findViewById(R.id.co2_value);
    }

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //注册高德地图对象
    private void initGaoDeMap(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置允许单词定位获取一次定位结果：
        mLocationOption.setOnceLocation(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);//绑定监听
    }

    String Province,City,District,Street;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                Province = aMapLocation.getProvince();
                City = aMapLocation.getCity();
                District = aMapLocation.getDistrict();
                Street = aMapLocation.getStreet();
                Log.d("data", Province +" "+City+" "+District+" "+Street);
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    long now = 0;
    /**
     * @Description: Method for refresh data.
     * @Author: Forever
     * @Date: 2019/3/20 15:18
     */ 
    private void refreshTitle(){
        srl = findViewById(R.id.refreshLayout);
        srl.setEnableRefresh(true);
        srl.setEnableLoadMore(false);
        srl.setHeaderHeight(50f);
        //刷新的监听
        srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //如果刷新则判定是否大于30秒，默认刷新三秒
                        if(System.currentTimeMillis() - now >= 30 *1000) {
                            mLocationClient.startLocation();
                            now = System.currentTimeMillis();
                            if(Province.equals(City)){
                                cityvalue = District;
                            }else if(!Province.equals(City)){
                                cityvalue = City;
                            }
                            if(cityvalue != null) {
                                initWeatherValue("weather", "air", cityvalue);
                            }
                        }else{//直接延时三秒并刷新，保护服务器
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    srl.finishRefresh();
                                }
                            }, 500);
                        }
                    }
                }, 0);
            }
        });
    }

    //自动设置根据时间变化背景图
    private TimerTask autogetTime(){
        TimerTask task = new TimerTask() {
            int resourceid = 0;
            @Override
            public void run() {
               if(isBelong("5:00", "7:00")){
                   resourceid = R.drawable.halfmorning;
               }else if(isBelong("7:00", "9:00")){
                   resourceid = R.drawable.morning;
               }else if(isBelong("9:00", "16:00")){
                   resourceid = R.drawable.afternoon;
               }else if(isBelong("16:00", "18:00")){
                   resourceid = R.drawable.halfevening;
               }else if(isBelong("18:00", "21:00")){
                   resourceid = R.drawable.evening;
               }else if(isBelong("21:00", "23:59")){
                   resourceid = R.drawable.dask;
               }else if(isBelong("0:00", "5:00")){
                   resourceid = R.drawable.dask;
               }else {
                   resourceid = R.drawable.bgsun;
               }
               Message msg = new Message();
               msg.what = AppConfig.REFRESH_BACKGROUND_UI;
               Bundle bundle = new Bundle();
               bundle.putInt("resourceid", resourceid);
               msg.setData(bundle);
               hd.sendMessage(msg);
            }
        };
        return task;
    }
    boolean dataisnull;
    /**
     * @Description: Method for set weather value.
     * @Author: Forever
     * @Date: 2019/3/20 18:09
     */
    //解析并设置天气数据
    public void initWeatherValue(final String type1, final String type2, final String city){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url1 = null,url2 = null;
                try {
                    url1 = new URL("https://free-api.heweather.com/s6/"+type1+"?key=8329729987ce47d881e8c94b574b638d&location="+city);
                    url2 = new URL("https://free-api.heweather.net/s6/"+type2+"/now?key=8329729987ce47d881e8c94b574b638d&location="+city);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String responsedata1 = NetWorkUtils.sendResponseByHttpUrlConnection(url1);
                String responsedata2 = NetWorkUtils.sendResponseByHttpUrlConnection(url2);
                if(responsedata1 != null && responsedata2 !=null) {
                    JSONObject js = NetWorkUtils.jsonReader(responsedata1);
                    JSONArray array1 = NetWorkUtils.jsonArrayReader(js, "HeWeather6");
                    JSONObject jsvalue1;
                    JSONObject jsCity = null;
                    JSONObject jsWeather = null;
                    JSONArray array2;
                    JSONObject jsToday = null;
                    JSONObject jsTomorrow = null;
                    JSONObject jsAfterday = null;
                    JSONArray array3;
                    JSONObject jssun = null;
                    JSONObject jsair = null;
                    JSONObject jsflu = null;
                    JSONObject jsdrsg = null;
                    JSONObject jspm2 = NetWorkUtils.jsonReader(responsedata2);
                    JSONArray array4 = NetWorkUtils.jsonArrayReader(jspm2, "HeWeather6");
                    JSONObject jspmvalue = null;
                    JSONObject object = null;
                    try {
                        jsvalue1 = array1.getJSONObject(0);
                        array2 = jsvalue1.getJSONArray("daily_forecast");
                        jsToday = array2.getJSONObject(0);
                        jsTomorrow = array2.getJSONObject(1);
                        jsAfterday = array2.getJSONObject(2);
                        jsCity = jsvalue1.getJSONObject("basic");
                        jsWeather = jsvalue1.getJSONObject("now");
                        array3 = jsvalue1.getJSONArray("lifestyle");
                        jsdrsg = array3.getJSONObject(1);
                        jsflu = array3.getJSONObject(2);
                        jssun = array3.getJSONObject(5);
                        jsair = array3.getJSONObject(7);
                        jspmvalue = array4.getJSONObject(0);
                        if(jspmvalue.toString().length()>50) {
                            object = jspmvalue.getJSONObject("air_now_city");
                            dataisnull = false;
                        }else {
                            dataisnull = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = AppConfig.REFRESH_WEATHER_UI;
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putString("City", jsCity.getString("location"));
                        bundle.putString("Weather", jsWeather.getString("cond_txt"));
                        bundle.putString("TemNow", jsWeather.getString("tmp"));
                        bundle.putString("WindStrong", jsWeather.getString("wind_sc"));
                        bundle.putString("WindWhere", jsWeather.getString("wind_dir"));
                        bundle.putString("WeatherToday_Sun", jsToday.getString("cond_txt_d"));
                        bundle.putString("TemTodayMax", jsToday.getString("tmp_max"));
                        bundle.putString("TemTodayMin", jsToday.getString("tmp_min"));
                        bundle.putString("WeatherTomorrow_Sun", jsTomorrow.getString("cond_txt_d"));
                        bundle.putString("TemTomorrowMax", jsTomorrow.getString("tmp_max"));
                        bundle.putString("TemTomorrowMin", jsTomorrow.getString("tmp_min"));
                        bundle.putString("WeatherAfterday_Sun", jsAfterday.getString("cond_txt_d"));
                        bundle.putString("TemAfterdayMax", jsAfterday.getString("tmp_max"));
                        bundle.putString("TemAfterdayMin", jsAfterday.getString("tmp_min"));
                        bundle.putString("Live_sun_value", jssun.getString("brf"));
                        bundle.putString("Live_flu_value", jsflu.getString("brf"));
                        bundle.putString("Live_drsg_value", jsdrsg.getString("brf"));
                        bundle.putString("Live_air_value", jsair.getString("brf"));
                        bundle.putString("Live_sun_suggestion", jssun.getString("txt"));
                        bundle.putString("Live_flu_suggestion", jsflu.getString("txt"));
                        bundle.putString("Live_drsg_suggestion", jsdrsg.getString("txt"));
                        bundle.putString("Live_air_suggestion", jsair.getString("txt"));
                        if(!dataisnull) {
                            bundle.putString("air_qlty", object.getString("qlty"));
                            bundle.putString("air_aqi", object.getString("aqi"));
                        }else if(dataisnull){
                            bundle.putString("air_qlty", "获取失败");
                            bundle.putString("air_aqi", "获取失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        weatherList.clear();
                        Weather weathertoday = new Weather("今天·" + jsToday.getString("cond_txt_d"), setImage(jsToday.getString("cond_txt_d")), jsToday.getString("tmp_min") + " - " + jsToday.getString("tmp_max") + "°C");
                        weatherList.add(weathertoday);
                        Weather weathertomorrow = new Weather("明天·" + jsTomorrow.getString("cond_txt_d"), setImage(jsTomorrow.getString("cond_txt_d")), jsTomorrow.getString("tmp_min") + " - " + jsTomorrow.getString("tmp_max") + "°C");
                        weatherList.add(weathertomorrow);
                        Weather weatherafterday = new Weather("后天·" + jsAfterday.getString("cond_txt_d"), setImage(jsAfterday.getString("cond_txt_d")), jsAfterday.getString("tmp_min") + " - " + jsAfterday.getString("tmp_max") + "°C");
                        weatherList.add(weatherafterday);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    msg.setData(bundle);
                    hd.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void bindListener() {
        Tem_live_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Tem_live_view.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

    }
    //设置天气图片
    private static int setImage(String weather){
        int imageid = 0;
        if (weather!=null) {
            if (weather.equals("多云")) {
                imageid = R.drawable.morecloud;
            } else if (weather.equals("晴")) {
                imageid = R.drawable.sun;
            } else if (weather.equals("阴")) {
                imageid = R.drawable.morecloud;
            } else if (weather.equals("小雨")) {
                imageid = R.drawable.smallrain;
            } else if (weather.equals("中雨")) {
                imageid = R.drawable.mediumrain;
            }
        }
        return imageid;
    }
    //判断天气时间
    public boolean isBelong(String timestart, String timeend){
        Boolean flag;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(System.currentTimeMillis()));
            beginTime = df.parse(timestart);
            endTime = df.parse(timeend);
        } catch (Exception e) {
            e.printStackTrace();
        }
        flag = belongCalendar(now, beginTime, endTime);
        return flag;
    }
}
