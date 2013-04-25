package com.example.navbelt;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	private AudioManager am;
	private AudioTrack audioTrack;
    private final int duration = 120; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];

    private final byte generatedSnd[] = new byte[2 * numSamples];
    void genTone(int freq){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freq));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
    	// kill any currently playing sounds
    	if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
        	audioTrack.flush();
        	audioTrack.stop();
        	audioTrack.release();
    	}
//    	am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);

        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
    
    void stopSound() {
    	if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
        	audioTrack.flush();
        	audioTrack.stop();
        	audioTrack.release();
    	}
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);
/*		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
	
		ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
		tg.startTone(ToneGenerator.TONE_DTMF_1, 99999);*/
		genTone(440);
		playSound();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void playToneNorth(View view){
		playTone(83);
	}
	
	public void playToneEast(View view){
		playTone(163);
	}

	public void playToneSouth(View view){
		playTone(241);
	}

	public void playToneWest(View view){
		playTone(353);
	}
	
	public void playToneStop(View view){
		stopSound();
	}
	
	void playTone(int freq) {
		
		genTone(freq);
		playSound();
	}
}
