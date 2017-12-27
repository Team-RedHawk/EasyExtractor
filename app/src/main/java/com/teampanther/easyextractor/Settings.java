package com.teampanther.easyextractor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public int theme;
    Boolean homeButton = false, themeChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Selecciona el tema guardado por el usuario
        theme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        //Configura el status bar y el toolbar
        toolbarStatusBar();
        setTitle(getResources().getString(R.string.setting));

        // Save current theme to use when user press dismiss inside dialog
        sharedPreferences = this.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        final Switch _switch = (Switch) findViewById(R.id.darkTheme);

        if (sharedPreferences.getInt("THEME", 1) == 2){
            _switch.setChecked(true);
        }
        _switch.setOnCheckedChangeListener(
                new CheckBox.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            theme=2;
                            Context context = getApplicationContext();
                            CharSequence text = "encendido";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        else {
                            theme=1;
                            Context context = getApplicationContext();
                            CharSequence text = "apagado";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        sharedPreferences.edit().putBoolean("THEMECHANGED", true).apply();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(Settings.this, Integer.toString(theme), duration);
                        toast.show();
                        settingThemeElection(theme);
                    }
                });

        // Checa si el tema fue cambiado para ajustar en el main activity y demas activitys
        themeChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_post clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (!homeButton) {
                NavUtils.navigateUpFromSameTask(Settings.this);
            }
            if (homeButton) {
                if (!themeChanged) {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("DOWNLOAD", false);
                    editor.apply();
                }
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void toolbarStatusBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
    }

    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 1);
        settingTheme(theme);
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


    public void settingThemeElection(int theme) {
        switch (theme) {
            case 1:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 1).apply();
                reiniciarActivity(this);
                break;
            case 2:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 2).apply();
                reiniciarActivity(this);
                break;
            default:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 1).apply();
                reiniciarActivity(Settings.this);
                break;
        }
    }

    private void themeChanged() {
        themeChanged = sharedPreferences.getBoolean("THEMECHANGED",false);
        homeButton = true;
    }

    //reinicia una Activity
    public static void reiniciarActivity(AppCompatActivity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }
}
