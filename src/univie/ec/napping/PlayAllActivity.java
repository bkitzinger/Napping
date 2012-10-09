package univie.ec.napping;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PlayAllActivity extends Activity implements Callback,
OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener,
OnErrorListener{
	
	private static final String TAG = PlayAllActivity.class
	.getSimpleName();
	
	private MediaPlayer mPlayer;
	private TextView videoIDview;
	private TextView instructions;
	private SurfaceView mPlayView;
	private FrameLayout framelayout;
	private Button userTriggerBtn;
	private SurfaceHolder mHolder;
	private String fileName;
	private File[] videoList;
	private int videoID;
	private int pauseduration;
	private boolean pauseUserTrigger;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.showvideo);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        videoID = 0;
        
        //get Video List
        videoList = Configuration.videos;
        
      //load preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        pauseduration = Integer.parseInt(prefs.getString("timingPref",  "3000" ));
        pauseUserTrigger = prefs.getBoolean("pauseUserTrigger", true);
        
        //prepare Views
        framelayout = (FrameLayout) this.findViewById(R.id.framelayout);
        instructions = (TextView) this.findViewById(R.id.instructions);
        instructions.setText("Make your notes. Tap to continue ...");
        instructions.setVisibility(View.INVISIBLE);
        videoIDview = (TextView) this.findViewById(R.id.text);
        videoIDview.setText(String.valueOf(videoID+1));
        //if userTrigger is active create a button to trigger
        if(pauseUserTrigger){
        	userTriggerBtn = new Button(this);
        	userTriggerBtn.setBackgroundColor(Color.TRANSPARENT);
        	userTriggerBtn.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        	framelayout.addView(userTriggerBtn);
        	}
        
        
        try {
			mPlayView = (SurfaceView) findViewById(R.id.video_surface);
			mHolder = mPlayView.getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		} catch (Exception e) {
			Log.e(TAG, "Error while creating Surface:" + e.toString());
		}     
    }
    
    
    @Override
	/**
	 * Called when the activity pauses
	 */
    protected void onPause() {
		super.onPause();
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
    
    
    @Override
	/**
	 * Called when the activity is destroyed.
	 */
    protected void onDestroy() {
		super.onDestroy();
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
    
	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called when the Media Player is finished preparing and ready to play.
	 */
	public void onPrepared(MediaPlayer player) {
		delayedStartVideo();
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		// TODO Auto-generated method stub
		mPlayer.reset();
		videoID++;
		
		//if all videos played, return to selection screen. Otherwise play next
		if(videoID >= Configuration.videos.length){
			finish();
			Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show();
		}else{
			if(!pauseUserTrigger){
				videoIDview.setText(String.valueOf(videoID+1));
				videoIDview.bringToFront();
				videoIDview.setTextColor(Color.WHITE);
				prepareVideo(videoID);
			}else{
				instructions.setVisibility(View.VISIBLE);
				instructions.bringToFront();
				userTriggerBtn.bringToFront();
				userTriggerBtn.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	videoIDview.setText(String.valueOf(videoID+1));
		 				videoIDview.setTextColor(Color.WHITE);
		 				prepareVideo(videoID);
		             }
		         });
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mPlayer = new MediaPlayer();
		prepareVideo(videoID);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void prepareVideo(int vID){
		instructions.setVisibility(View.INVISIBLE);
		try {
			//resolve video ID
			fileName = videoList[vID].getName();
			File videoFile = new File(Configuration.sFolderVideos, fileName);
			if ((!videoFile.exists()) || (!videoFile.canRead())) {
				throw new IOException("Video file " + fileName + "not found!");
			}
			mPlayer.setDataSource(videoFile.getPath());
			mPlayer.setDisplay(mHolder);
			mPlayer.setScreenOnWhilePlaying(true);
			mPlayer.setOnPreparedListener(this);
			mPlayer.setOnCompletionListener(this);
			mPlayer.setOnVideoSizeChangedListener(this);
			mPlayer.setOnErrorListener(this);
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}
	
	//Starts video after showing Video ID
	public void delayedStartVideo(){
		
		 new Thread(new Runnable() {
		        public void run() {
		        	SystemClock.sleep(pauseduration);
		        	mPlayer.start();
		        }
		    }).start();
		 SystemClock.sleep(pauseduration);
		videoIDview.setTextColor(Color.TRANSPARENT);;
		
	}
}