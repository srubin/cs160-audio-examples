package com.example.speechtotext;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SpeechToTextActivity extends Activity implements RecognitionListener {

	private SpeechRecognizer mSpeechRecognizer;
	private Button mListenButton;
	private TextView mResultText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speech_to_text);
		
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer.setRecognitionListener(this);
		mListenButton = (Button)findViewById(R.id.listenButton);
		mResultText = (TextView)findViewById(R.id.resultText);
		
		mListenButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak!");
				i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.example.speechtotext");
				mSpeechRecognizer.startListening(i);
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speech_to_text, menu);
		return true;
	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBufferReceived(byte[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPartialResults(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {}

	@Override
	public void onResults(Bundle b) {
		ArrayList<String> speechResults = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		if (speechResults.size() > 0) {
			mResultText.setText(speechResults.get(0));
		} else {
			mResultText.setText("Could not detect speech!");
		}
	}

	@Override
	public void onRmsChanged(float arg0) {
		// TODO Auto-generated method stub
		
	}

}
