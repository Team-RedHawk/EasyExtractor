package teampanther.developers.easyextractor.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import teampanther.developers.easyextractor.R;
import teampanther.developers.easyextractor.UtilsHelper.Closer;

import static android.content.ContentValues.TAG;
import static android.provider.Telephony.Mms.Part.FILENAME;

/**
 * Created by luffynando on 28/01/2018.
 */

public class ScriptDialog extends DialogFragment {
    private View view;
    private File file;

    public void addFile(File file){
        this.file = file;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.script_dialog, container);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView script= (TextView) view.findViewById(R.id.script_text_dialog);

        executeScript(script,R.raw.test);
        return view;
    }

    public void executeScript(TextView textView, int file){
        String res = "";
        try {
            /*InputStream ins = getResources().openRawResource(file);
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            FileOutputStream fos = getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(buffer);
            fos.close();


            File file2 = getContext().getFileStreamPath (FILENAME);
            file2.setExecutable(true);*/
            /*String[] strings= {"chmod +x "+this.file.getAbsolutePath()+File.separator+"test.sh","./"+this.file.getAbsolutePath()+File.separator+"test.sh"};
            res= sudoForResult(strings);*/
            Process process = Runtime.getRuntime().exec("sh "+this.file.getAbsolutePath()+File.separator+"test.sh");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String listOfFiles = "";
            String line;
            while ((line = in.readLine()) != null) {
                listOfFiles += line;
            }
            textView.setText(listOfFiles);
        } catch (Exception e) {
            e.printStackTrace();
            textView.setText("Error");
        }
    }

}
