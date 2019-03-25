package com.forever.weather.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.forever.weather.BaseClass.BaseActivity;
import com.forever.weather.Collector.ActivityCollector;
import com.forever.weather.R;

import java.util.Timer;
import java.util.TimerTask;

public class LabelActivity extends BaseActivity implements AMapLocationListener {
    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        initGaoDeMap();
        //启动定位
        ActivityCompat.requestPermissions(LabelActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        if(ContextCompat.checkSelfPermission(LabelActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(LabelActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
            flag = false;
        }else{
            flag = true;
        }
        if(flag) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mLocationClient.startLocation();
                }
            }).start();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent it = new Intent(LabelActivity.this, MainActivity.class);
                    it.putExtra("Province", Province);
                    it.putExtra("City", City);
                    it.putExtra("District", District);
                    it.putExtra("Street", Street);
                    startActivity(it);
                }
            }, 4000);
        }else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    ActivityCollector.finishAll();
                }
            }, 4000);
        }
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
    @Override
    protected void initControl() {

    }

    @Override
    public void bindListener() {

    }
    /**
     * @Description: 获取定位权限的提示
     * @Author: Forever
     * @Date: 2019/3/22 11:43
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200://刚才的识别码
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){//用户同意权限,执行我们的操作
                    flag = true;
                }else{//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                    Toast.makeText(LabelActivity.this, "请在程序设置中同意定位权限！否则程序将无法运行", Toast.LENGTH_LONG).show();
                }
                break;
            default:break;
        }
    }
}
