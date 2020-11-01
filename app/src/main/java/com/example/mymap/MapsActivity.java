package com.example.mymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG="Estilo del mapa";
    private static final int REQUEST_LOCATION_PERMISSION=1;
    private Spinner spinner;
    private double actualLat = 0;
    private double actualLong = 0;
    private double finalLat = 0;
    private double finalLong = 0;
    private LatLng actualPosition;
    private String description="";
    private int seleccion = 0;
    private LatLng japon = new LatLng(35.680513, 139.769051);
    private LatLng alemania = new LatLng(52.516934, 13.403190);
    private LatLng italia = new LatLng(41.902609, 12.494847);
    private LatLng francia = new LatLng(48.843489, 2.355331);
    private boolean isAlertDisplayed = false;
    static final String ALERT_STATE = "state_of_Alert";
    static final String SELECTED_TYPE = "selected_type";
    static final String MESSAGE_ALERT = "message_alert";
    static final String LATITUD_FINAL = "latitud_final";
    static final String LONGITUD_FINAL = "longitud_final";
    static final String LATITUD_ACTUAL = "latitud_actual";
    static final String LONGITUD_ACTUAL = "longitud_actual";

    private ArrayList<SpinnerItem> mSpinnerList;
    private ItemAdapter mAdapter;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(ALERT_STATE,isAlertDisplayed);
        savedInstanceState.putInt(SELECTED_TYPE,seleccion);
        savedInstanceState.putString(MESSAGE_ALERT,description);
        savedInstanceState.putDouble(LATITUD_FINAL,finalLat);
        savedInstanceState.putDouble(LONGITUD_FINAL,finalLong);
        savedInstanceState.putDouble(LATITUD_ACTUAL,actualLat);
        savedInstanceState.putDouble(LONGITUD_ACTUAL,actualLong);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        spinner = (Spinner) findViewById(R.id.maptype);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initList();
        mAdapter = new ItemAdapter(this,mSpinnerList);
        spinner.setAdapter(mAdapter);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map,mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            isAlertDisplayed = savedInstanceState.getBoolean(ALERT_STATE);
            seleccion = savedInstanceState.getInt(SELECTED_TYPE);
            description = savedInstanceState.getString(MESSAGE_ALERT);
            finalLong = savedInstanceState.getDouble(LONGITUD_FINAL);
            finalLat = savedInstanceState.getDouble(LATITUD_FINAL);
            actualLong = savedInstanceState.getDouble(LONGITUD_ACTUAL);
            actualLat = savedInstanceState.getDouble(LATITUD_ACTUAL);
        }
    }

    private void initList(){
        String[] maptypes = getResources().getStringArray(R.array.map_style);
        int[] images = {R.drawable.home,R.drawable.world,R.drawable.satel,R.drawable.mountain,R.drawable.pano};
        mSpinnerList=new ArrayList<>();
        for (int i=0;i<maptypes.length;i++){
            mSpinnerList.add(new SpinnerItem(maptypes[i],images[i]));
        }

    }
    private void setUp() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seleccion=position;
                isAlertDisplayed=true;
                setMapStyle(seleccion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setMapStyle(int pos){
        actualPosition = new LatLng(actualLat, actualLong);
        LatLng ubicacionFinal = new LatLng(0,0);
        String name = getResources().getString(R.string.destino) +": ";
        switch (seleccion){
            case 1 :
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                setCamera(japon);
                ubicacionFinal=japon;
                name+= getResources().getString(R.string.japon);
                break;
            case 2 :
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                setCamera(alemania);
                ubicacionFinal=alemania;
                name+= getResources().getString(R.string.alemania);
                break;
            case 3 :
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                setCamera(italia);
                ubicacionFinal=italia;
                name+= getResources().getString(R.string.italia);
                break;
            case 4 :
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                setCamera(francia);
                ubicacionFinal=francia;
                name+= getResources().getString(R.string.francia);
                break;
            case 0:
                mMap.clear();
                initMarkers();
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                setCamera(actualPosition);
                break;
        }
        if(pos!=0) {
            Location finalLoc = new Location("Destino");
            Location actualLoc = new Location("Origen");
            actualLoc.setLatitude(actualLat);
            actualLoc.setLongitude(actualLong);
            finalLoc.setLatitude(ubicacionFinal.latitude);
            finalLoc.setLongitude(ubicacionFinal.longitude);
            setRoute(ubicacionFinal, actualPosition, name);
            if(isAlertDisplayed){
                description=getDistance(actualLoc,finalLoc);
                AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
                alert.setTitle(R.string.distancia);
                alert.setMessage(description);
                alert.setCancelable(false);
                alert.setPositiveButton(R.string.aceptar, ((DialogInterface dialogInterface, int i) -> {

                        isAlertDisplayed=false;

                }));

                AlertDialog dialog = alert.create();
                dialog.show();
            }
        }
    }

    private void initMarkers(){
        getUbication();
        float zoom = 16;
        mMap.addMarker(new MarkerOptions().position(japon).title(getResources().getString(R.string.japon)));
        mMap.addMarker(new MarkerOptions().position(alemania).title(getResources().getString(R.string.alemania)));
        mMap.addMarker(new MarkerOptions().position(italia).title(getResources().getString(R.string.italia)));
        mMap.addMarker(new MarkerOptions().position(francia).title(getResources().getString(R.string.francia)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualPosition,zoom));
    }

    private String  getDistance(Location actualLocation, Location finalLocation) {

        double distancia = actualLocation.distanceTo(finalLocation);
        distancia /= 1000;
        return getString(R.string.origen) + getNameUbication(actualLocation.getLatitude(),actualLocation.getLongitude()) + "\n\n" + getString(R.string.destino) + getNameUbication(finalLocation.getLatitude(),finalLocation.getLongitude()) + "\n\n" + getString(R.string.distancia) + distancia + getString(R.string.medida);
    }

    private String getNameUbication(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            return e.getMessage();
        }
    }


    private void setRoute(LatLng ubicacionFinal, LatLng actualPosition,String name) {
        finalLat = ubicacionFinal.latitude;
        finalLong = ubicacionFinal.longitude;
        mMap.clear();
        Polyline line = mMap.addPolyline(new PolylineOptions().add(actualPosition, ubicacionFinal).width(15).color(Color.BLUE).geodesic(true));
        mMap.addMarker(new MarkerOptions().position(actualPosition).title(getString(R.string.actualLocation)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.addMarker(new MarkerOptions().position(ubicacionFinal).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualPosition,0));
    }

    private void setCamera(LatLng latLng) {
        float zoom=13;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocation();

        setMapLongClick(mMap);
        setPoiClick(mMap);
        setInfoWindowClickToPanorama(mMap);


        //String[] maptypes = getResources().getStringArray(R.array.map_style);
        //ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(maptypes));
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.style_spinner,arrayList);
        //spinner.setAdapter(arrayAdapter);

    }

    private void getUbication() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0, locListener);
        actualLat = location.getLatitude();
        actualLong = location.getLongitude();
        actualPosition = new LatLng(actualLat, actualLong);
        addMarker(actualPosition);
    }

    private void addMarker(LatLng actualPosition) {
        CameraUpdate ubicacion = CameraUpdateFactory.newLatLngZoom(actualPosition, 16);
        mMap.addMarker(new MarkerOptions().position(actualPosition).title(getString(R.string.actualLocation)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.animateCamera(ubicacion);
    }

    private void setMapLongClick(final GoogleMap map){
        map.setOnMapLongClickListener((LatLng latLng) -> {
                       String snippet = String.format(Locale.getDefault(),"Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                        .position(latLng)
                        .title(getString(R.string.app_name))
                        .snippet(snippet));

        });
    }

    private void setPoiClick(final GoogleMap map){
        map.setOnPoiClickListener((PointOfInterest pointOfInterest) -> {

                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title(pointOfInterest.name));
                poiMarker.setTag("poi");
                poiMarker.showInfoWindow();

        });
    }

    private void enableMyLocation() {
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager
        .PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            setMapStyle(seleccion);
            setUp();
        } else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_LOCATION_PERMISSION){
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    enableMyLocation();
                }
        }
    }

    private void setInfoWindowClickToPanorama(GoogleMap mMap){
        mMap.setOnInfoWindowClickListener((Marker marker)-> {

                if(marker.getTag()=="poi"){
                    StreetViewPanoramaOptions options = new StreetViewPanoramaOptions()
                            .position(marker.getPosition());
                    SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                            new SupportStreetViewPanoramaFragment().newInstance(options);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.map,streetViewPanoramaFragment)
                            .addToBackStack(null)
                            .commit();

                }

        });
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

}