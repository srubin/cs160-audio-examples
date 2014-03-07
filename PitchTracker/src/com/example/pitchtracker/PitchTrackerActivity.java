package com.example.pitchtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler;
import be.hogent.tarsos.dsp.pitch.PitchDetectionResult;
import be.hogent.tarsos.dsp.pitch.PitchProcessor;

public class PitchTrackerActivity extends Activity implements AudioProc.OnAudioEventListener, PitchDetectionHandler {

	private static final int SAMPLE_RATE = 16000;
	private AudioProc mAudioProc;
	private PitchProcessor mPitchProcessor;
	private TextView mPitchBox;
	private Button mListenButton;
	private static final String TAG = "PitchTrackerActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pitch_tracker);
		
		mAudioProc = new AudioProc(SAMPLE_RATE);
		setPitchProcessor();
		mAudioProc.setOnAudioEventListener(this);
		mPitchBox = (TextView)findViewById(R.id.pitch);
		mListenButton = (Button)findViewById(R.id.listen);
		
		mListenButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mAudioProc.isRecording()) {
					mAudioProc.stop();
					mListenButton.setText("Listen");
				} else {
					mAudioProc.listen();
					mListenButton.setText("Stop listening");
				}
			}
		});
		
	}

	public void setPitchProcessor() {
		PitchProcessor.PitchEstimationAlgorithm alg = 
				PitchProcessor.PitchEstimationAlgorithm.AMDF;
		mPitchProcessor = new PitchProcessor(alg, SAMPLE_RATE, mAudioProc.getBufferSize(), this);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pitch_tracker, menu);
		return true;
	}

	@Override
	public void processAudioProcEvent(AudioEvent ae) {
		// detect pitch
		mPitchProcessor.process(ae);
	}

	@Override
	public void handlePitch(final PitchDetectionResult pitchDetectionResult,
			AudioEvent audioEvent) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (pitchDetectionResult.isPitched()) {
					mPitchBox.setText(String.valueOf(pitchDetectionResult.getPitch()) + " Hz");
				} else {
					mPitchBox.setText("No pitch detected");
				}
			
			}
			
		});
	}

}
