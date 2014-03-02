package com.example.pitchtracker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import be.hogent.tarsos.dsp.AudioEvent;

public class AudioProc {
	
	public interface OnAudioEventListener {
		public void processAudioProcEvent(AudioEvent ae);
	}

	private int mBufferSize;
	private short[] mBuffer;
	private AudioRecord mRecorder;
	private int mSampleRate;
	private boolean mIsRecording = false;
	private be.hogent.tarsos.dsp.AudioFormat mTarsosFormat;
	private AudioProc.OnAudioEventListener mOnAudioEventListener;

	public AudioProc(int sampleRate) {
		mSampleRate = sampleRate;
		mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mBuffer = new short[mBufferSize];
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				mBufferSize);
		mTarsosFormat = new be.hogent.tarsos.dsp.AudioFormat(
				(float)mSampleRate, 16, 1, true, true);
	}
	
	public int getBufferSize() {
		return mBufferSize;
	}
	
	public boolean isRecording() {
		return mIsRecording;
	}
	
	public void listen() {
		mRecorder.startRecording();
		mIsRecording  = true;
		processAudio();
	}
	
	public void stop() {
		mIsRecording = false;
		mRecorder.stop();
	}
	
	public void setOnAudioEventListener(AudioProc.OnAudioEventListener listener) {
		mOnAudioEventListener = listener;
	}
	
	private void processAudio() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (mIsRecording) {
					int bufferReadResult = mRecorder.read(mBuffer, 0, mBufferSize);
					AudioEvent audioEvent = new AudioEvent(mTarsosFormat, bufferReadResult);
					audioEvent.setFloatBufferWithShortBuffer(mBuffer);
					if (mOnAudioEventListener != null) {
						mOnAudioEventListener.processAudioProcEvent(audioEvent);	
					}
				}
			}
		}).start();
	}
}
