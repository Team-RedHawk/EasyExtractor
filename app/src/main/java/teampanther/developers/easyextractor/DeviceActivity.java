package teampanther.developers.easyextractor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.LinkedList;

import teampanther.developers.easyextractor.RecyclerView.DeviceAdapter;
import teampanther.developers.easyextractor.RecyclerView.DeviceInfo;

public class DeviceActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    SharedPreferences sharedPreferences;
    LinkedList<DeviceInfo> items = new LinkedList<>();
    public int theme;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Selecciona el tema guardado por el usuario
        theme();
        setContentView(R.layout.device_info);
        // Save current theme to use when user press dismiss inside dialog
        sharedPreferences = this.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        context= getApplicationContext();
        //Configura el status bar y el toolbar
        toolbarStatusBar();
        setTitle(getResources().getString(R.string.custom_device_info));
        setUpRecycler();
    }

    private void setUpRecycler() {
        mRecyclerView = findViewById(R.id.recycler_device_info);
        items.add(new DeviceInfo("VERSION.RELEASE", Build.VERSION.RELEASE));
        items.add(new DeviceInfo("VERSION.INCREMENTAL" ,Build.VERSION.INCREMENTAL));
        items.add(new DeviceInfo("VERSION.SDK.NUMBER",String.valueOf(Build.VERSION.SDK_INT )));
        items.add(new DeviceInfo("BOARD",Build.BOARD));
        items.add(new DeviceInfo("BOOTLOADER",Build.BOOTLOADER));
        items.add(new DeviceInfo("BRAND",Build.BRAND));
        items.add(new DeviceInfo("CPU_ABI",Build.CPU_ABI));
        items.add(new DeviceInfo("CPU_ABI2",Build.CPU_ABI2));
        items.add(new DeviceInfo("DISPLAY",Build.DISPLAY));
        items.add(new DeviceInfo("FINGERPRINT",Build.FINGERPRINT));
        items.add(new DeviceInfo("HARDWARE",Build.HARDWARE));
        items.add(new DeviceInfo("HOST",Build.HOST));
        items.add(new DeviceInfo("ID",Build.ID));
        items.add(new DeviceInfo("MANUFACTURER",Build.MANUFACTURER));
        items.add(new DeviceInfo("MODEL",Build.MODEL));
        items.add(new DeviceInfo("PRODUCT",Build.PRODUCT));
        items.add(new DeviceInfo("SERIAL",Build.SERIAL));
        items.add(new DeviceInfo("TAGS",Build.TAGS));
        items.add(new DeviceInfo("TIME",String.valueOf(Build.TIME)));
        items.add(new DeviceInfo("TYPE",Build.TYPE));
        items.add(new DeviceInfo("UNKNOWN",Build.UNKNOWN));
        items.add(new DeviceInfo("USER",Build.USER));
        DeviceAdapter mAdapter = new DeviceAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_post clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_copyD) {
            ClipData clip = ClipData.newPlainText("text", getInformationDevice());
            ClipboardManager clipboard = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            return true;
        }

        if (id == R.id.action_sendD){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getInformationDevice() {
        String var= "";
        var= var.concat(context.getString(R.string.custom_device_info));
        for (DeviceInfo info:items) {
            var = var.concat("\n"+info.getNombre()+": "+info.getDesc());
        }
        return var;
    }

    public void toolbarStatusBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeviceActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
