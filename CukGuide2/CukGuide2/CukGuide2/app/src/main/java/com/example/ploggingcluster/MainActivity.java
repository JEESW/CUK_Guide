package com.example.ploggingcluster;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnTabItemSelectedListener, OnRequestListener, MyApplication.OnResponseListener{
    private static final String TAG = "MainActivity";

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    FragmentPicture fragment_picture;
    FragmentMap fragment_map;
    ChatFragment chatFragment;

    String currentAddress;
    String currentDateString;
    Date currentDate;
    SimpleDateFormat todayDateFormat;

    BottomNavigationView bottomNavigation;

    Location currentLocation; // 현재 위치를 담을 변수
    GPSListener gpsListener;  // 위치 정보를 수신할 변수

    int locationCount = 0; // 위치를 한 번 확인한 후에는 위치 요청을 취소할 수 있도록 위치 정보 확인횟수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment_picture = new FragmentPicture();
        fragment_map = new FragmentMap();
        chatFragment = new ChatFragment();

        Intent intent = getIntent(); /*데이터 수신*/
        String name = intent.getExtras().getString("name");
        if (name != null) {
            fragment_picture.setName(name);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                        return true;
                    case R.id.tab2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_map).commit();
                        return true;
                    case R.id.tab3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                        return true;

                }
                return false;
            }
        });

        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.ACCESS_FINE_LOCATION,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
//                        showToast("허용된 권한 갯수 : " + permissions.size());
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        showToast("거부된 권한 갯수 : " + permissions.size());
                    }
                })
                .start();

        setPicturePath();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void setPicturePath() {  // 사진경로 넣기
        String folderPath = getFilesDir().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = folderPath + File.separator + "photo";

        File photoFolder = new File(AppConstants.FOLDER_PHOTO);
        if (!photoFolder.exists()) {
            photoFolder.mkdirs();
        }
    }

    @Override
    public void onTabSelected(int position){
        if (position == 0) {
//            bottomNavigation.setSelectedItemId(R.id.tab1);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
        } else if (position == 1) {
//            bottomNavigation.setSelectedItemId(R.id.tab2);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
        } else if (position == 2) {
//            bottomNavigation.setSelectedItemId(R.id.tab3);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
        } else if (position == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_picture).commit();
        } else if (position == 4) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_map).commit();
        } else if (position == 5) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
        }
    }

    @Override
    public void showFragmentItem(MainData item) {
        fragment2 = new Fragment2();
        if(item != null){
            fragment2.setItem(item);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment2).commit();
    }

    public void onRequest(String command) {
        // 게시판 등록 프래그먼트에서 호출
        // 이 메서드가 호출되면 위치 확인이 시작될 수 있도록 getCurrentLocation 메서드 호출
        if (command != null) {
            if(command.equals("getCurrentLocation")){
                getCurrentLocation();
            }
        }

    }
    public void getCurrentLocation() {
        // 현재 일자를 확인하여 picture 프래그먼트에 설정한 후 LocationManager 객체에게 현재 위치를 요청합니다.
        // set current time
        currentDate = new Date();

        //currentDateString = AppConstants.dateFormat3.format(currentDate);
        if (todayDateFormat == null) {
            todayDateFormat = new SimpleDateFormat(getResources().getString(R.string.today_date_format));
        }
        currentDateString = todayDateFormat.format(currentDate);
        AppConstants.println("currentDateString : " + currentDateString);

        if (fragment_picture != null) {
            fragment_picture.setDateString(currentDateString);
        }


        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();

                String message = "Last Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
                println(message);

                getCurrentAddress();
            }

            gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, gpsListener);

            println("Current location requested.");

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationService(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            manager.removeUpdates(gpsListener);
            println("Current location requested");

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResponse(int requestCode, int responseCode, String response) {

    }

    class GPSListener implements LocationListener {
        public void onLocationChanged (Location location) {
            currentLocation = location;

            locationCount++;

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            LatLng curPoint = new LatLng(latitude, longitude);
            FragmentMap fragmentMap = new FragmentMap();
            fragmentMap.setPoint(curPoint);

            String message = "Current Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
            println(message);

            getCurrentAddress();
        }

        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            LocationListener.super.onStatusChanged(provider, status, extras);
        }
    }

    public void getCurrentAddress() {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            currentAddress = null;

            Address address = addresses.get(0);
            if (address.getLocality() != null) {
                currentAddress =  address.getLocality();
            }

            if (address.getSubLocality() != null) {
                if (currentAddress != null) {
                    currentAddress += " " + address.getSubLocality();
                } else {
                    currentAddress = address.getSubLocality();
                }
            }

            String adminArea = address.getAdminArea();
            String country = address.getCountryName();
            println("Address : " + country + " " + adminArea + " " + currentAddress);

            if (fragment_picture != null) {
                fragment_picture.setAddress(currentAddress);
            }
        }
    }

    private void println(String data) {
        Log.d(TAG, data);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}