package com.example.navbelt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Waypoints extends Activity {
	
	SoundPlayer sp;
	ArrayList<Location> waypoints;
	int targetindex;
	double change_t = 4; // threshold distance to advance to next waypoint, in metres.
	double warn_t = 6; // distance at which to send warning buzz about impending turn.
	boolean stopNavigation;
	
	void populateWaypoints() {
		double longitudes[] = {-74.651655, -74.651233, -74.651098, -74.650978, -74.649434, -74.648924};
		double latitudes[] = { 40.349905, 40.348948, 40.34896, 40.348832, 40.349228, 40.34848};
		assert (longitudes.length == latitudes.length);
		
		for (int i=0; i<latitudes.length; i++) {
			Location wp = new Location("dummy");
			wp.setLatitude(latitudes[i]);
			wp.setLongitude(longitudes[i]);
			waypoints.add(wp);
		}
	}
	
	String directionName(double bearing) {
		String dirs[] = {"NORTH", "NORTHEAST", "EAST", "SOUTHEAST", "SOUTH", "SOUTHWEST", "WEST", "NORTHWEST"};
		int index = (int)Math.round(bearing / 45.0);
		index = (index + 8) % 8; // take care of weird corner cases...
		return dirs[index];
	}

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
        
        waypoints = new ArrayList<Location>();
        populateWaypoints();
        targetindex = 0;
        sp = new SoundPlayer();
        stopNavigation = false;
        
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
	
	public void nextWaypoint(View view) {
		if (targetindex < waypoints.size() - 1) targetindex++;
		else targetindex = waypoints.size() - 1;
	}
	
	public void prevWaypoint(View view) {
		if (targetindex > 0) targetindex--;
		else targetindex = 0;
		stopNavigation = false;
	}
	
	/* Class My Location Listener */
    public class MyLocationListener implements LocationListener {
    	

    	@Override
    	public void onLocationChanged(Location loc) {
    		//TODO: consider adaptively setting threshold as function of distance between next two waypoints.
    		Location dest = waypoints.get(targetindex);
    		
    		String ndname = directionName(loc.bearingTo(dest));

    		String llText = "Waypoint " + targetindex + "\n\n"
    					+ "Current Location:\n" +
    				    + loc.getLatitude() + ", " + loc.getLongitude() + "\n\n" 
    					+ loc.bearingTo(dest) + "\n"
    					+ ndname + "\n\n"
    					+ loc.distanceTo(dest) + " m";
    		
    		if (stopNavigation) llText = llText + "\nNavigation stopped.";

    		TextView text = (TextView) findViewById(R.id.textView1);
    		text.setText(llText);
    		
    		if (!stopNavigation) {
	    		
	    		// send appropriate tone to Arduino
	    		sp.playSoundForHeading(loc.bearingTo(dest));
	    		
	    		// check distance to the current destination
	    		assert(warn_t >= change_t);
	    		if (loc.distanceTo(dest) < warn_t) {
	    			if (loc.distanceTo(dest) > change_t) {
	    				// warn the user about the impending change in direction
	    				// for now: do nothing.
	    			} else {
	    				// we are within the change zone: update the waypoint index
	
	    				// buzz the phone to warn the user
	    				Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	    				v.vibrate(500);
	    				
	    				// check if we are at final destination
	    				if (targetindex == waypoints.size()-1) {
	    					// tell the arduino to stop
	    					sp.playSoundForStop();
	    					
	    					// buzz the phone quickly three times.
	    					long[] times = {300,300,300,300,300,300}; 
	    					v.vibrate(times, -1);
	    					
	    					stopNavigation = true;
	    					
	    				} else {
	    					targetindex++;
	    				}
	    			}
	    		}
    		}
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
