package net.teamredhawk.hawktool.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import net.teamredhawk.hawktool.R;
import net.teamredhawk.hawktool.UtilsHelper.FileHelper;

import static net.teamredhawk.hawktool.UtilsHelper.FileHelper.FileType.APK;
import static net.teamredhawk.hawktool.UtilsHelper.FileHelper.getImageResource;
import static net.teamredhawk.hawktool.UtilsHelper.FileHelper.getLastModified;
import static net.teamredhawk.hawktool.UtilsHelper.FileHelper.getName;
import static net.teamredhawk.hawktool.UtilsHelper.FileHelper.getSize;
import static net.teamredhawk.hawktool.UtilsHelper.PreferenceUtil.getBoolean;

/**
 * Created by luffynando on 25/01/2018.
 */

final class ViewHolder0 extends ViewHolder {

    private TextView name;

    private TextView date;

    private TextView size;

    private SharedPreferences sharedPreferences;

    ViewHolder0(Context context, OnItemClickListener listener, View view) {

        super(context, listener, view);
    }

    @Override
    protected void loadIcon() {

        image = (ImageView) itemView.findViewById(R.id.folder_file);
    }

    @Override
    protected void loadName() {

        name = (TextView) itemView.findViewById(R.id.name_file);
    }

    @Override
    protected void loadInfo() {

        date = (TextView) itemView.findViewById(R.id.metaData);

        size = (TextView) itemView.findViewById(R.id.sizeFile);
    }

    @Override
    protected void bindIcon(File file, Boolean selected) {
        if (FileHelper.FileType.getFileType(file) == APK){
            try {
                String APKFilePath = file.getPath();
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);
                // the secret are these two lines....
                pi.applicationInfo.sourceDir = APKFilePath;
                pi.applicationInfo.publicSourceDir = APKFilePath;
                //
                Drawable APKicon = pi.applicationInfo.loadIcon(pm);
                Bitmap bitmap = ((BitmapDrawable) APKicon).getBitmap();
                Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 48, 48, true));
                image.setImageDrawable(d);
            }catch (Exception e){
                image.setImageResource(getImageResource(file));
            }
        }else{
            image.setImageResource(getImageResource(file));

            //Si queremos aplicar color de iconos
        /*
        if (getBoolean(context, "pref_icon", true)) {

            image.setOnClickListener(onActionClickListener);

            image.setOnLongClickListener(onActionLongClickListener);

            if (selected) {

                int color = ContextCompat.getColor(context, R.color.misc_file);

                image.setBackground(getBackground(color));

                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_selected);

                DrawableCompat.setTint(drawable, Color.rgb(255, 255, 255));

                image.setImageDrawable(drawable);
            }
            else {

                int color = ContextCompat.getColor(context, getColorResource(file));

                image.setBackground(getBackground(color));

                Drawable drawable = ContextCompat.getDrawable(context, getImageResource(file));

                DrawableCompat.setTint(drawable, Color.rgb(255, 255, 255));

                image.setImageDrawable(drawable);
            }
        }
        else {

            int color = ContextCompat.getColor(context, getColorResource(file));

            image.setBackground(null);

            Drawable drawable = ContextCompat.getDrawable(context, getImageResource(file));

            DrawableCompat.setTint(drawable, color);

            image.setImageDrawable(drawable);
        }*/
        }
    }

    @Override
    protected void bindName(File file) {
        sharedPreferences = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        boolean extension = sharedPreferences.getBoolean("HIDEEXTENSIONS",true);

        name.setText(extension ? getName(file) : file.getName());
    }

    @Override
    protected void bindInfo(File file) {
        sharedPreferences = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        date.setText(getLastModified(file));

        size.setText(getSize(context, file));

        setVisibility(date, sharedPreferences.getBoolean("LASTMODIFIED",true));

        setVisibility(size, sharedPreferences.getBoolean("SIZEFILES",false));
    }

    private ShapeDrawable getBackground(int color) {

        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        int size = (int) context.getResources().getDimension(R.dimen.avatar_size);

        shapeDrawable.setIntrinsicWidth(size);

        shapeDrawable.setIntrinsicHeight(size);

        shapeDrawable.getPaint().setColor(color);

        return shapeDrawable;
    }
}
