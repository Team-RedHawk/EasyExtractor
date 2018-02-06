package teampanther.developers.easyextractor.Dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.system.Os;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

import teampanther.developers.easyextractor.R;
import teampanther.developers.easyextractor.UtilsHelper.FileHelper;

/**
 * Created by luffynando on 28/01/2018.
 */

public class ScriptDialog extends DialogFragment {
    private View view;
    private String fileImgName;
    private String directorio;
    private ScrollView scrollView;
    private TextView script;
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;
    private Button boton;
    private String error;
    private Boolean iserror= false;
    private File direct;
    private String res;


    //Boolean mode si es verdadero unpack, si es falso repack
    private Boolean mode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.script_dialog, container);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        setCancelable(false);

        script = (TextView) view.findViewById(R.id.script_text_dialog);
        scrollView = (ScrollView) view.findViewById(R.id.script_scroll);
        linearLayout= (LinearLayout) view.findViewById(R.id.progressLinear);
        boton= (Button) view.findViewById(R.id.buttonAcept);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        scrollView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        if (initCommands()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    res = "";
                    res = FileHelper.sudoForResult("chmod 0666 /data/local/tmp/ARM/"+fileImgName,"cd data/local/tmp/ARM","./mkboot "+fileImgName+" "+directorio);
                    if (res.isEmpty()) {
                        iserror = true;
                        error = getString(R.string.error_empty_string);
                    } else {
                        iserror = false;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iserror) {
                                Toast nueva = Toast.makeText(getContext(),
                                        error, Toast.LENGTH_SHORT);
                                nueva.show();
                                getDialog().dismiss();
                            } else {
                                script.setText(res);
                                linearLayout.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                }
            });
            t.start();
        }else{
            script.setText(getString(R.string.Error_no_permissions_or_space));
            linearLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void addNameImg(String img){
        fileImgName= img;
    }

    public void addDirectName(String directorio){
        this.directorio=directorio;
    }

    public void mode(Boolean mode){
        this.mode= mode;
    }

    public void setFileDirect(File dir){
        this.direct= dir;
    }

    public boolean initCommands(){
        boolean flag= false;
        if (mode){
            //UNPACK
            File boot= new File(direct,fileImgName);
            File directorio= new File("/data/local/tmp/ARM");
            try {
                FileHelper.copyFile(boot,directorio);
                flag=true;
            }catch (Exception e){
                e.printStackTrace();
                flag= false;
            }
        }else{
            //REPACK

        }
        return  flag;
    }

}
