package com.example.loopbacklive;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class LoopbackLive extends Activity {

    private Button mLoopbackButton;
	private boolean mIsRecording;
	private byte[] buffer;
	private AudioRecord recorder;
	private AudioTrack audioTrack;
	
	static final int SAMPLE_RATE = 16000;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loopback_live);
        
        // set recording boolean to false
        mIsRecording = false;
        
        // button to initialize loopback
        mLoopbackButton = (Button)findViewById(R.id.loopbackButton);
        mLoopbackButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIsRecording) {
					mLoopbackButton.setText("Start loopback");
					mIsRecording = false;
				} else {
					mLoopbackButton.setText("Stop loopback");
					mIsRecording = true;
					// STEP 4: start recording
					loopbackAudio();
					// END STEP 4
				}
			}
		});
        
        // STEP 1: setup AudioRecord
        int minBufferSize = AudioRecord.getMinBufferSize(
        		SAMPLE_RATE,
        		AudioFormat.CHANNEL_IN_MONO,
        		AudioFormat.ENCODING_PCM_16BIT);
        buffer = new byte[minBufferSize];
        recorder = new AudioRecord(
        		MediaRecorder.AudioSource.MIC,
        		SAMPLE_RATE,
        		AudioFormat.CHANNEL_IN_MONO,
        		AudioFormat.ENCODING_PCM_16BIT,
        		minBufferSize);
        // END STEP 1
        
        // STEP 2: setup AudioTrack - for audio output
        audioTrack = new AudioTrack(
        		AudioManager.STREAM_MUSIC,
        		SAMPLE_RATE,
        		AudioFormat.CHANNEL_OUT_MONO,
        		AudioFormat.ENCODING_PCM_16BIT,
        		minBufferSize,
        		AudioTrack.MODE_STREAM);
        // END STEP 2
       
    }
	
	// STEP 3: while the audio is recording, play back the audio
	public void loopbackAudio() {
		recorder.startRecording();
		audioTrack.play();
		
		Thread loopbackThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (mIsRecording) {
					int bufferReadResult = recorder.read(buffer, 0, buffer.length);
					audioTrack.write(buffer, 0, bufferReadResult);
				}
				recorder.stop();
				audioTrack.stop();
				audioTrack.flush();
			}
			
		});
		
		loopbackThread.start();
	}
	// END STEP 3


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loopback_live, menu);
        return true;
    }
    
}
