package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.teamredhawk.hawktool.R;

public class WallpaperViewModel extends RecyclerView.ViewHolder {
    LinearLayout wl;
    TextView vieww;
    ImageView imagenw;
    public WallpaperViewModel(View itemView, final Context context) {
        super(itemView);
        wl = itemView.findViewById(R.id.wall_layout);
        vieww= itemView.findViewById(R.id.textw);
        imagenw= itemView.findViewById(R.id.imagew);
    }
}
