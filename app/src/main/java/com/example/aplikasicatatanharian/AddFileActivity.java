package com.example.aplikasicatatanharian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class AddFileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE__STORAGE = 100;
    int eventID = 0;
    EditText edtFileName, edtContent;
    Button btnSimpan;
    boolean isEditable = false;
    String fileName = "";
    String tempCatatan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);

        edtFileName = findViewById(R.id.editFilenama);
        edtContent = findViewById(R.id.editKeterangan);
        btnSimpan = findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            fileName = extras.getString("Filename");
            edtFileName.setText(fileName);
            getSupportActionBar().setTitle("Ubah Catatan");
        } else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }
        eventID = 1;
        if (Build.VERSION.SDK_INT >= 23) {
            if(periksaIzinPenyimpanan()){
                bacaFile();
            }
        } else {
            bacaFile();
        }
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSimpan:
                eventID = 1;
                if(!tempCatatan.equals(edtContent.getText().toString())){
                    if (Build.VERSION.SDK_INT >= 23){
                        if(periksaIzinPenyimpanan()){
                            tampilkanDialogKonfirmasiPenyimpanan();
                        }
                    } else {
                        tampilkanDialogKonfirmasiPenyimpanan();
                    }
                }
                break;
        }
    }
    public boolean periksaIzinPenyimpanan(){
        if(Build.VERSION.SDK_INT >= 23){
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return false;
            } else {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE__STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }
    void bacaFile(){
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File file = new File(path, edtFileName.getText().toString());
        if (file.exists()){
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null){
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e){
                System.out.println("Error "+ e.getMessage());
            }
            tempCatatan = text.toString();
            edtContent.setText(text.toString());
        }
    }
    void buatDanUbah(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return;
        }
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File parent = new File(path);
        if(parent.exists()){
            File file = new File(path, edtFileName.getText().toString());
            FileOutputStream outputStream = null;
            try{
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(edtContent.getText());
                streamWriter.flush();
                streamWriter.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            parent.mkdir();
            File file = new File(path, edtFileName.getText().toString());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(edtContent.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
                } catch (Exception e){
                e.printStackTrace();
            }
        }
        onBackPressed();
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        switch (requestCode){
            case REQUEST_CODE__STORAGE:
            if(grantResult[0] == PackageManager.PERMISSION_GRANTED){
                if (eventID == 1){
                    bacaFile();
                } else {
                    tampilkanDialogKonfirmasiPenyimpanan();
                }
            } break;
        }
    }
    void tampilkanDialogKonfirmasiPenyimpanan(){
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Apakah anda akan menyimpan Catatan ini?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton){
                    buatDanUbah();
                }
                })
                .setNegativeButton(android.R.string.yes, null).show();
    }
    public void onBackPressed(){
        if(!tempCatatan.equals(edtContent.toString())){
            tampilkanDialogKonfirmasiPenyimpanan();
        }
        super.onBackPressed();
    }
}