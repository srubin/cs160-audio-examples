package com.example.pitchlive;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.AudioFormat.Encoding;
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler;
import be.hogent.tarsos.dsp.pitch.PitchDetectionResult;
import be.hogent.tarsos.dsp.pitch.PitchProcessor;

public class PitchLiveActivity extends Activity implements PitchDetectionHandler {

	private Button mListenButton;
	private AudioRecord recorder;
	private byte[] buffer;
	private PitchProcessor mPitchProcessor;
	private TextView mPitchText;
	private boolean mIsRecording;
	private be.hogent.tarsos.dsp.AudioFormat tarsosFormat;
	public static final int SAMPLE_RATE = 16000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pitch_live);
		
		mIsRecording = false;
		
		mPitchText = (TextView)findViewById(R.id.pitch);
		
		mListenButton = (Button)findViewById(R.id.listen);
		mListenButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIsRecording) {
					mListenButton.setText("Listen");
					mIsRecording = false;
				} else {
					mListenButton.setText("Stop listening");
					mIsRecording = true;
					// STEP 5: start recording and detecting pitch
					listen();
					// END STEP 5
				}
			}
		});
		
		// STEP 1: set up recorder... same as in loopback example
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
		
		// STEP 2: create pitch detector
		mPitchProcessor = new PitchProcessor(
				PitchProcessor.PitchEstimationAlgorithm.AMDF,
				SAMPLE_RATE,
				minBufferSize,
				this);
		// END STEP 2
		
	}

	// STEP 3: Handle pitch event
	@Override
	public void handlePitch(
			PitchDetectionResult pitchDetectionResult,
			AudioEvent audioEvent) {
		String newText;
		
		// have we detected a pitch?
		if (pitchDetectionResult.isPitched()) {
			float result =
					pitchDetectionResult.getPitch();
			newText = String.valueOf(result) + " Hz";
		} else {
			newText = "No pitch detected";
		}
		
		final String pitchText = newText;
		
		// handlePitch will be run from a background thread
		// so we need to run it on the UI thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPitchText.setText(pitchText);
			}
		});
	}
	// END STEP 3
	
	// STEP 4: setup recording
	public void listen() {
		recorder.startRecording();
		
		tarsosFormat = new be.hogent.tarsos.dsp.AudioFormat(
						(float)SAMPLE_RATE, // sample rate
						16, // bit depth
						1, // channels
						true, // signed samples?
						false // big endian?
						);
		
		Thread listeningThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (mIsRecording) {
					int bufferReadResult =
							recorder.read(buffer, 0, buffer.length);
					AudioEvent audioEvent =
							new AudioEvent(
									tarsosFormat,
									bufferReadResult);
					audioEvent.setFloatBufferWithByteBuffer(buffer);
					mPitchProcessor.process(audioEvent);
				}
				recorder.stop();
			}
			
		});
		
		listeningThread.start();
	}
	// END STEP 4
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pitch_live, menu);
		return true;
	}


}
