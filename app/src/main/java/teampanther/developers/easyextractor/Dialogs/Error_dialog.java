package teampanther.developers.easyextractor.Dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import teampanther.developers.easyextractor.R;

/**
 * Created by luffynando on 31/01/2018.
 */

public class Error_dialog extends DialogFragment {
    private View view;
    private String title, link;
    private String text;
    private TextView titulo, texto,links;
    private android.text.Spanned msg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.error_dialog, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        titulo = (TextView) view.findViewById(R.id.textViewErrorDialogTitle);
        texto = (TextView) view.findViewById(R.id.textViewErrorDialogDialog);
        links = (TextView) view.findViewById(R.id.error_links);

        titulo.setText(title);
        if (msg != null) {
            texto.setText(msg);
            links.setText("Link");
            links.setVisibility(View.VISIBLE);
            links.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + link)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + link)));
                    }
                }
            });
        } else {
            texto.setText(text);
        }
        return view;
    }

    public void setTitulo(String text) {
        title = text;
    }

    public void setMessage(String text) {
        this.text = text;
    }

    public void setMessage(android.text.Spanned msg) {
        this.msg = msg;
    }

    public void setLink(String link){
        this.link= link;
    }
}
