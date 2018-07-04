package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.teamredhawk.hawktool.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder{
    TextView title, more;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        title =  itemView.findViewById(R.id.wall_section);
        more = itemView.findViewById(R.id.wall_more);
    }
}
