/**
 * 
 */
package com.example.navbelt;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 *
 *
 */
public class SoundPlayer {

	private AudioTrack audioTrack;
	private final int duration = 5; // seconds
    private final int sampleRate = 4000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final byte generatedSnd[] = new byte[2 * numSamples];
	
	public SoundPlayer() {
		super();
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, generatedSnd.length);
	}
	
	/**
	 * The frequency is related to the heading h by f = h + 75.
	 * @param heading
	 * @return
	 */
	private float getFreq(float heading) {
		return 75 + heading;
	}
	
	/**
	 * Fill the generatedSnd array with samples corresponding to a sine wave 
	 * of the appropriate frequency for the desired heading.
	 * @param heading
	 */
	private void genToneForHeading(float heading) {
		genTone(getFreq(heading));
	}
	
	private void genToneForStop() {
		genTone(467); // the frequency of the stop signal is 467 Hz.
	}
	
    private void genTone(float freq){
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

    /**
     * Plays the sound encoded in PCM in the generatedSnd array.
     */
    private void playSound() {
    	stopSound();
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
    
    /**
     * Stops the currently playing sound.
     */
    public void stopSound() {
    	if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
    		audioTrack.pause();
    		audioTrack.flush();
    		audioTrack.release();
    	}
    }
	
	/**
	 * Play the sound needed to signal to the Arduino to move toward the given absolute heading.
	 * @param heading
	 */
	public void playSoundForHeading(float heading) {
		// sanitize the input
		float h = heading;
		while (h < 0) h += 360;
		h = h % 360;
		
		// produce the sound
		genToneForHeading(h);
		playSound();
	}
	
	public void playSoundForStop() {
		genToneForStop();
		playSound();
	}

	/**
	 * @param args
	 */
	public void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
