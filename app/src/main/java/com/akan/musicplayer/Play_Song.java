package com.akan.musicplayer;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class Play_Song extends AppCompatActivity {

    TextView textView;
    ImageView play, next, previous;
    String songName;
    SeekBar seekBar;
    Thread updateSeek;
    TextView txt1, txt2;
    BarVisualizer barVisualizer;

    public static final String EXTRA_NAME = "songname";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> songs;

    @Override
    protected void onDestroy() {
        if(barVisualizer!=null){
            barVisualizer.release();
        }
//        mediaPlayer.stop();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);
        next = findViewById(R.id.next);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        barVisualizer = findViewById(R.id.blast);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songs = (ArrayList) bundle.getParcelableArrayList("songs");
        String sName = intent.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        textView.setSelected(true);
        Uri uri = Uri.parse(songs.get(position).toString());
        songName = songs.get(position).getName();
        textView.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                while (currentPosition < mediaPlayer.getDuration()) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeek.start();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        txt2.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txt1.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                songName = songs.get(position).getName();
                textView.setText(songName);
                mediaPlayer.start();
                String endTime = createTime(mediaPlayer.getDuration());
                txt2.setText(endTime);

                int audioSessionId = mediaPlayer.getAudioSessionId();
                if(audioSessionId !=- 1)
                    barVisualizer.setAudioSessionId(audioSessionId);
            }
        });

        int audioSessionId = mediaPlayer.getAudioSessionId();
        if(audioSessionId !=- 1)
            barVisualizer.setAudioSessionId(audioSessionId);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % songs.size());
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                songName = songs.get(position).getName();
                textView.setText(songName);
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();

                String endTime = createTime(mediaPlayer.getDuration());
                txt2.setText(endTime);

                int audioSessionId = mediaPlayer.getAudioSessionId();
                if(audioSessionId !=- 1)
                    barVisualizer.setAudioSessionId(audioSessionId);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
            }
        });
    }

    public String createTime(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time = time + min + ":";
        if (sec < 10) {
            time = time + "0";
        }
        time += sec;
        return time;
    }
}

