package com.example.myapplication;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecodingFileClass {

    Context context;
    SerialStart serialStart;
    OutputStreamWriter writer;
    private boolean flagExceptionWriteFile;

    public RecodingFileClass(Context context,SerialStart serialStart,OutputStreamWriter writer){
        this.context=context;
        this.serialStart=serialStart;
        this.writer=writer;
        flagExceptionWriteFile=false;
    }

    public void writeFile(String line){
        try {
            writer.write(line);
        } catch (IOException e) {
            if(!flagExceptionWriteFile){
                Toast.makeText(context,"Ошибка записи в файл!",Toast.LENGTH_LONG).show();
                flagExceptionWriteFile=true;
            }
        }
    }

    public void StartRecoding(String fileName){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(context.getExternalFilesDir(null),fileName);
            File fhandle = new File(file.getAbsolutePath());
            if (!fhandle.getParentFile().exists()) {
                fhandle.getParentFile().mkdirs();
            }
            try {
                fhandle.createNewFile();
                writer =new OutputStreamWriter(new FileOutputStream(fhandle,true));

                Date dateNow = new Date();
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                writer.write("B-"+formatForDateNow.format(dateNow)+"\n");

                serialStart.recodingFlagTrue();
            } catch (IOException e) {
                Toast.makeText(context,"Поток записи не открыт!",Toast.LENGTH_SHORT);
            }
        }

    }

    public void StopRecoding(){
        serialStart.recodingFlagFalse();
        try {
            Date dateNow = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            writer.write("E-"+formatForDateNow.format(dateNow)+"\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
