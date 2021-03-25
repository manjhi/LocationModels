package com.example.autopalce;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    double lats,lngs,latd,lngd;
    String name;
     GoogleMap mMap;
    Api api;
    Button search;
    String sourcelat,sourcelng,destilat,destilng;

    GoogleApiClient mGoogleApiClient;



     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);




         search=(Button)findViewById(R.id.find);

         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                 .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);


         api = ApiClient.apiclient().create(Api.class);


         final PlaceAutocompleteFragment source = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.source);
         source.setOnPlaceSelectedListener(new PlaceSelectionListener() {
             @Override
             public void onPlaceSelected(Place place) {
                 Toast.makeText(getApplicationContext(), "Latitude: " + (int) place.getLatLng().latitude + "\n" + "Longitude: " + (int) place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
                 lats = place.getLatLng().latitude;
                 lngs = place.getLatLng().longitude;
                 sourcelat = String.valueOf(lats);
                 sourcelng = String.valueOf(lngs);

                 name = (String) place.getName();
                 // Add a sydnay in Sydney, Australia, and move the camera.
                 LatLng source = new LatLng(lats, lngs);
                 mMap.addMarker(new MarkerOptions().position(source).title(name));
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 18f));
             }

             @Override
             public void onError(Status status) {
                 Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();

             }
         });


         PlaceAutocompleteFragment destination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.desti);
         destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
             @Override
             public void onPlaceSelected(Place place) {
                 Toast.makeText(getApplicationContext(), "Latitude: " + (int) place.getLatLng().latitude + "\n" + "Longitude: " + (int) place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
                 latd = place.getLatLng().latitude;
                 lngd = place.getLatLng().longitude;

                 destilat = String.valueOf(latd);
                 destilng = String.valueOf(lngd);
                 name = (String) place.getName();
                 // Add a sydnay in Sydney, Australia, and move the camera.
                 LatLng desti = new LatLng(latd, lngd);
                 mMap.addMarker(new MarkerOptions().position(desti).title(name));
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(desti, 18f));
             }

             @Override
             public void onError(Status status) {
                 Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();

             }
         });


         // Retrieve the content view that renders the map.
         // setContentView(R.layout.m);
         // Get the SupportMapFragment and request notification
         // when the map is ready to be used.
         search.setOnClickListener(new View.OnClickListener() {
             LatLng sour=new LatLng(lats,lngs);
             LatLng destina=new LatLng(latd,lngd);
             @Override
             public void onClick(final View v) {
                 LinkedHashMap<String, String> data = new LinkedHashMap<>();
                 data.put("origin", sourcelat + "," + sourcelng);
                 data.put("destination", destilat + "," + destilng);
                 data.put("key", getResources().getString(R.string.key));

                 Call<Map> placeDataCall = api.placedata(data);
                 placeDataCall.enqueue(new Callback<Map>() {
                     @Override
                     public void onResponse(Call<Map> call, Response<Map> response) {
//                         Log.d("onResponse: ", response.toString());
//                         response.body();
                         String status=response.body().get("status").toString();

                         Log.d("onResponse",status);

                         if (status.equals("OK")){
                             String distance=((LinkedTreeMap)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)((ArrayList)response.body().get("routes")).get(0)).get("legs")).get(0)).get("distance")).get("text").toString();
                             String duration=((LinkedTreeMap)((LinkedTreeMap)(((ArrayList)((LinkedTreeMap)((ArrayList)response.body().get("routes")).get(0)).get("legs")).get(0))).get("duration")).get("text").toString();
                             String points=((LinkedTreeMap)((LinkedTreeMap)((ArrayList)response.body().get("routes")).get(0)).get("overview_polyline")).get("points").toString();
                             Toast.makeText(MainActivity.this, distance+"\n"+duration+"\n"+points, Toast.LENGTH_SHORT).show();

                             List<LatLng> pointt=PolyLines.decodePoly(points);


//                             for (LatLng s:pointt){
//                                 PolylineOptions route=new PolylineOptions();
//                                 Log.d("latlng", s.latitude+","+s.longitude);
//                                     route.add(new LatLng(s.latitude,s.longitude));
//                                     route.color(Color.BLUE).width(10);
//                                 mMap.addPolyline(route);
//                                 }
                             ArrayList p=new ArrayList();
//
//                             PolylineOptions polylineOptions=new PolylineOptions();


                             for (LatLng s:pointt){
                                    LatLng pos=new LatLng(s.latitude,s.longitude);
                                 Log.d("Latlng1",s.latitude+","+s.longitude);
                                     p.add(pos);
                                 }
//                                 polylineOptions.addAll(p);
//                                 polylineOptions.width(12);
//                                 polylineOptions.color(Color.BLUE);
//                                 polylineOptions.geodesic(true);

                             double d1[]=new double[p.size()];
                             double d2[]=new double[p.size()];
                                 for (int i=0;i<p.size();i++) {
                                     Log.d("ArrayList", p.toString());
                                     d1[i]=((LatLng) p.get(i)).latitude;
                                     d2[i]=((LatLng) p.get(i)).longitude;
                                 }


                                 for (int i=0;i<d1.length;i++){
                                     Log.d("d1 and d2",d1[i]+","+d2[i]);
                                    PolylineOptions polylineOptions=new PolylineOptions();
                                     LatLng latLng=new LatLng(d1[i],d2[i]);
                                     polylineOptions.add(latLng);


                                     polylineOptions.color(Color.BLUE);
                                     polylineOptions.width(12);
                                     mMap.addPolyline(polylineOptions);

                                 }

//                             PolylineOptions route=new PolylineOptions();
//                             for (LatLng s:pointt){
//                                 LatLng pos=new LatLng(s.latitude,s.longitude);
//                                 route.add(pos);
//                             }

                             //route.add(sour,destina);
                            // mMap.addPolyline();
                             Log.d("size", String.valueOf(points.length()));

//                             Log.d("route",route.toString());
                         }else {
                             Toast.makeText(MainActivity.this, "Can't Find", Toast.LENGTH_SHORT).show();

                         }

                     }

                     @Override
                     public void onFailure(Call<Map> call, Throwable t) {

                     }
                 });
             }
         });
         }




    @Override
    public void onMapReady(GoogleMap googleMap) {
    mMap=googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

}
