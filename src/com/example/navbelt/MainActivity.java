package com.example.navbelt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private SoundPlayer sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp = new SoundPlayer();
		
		Button next = (Button) findViewById(R.id.button1);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Waypoints.class);
                startActivityForResult(myIntent, 0);
            }

        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void playToneNorth(View view){
		sp.generateSound(SoundPlayer.DirFreq.NORTH);
	}
	
	public void playToneEast(View view){
		sp.generateSound(SoundPlayer.DirFreq.EAST);
	}

	public void playToneSouth(View view){
		sp.generateSound(SoundPlayer.DirFreq.SOUTH);
	}

	public void playToneWest(View view){
		sp.generateSound(SoundPlayer.DirFreq.WEST);
	}
	
	public void playToneStop(View view){
		sp.stopSound();
	}
}
