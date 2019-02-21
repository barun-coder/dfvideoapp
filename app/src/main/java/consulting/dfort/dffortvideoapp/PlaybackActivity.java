package consulting.dfort.dffortvideoapp;

import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import consulting.dfort.dffortvideoapp.widget.MyVideoView;

/**
 * Created by pc on 18/02/2019 13:12.
 * MyApplication
 */
public class PlaybackActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, TextureView.SurfaceTextureListener {
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private static final String VIDEO_PATH = "https://inducesmile.com/wp-content/uploads/2016/05/small.mp4";
    private MyVideoView videoView;
    int stopPosition = 0;
    private static MediaController mediaController;
    String demoVideoFolder = null;
    String demoVideoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_scroll_layout);
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            ShowToast("A-Potrait ");
        } else {
            ShowToast("A-Landscape");
        }
        init();
        setDefaultVideoPath();

        setVideoView(savedInstanceState);
        setMediaController();
    }

    private void setDefaultVideoPath() {
        demoVideoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Videokit/";
        demoVideoPath = demoVideoFolder + "out1neAspect.mp4";
    }

    private void CallHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT) {
                    ShowToast("Potrait ");
                } else {
                    ShowToast("Landscape");
                }
            }
        }, 8000);

    }

    private void init() {
        Button buttonSetPortrait = (Button) findViewById(R.id.setPortrait);
        Button buttonSetLandscape = (Button) findViewById(R.id.setLandscape);

        buttonSetPortrait.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT) {
                    ShowToast("Potrait ");
                } else {
                    ShowToast("Landscape");
                }
            }

        });

        buttonSetLandscape.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT) {
                    ShowToast("Potrait ");
                } else {
                    ShowToast("Landscape");
                }
            }

        });
    }

    private void setVideoView(Bundle savedInstanceState) {
        videoView = findViewById(R.id.videoView1);
        if (savedInstanceState != null) {
            stopPosition = savedInstanceState.getInt("position");
            Log.d("", "savedInstanceState called" + stopPosition);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        videoView.setVideoSize(height, width);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        String path = "android.resource://" + getPackageName() + "/" + R.raw.footboys;
//        SetOrient(path);
        Uri uri = Uri.parse(path);
        ShowToast(path);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }


    private void setMediaController() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        holder.lockCanvas().rotate(270);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceHolder);


        try {
            AssetFileDescriptor afd = getAssets().openFd("blueball.mp4");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ShowToast("In SDk Media Player Set ");
                mMediaPlayer.setDataSource(demoVideoPath);
            } else {
                ShowToast("Out Of SDK");
            }
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(stopPosition);
        videoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void ShowToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        videoView.pause();
        outState.putInt("position", 100);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stopPosition = savedInstanceState.getInt("position");
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("VideoTextures", "onSurfaceTextureAvailable");


    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d("VideoTextures", "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("VideoTextures", "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d("VideoTextures", "onSurfaceTextureUpdated");
    }
}