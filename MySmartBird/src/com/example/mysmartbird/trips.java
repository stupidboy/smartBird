package com.example.mysmartbird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;

public class trips {
    int mXpos;

    int mYpos;

    int mWidth;

    int mHeight;

    Bitmap mBitmapUp;
    Bitmap mBitmapDown;

    int mPanelWidth;

    int mPandleHeight;

    Context mContext;
    Random mRandom = new Random();
    RectF mRect = new RectF();
    int mCross;
    int mVx = 2;
    boolean mCrossed = false;
    public static int TRIPS_WIDTH = 80;
    boolean mHardMode =false;
    int mVy = 1;
    public trips(Context context, Bitmap upBit, Bitmap downBit, int panel_width, int panel_height){
        mPanelWidth = panel_width;
        mPandleHeight = panel_height;
        mContext = context;
        mBitmapUp = upBit;//BitmapFactory.decodeResource(context.getResources(), R.drawable.leo_bird);
        mBitmapDown= downBit;
       // mHeight = hight;
        mYpos = 0;
        mWidth = 100;
       // mCross = cross;
        
        setHightandCross();
    }
    public void setHardMode(boolean enable){
        
        mHardMode = enable;
    }
    void setHightandCross(){     
        mHeight =200+mRandom.nextInt(250);
        mCross = 200+mRandom.nextInt(100);
        mCrossed = false;
        mDelta = 0;
    }
    public void draw(Canvas canvas,int status) {
        if(status == 1){
            getNextPos();
            if(mHardMode){
                getNextHeight();
            }
            
        }else if(status == 0){
           // resetPos();
        }
       
        //draw up
        mRect.set(mXpos, mYpos, mXpos + mWidth, mYpos + mHeight);
        canvas.drawBitmap(mBitmapUp, null, mRect, null);
        //draw down
        mRect.set(mXpos,mYpos+mHeight+mCross,mXpos+mWidth,mPandleHeight);
        canvas.drawBitmap(mBitmapDown, null, mRect, null);   
    }
    private double mDelta =0;
    private double mPexleMsend = 0.05;
    void getNextHeight(){ 
        if(mDelta>2||mDelta<0){
            mPexleMsend = -mPexleMsend;
        }
        mDelta+=mPexleMsend;
        mHeight +=mDelta;
    }
    public void setX(int x) {
        mXpos = x;
    }

    public void setY(int y) {
        mYpos = y;
    }

    private void getNextPos() {
        mXpos -=mVx;
        if(mXpos <=-mWidth-10){
            resetPos();     
        }
    }
    private void resetPos(){
        mXpos+=mPanelWidth+mWidth+10;
        setHightandCross();
    }
    public boolean isbirdCrash(int x, int y,int width ,int hight){
        RectF  ret = new RectF();
        ret.set(mXpos -width,mYpos,mXpos+mWidth,mYpos+mHeight);
        boolean inUp = ret.contains(x, y);
        ret.set(mXpos-width,mYpos+mHeight+mCross-hight,mXpos+mWidth,mPandleHeight);
        boolean inDown = ret.contains(x, y);
        boolean isGround = y>mPandleHeight-width;
        return inDown||inUp||isGround;
    }
    public boolean  birdCross(int x, int y,int width ,int hight){
    
        if((!isbirdCrash(x,y,width,hight))&&x>=mXpos+mWidth&&!mCrossed){
            mCrossed = true;
            return true;
        }
        return false;
    } 
}
