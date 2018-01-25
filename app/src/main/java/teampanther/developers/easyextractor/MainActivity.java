package teampanther.developers.easyextractor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.Context;
import android.provider.Settings;
import android.view.View;

import teampanther.developers.easyextractor.Fragments.StorageFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,About.OnFragmentInteractionListener,StorageFragment.OnFragmentInteractionListener{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    NavigationView navigationView;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.app_name));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initCoordinatorLayout();

        hideItem();

        cargarFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 0) {

            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Snackbar.make(coordinatorLayout, "Permisos requeridos", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ajustes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.this.gotoApplicationSettings();
                            }
                        })
                        .show();
            else {
               cargarFragment();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void cargarFragment() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {

            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);

            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        StorageFragment fragment = new StorageFragment();
        transaction.add(R.id.contenedor_fragment,fragment).commit();
    }

    private void hideItem()
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (FileHelper.getStoragePath(MainActivity.this,true) == null) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.external).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void gotoApplicationSettings() {

        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        intent.setData(Uri.fromParts("package", "teampanther.developers.easyextractor", null));

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Fragment transaction and more
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (id == R.id.internal) {
            StorageFragment fragment = new StorageFragment();
            transaction.replace(R.id.contenedor_fragment,fragment);
        } else if (id == R.id.external) {
            StorageFragment fragment = StorageFragment.newInstance("SD","");
            transaction.replace(R.id.contenedor_fragment,fragment);
        }  else if (id == R.id.about) {

            About fragment = new About();
            transaction.replace(R.id.contenedor_fragment,fragment);
        }


        transaction.commit();
        //end Fragment transaction and more
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initCoordinatorLayout() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }


    public void theme() {
                sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
                int theme = sharedPreferences.getInt("THEME", 1);
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


}