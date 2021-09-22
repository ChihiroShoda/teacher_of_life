package com.example.unitokyodemo0911;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    int REQUEST_CODE_STT = 1;
    DetectIntent mDetectIntent;
    String mMsg ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetectIntent = new DetectIntent(this);
//        test();

    }

    public void start_onClick(View v) {
        Intent sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!");

        try {
            startActivityForResult(sttIntent, REQUEST_CODE_STT);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Your device does not support STT.", Toast.LENGTH_LONG).show();
        }
    }
    protected void test() {
// Instantiates a client

// The text to analyze
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            String text = "Hello, world!";
            Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            System.out.printf("Text: %s%n", text);
            System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if ( requestCode == REQUEST_CODE_STT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.size() > 0) {
                    String wData = result.get(0);
                    EditText wEditText =  findViewById(R.id.et_text_input);
                    wEditText.setText(wData);
                    String wRes = mDetectIntent.send(wData);
                    TextView wText =  findViewById(R.id.et_text_output);
                    mMsg = mMsg +"\nYou:"+wData+"\nCom:"+wRes;
                    wText.setText(mMsg);
                }
            }
        }
    }
}