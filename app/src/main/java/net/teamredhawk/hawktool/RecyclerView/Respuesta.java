package net.teamredhawk.hawktool.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;

import java.io.File;

import net.teamredhawk.hawktool.UtilsHelper.FileHelper;
import net.teamredhawk.hawktool.UtilsHelper.PreferenceUtil;

/**
 * Created by luffynando on 25/01/2018.
 */

class Respuesta extends SortedListAdapterCallback<File> {
    private int criteria;

    Respuesta(Context context, RecyclerView.Adapter adapter) {
        super(adapter);
        this.criteria = PreferenceUtil.getInteger(context, "pref_sort", 0);
    }

    @Override
    public int compare(File o1, File o2) {
        boolean isDirectory1 = o1.isDirectory();

        boolean isDirectory2 = o2.isDirectory();

        if (isDirectory1 != isDirectory2) return isDirectory1 ? -1 : +1;

        switch (criteria) {

            case 0:
                return FileHelper.compareName(o1, o2);

            case 1:
                return FileHelper.compareDate(o1, o2);

            case 2:
                return FileHelper.compareSize(o1, o2);

            default:
                return 0;
        }
    }

    @Override
    public boolean areContentsTheSame(File oldItem, File newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(File item1, File item2) {
        return item1.equals(item2);
    }

    boolean update(int criteria) {

        if (criteria == this.criteria) return false;

        this.criteria = criteria;

        return true;
    }
}
