package com.example.lowpass;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import be.hogent.tarsos.dsp.AudioEvent;

public class AudioProc {
	
	public interface OnAudioEventListener {
		public void processAudioProcEvent(AudioEvent ae);
	}

	private int mBufferSize;
	public byte[] mBuffer;
	private AudioRecord mRecorder;
	private int mSampleRate;
	private boolean mIsRecording = false;
	private be.hogent.tarsos.dsp.AudioFormat mTarsosFormat;
	private AudioProc.OnAudioEventListener mOnAudioEventListener;
	public boolean useMic;
	private Context context;
	private BufferedInputStream mBufStream;
	private be.hogent.tarsos.dsp.AudioFormat mTarsosFormatForWavFile;

	public AudioProc(int sampleRate, Context context) {
		mSampleRate = sampleRate;
		this.context = context;
		mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mBuffer = new byte[mBufferSize];
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				mBufferSize);
		mTarsosFormat = new be.hogent.tarsos.dsp.AudioFormat(
				(float)mSampleRate, 16, 1, true, false);
		
		useMic = true;
	

		mBufStream = new BufferedInputStream(context.getResources().openRawResource(R.raw.cellobells));

	}
	
	public int getBufferSize() {
		return mBufferSize;
	}
	
	public boolean isRecording() {
		return mIsRecording;
	}
	
	public void listen() {
		if (useMic) {
			mRecorder.startRecording();
		};
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
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				while (mIsRecording) {
					AudioEvent audioEvent;
					if (useMic) {
						int bufferReadResult = mRecorder.read(mBuffer, 0, mBufferSize);
						audioEvent = new AudioEvent(mTarsosFormat, bufferReadResult);
						audioEvent.setFloatBufferWithByteBuffer(mBuffer);
					} else {
						audioEvent = new AudioEvent(mTarsosFormat, mBufferSize);
						try {
							mBufStream.read(mBuffer, 0, mBufferSize);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						audioEvent.setFloatBufferWithByteBuffer(mBuffer);
					}
					
					

					if (mOnAudioEventListener != null) {
						mOnAudioEventListener.processAudioProcEvent(audioEvent);	
					}
				}
			}
		}).start();
	}
}
