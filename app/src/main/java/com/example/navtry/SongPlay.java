package com.example.navtry;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SongPlay extends Activity implements AudioManager.OnAudioFocusChangeListener {


    String str;
    ContentResolver contentResolver;

    Cursor cursor;

    Uri uri;
    TextView t1,t2;
    ImageView imgV;
    private TextView temperatureLabel,textvl;
    Handler mHandler;
    ImageView imgb;
    MediaPlayer mPlayer;
    SeekBar mSeekBar;
    private Runnable mRunnable;
    AudioManager mAudioManager;
    String SongTitle ;
    String SongArtist;
    // float temperaturebms;
    DataBaseHelper db;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction()))
        {
            str = (getIntent().getData().getPath());

            // do what you want with the file...
        }else {
            Bundle extras = getIntent().getExtras();
            str = extras.getString("SongData");
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        t1= (TextView)findViewById(R.id.textView);
        t2= (TextView)findViewById(R.id.textView2);
        imgb=(ImageView)findViewById(R.id.imageView5);
        textvl=(TextView)findViewById(R.id.textView4);
        mSeekBar=(SeekBar)findViewById(R.id.seekBar);

        temperatureLabel= (TextView)findViewById(R.id.textView3);

        imgV = (ImageView)findViewById(R.id.imageView2) ;
        mHandler = new Handler();
        mPlayer= new MediaPlayer();


        db=new DataBaseHelper(this);

        SongPrep();

        //getCpuTemp();

        start();

        // Initialize the handler



        // Click listener for playing button
        imgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playIt();

            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer != null && fromUser){
                    mPlayer.seekTo(progress);
                }
            }
        });



    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                mPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                // Lost focus for an unbounded amount of time: stop playback and release media player

                if (mPlayer.isPlaying()) mPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mPlayer.isPlaying()) mPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }


    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mPlayer != null) {
                            mPlayer.pause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                mPlayer.start();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void playIt()
    {

        if(mPlayer.isPlaying())
        {
            pause();
        }else
        {
            play();
        }
    }

    public void play()
    {
        mPlayer.start();
        imgb.setImageResource(R.drawable.pauseim);
    }

    public void pause()
    {
        imgb.setImageResource(R.drawable.playim);
        mPlayer.pause();
    }

    public void start()
    {

        try{
            mPlayer.setDataSource(str);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.setLooping(true);
                    mp.start();
                    mPlayer.seekTo(db.ReadPos(str));
                    getAudioStats();
                    // Initialize the seek bar
                    initializeSeekBar();
                }
            });
        }catch (Exception e){
            try{
                mPlayer.start();
            }catch (Exception a){}
        }


        imgb.setImageResource(R.drawable.pauseim);

    }

    protected void stopPlaying(){
        // If media player is not null then try to stop it
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            // Toast.makeText(SongPlay.this,"Stop playing.",Toast.LENGTH_SHORT).show();
            if(mHandler!=null){
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }

    protected void getAudioStats(){
        int duration  = mPlayer.getDuration()/1000; // In milliseconds
        int pass =  mPlayer.getCurrentPosition()/1000;

        textvl.setText(pass / 60 +":"+ pass % 60 +" | "+Integer.toString(duration/60)+":"+Integer.toString(duration%60));
    }

    protected void initializeSeekBar(){
        mSeekBar.setMax(mPlayer.getDuration());

        SongPlay.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mPlayer != null){
                    seekme();
                    getAudioStats();
                }
                mHandler.postDelayed(this, 1);
            }
        });
    }

    public void seekme(){

        int mcurpos=mPlayer.getCurrentPosition();
        mSeekBar.setProgress(mcurpos);

    }


    public void SongPrep()
    {
        contentResolver = SongPlay.this.getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] STAR={"*"};
        cursor = contentResolver.query(
                uri,
                STAR,
                selection,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(SongPlay.this, "Something Went Wrong.", Toast.LENGTH_SHORT);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(SongPlay.this, "No Music Found on SD Card.", Toast.LENGTH_SHORT);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int Data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int Artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int ID = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int Duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int AlbumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);


            do {

                String SongData = cursor.getString(Data);

                if(SongData.equals(str))
                {
                    SongTitle = cursor.getString(Title);
                    SongArtist = cursor.getString(Artist);
                    // String SongID = cursor.getString(ID);
                    int SongDuration = Integer.parseInt(cursor.getString(Duration));
                    long SongAlbumID = Long.parseLong(cursor.getString(AlbumId));
                    SongDuration=SongDuration/1000;
                    textvl.setText("0:0 | "+ SongDuration / 60 +":"+ SongDuration % 60);

                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, SongAlbumID);
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.icoma);;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(SongPlay.this.getContentResolver(), albumArtUri);
                    }catch(Exception e){}

                    t1.setText(SongTitle);
                    t2.setText(SongArtist);
                    imgV.setImageBitmap(bitmap);

                    break;
                }
                temperatureLabel.setText("Now Playing");


            } while (cursor.moveToNext());
        }

    }

    //public void getCpuTemp() {
    //Process p;
    // try {
    //p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
    // p.waitFor();
    //BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

    //String line = reader.readLine();
    // temperaturebms = Float.parseFloat(line) / 1000.0f-5;



    // } catch (Exception e) {
    //   e.printStackTrace();
    //  temperatureLabel.setText("Exception ! ");
    //}
    //  }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        String testme = db.MarkPos(str,mPlayer.getCurrentPosition());
        Toast.makeText(this,testme,Toast.LENGTH_SHORT).show();
        if(mPlayer.isPlaying()) {
            stopPlaying();
        }
        mAudioManager.abandonAudioFocus(this);
    }

}
