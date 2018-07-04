package net.teamredhawk.hawktool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import java.util.LinkedList;

import net.teamredhawk.hawktool.RecyclerView.DeviceAdapter;
import net.teamredhawk.hawktool.RecyclerView.DeviceInfo;
import net.teamredhawk.hawktool.UtilsHelper.FileHelper;

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
        items.add(new DeviceInfo("VERSION.SDK.NUMBER",String.valueOf(Build.VERSION.SDK_INT )));
        items.add(new DeviceInfo("BOARD",Build.BOARD));
        items.add(new DeviceInfo("BOOTLOADER",Build.BOOTLOADER));
        items.add(new DeviceInfo("BRAND",Build.BRAND));
        items.add(new DeviceInfo("HARDWARE",Build.HARDWARE));
        items.add(new DeviceInfo("HOST",Build.HOST));
        items.add(new DeviceInfo("ID",Build.ID));
        items.add(new DeviceInfo("MANUFACTURER",Build.MANUFACTURER));
        items.add(new DeviceInfo("MODEL",Build.MODEL));
        items.add(new DeviceInfo("PRODUCT",Build.PRODUCT));
        items.add(new DeviceInfo("DENSITY", getDpiDevice()));
        items.add(new DeviceInfo("KERNEL",System.getProperty("os.version")));
        items.add(new DeviceInfo("SCREEN RESOLUTION",getDeviceScreenResolution()));
        if (FileHelper.getStatusRoot(context)){
            items.add(new DeviceInfo("MOUNTPOINTS",getMountPoints()));
        }
        DeviceAdapter mAdapter = new DeviceAdapter(items, context);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private String getMountPoints() {
        String res;
        if (!FileHelper.sudoForResult("cat /proc/mtd").isEmpty()){
            res = FileHelper.sudoForResult("cat /proc/mtd");
        }
        else if(!FileHelper.sudoForResult("cat /proc/emmc").isEmpty())
        {
            res= FileHelper.sudoForResult("cat /proc/emmc");
        }else{
            res= "Desconocido";
        }
        return res;
    }

    public String getDeviceScreenResolution() {
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x; //device width
        int height = size.y; //device height

        return "" + width + " x " + height; //example "480 * 800"
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
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getInformationDevice());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getDpiDevice(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return String.valueOf(metrics.densityDpi);
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
