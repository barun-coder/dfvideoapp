package consulting.dfort.dffortvideoapp;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by pc on 18/02/2019 13:12.
 * MyApplication
 */
public class TexturePlaybackActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener,
        TextureView.SurfaceTextureListener {

    private MediaPlayer mediaPlayer;
    private TextureView videoView;
    private boolean startedPlayback = false;
    private boolean playerReady = false;
    public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
    private String TAG = "TexturePlayback";
    private Surface surface;

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void onCompletion(MediaPlayer mp) {
        Log.w(TAG, "Video playback finished");
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            /*
             * Restart play back in case we did not start anything yet. This may
             * be the case when we tried to tune in in very first secs of the
             * broadcast when there is no data yet.
             */
            if (mediaPlayer != null && !mediaPlayer.isPlaying() && !startedPlayback) {
                mediaPlayer.reset();
            } else {
                Log.w(TAG, "No media in stream");
            }
        } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Log.w(TAG, "Media service died unexpectedly");
        } else {
            Log.w(TAG, "Unknown media error");
        }
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Log.w(TAG, "Media is too complex to decode it fast enough.");
                startedPlayback = true;
                break;
            case MEDIA_INFO_NETWORK_BANDWIDTH:
                Log.w(TAG, "Bandwith in recent past.");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.w(TAG, "Start of media bufferring.");
                startedPlayback = true;
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.w(TAG, "End of media bufferring.");
                startedPlayback = true;
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Log.w(TAG, "Media is not properly interleaved.");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Log.w(TAG, "Stream is not seekable.");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Log.w(TAG, "New set of metadata is available.");
                break;
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
            default:
                Log.w(TAG, "Unknown playback info (" + what + ":" + extra + ").");
                break;
        }
        return true;
    }

    private void startPlayback() {
        if (mediaPlayer != null) {
            onLoaded(mediaPlayer);
            mediaPlayer.start();
        }
    }

    private void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    private void resumePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    private void onLoaded(MediaPlayer mp) {
    }

    public void onPrepared(MediaPlayer mp) {
        playerReady = true;
        startPlayback();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.texture_video_scroll_layout);
        videoView = (TextureView) findViewById(R.id.texture_view);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        videoView.setSurfaceTextureListener(this);
        createMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        if (surface != null) {
            surface.release();
            surface = null;
        }
        super.onDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = new Surface(surface);
        String someurl = "android.resource://" + getPackageName() + "/" + R.raw.footboys;
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ads/" + "asus.mp4";
//        loadMedia("https://storage.googleapis.com/coverr-main/mp4/Unicorns%20and%20horses.mp4");
        loadMedia(filePath);
    }


    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (this.surface != null) {
            releaseMediaPlayer();
            this.surface.release();
            this.surface = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int w, int h) {
        if (w > 0 && h > 0 && !videoSizeSetupDone) {
            Log.w(TAG, "Video size changed: " + w + "x" + h);
            changeVideoSize(w, h);
        }
    }

    private boolean videoSizeSetupDone = false;

    private void changeVideoSize(int w, int h) {
        RelativeLayout.LayoutParams params;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
//        if (orientation == LANDSCAPE) {
        params = new RelativeLayout.LayoutParams(1080, 1920);
//        } else {
        float rotation = -90.0f;
        params = new RelativeLayout.LayoutParams(height, width);
        float scale = (w * 1.0f) / (h * 1.0f);
        videoView.setRotation(rotation);
        videoView.setScaleX(scale);
        //        }
        params.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
//        videoView.setLayoutParams(params);
        videoSizeSetupDone = true;
    }

    private void loadMedia(String url) {
        if (surface == null)
            return;
        Log.d(TAG, "Loading url: " + url);

        startedPlayback = false;
        try {
            mediaPlayer.reset();
            mediaPlayer.setSurface(surface);
//            mediaPlayer.setDataSource(url);
            AssetFileDescriptor afd = getAssets().openFd("asus.mp4");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaPlayer.setDataSource(afd);
            } else {
            }
            mediaPlayer.setLooping(true);

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.w(TAG, "Media load failed");

        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}