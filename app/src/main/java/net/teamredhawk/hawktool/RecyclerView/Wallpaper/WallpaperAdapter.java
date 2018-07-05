package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.teamredhawk.hawktool.GlideApp;
import net.teamredhawk.hawktool.R;

public class WallpaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;
    private ArrayList<ItemInterface> mWallpaperAndSectionList;
    private WeakReference<Context> mContextWeakReference;

    public WallpaperAdapter(ArrayList<ItemInterface> usersAndSectionList, Context context) {

        this.mWallpaperAndSectionList = usersAndSectionList;

        this.mContextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = mContextWeakReference.get();
        if (viewType == SECTION_VIEW) {
            return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_item_group, parent, false));
        }
        return new WallpaperViewModel(LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_item_layout, parent, false), context);
    }

    @Override
    public int getItemViewType(int position) {
        if (mWallpaperAndSectionList.get(position).isSection()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final Context context = mContextWeakReference.get();

        if (context == null) {
            return;
        }

        if (SECTION_VIEW == getItemViewType(position)) {

            CategoryViewHolder sectionViewHolder = (CategoryViewHolder) holder;
            CategoryModel sectionItem = ((CategoryModel) mWallpaperAndSectionList.get(position));
            sectionViewHolder.title.setText(sectionItem.title);
            sectionViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            return;
        }

        WallpaperViewModel myViewHolder = (WallpaperViewModel) holder;

        final WallpaperModel currentUser = ((WallpaperModel) mWallpaperAndSectionList.get(position));
        myViewHolder.vieww.setText(currentUser.vistas);
        GlideApp.with(context).load(currentUser.url).centerCrop().into(myViewHolder.imagenw);
        myViewHolder.wl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                final View imgEntryView = inflater.inflate(R.layout.wallpaper_full_view, null);
                final Dialog dialog=new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //default fullscreen titlebar
                ImageView img = imgEntryView.findViewById(R.id.usericon_large);
                LinearLayout descargar= imgEntryView.findViewById(R.id.llDownloadWallpaper);
                LinearLayout setwall= imgEntryView.findViewById(R.id.llSetWallpaper);
                GlideApp.with(imgEntryView).load(currentUser.urlFull).fitCenter().into(img);
                dialog.setContentView(imgEntryView);
                dialog.show();
                imgEntryView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        dialog.cancel();
                    }
                });
                descargar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                setwall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlideApp.with(imgEntryView).asBitmap().load(currentUser.urlFull).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    WallpaperManager.getInstance(context).setBitmap(resource);
                                    Toast.makeText(context, "Fondo Establecido", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                }catch (IOException e){
                                    Toast.makeText(context, "Error al poner wallpaper", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                }
                            }
                        });
                    }
                });

            }
        });
    }


    @Override
    public int getItemCount() {
        return mWallpaperAndSectionList.size();
    }
}
