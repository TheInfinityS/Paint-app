package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;

public class PaintView extends View {
    public static int brush_size=10;
    public static final int default_color= Color.BLACK;
    public static final int default_bg_color=Color.WHITE;
    private static final float touch_tolerance=4;
    public static final int opacity=0xff;
    public static final int blurRadius=10;

    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private int currentColor;
    private int backgroundColor=default_bg_color;
    private int strokeWidth;
    private int currentOpacity;
    private int currentBlurRadius;
    private BlurMaskFilter currentBlurMaskFilter;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int color_eraser=backgroundColor;
    private Paint mBitmapPaint=new Paint(Paint.DITHER_FLAG);
    private PorterDuffXfermode porterDuffXfermode;

    int height;
    int width;


    private ArrayList<Draw> paths=new ArrayList<>();
    private ArrayList<Draw> undo=new ArrayList<>();


    public PaintView(Context context){
        super(context,null);
    }
    public PaintView(Context context, AttributeSet attrs){
        super(context,attrs);

        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(default_color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setMaskFilter(null);
        mPaint.setXfermode(null);

    }
    public void initialise(DisplayMetrics displayMetrics){
        height=displayMetrics.heightPixels;
        width=displayMetrics.widthPixels;
        mBitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(mBitmap);
        currentOpacity=opacity;
        currentColor=default_color;
        strokeWidth=brush_size;
        currentBlurRadius=blurRadius;
    }
    public void initializeImage(InputStream inputStream){
        Bitmap bitmapImage=BitmapFactory.decodeStream(inputStream).copy(Bitmap.Config.ARGB_8888,true);
        backgroundColor=0;
        mCanvas.drawBitmap(bitmapImage,new Rect(0,0,bitmapImage.getWidth(),bitmapImage.getHeight()),new Rect(0,0,width,height),mPaint);
    }
    @Override
    protected void onDraw(Canvas canvas){
        canvas.save();
        mCanvas.drawColor(backgroundColor);
        for(Draw draw:paths){
            mPaint.setColor(draw.color);
            mPaint.setStrokeWidth(draw.strokeWidth);
            mPaint.setAlpha(draw.opacity);
            mPaint.setMaskFilter(draw.blurMaskFilter);
            mPaint.setXfermode(porterDuffXfermode);
            mCanvas.drawPath(draw.path,mPaint);
        }
        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
        canvas.restore();
    }
    private void touchStart(float x, float y){
        mPath=new Path();
        Draw draw=new Draw(currentColor,strokeWidth,mPath,currentOpacity,currentBlurMaskFilter,porterDuffXfermode);
        paths.add(draw);
        mPath.reset();
        mPath.moveTo(x,y);
        mX=x;
        mY=y;
    }
    private void touchMove(float x,float y){
        float dx=Math.abs(x-mX);
        float dy=Math.abs(y-mY);

        if((dx>=touch_tolerance)||(dy>=touch_tolerance)){
            mPath.quadTo(mX,mY,(x+mX)/2,(y+mY)/2);
            mX=x;
            mY=y;
        }
    }
    private void touchUp(){
        mPath.lineTo(mX,mY);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x=event.getX();
        float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                invalidate();
                break;
        }
        return true;
    }
    public void clear(){
        backgroundColor=default_bg_color;
        mPath=new Path();
        Draw draw=new Draw(backgroundColor,width,mPath,opacity,null,null);
        paths.add(draw);
        mPath.reset();
        mPath.moveTo(width/2,0);
        mPath.lineTo(width/2,height);
    }
    public void undo(){
        if(paths.size()>0){
            undo.add(paths.remove(paths.size()-1));
            invalidate();
        }
        else{
            Toast.makeText(getContext(),"Nothing to undo",Toast.LENGTH_LONG).show();
        }
    }
    public void redo(){
        if(paths.size()>0){
            paths.add(undo.remove(undo.size()-1));
            invalidate();
        }
        else{
            Toast.makeText(getContext(),"Nothing to redo",Toast.LENGTH_LONG).show();
        }
    }
    public void setPencil(){
        currentBlurMaskFilter=new BlurMaskFilter(currentBlurRadius, BlurMaskFilter.Blur.INNER);
    }
    public void setBrush(){
        currentBlurMaskFilter=null;
    }
    public void setFeltPen(){
        currentBlurMaskFilter=new BlurMaskFilter(currentBlurRadius, BlurMaskFilter.Blur.SOLID);
    }
    public void setPen(){
        currentBlurMaskFilter=new BlurMaskFilter(currentBlurRadius, BlurMaskFilter.Blur.NORMAL);
    }
    public void setWierd(){
        currentBlurMaskFilter=new BlurMaskFilter(currentBlurRadius, BlurMaskFilter.Blur.OUTER);
    }
    public void setBlurRadius(int Radius){
        currentBlurRadius=Radius;
    }
    public void setStrokeWidth(int width){strokeWidth=width;}
    public void setColor(int color){
        currentColor=color;
    }
    public void eraser(){currentColor=color_eraser;}
    public void setCurrentOpacity(int opacity){currentOpacity=opacity;}
    public void saveImage(String name){
        int count=0;
        File sdDIRECTORY= Environment.getExternalStorageDirectory();
        File subDirectory=new File(sdDIRECTORY.toString()+"/Pictures/Paint");
        if(subDirectory.exists()){
            File[] existing=subDirectory.listFiles();
            for(File file:existing){
                while(file.getName().equals(name+""+count+".jpg")||file.getName().equals(name+""+count+".png")){
                    count++;
                }
            }
        }
        else {
            subDirectory.mkdir();
        }
        if(subDirectory.exists()){
            File image=new File(subDirectory,"/"+name+""+(count)+".png");
            FileOutputStream fileOutputStream;
            try{
                fileOutputStream=new FileOutputStream(image);
                mBitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText(getContext(),"exported",Toast.LENGTH_LONG).show();
            }catch (FileNotFoundException e){

            }
            catch (IOException e){

            }
        }
    }
    public void poring(){
        mPath=new Path();
        Draw draw=new Draw(currentColor,width,mPath,currentOpacity,currentBlurMaskFilter,porterDuffXfermode);
        paths.add(draw);
        mPath.reset();
        mPath.moveTo(width/2,0);
        mPath.lineTo(width/2,height);
    }

    public void saveFile(){
        Toast.makeText(getContext(),"saved",Toast.LENGTH_LONG).show();
    }
    public void newFile(){
        backgroundColor=default_bg_color;
        paths.clear();
        invalidate();
    }
    public void blendMode(int i){
        switch (i){
            case 0:
                porterDuffXfermode=null;
                break;
            case 1:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
                break;
            case 2:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.ADD);
                break;
            case 3:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.DARKEN);
                break;
            case 4:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN);
                break;
            case 5:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
                break;
            case 6:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.OVERLAY);
                break;
            case 7:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.SCREEN);
                break;
            case 8:
                porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.XOR);
                break;
        }
    }
}
