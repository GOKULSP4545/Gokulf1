package com.gokulsundar4545.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity {


    ImageView btn_play, btn_next, btn_prev, btn_ff, btn_fr, imageView1;
    TextView txts_name, txts_start, txts_stop;
    SeekBar seekmusic;
    com.gauravk.audiovisualizer.visualizer.BarVisualizer Visualizer1;

    String sname;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    Thread Updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (Visualizer1 != null) {
            Visualizer1.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btn_prev = findViewById(R.id.btnPrev);
        btn_ff = findViewById(R.id.btnff);
        btn_fr = findViewById(R.id.btnfr);
        btn_next = findViewById(R.id.btnnext);
        btn_play = findViewById(R.id.playbtn);
        txts_name = findViewById(R.id.txtsn);
        txts_start = findViewById(R.id.txtsstart);
        txts_stop = findViewById(R.id.txtsstop);
        seekmusic = findViewById(R.id.seekbar);
        imageView1 = findViewById(R.id.imageView);
        Visualizer1 = findViewById(R.id.Visualizer);


        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songename");
        position = bundle.getInt("pos", 0);
        txts_name.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        sname = mySongs.get(position).getName();
        txts_name.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();


        Updateseekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentpostion = mediaPlayer.getCurrentPosition();
                while (currentpostion < totalDuration) {
                    try {
                        sleep(1000);
                        currentpostion = mediaPlayer.getCurrentPosition();

                        seekmusic.setProgress(currentpostion);

                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        //Max duration na get paniko see -line 98
        seekmusic.setMax(mediaPlayer.getDuration());
        Updateseekbar.start();
        //To set colou of seekbar


        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //seekbar how many sec move akutho that much time mediaplayer also want to move
                mediaPlayer.seekTo(seekBar.getProgress());
                mediaPlayer.start();
                btn_play.setBackgroundResource(R.drawable.pausewhite);


            }
        });

// Duration means Totaltime of song
        //To set Total time of song in -TextView txts_stop
        String endTime = creatTime(mediaPlayer.getDuration());
        txts_stop.setText(endTime);
//To set Total currenttime of song in -TextView txts_start
        final Handler handler = new Handler();
        final int delay = 1000;
        //Song is measured in milesecond
        //We want To set only second
        //so that why we delay 1000mm=1sec

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = creatTime(mediaPlayer.getCurrentPosition());
                txts_start.setText(currentTime);
                handler.postDelayed(this, delay);


            }
        }, 1000);//after 1000msec set the current position

//To set Bar Visulizer in my app
        int audiosessionId = mediaPlayer.getAudioSessionId();
        if (audiosessionId != -1) {
            Visualizer1.setAudioSessionId(audiosessionId);

        }

//To play song and pause song
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    btn_play.setBackgroundResource(R.drawable.playwhite);
                    mediaPlayer.pause();
                } else {
                    btn_play.setBackgroundResource(R.drawable.pausewhite);
                    mediaPlayer.start();

                }

            }
        });
//if song is completed automaticaly move to next song
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                   seekmusic.setProgress(000);
                   mediaPlayer.seekTo(000);
                   btn_play.setBackgroundResource(R.drawable.playwhite);

                seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //seekbar how many sec move akutho that much time mediaplayer also want to move
                        mediaPlayer.seekTo(seekBar.getProgress());
                        mediaPlayer.start();
                        btn_play.setBackgroundResource(R.drawable.pausewhite);


                    }
                });

            }
        });
//visulizer
        int audiosesssionId = mediaPlayer.getAudioSessionId();
        if (audiosessionId != -1) {
            Visualizer1.setAudioSessionId(audiosessionId);
        }

//To move next song
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % mySongs.size());
                Uri uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekmusic.setProgress(0);
                sname = mySongs.get(position).getName();
                txts_name.setText(sname);
                mediaPlayer.start();
                btn_play.setBackgroundResource(R.drawable.pausewhite);
                startAnimation1(imageView1);

                txts_stop.setText(creatTime(mediaPlayer.getDuration()));
                int audiosessionId = mediaPlayer.getAudioSessionId();
                if (audiosessionId != -1) {
                    Visualizer1.setAudioSessionId(audiosessionId);
                    seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            //seekbar how many sec move akutho that much time mediaplayer also want to move
                            mediaPlayer.seekTo(seekBar.getProgress());
                            mediaPlayer.start();
                            btn_play.setBackgroundResource(R.drawable.pausewhite);


                        }
                    });


                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            seekmusic.setProgress(000);
                            mediaPlayer.seekTo(000);
                            btn_play.setBackgroundResource(R.drawable.playwhite);


                            seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                    //seekbar how many sec move akutho that much time mediaplayer also want to move
                                    mediaPlayer.seekTo(seekBar.getProgress());
                                    mediaPlayer.start();
                                    btn_play.setBackgroundResource(R.drawable.pausewhite);


                                }
                            });

                        }
                    });

                }


            }
        });
//To move Previes Song
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) % mySongs.size());

                Uri uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekmusic.setProgress(0);
                sname = mySongs.get(position).getName();
                txts_name.setText(sname);
                mediaPlayer.start();
                btn_play.setBackgroundResource(R.drawable.pausewhite);
                startAnimation2(imageView1);

                txts_stop.setText(creatTime(mediaPlayer.getDuration()));

                int audiosessionId = mediaPlayer.getAudioSessionId();
                if (audiosessionId != -1) {
                    Visualizer1.setAudioSessionId(audiosessionId);

                    seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            //seekbar how many sec move akutho that much time mediaplayer also want to move
                            mediaPlayer.seekTo(seekBar.getProgress());
                            mediaPlayer.start();
                            btn_play.setBackgroundResource(R.drawable.pausewhite);


                        }
                    });


                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            seekmusic.setProgress(000);
                            mediaPlayer.seekTo(000);
                            btn_play.setBackgroundResource(R.drawable.playwhite);

                            seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                    //seekbar how many sec move akutho that much time mediaplayer also want to move
                                    mediaPlayer.seekTo(seekBar.getProgress());
                                    mediaPlayer.start();
                                    btn_play.setBackgroundResource(R.drawable.pausewhite);


                                }
                            });

                        }
                    });


                }

            }
        });
//To maove fast Rewind  -so we increment 10sec
        btn_ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });
//To maove fast Rewind  -so we decrement 10sec
        btn_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });

    }

    //To set Animation in RightRotation
    public void startAnimation1(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView1, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    //To  set  Animation in leftRotate
    public void startAnimation2(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView1, "rotation", 0f, -360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    //To Convert time to like this,"00:00"
    public String creatTime(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time += min + ":";

        if (sec < 10) {

            time += "0";
        }
        time += sec;

        return time;
    }

}