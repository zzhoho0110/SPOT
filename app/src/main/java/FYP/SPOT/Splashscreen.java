package FYP.SPOT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.VideoView;


public class Splashscreen extends AppCompatActivity {
    MediaPlayer myMus = null;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);


        getSupportActionBar().hide();
        videoView = (VideoView)findViewById(R.id.videoView);
        Uri video = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.splash);
        videoView.setVideoURI(video);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startActivity(new Intent(Splashscreen.this, MainActivity.class));
                finish();
            }
        });
    videoView.start();
        myMus = MediaPlayer.create(this, R.raw.splash_sound);
        myMus.setLooping(true);
    }
}
