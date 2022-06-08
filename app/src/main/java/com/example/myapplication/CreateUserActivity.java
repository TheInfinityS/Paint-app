package com.example.myapplication;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;
import android.content.Intent;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;


public class CreateUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private PaintView paintView;
    private int defaultColor;
    private int STORAGE_PERMISSION_CODE=1;
    final boolean[] eraserView = {false};
    ImageButton eraser;
    DisplayMetrics displayMetrics=new DisplayMetrics();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase database=getBaseContext().openOrCreateDatabase("painatail.db",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS drawnings (id INTEGER,name STRING, data BLOB)");

        ImageButton button;
        ImageButton option;
        ImageButton changeBrushes;
        ImageButton blendMode;
        ImageButton pouring;
        ImageButton pen;
        ImageButton brush;
        ImageButton pencil;
        ImageButton feltPen;
        ImageButton wierd;
        SeekBar opacityBar;
        SeekBar blurRadius;
        final TextView radius;
        LinearLayout buttons_option;
        LinearLayout brushesAndRadius;
        ScrollView scrollButtons;
        List<Button> listOfButtonsMode=new ArrayList<>();
        Button normalMode;
        Button clearMode;
        Button addMode;
        Button darkenMode;
        Button lightenMode;
        Button multiplayMode;
        Button overlayMode;
        Button screenMode;
        Button xorMode;
        final boolean[] optionsView = {true};
        final boolean[] brushesView={false};
        final boolean[] scrollVisible={false};

        NavigationView navigationView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView=findViewById(R.id.paintView);

        normalMode=findViewById(R.id.normalMode);
        listOfButtonsMode.add(normalMode);
        clearMode=findViewById(R.id.clearMode);
        listOfButtonsMode.add(clearMode);
        addMode=findViewById(R.id.addMode);
        listOfButtonsMode.add(addMode);
        darkenMode=findViewById(R.id.darkenMode);
        listOfButtonsMode.add(darkenMode);
        lightenMode=findViewById(R.id.lightenMode);
        listOfButtonsMode.add(lightenMode);
        multiplayMode=findViewById(R.id.multiplyMode);
        listOfButtonsMode.add(multiplayMode);
        overlayMode=findViewById(R.id.overlayMode);
        listOfButtonsMode.add(overlayMode);
        screenMode=findViewById(R.id.screenMode);
        listOfButtonsMode.add(screenMode);
        xorMode=findViewById(R.id.xorMode);
        listOfButtonsMode.add(xorMode);

        navigationView=findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        button=findViewById(R.id.change_color_button);
        option=findViewById(R.id.options);
        opacityBar=findViewById(R.id.opacityBar);
        eraser=findViewById(R.id.eraser);
        final TextView opacityText=findViewById(R.id.opacity);
        changeBrushes=findViewById(R.id.changeBrushes);
        blendMode=findViewById(R.id.blendMode);
        pouring=findViewById(R.id.pouring);
        buttons_option=findViewById(R.id.buttons_option);
        pen=findViewById(R.id.pen);
        pencil=findViewById(R.id.pencil);
        brush=findViewById(R.id.brush);
        feltPen=findViewById(R.id.feltPen);
        wierd=findViewById(R.id.wierd);
        brushesAndRadius=findViewById(R.id.brushesAndRadius);
        blurRadius=findViewById(R.id.blurRadius);
        radius=findViewById(R.id.raidus);
        scrollButtons=findViewById(R.id.scrollViewButtons);

        SeekBar seekBar=findViewById(R.id.seeBar);
        final TextView textView=findViewById(R.id.current_pen_size);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        paintView.initialise(displayMetrics);

        opacityBar.setProgress(0xff);
        seekBar.setProgress(10);
        blurRadius.setProgress(10);
        opacityText.setText("Opacity: "+opacityBar.getProgress());
        textView.setText("Pen size: "+ seekBar.getProgress());
        radius.setText("Blur radius: "+blurRadius.getProgress());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColourPicker();
            }
        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsView[0] =!optionsView[0];
                if(optionsView[0]==true){
                    buttons_option.setVisibility(View.INVISIBLE);
                    option.setBackgroundColor(getResources().getColor(R.color.grey));
                }
                else {
                    buttons_option.setVisibility(View.VISIBLE);
                    option.setBackgroundColor(getResources().getColor(R.color.whiteGrey));
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                paintView.setStrokeWidth(seekBar.getProgress());
                textView.setText("Pen size: "+ seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        opacityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintView.setCurrentOpacity(opacityBar.getProgress());
                opacityText.setText("Opacity: "+opacityBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        blurRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintView.setBlurRadius(blurRadius.getProgress());
                radius.setText("Blur radius: "+blurRadius.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraserView[0] =!eraserView[0];
                if(eraserView[0] ==true){
                    paintView.eraser();
                    eraser.setBackgroundColor(getResources().getColor(R.color.whiteGrey));
                }
                else{
                    paintView.setColor(defaultColor);
                    eraser.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });
        blendMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollVisible[0]=!scrollVisible[0];
                if(scrollVisible[0]==true){
                    scrollButtons.setVisibility(View.VISIBLE);
                    blendMode.setBackgroundColor(getResources().getColor(R.color.whiteGrey));
                }
                else{
                    scrollButtons.setVisibility(View.INVISIBLE);
                    blendMode.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });
        pouring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.poring();
            }
        });
        changeBrushes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brushesView[0]=!brushesView[0];
                if(brushesView[0]==false){
                    brushesAndRadius.setVisibility(View.INVISIBLE);
                    changeBrushes.setBackgroundColor(getResources().getColor(R.color.grey));
                }
                else{
                    brushesAndRadius.setVisibility(View.VISIBLE);
                    changeBrushes.setBackgroundColor(getResources().getColor(R.color.whiteGrey));
                }
            }
        });
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setPen();
            }
        });
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setPencil();
            }
        });
        feltPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setFeltPen();
            }
        });
        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setBrush();
            }
        });
        wierd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setWierd();
            }
        });
        for(int i=0;i<listOfButtonsMode.size();i++){
            int finalI = i;
            listOfButtonsMode.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintView.blendMode(finalI);
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }
    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permission needed").setMessage("Nedded to save image").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(CreateUserActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Access granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Access denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.clear_buttton:
                paintView.clear();
                return  true;
            case R.id.undo_button:
                paintView.undo();
                return true;
            case R.id.redo_buttton:
                paintView.redo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.export:
                if(ContextCompat.checkSelfPermission(CreateUserActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    requestStoragePermission();
                }
                saveProject();
                break;
            case R.id.openImage:
                openImages();
                break;
            case R.id.newFile:
                paintView.newFile();
                break;
        }
        return true;
    }
    protected void openColourPicker(){
        AmbilWarnaDialog ambilWarnaDialog=new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(CreateUserActivity.this,"Unavailable",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor=color;
                paintView.setColor(color);
                if(eraserView[0] ==true){
                    eraser.setBackgroundColor(getResources().getColor(R.color.grey));
                    eraserView[0]=!eraserView[0];
                }
            }
        });
        ambilWarnaDialog.show();
    }
    protected void openImages(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Pick an image"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                paintView.initializeImage(inputStream);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void saveProject(){
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(
                CreateUserActivity.this);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        EditText nameProject=bottomSheetDialog.findViewById(R.id.projectName);
        Button buttonSaveProject=bottomSheetDialog.findViewById(R.id.projectSave);
        Button buttonCancel=bottomSheetDialog.findViewById(R.id.projectCancel);

        buttonSaveProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.saveImage(nameProject.getText().toString());
                bottomSheetDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }
}