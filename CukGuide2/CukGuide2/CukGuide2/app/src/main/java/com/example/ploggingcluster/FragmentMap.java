package com.example.ploggingcluster;


import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class FragmentMap extends Fragment {

    OnTabItemSelectedListener listener;
    Context context;
    OnRequestListener requestListener;

    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;

    MainData item;

    MarkerOptions myLocationMarker;
    GPSListener gpsListener;
    Location location;
    LatLng curPoint;

    public void setMapItem(MainData item){
        this.item = item;
        if (requestListener != null) {
            requestListener.onRequest(("getCurrentLocation"));
        } // OnRequestListener 객체의 onRequest 메서드를 호출
    }

    public void setPoint(LatLng curPoint){
        this.curPoint = curPoint;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        // 프래그먼트가 액티비티 위에 올라갈 때
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }

        if (context instanceof OnRequestListener) {
            requestListener = (OnRequestListener) context;
        }
    }

    @Override
    public void onDetach() {
        // 프래그먼트가 액티비티에서 내려올 때 호출
        super.onDetach();
        if(context != null) {
            context = null;
            listener = null;
            requestListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //        프래그먼트가 자신의 인터페이스를 처음 그리기 위해 호출합니다. View를 반환해야 합니다.
//        이 메서드는 프래그먼트의 레이아웃 루트이기 때문에 UI를 제공하지 않는 경우에는 null을 반환하면 됩니다.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Log.d("Map", "지도 준비됨.");
                mGoogleMap = googleMap;
            }
        });


        try {

            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationService();
            }
        });

        return rootView;

    }

    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void startLocationService() {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String message = "최근 위치 -> Latitude : " + latitude + "\nLongitude:" + longitude;

                Log.d("Map", message);

            }

            gpsListener = new GPSListener();
            long minTime = 100000;
            float minDistance = 0;

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, gpsListener);

            Toast.makeText(context.getApplicationContext(), "내 위치확인 요청함",
                    Toast.LENGTH_SHORT).show();

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "내 위치 -> Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.d("Map", message);


            showCurrentLocation(latitude, longitude);
        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    private void showCurrentLocation(Double latitude, Double longitude) {

        LatLng curPoint = new LatLng(latitude, longitude);

        if (mGoogleMap != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            showMyLocationMarker(curPoint);
        }
    }

    void showMyLocationMarker(LatLng curPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(curPoint);
            myLocationMarker.title("● 내 위치\n");
            myLocationMarker.snippet("● GPS로 확인한 위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            mGoogleMap.addMarker(myLocationMarker);
        } else {
            myLocationMarker.position(curPoint);
        }
    }

    public void shownMarker() {
        if (curPoint != null) {
            if (myLocationMarker == null && item != null) {
                myLocationMarker = new MarkerOptions();
                myLocationMarker.position(curPoint);
                myLocationMarker.title(item.getTitle());
                myLocationMarker.snippet("● GPS로 확인한 아이템 위치");
                myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
                mGoogleMap.addMarker(myLocationMarker);
                println("지도 성공");
            } else {
                myLocationMarker.position(curPoint);
                println("지도 실패");
            }
        }
    }


    private void println(String data) {
        Log.d(TAG, data);
    }

}