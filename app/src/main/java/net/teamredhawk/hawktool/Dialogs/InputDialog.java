package net.teamredhawk.hawktool.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import net.teamredhawk.hawktool.R;

/**
 * Created by luffynando on 22/01/2018.
 */

public abstract class InputDialog extends AlertDialog.Builder {

    private final EditText editText;

    protected InputDialog(Context context, String positive, String title) {

        super(context);

        View view = View.inflate(context, R.layout.dialog_edit_text, null);

        editText = (EditText) view.findViewById(R.id.dialog_edit_text);

        setView(view);

        setNegativeButton("Cancel", null);

        setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (editText.length() != 0)
                    InputDialog.this.onActionClick(editText.getText().toString());
            }
        });

        setTitle(title);
    }

    public abstract void onActionClick(String text);

    public void setDefault(String text) {

        editText.setText(text);

        editText.setSelection(editText.getText().length());
    }
}