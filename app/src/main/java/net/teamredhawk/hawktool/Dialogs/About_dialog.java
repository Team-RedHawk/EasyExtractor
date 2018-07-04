package net.teamredhawk.hawktool.Dialogs;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.teamredhawk.hawktool.R;

/**
 * Created by luffynando on 27/01/2018.
 */

public class About_dialog extends DialogFragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about_dialog, container);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        ImageView logo= (ImageView) view.findViewById(R.id.logo_about);
        TextView acerca= (TextView) view.findViewById(R.id.textViewAboutDialogDialog);

        setRoundedImage(logo,R.drawable.splash_logo);
        setTextAbout(acerca,R.raw.about);

        return view;
    }

    public void setRoundedImage(ImageView imageView, int img){
        Drawable imagen = getResources().getDrawable(img);
        Bitmap imagen_bitmap = ((BitmapDrawable) imagen).getBitmap();
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),imagen_bitmap);
        roundedBitmapDrawable.setCornerRadius(imagen_bitmap.getHeight());
        imageView.setImageDrawable(roundedBitmapDrawable);

    }

    public void setTextAbout(TextView editText, int file) {

        InputStream canal = getResources().openRawResource(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(canal));

        try {

            String linea = bufferedReader.readLine();
            String res="";
            while(linea != null){
                res= res.concat(linea+"\n");
                linea = bufferedReader.readLine();
            }
            canal.close();
            editText.setText(res);
        }catch (Exception e){
            editText.setText("LARALALALA");
        }

    }

}
