package teampanther.developers.easyextractor.Dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import teampanther.developers.easyextractor.R;
import teampanther.developers.easyextractor.UtilsHelper.FileHelper;

import static android.content.ContentValues.TAG;

/**
 * Created by luffynando on 31/01/2018.
 */

public class ToolDialogSelect extends DialogFragment implements View.OnClickListener{
    private View view;
    private CardView external, internal;
    private Button aceptar, cancelar;
    private RelativeLayout items_layout;
    private LinearLayout linearLayout_buttons, progress;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView textView1, textView2, textView3;
    private Switch tool;
    private Boolean isExternal, iserror=false;
    private int CurrentId=-1;
    private String error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        view = inflater.inflate(R.layout.unpack_repack_dialog, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        init();

        return view;
    }

    public  void init(){
        external= (CardView) view.findViewById(R.id.card_view1);
        internal= (CardView) view.findViewById(R.id.card_view2);
        aceptar= (Button) view.findViewById(R.id.buttonAgree);
        cancelar= (Button) view.findViewById(R.id.buttonDisagree);
        textView1= (TextView) view.findViewById(R.id.textView1);
        textView2= (TextView) view.findViewById(R.id.textView2);
        textView3= (TextView) view.findViewById(R.id.textProgress);
        items_layout= (RelativeLayout) view.findViewById(R.id.relativeLayoutCardViews);
        linearLayout_buttons= (LinearLayout) view.findViewById(R.id.linearLayoutButtons);
        progress= (LinearLayout) view.findViewById(R.id.progressLinear);

        if(!isExternal){
            external.setVisibility(View.GONE);
        }

        external.setOnClickListener(this);
        internal.setOnClickListener(this);
        aceptar.setOnClickListener(this);
        cancelar.setOnClickListener(this);
    }

    public void setTool(Switch tool){
        this.tool = tool;
    }

    public void setifExternal(Boolean flag){
        isExternal= flag;
    }

    public void resaltadonormal(){
        textView1.setTypeface(null, Typeface.NORMAL);
        textView2.setTypeface(null, Typeface.NORMAL);
        textView1.setTextSize(14);
        textView2.setTextSize(14);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_view1:
                resaltadonormal();
                textView1.setTypeface(null, Typeface.BOLD);
                textView1.setTextSize(16);
                CurrentId= 1;
                break;
            case R.id.card_view2:
                resaltadonormal();
                textView2.setTypeface(null, Typeface.BOLD);
                textView2.setTextSize(16);
                CurrentId=2;
                break;
            case R.id.buttonAgree:
                tool.setChecked(true);
                linearLayout_buttons.setVisibility(View.GONE);
                items_layout.setVisibility(View.GONE);
                textView3.setText("Procesando");
                progress.setVisibility(View.VISIBLE);
                Thread hilo= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (CurrentId != -1){
                            //Setear si es 1 externa, si es 2 interna
                            if(CurrentId == 1){
                                if (createToolDirectory(FileHelper.getStoragePath(getContext(),true)+ File.separator+"Mkboot")) {
                                    if (copyAssets("ARM",FileHelper.getStoragePath(getContext(),true)+ File.separator+"Mkboot")){
                                        getPermissions(FileHelper.getStoragePath(getContext(),true)+ File.separator+"Mkboot");
                                        if (copyBash(FileHelper.getStoragePath(getContext(),true)+ File.separator+"Mkboot")){
                                            setPathShared(FileHelper.getStoragePath(getContext(),true)+ File.separator+"Mkboot");
                                        }else {
                                            error=getString(R.string.error_copy_bash);
                                            iserror=true;
                                        }
                                    }else{
                                        error=getString(R.string.error_copy_file);
                                        iserror=true;
                                    }
                                }else {
                                    error=getString(R.string.error_create_directory);
                                    iserror=true;
                                }
                            }else{
                                if (createToolDirectory(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot")){
                                    if (copyAssets("ARM",FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot")){
                                        getPermissions(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot");
                                        if (copyBash(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot")){
                                            setPathShared(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot");
                                        }else {
                                            error=getString(R.string.error_copy_bash);
                                            iserror=true;
                                        }
                                    }else{
                                        error=getString(R.string.error_copy_file);
                                        iserror=true;
                                    }
                                }else {
                                    error=getString(R.string.error_create_directory);
                                    iserror=true;
                                }
                            }
                        }else{
                            //setear por defecto interno
                            if (createToolDirectory(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot")){
                                if (copyAssets("ARM",FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot")){
                                    getPermissions(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot");
                                    if (copyBash(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot")){
                                        setPathShared(FileHelper.getStoragePath(getContext(),false)+ File.separator+"Mkboot");
                                    }else {
                                        error=getString(R.string.error_copy_bash);
                                        iserror=true;
                                    }
                                }else{
                                    error=getString(R.string.error_copy_file);
                                    iserror=true;
                                }
                            }else {
                                error= getString(R.string.error_create_directory);
                                iserror=true;
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (iserror){
                                    unSetFail(error);
                                }
                                getDialog().dismiss();
                            }
                        });
                    }
                });
                hilo.start();
                break;
            case R.id.buttonDisagree:
                tool.setChecked(false);
                editor = sharedPreferences.edit();
                editor.putBoolean("TOOLENABLE",false).apply();
                editor.putString("PATHTOOL","").apply();
                getDialog().dismiss();
                break;
        }
    }

    public void setPathShared(String path){
        editor = sharedPreferences.edit();
        editor.putBoolean("TOOLENABLE",true).apply();
        editor.putString("PATHTOOL",path).apply();
    }

    public Boolean copyBash(String path){
        Boolean flag= false;
        try{
            String[] wtf= {"busybox mount -o rw,remount system","busybox cp "+path+File.separator+"bash"+" /system/xbin/bash"};
            FileHelper.sudoForResult(wtf);
            FileHelper.sudoForResult("chmod 0755 /system/xbin/bash","busybox mount -o ro,remount system");
            File nuevo= new File("/system/xbin/bash");
            if (nuevo.exists()){
                flag=true;
            }else{
                flag= false;
            }
        }catch (Exception e){
            e.printStackTrace();
            flag=false;
        }
        return flag;
    }

    public void unSetFail(String error){
        editor = sharedPreferences.edit();
        editor.putBoolean("TOOLENABLE",false).apply();
        tool.setChecked(false);
        this.error= error;
        Toast nueva= Toast.makeText(getContext(),
                error, Toast.LENGTH_SHORT);
        nueva.show();
    }

    public Boolean createToolDirectory(String path){
        Boolean flag=false;
        File directory= new File(path) ;
        try {
            if (!directory.exists()){
                if (CurrentId != -1){
                    if (CurrentId == 1){
                        String externalpath= FileHelper.getStoragePath(getContext(),true);
                        if (externalpath != null){
                            File nueva= new File(externalpath);
                            FileHelper.createDirectory(nueva,"Mkboot");
                            flag=true;
                        }else{
                            flag= false;
                        }
                    }else{
                        FileHelper.createDirectory(FileHelper.getInternalStorage(), "Mkboot");
                        flag= true;
                    }
                }else{
                    FileHelper.createDirectory(FileHelper.getInternalStorage(), "Mkboot");
                    flag= true;
                }
            }else {
                flag= true;
            }
        }catch (Exception e){
            Log.e("TOOL: ", "Error en crear directorio");
            flag= false;
        }
        return flag;
    }

    public void getPermissions(String path){
        String[] wtf= {"chmod 0777 "+path+File.separator,
                        "chmod 0777 "+path+File.separator+"bash",
                        "chmod 0777 "+path+File.separator+"cpio",
                        "chmod 0777 "+path+File.separator+"file",
                        "chmod 0777 "+path+File.separator+"grep",
                        "chmod 0777 "+path+File.separator+"gzip",
                        "chmod 0777 "+path+File.separator+"lz4",
                        "chmod 0777 "+path+File.separator+"lzma",
                        "chmod 0777 "+path+File.separator+"magic",
                        "chmod 0777 "+path+File.separator+"mkboot",
                        "chmod 0777 "+path+File.separator+"mkbootfs",
                        "chmod 0777 "+path+File.separator+"mkbootimg",
                        "chmod 0777 "+path+File.separator+"mkimage",
                        "chmod 0777 "+path+File.separator+"od",
                        "chmod 0777 "+path+File.separator+"xz"};
        FileHelper.sudoForResult(wtf);
    }

    private Boolean copyAssets(String path, String outPath) {
        Boolean flag= false;
        AssetManager assetManager = getActivity().getAssets();
        String assets[];
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                flag= copyFile(path, outPath);
            } else {
                File dir = new File(outPath);
                if (!dir.exists())
                    if (!dir.mkdir()) Log.e(TAG, "No create external directory: " + dir );
                for (String asset : assets) {
                    flag= copyAssets(path + "/" + asset, outPath);
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "I/O Exception", ex);
            flag= false;
        }
        return flag;
    }

    private Boolean copyFile(String filename, String outPath) {
        Boolean flag= false;
        AssetManager assetManager = getActivity().getAssets();

        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            String add;
            if (filename.contains("bash")){
                add= "/";
            }else{
                add= "/.";
            }
            String newFileName = outPath + add + filename.replace("ARM/","");
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            flag= true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            flag= false;
        }
        return flag;
    }
}
