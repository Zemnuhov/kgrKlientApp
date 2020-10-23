package com.example.myapplication.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteLableDialog extends DialogFragment {

    private int count;
    private Context context;
    private OutputStreamWriter myOutWriter;
    private EditText textEdit;
    private Button lableWriteButton;

    public static WriteLableDialog newInstance(int count, Context context) {
        Bundle args = new Bundle();
        WriteLableDialog fragment = new WriteLableDialog();

        fragment.count=count;
        fragment.context=context;

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.write_lable_dialog,null);

        textEdit=view.findViewById(R.id.label_edit);
        lableWriteButton=view.findViewById(R.id.label_write_button);
        lableWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLable(textEdit.getText().toString());
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    void writeLable(String text){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(context.getExternalFilesDir(null),"lable.txt");
            File fhandle = new File(file.getAbsolutePath());
            if (!fhandle.getParentFile().exists()) {
                fhandle.getParentFile().mkdirs();
            }
            try {
                fhandle.createNewFile();
                myOutWriter=new OutputStreamWriter(new FileOutputStream(fhandle,true));
            } catch (IOException e) {
                return;
            }
        }

        try {
            myOutWriter.write(String.valueOf(count)+" "+text+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            myOutWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
