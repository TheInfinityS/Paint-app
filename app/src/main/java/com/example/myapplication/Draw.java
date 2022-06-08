package com.example.myapplication;

import android.graphics.BlurMaskFilter;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;

public class Draw {
    public int color;
    public  int strokeWidth;
    public Path path;
    public int opacity;
    public BlurMaskFilter blurMaskFilter;
    public PorterDuffXfermode porterDuffXfermode;
    public Draw(int color, int strokeWidth, Path path, int opacity,BlurMaskFilter blurMaskFilter, PorterDuffXfermode porterDuffXfermode){
        this.color=color;
        this.strokeWidth=strokeWidth;
        this.path=path;
        this.opacity=opacity;
        this.blurMaskFilter=blurMaskFilter;
        this.porterDuffXfermode=porterDuffXfermode;
    }
}
