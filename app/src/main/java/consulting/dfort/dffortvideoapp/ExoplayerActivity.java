package consulting.dfort.dffortvideoapp;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by pc on 18/02/2019 13:12.
 * MyApplication
 */
public class ExoplayerActivity extends AppCompatActivity {
    private static final String VIDEO_PATH = "https://inducesmile.com/wp-content/uploads/2016/05/small.mp4";
    private VideoView videoView;
    int stopPosition = 0;
    private static MediaController mediaController;
    private SimpleExoPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exo_player_video_scroll_layout);
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            ShowToast("A-Potrait ");
        } else {
            ShowToast("A-Landscape");
        }
        init();
        setMediaController();
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

    private void setMediaController() {
        playerView = findViewById(R.id.player_view);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), new DefaultBandwidthMeter());
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        playerView.setPlayer(player);

        player.setPlayWhenReady(true);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.blue_ball_v;
        Uri uri = Uri.parse(path);
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(uri,
                dataSourceFactory, extractorsFactory, null, null);
        player.prepare(mediaSource);


    }

    private void setVideoView(Bundle savedInstanceState) {
        videoView = (VideoView) findViewById(R.id.videoView1);
        if (savedInstanceState != null) {
            stopPosition = savedInstanceState.getInt("position");
            Log.d("", "savedInstanceState called" + stopPosition);
        }
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        String path = "android.resource://" + getPackageName() + "/" + R.raw.blue_ball_v;
        Uri uri = Uri.parse(path);
        ShowToast(path);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {

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
}