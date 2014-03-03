package com.example.lowpass;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.filters.LowPassFS;
import be.hogent.tarsos.dsp.filters.LowPassSP;

import com.example.lowpass.AudioProc.OnAudioEventListener;

public class LowPass extends Activity implements OnAudioEventListener {

	public static final int SAMPLE_RATE = 11025;
	public static final String TAG = "LowPass";
	private AudioProc mAudioProc;
	private LowPassFS mLowPassFilter;
	private Button mListenButton;
	private AudioTrack mAudioTrack;
	private SeekBar mFreqSeek;
	private TextView mFreq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_low_pass);
		
		mFreqSeek = (SeekBar)findViewById(R.id.freqSeek);
		mFreq = (TextView)findViewById(R.id.freq);
		
		mLowPassFilter = new LowPassFS(0, SAMPLE_RATE);
		
		mFreqSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int freq,
					boolean fromUser) {
				if (fromUser) {
					mFreq.setText("" + freq + " Hz");
					mLowPassFilter = new LowPassFS(freq, SAMPLE_RATE);
				}
			}
		});
		
		

		mAudioProc = new AudioProc(SAMPLE_RATE);
		mAudioProc.setOnAudioEventListener(this);
		
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				SAMPLE_RATE,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				mAudioProc.getBufferSize(),
				AudioTrack.MODE_STREAM
				);
		mAudioTrack.setPlaybackRate(SAMPLE_RATE);

		mListenButton = (Button)findViewById(R.id.loopbackBtn);
		mListenButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mAudioProc.isRecording()) {
					mAudioProc.stop();
					mAudioTrack.stop();
					mListenButton.setText("Start Loopback");
				} else {
					mAudioProc.listen();
					mAudioTrack.play();
					mListenButton.setText("Stop Loopback");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.low_pass, menu);
		return true;
	}

	@Override
	public void processAudioProcEvent(AudioEvent ae) {
		mLowPassFilter.process(ae);
		byte[] filteredBuffer = ae.getByteBuffer();
		mAudioTrack.write(filteredBuffer, 0, filteredBuffer.length);
	}

}
