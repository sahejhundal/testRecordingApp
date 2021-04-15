package com.example.recordingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button recordButton, stopRecordingButton, stopPlayingButton, playButton;
    TextView statusmsg;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String pathSave="";
    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init View
        recordButton = (Button)findViewById(R.id.recordButton);
        stopPlayingButton = (Button)findViewById(R.id.stopPlayingButton);
        stopRecordingButton = (Button)findViewById(R.id.stopRecordingButton);
        playButton = (Button)findViewById(R.id.playButton);

        if(checkPermissionFromDevice()){
            recordButton.setOnClickListener(v -> {
                pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"
                        + UUID.randomUUID().toString()+"_audio_record.3gp";
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e){
                    e.printStackTrace();
                }
                playButton.setEnabled(false);
                stopPlayingButton.setEnabled(false);
                Toast.makeText(MainActivity.this, "Recording", Toast.LENGTH_SHORT).show();
            });
            stopRecordingButton.setOnClickListener(v -> {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (IllegalStateException ise){
                    ise.printStackTrace();
                }
                stopRecordingButton.setEnabled(false);
                playButton.setEnabled(true);
                recordButton.setEnabled(true);
                stopPlayingButton.setEnabled(false);
                Toast.makeText(MainActivity.this, "Stopped Recording", Toast.LENGTH_SHORT).show();
            });
            playButton.setOnClickListener(v -> {
                stopPlayingButton.setEnabled(true);
                stopRecordingButton.setEnabled(false);
                recordButton.setEnabled(false);
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();


                } catch(IOException e){
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Playing", Toast.LENGTH_SHORT).show();
            });
        }
        else{
            requestPermission();
        }

        /*mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mediaRecorder.setOutputFile(file);
        statusmsg = (TextView)findViewById(R.id.statusmsg);*/

    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    /*public void onClick(View view) {
        if(view.getId() == R.id.recordButton){
            // record
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
                        10);
            } else {
                record();
            }
            //record();
        }
        if(view.getId() == R.id.playButton){
            // play
            //play();
        }
        if(view.getId() == R.id.stopButton){
            // stop
            stopAudio();
        }
    }


    private void play() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void record() {
        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch(IOException e){
            e.printStackTrace();
        }
        statusmsg.setText("Audio Recording");
    }

    private void stopAudio() {
        mediaRecorder.stop();
        mediaRecorder.release();
        statusmsg.setText("Stopped Recording");
    }*/

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED )
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
                break;
        }
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}