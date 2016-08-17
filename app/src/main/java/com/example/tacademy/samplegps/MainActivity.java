package com.example.tacademy.samplegps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LocationManager mLM;
    String mProvider = LocationManager.GPS_PROVIDER;

    TextView messageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = (TextView) findViewById(R.id.text_message);
        mLM = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
            requestLocationPermission();


            Criteria criteria = new Criteria();//GPS Provider 가 있을수도 있으나 없을 수도 있는 경우
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);//나는 어느정도에 정확도 인지를 정해 줄 수 있다.
            criteria.setPowerRequirement(Criteria.POWER_HIGH);//파워 사용량
            criteria.setCostAllowed(true);//비용 발생 여부
            criteria.setAltitudeRequired(false);//고도를쓴건지 여부
            criteria.setBearingRequired(false);//자기 머리통 직각 기준으로 각도 여부
            criteria.setSpeedRequired(false);//속도 사용 여부

            mProvider = mLM.getBestProvider(criteria, true);//설정에 인에이블로 되잇는 것중에 베스트 인애를 넘겨 달라는 부분
        }

    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_PERMISSION);
    }

    private static final int RC_LOCATION_PERMISSION = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION_PERMISSION) {
            if (permissions != null && permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
        Toast.makeText(this, "need location permission", Toast.LENGTH_SHORT).show();
        finish();
    }

    boolean isFirst = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!mLM.isProviderEnabled(mProvider)) {
            if (isFirst) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                isFirst = false;
            } else {
                Toast.makeText(this, "location enable setting...", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }

        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {

        }
        mLM.requestLocationUpdates(mProvider, 2000, 5, mListener);
        // mLM.requestSingleUpdate(mProvider, mListener, )//위치정보를 한번 얻어오고 싶을때 사용
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    private void displayLocation(Location location) {
        messageView.setText("lat : " + location.getLatitude() + ", lng : " + location.getLongitude());
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            displayLocation(location);
        }

        @Override //내가 사용하고 있는 프로바이더 상태가 바뀔 경우 : GPS 위치 수신하다 실내 들어오는 순간 수신 안 되는 경우 처리해줘야한다.
        public void onStatusChanged(String provider, int status, Bundle bundle) {
        switch (status){//상태가 바뀌면
            case LocationProvider.AVAILABLE :
            case LocationProvider.TEMPORARILY_UNAVAILABLE : // 일시적으로 안되는 경우
            case LocationProvider.OUT_OF_SERVICE : // 일정 시간 지나면
        }
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


}
