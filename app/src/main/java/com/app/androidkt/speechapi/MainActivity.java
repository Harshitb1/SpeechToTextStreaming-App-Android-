package com.app.androidkt.speechapi;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity  extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static final int RECORD_REQUEST_CODE = 101;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.textMessage)
    TextView textMessage;

    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.listview2)
    ListView listView1;

    private List<String> stringList;
    private SpeechAPI speechAPI;
    private List<String> stringList1;
    private SpeechAPI speechAPI1;
    private VoiceRecorder mVoiceRecorder;
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            if (speechAPI != null) {
                speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
                speechAPI1.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null) {
                speechAPI.recognize(data, size);
                speechAPI1.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            if (speechAPI != null) {
                speechAPI.finishRecognizing();
                speechAPI1.finishRecognizing();
            }
        }

    };
    private ArrayAdapter adapter;
    private ArrayAdapter adapter1;
    private final SpeechAPI.Listener mSpeechServiceListener =
            new SpeechAPI.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (textMessage != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    textMessage.setText(null);
                                    stringList.add(0,text);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    textMessage.setText(text);
                                }
                            }
                        });
                    }
                }
            };
    private final SpeechAPI.Listener mSpeechServiceListener1 =
            new SpeechAPI.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (textMessage != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    textMessage.setText(null);
                                    stringList1.add(0,text);
                                    adapter1.notifyDataSetChanged();
                                } else {
                                    textMessage.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        speechAPI = new SpeechAPI(MainActivity.this,"en-US");
        stringList = new ArrayList<>();
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(adapter);
        speechAPI1 = new SpeechAPI(MainActivity.this,"hi-IN");
        stringList1 = new ArrayList<>();
        adapter1 = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, stringList1);
        listView1.setAdapter(adapter1);
    }

    @Override
    protected void onStop() {
        stopVoiceRecorder();

        // Stop Cloud Speech API
        speechAPI.removeListener(mSpeechServiceListener);
        speechAPI.destroy();
        speechAPI = null;

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else {
            makeRequest(Manifest.permission.RECORD_AUDIO);
        }
        speechAPI.addListener(mSpeechServiceListener);
        speechAPI1.addListener(mSpeechServiceListener1);
    }

    private int isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                startVoiceRecorder();
            }
        }
    }

}
