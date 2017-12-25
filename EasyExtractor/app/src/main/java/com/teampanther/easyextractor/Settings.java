package com.teampanther.easyextractor;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    public int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.setting));

        final Switch _switch = (Switch) findViewById(R.id.darkTheme);
        _switch.setOnCheckedChangeListener(
                new CheckBox.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            theme=1;
                            Context context = getApplicationContext();
                            CharSequence text = "encendido";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        else {
                            theme=2;
                            Context context = getApplicationContext();
                            CharSequence text = "apagado";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        settingTheme(theme);
                    }
                });
    }



    public void settingTheme(int theme) {
        switch (theme) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }
}
