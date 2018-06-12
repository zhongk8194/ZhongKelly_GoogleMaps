package com.example.zhongk8194.mymapsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private EditText locationSearch;
    private Location myLocation;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private boolean gotMyLocationOneTime;
    private double latitude, longitude;
    private boolean notTrackingMyLocation = false;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 5; //updates in msec
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0.0f;
    private static final int MY_LOC_ZOOM_FACTOR = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        //Add a marker at your place of birth and move the camera to it
        // When the marker is tapped, display "Born here."
        LatLng sanDiego = new LatLng(33, -117);
        mMap.addMarker(new MarkerOptions().position(sanDiego).title("Born here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sanDiego));

        /** if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         Log.d("MyMapsApp", "Failed FINE Permission Check");
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
         }

         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         Log.d("MyMapsApp", "Failed COARSE Permission Check");
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
         }

         if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
         (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
         mMap.setMyLocationEnabled(true);

         }
         **/
        locationSearch = (EditText) findViewById(R.id.editText);

        //Search supplement
        gotMyLocationOneTime = false;
        getLocation();
    }

    public void dropAmarker(String provider) {
        //if(locationManager != null)
        //  if (checkSelfPermission fails) // like above code
        //      return
        //  myLocation = locationManager.getLastKnownLocation(provider)
        //LatLng userLocation = null;
        //if (myLocation == null) print log or toast message
        //else
        //  userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude)
        //  CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR)
        //  if (provider == LocationManager.GPS_PROVIDER)
        //      add circle for the marker with 2 outer rings (red)
        //      mMap.addCircle(new CircleOptions())
        //          .center(userLocation)
        //          .radius(1)
        //          .strokeColor(Color.RED)
        //          .strokeWidth(2)
        //          .fillColor(Color.RED)
        //  else add circle for the marker with 2 outer rings (blue)
        //      mMap.addCircle(new CircleOptions())
        //          .center(userLocation)
        //          .radius(1)
        //          .strokeColor(Color.BLUE)
        //          .strokeWidth(2)
        //          .fillColor(Color.BLUE)
        //  mMap.animateCamera(update);


        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            LatLng userLocation = null;
            myLocation = locationManager.getLastKnownLocation(provider);

            if (myLocation == null) {
                Log.d("MyMapsApp", "myLocation is null");
            } else {
                userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);
                if (provider == LocationManager.GPS_PROVIDER) {
                    mMap.addCircle(new CircleOptions().center(userLocation).radius(1).strokeColor(Color.RED).strokeWidth(2).fillColor(Color.RED));
                    mMap.addCircle(new CircleOptions().center(userLocation).radius(2).strokeColor(Color.RED).strokeWidth(2).fillColor(Color.TRANSPARENT));
                } else if (provider == LocationManager.NETWORK_PROVIDER) {
                    mMap.addCircle(new CircleOptions().center(userLocation).radius(1).strokeColor(Color.BLUE).strokeWidth(2).fillColor(Color.BLUE));
                    mMap.addCircle(new CircleOptions().center(userLocation).radius(2).strokeColor(Color.BLUE).strokeWidth(2).fillColor(Color.TRANSPARENT));
                }
                mMap.animateCamera(update);
            }
        }
    }

    public void trackMyLocation(View view) {
        //kick off the location tracker using getLocation to start the LocationListener
        //if (notTrackingMyLocation) {getLocation(); notTrackingMyLocation = false;}
        //else {removeUpdates for both network and gps; notTrackingMyLocation = true;}
        getLocation();
        Log.d("MyMapsActivity", "trackMyLocation: calling getLocation");
        if (notTrackingMyLocation) {
            getLocation();
            notTrackingMyLocation = false;
        } else {
            locationManager.removeUpdates(locationListenerGPS);
            locationManager.removeUpdates(locationListenerNetwork);
            notTrackingMyLocation = true;
        }

    }

    //Add a View button and method to switch between satellite and map views
    public void changeView(View view) {
        Log.d("MyMapsApp", "changing view");
        if (mMap.getMapType() == 1) {
            mMap.setMapType(2);
        } else if (mMap.getMapType() == 2) {
            mMap.setMapType(1);
        }
    }

    public void onSearch(View v) {
        String location = locationSearch.getText().toString();

        List<Address> addressList = null;
        List<Address> addressListZip = null;

        //Use LocationManager for user location
        //Implement the LocationListener interface to setup location services
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);

        Log.d("MyMapsApp", "onSearch: location = " + location);
        Log.d("MyMapsApp", "onSearch: provider " + provider);


        LatLng userLocation = null;

        //Check the last known location, need to specifically list the provider (network or gps)

        try {
            if (locationManager != null) {
                Log.d("MyMapsApp", "onSearch: locationManager is not null");

                if ((myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)) != null) {
                    userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    Log.d("MyMapsApp", "onSearch: using NETWORK_PROVIDER userLocation is: "
                            + myLocation.getLatitude() + " " + myLocation.getLongitude());
                    Toast.makeText(this, "UserLoc" + myLocation.getLatitude() + myLocation.getLongitude(), Toast.LENGTH_SHORT);
                } else if ((myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)) != null) {
                    userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    Log.d("MyMapsApp", "onSearch: using GPS_PROVIDER userLocation is: "
                            + myLocation.getLatitude() + " " + myLocation.getLongitude());
                    Toast.makeText(this, "UserLoc" + myLocation.getLatitude() + myLocation.getLongitude(), Toast.LENGTH_SHORT);
                } else {
                    Log.d("MyMapsApp", "onSearch: myLocation is null from getLastKnownLocation");
                }
            }
        } catch (SecurityException | IllegalArgumentException e) {
            Log.d("MyMapsApp", "onSearch: Exception getLastKnownLocation");
            Toast.makeText(this, "onSearch: Exception getLastKnownLocation", Toast.LENGTH_SHORT);

        }
        if (!location.matches("")) {
            Log.d("MyMapsApp", "onSearch: location field is populated");

            Geocoder geocoder = new Geocoder(this, Locale.US);

            try {
                //Get a list of the addresses
                addressList = geocoder.getFromLocationName(location, 100,
                        userLocation.latitude - (5.0 / 60),
                        userLocation.longitude - (5.0 / 60),
                        userLocation.latitude + (5.0 / 60),
                        userLocation.longitude + (5.0 / 60));

                Log.d("MyMapsApp", "onSearch: addressList is created");

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!addressList.isEmpty()) {
                Log.d("MyMapsApp", "onSearch: AddressList size is: " + addressList.size());
                for (int i = 0; i < addressList.size(); i++) {
                    Address address = addressList.get(i);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    //Place a marker on the map
                    mMap.addMarker(new MarkerOptions().position(latLng).title(i + ": " + address.getSubThoroughfare()
                            + " " + address.getThoroughfare()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    Log.d("MyMapsApp", "added Marker and updated animateCamera");
                }
            }
        }
        //end onSearch()
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //Get GPS status, isProviderEnabled returns true if user has enabled gps
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                Log.d("MyMapsApp", "getLocation: GPS is enabled");
            }

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                Log.d("MyMapsApp", "getLocation: Network is enabled");
            }

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("MyMapsApp", "getLocation: no provider enabled!");
            } else {
                if (isNetworkEnabled) {
                    //Request location updates
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        locationListenerNetwork);
            }
            if (isGPSEnabled) {
                //locationManager request for GPS_PROVIDER
                //Code here...
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } //is this if statement needed
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
            }
        } catch (Exception e) {
            Log.d("MyMapsApp", "getLocation: Exception in getLocation");
            e.printStackTrace();
        }
    }

    //LocationListener to setup callbacks for requestLocationUpdates
    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            dropAmarker(LocationManager.NETWORK_PROVIDER);

            //Check if doing one time, if so remove updates to both gps and network
            if (gotMyLocationOneTime == false) {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerNetwork);
                gotMyLocationOneTime = true;
            } else {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("MyMapsApp", "locationListenerNetwork: status change");
            switch (i) {
                case LocationProvider.AVAILABLE:
                    String status = "AVAILABLE";
                    Log.d("MyMapsApp", "onStatusChanged: status = " + status + ", location is updating");
                    Toast.makeText(getApplicationContext(), "onStatusChanged: Location Status = " + status + ", updating", Toast.LENGTH_SHORT).show();
                    break;

                case LocationProvider.OUT_OF_SERVICE:
                    String status1 = "OUT_OF_SERVICE";
                    Log.d("MyMapsApp", "onStatusChanged: status = " + status1);
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    String status2 = "TEMPORARILY_UNAVAILABLE";
                    Log.d("MyMapsApp", "onStatusChanged: status = " + status2);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;
                default:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;
            }

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            dropAmarker(LocationManager.GPS_PROVIDER);
            //if doing one time remove updates to both gps and network
            //else do nothing.

            if (gotMyLocationOneTime == false) {
                locationManager.removeUpdates(locationListenerNetwork);
                locationManager.removeUpdates(locationListenerGPS);
                gotMyLocationOneTime = true;
            }


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

            //switch (i)
            //case LocationProvider.AVAILABLE
            //printout log.d and, or toast message
            //break;
            //case LocationProvider.OUT_OF_SERVICE
            //enable network updates
            //break;
            //case LocationProvider.TEMPORARILY_UNAVAILABLE:
            //enable both network and gps
            //break;
            //default:
            //enable both network and gps

            switch (i) {
                case LocationProvider.AVAILABLE:
                    String status = "AVAILABLE";
                    Log.d("MyMapsApp", "onStatusChanged: status = " + status + ", location is updating");
                    Toast.makeText(getApplicationContext(), "onStatusChanged: Location Status = " + status + ", updating", Toast.LENGTH_SHORT).show();
                    break;

                case LocationProvider.OUT_OF_SERVICE:
                    String status1 = "OUT_OF_SERVICE";
                    Log.d("MyMapsApp", "onStatusChanged: status = " + status1);
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    String status2 = "TEMPORARILY_UNAVAILABLE";
                    Log.d("MyMapsApp", "onStatusChanged: status = " + status2);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;
                default:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;
            }

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    public void clear(View view){
        mMap.clear();
    }
}

