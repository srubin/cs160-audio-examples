package com.example.texttospeech;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TextToSpeechActivity extends Activity implements TextToSpeech.OnInitListener {

	private Button mSpeakButton;
	private EditText mText;
	private TextToSpeech mTTS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_to_speech);
		
		mTTS = new TextToSpeech(this, this);
		mText = (EditText) findViewById(R.id.text);
		mSpeakButton = (Button) findViewById(R.id.speakButton);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_to_speech, menu);
		return true;
	}

	// For TTS initialization
	@Override
	public void onInit(int status) {
		mSpeakButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTTS.speak(mText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
			}
		});
	}

}
