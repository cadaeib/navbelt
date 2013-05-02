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
    
	public enum DirFreq {
		NORTH (83),
		NORTHEAST (127),
		EAST (163),
		SOUTHEAST (193),
		SOUTH (241),
		SOUTHWEST (293),
		WEST (353),
		NORTHWEST (397),
		STOP (449);
		
		private int freq = 0;
		
		DirFreq(int freq) {
			this.freq = freq;
		}
		
		int getFreq() {
			return freq;
		}
	}
	
	public SoundPlayer() {
		super();
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, generatedSnd.length);
	}
	
	private void genTone(DirFreq df) {
		genTone(df.getFreq());
	}
	
    private void genTone(int freq){
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
    	audioTrack.pause();
    	audioTrack.flush();
    	audioTrack.release();
    }
	
	
	public void generateSound(DirFreq df) {
		genTone(df);
		playSound();
	}

	/**
	 * @param args
	 */
	public void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
