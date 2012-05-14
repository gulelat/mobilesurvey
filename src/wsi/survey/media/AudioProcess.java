package wsi.survey.media;

import java.util.HashMap;
import java.util.Random;

import wsi.survey.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class AudioProcess {

	private static MediaPlayer mediaPlayer = null;
	
	private static SoundPool soundPool = null;
	private static HashMap<Integer, Integer> soundMap;
	
	// media
	public static void loadMedia(Context context){
		try {
			Random random = new Random(System.currentTimeMillis());
			int rand = random.nextInt(3);
			switch (rand) {
			case 0:
				mediaPlayer = MediaPlayer.create(context, R.raw.music_0);
				break;
			case 1:
				mediaPlayer = MediaPlayer.create(context, R.raw.music_1);
				break;
			case 2:
				mediaPlayer = MediaPlayer.create(context, R.raw.music_2);
				break;
			}
			mediaPlayer.setLooping(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void playMedia(){
		if(mediaPlayer != null && !mediaPlayer.isPlaying()){
			mediaPlayer.start();
		}
	}
	
	public static void pauseMedia(){
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			mediaPlayer.pause();
		}
	}
	
	public static void stopMedia(){
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	
	
	
	// sound
	public static void loadSound(Context context){
		try {
			soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
			soundMap = new HashMap<Integer, Integer>();
			
			soundMap.put(0, soundPool.load(context, R.raw.btn_right, 1));		// btn right
			soundMap.put(1, soundPool.load(context, R.raw.btn_wrong, 1));		// btn wrong
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void playBtnRight(float volume){
		if(soundPool != null){
			soundPool.play(soundMap.get(0), volume, volume, 1, 0, 1.0f);
		}
	}

	public static void playBtnWrong(float volume){
		if(soundPool != null){
			soundPool.play(soundMap.get(1), volume, volume, 1, 0, 1.0f);
		}
	}
	
	public static void stopSound(){
		if(soundPool != null){
			for (int i = 0; i < soundMap.size(); i++) {
				soundPool.stop(i);
			}
			soundPool.release();
			soundPool = null;
		}
	}
	
}
