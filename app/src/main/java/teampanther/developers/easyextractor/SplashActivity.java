package teampanther.developers.easyextractor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                startActivity( new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        };


        //Animacion de Splash Screen

        ImageView splash = (ImageView) findViewById(R.id.splash_img);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.transparent_animation);
        animation.reset();
        splash.setAnimation(animation);
        animation.reset();
        //timer
        Timer timer = new Timer();
        timer.schedule(timerTask,3000);

        //TextView con nombre de version etc

        TextView texto = (TextView)findViewById(R.id.label_splash);
        animation.reset();
        texto.setAnimation(animation);
        animation.reset();
    }
}
