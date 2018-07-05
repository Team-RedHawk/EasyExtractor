package net.teamredhawk.hawktool.Dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import net.teamredhawk.hawktool.RecyclerView.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.Objects;

import net.teamredhawk.hawktool.R;
import net.teamredhawk.hawktool.UtilsHelper.FileHelper;

import static net.teamredhawk.hawktool.UtilsHelper.FileHelper.getChildren;

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
    private final String PATHDIRECTORY= "/data/local/tmp/ARM";
    private Adapter adapter;


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
        if (mode){
            UnPackMode();
        }else{
            RePackMode();
        }

        return view;
    }

    private void UnPackMode(){
        Thread hiloUnpack= new Thread(new Runnable() {
            @Override
            public void run() {
                res = "";
                final File bootimg= new File(direct,fileImgName);
                final String permisoboot= "chmod 0666 "+bootimg.getAbsolutePath();
                String pathfilescript= "cd "+PATHDIRECTORY;
                final String pathfiletest= "../../../.."+bootimg.getAbsolutePath();
                final String pathdirectout= "../../../.."+bootimg.getParentFile()+"/"+directorio;
                res = FileHelper.sudoForResult(permisoboot,pathfilescript,"./mkboot "+pathfiletest+" "+pathdirectout);
                if (res.isEmpty()) {
                    iserror = true;
                    error = getString(R.string.error_empty_string);
                } else {
                    iserror = false;
                }
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        File directorionuevo= new File(pathdirectout);
                        if (directorionuevo.exists()){
                            if (iserror) {
                                Toast nueva = Toast.makeText(getContext(),
                                        error, Toast.LENGTH_SHORT);
                                nueva.show();
                                getDialog().dismiss();
                            } else {
                                script.setText(res);
                                linearLayout.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                                adapter.clear();
                                adapter.clearSelection();
                                adapter.addAll(getChildren(direct));
                            }
                        }else {
                            Toast nueva = Toast.makeText(getContext(),
                                    getString(R.string.Error_no_permissions_or_space), Toast.LENGTH_SHORT);
                            nueva.show();
                            getDialog().dismiss();
                        }
                    }
                });
            }
        });
        hiloUnpack.start();
    }

    private void RePackMode(){
        Thread hiloRepack= new Thread(new Runnable() {
            @Override
            public void run() {
                res= "";
                final File filedirectorio= new File(direct.getAbsolutePath()+File.separator+directorio);
                String pathfilescript= "cd "+PATHDIRECTORY;
                final File bootimg= new File(direct,fileImgName);
                res = FileHelper.sudoForResult(pathfilescript,"./mkboot "+filedirectorio.getAbsolutePath()+" "+bootimg.getAbsolutePath());
                if (res.isEmpty()) {
                    iserror = true;
                    error = getString(R.string.error_empty_string);
                } else {
                    iserror = false;
                }
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bootimg.exists()){
                            if (iserror) {
                                Toast nueva = Toast.makeText(getContext(),
                                        error, Toast.LENGTH_SHORT);
                                nueva.show();
                                getDialog().dismiss();
                            } else {
                                script.setText(res);
                                linearLayout.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                                try{ FileHelper.deleteFile(filedirectorio);}catch (Exception e){}
                                adapter.clear();
                                adapter.clearSelection();
                                adapter.addAll(getChildren(direct));
                            }
                        }else {
                            Toast nueva = Toast.makeText(getContext(),
                                    getString(R.string.Error_no_permissions_or_space), Toast.LENGTH_SHORT);
                            nueva.show();
                            getDialog().dismiss();
                        }
                    }
                });
            }
        });
        hiloRepack.start();
    }

    public void setAdapter(Adapter adapter){
        this.adapter = adapter;
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

}
