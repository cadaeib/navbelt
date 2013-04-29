package com.example.navbelt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Waypoints extends Activity {
	ArrayList<Double[]> waypoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waypoints);
		Button next = (Button) findViewById(R.id.button1);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
        waypoints = new ArrayList<Double[]>();
        waypoints.add( new Double[] {40.35, -74.66});
        
        /* Use the LocationManager class to obtain GPS locations */
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        LocationListener mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.waypoints, menu);
		return true;
	}
	
	public void getCurrentLocation(View view) {
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setText("Er this doesn't actually work.");
	}
	
	public double getBearing(double initLat, double initLon, ArrayList<Double[]> waypoints, int index) {
		Double[] nextPair = waypoints.get(index);
		double nextLong = nextPair[0];
		double nextLat = nextPair[1];
		
		double dLon = nextLong - initLon;
		
		double y = Math.sin(dLon) * Math.cos(nextLat);
		double x = Math.cos(initLat)*Math.sin(nextLat) -
		        Math.sin(initLat)*Math.cos(nextLat)*Math.cos(dLon);
		double bearing = Math.toDegrees(Math.atan2(y, x));
		
		return bearing;
	}
	
	/* Class My Location Listener */
    public class MyLocationListener implements LocationListener {

      @Override
      public void onLocationChanged(Location loc) {
        loc.getLatitude();
        loc.getLongitude();

        String Text = "My current location is: " +
        "Latitud = " + loc.getLatitude() +
        "Longitud = " + loc.getLongitude();
        
        Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setText("" + getBearing(loc.getLatitude(), loc.getLongitude(), waypoints, 0));
      }

      @Override
      public void onProviderDisabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
      }

      @Override
      public void onProviderEnabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {

      }
    }

}
