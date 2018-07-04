package net.teamredhawk.hawktool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import net.teamredhawk.hawktool.RecyclerView.Wallpaper.ItemInterface;
import net.teamredhawk.hawktool.RecyclerView.Wallpaper.WallpaperAdapter;

public class WallpaperActivity extends AppCompatActivity {
    private WallpaperAdapter wadapter;
    private ArrayList<ItemInterface> mwallpaperAndSectionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
    }


}
