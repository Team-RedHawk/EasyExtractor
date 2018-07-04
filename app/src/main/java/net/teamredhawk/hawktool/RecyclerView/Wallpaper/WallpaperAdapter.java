package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.teamredhawk.hawktool.R;

public class WallpaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SECTION_VIEW = 0;
    private static final int CONTENT_VIEW = 1;

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

        Context context = mContextWeakReference.get();

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
                    //default
                }
            });
            return;
        }

        WallpaperViewModel myViewHolder = (WallpaperViewModel) holder;

        WallpaperModel currentUser = ((WallpaperModel) mWallpaperAndSectionList.get(position));
        myViewHolder.vieww.setText(currentUser.vistas);
        Glide.with(context).load(currentUser.url).into(myViewHolder.imagenw);
        myViewHolder.wl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //default
            }
        });
    }


    @Override
    public int getItemCount() {
        return mWallpaperAndSectionList.size();
    }
}
