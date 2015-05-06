
package com.example.mysmartbird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.Locale;

public class SmartBirdSurface extends SurfaceView implements Callback, Runnable {

    Canvas mCanvas = null;

    SurfaceHolder mHolder = null;

    Thread mRenderThread = null;

    boolean isRunning = false;

    Bitmap mBg = null;

    int mWidth = 480;

    int mHeight = 800;

    RectF mPanelRect = new RectF();

    LeoBird mBird = null;

    Context mContext;

    Bitmap mTripBitmapDown = null;

    Bitmap mTripBitmapUp = null;

    final static int MAX_TRIPS = 2;

    trips[] mTrips = new trips[MAX_TRIPS];

    final int STATUS_INIT = 0;

    final int STATUS_RUNNING = 1;

    final int STATUS_STOP = 2;

    private int mGameStatus = 0;
    
    private int mPoint = 0;

    public SmartBirdSurface(Context context) {
        super(context);
        mHolder = this.getHolder();
        mContext = context;
        mHolder.addCallback(this);
        this.setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        mBg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
    }

    public SmartBirdSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void createTrips() {
        mTripBitmapDown = BitmapFactory.decodeResource(getResources(), R.drawable.trip);
        Matrix ma = new Matrix();
        ma.postRotate(-180);
        mTripBitmapUp = Bitmap.createBitmap(mTripBitmapDown, 0, 0, mTripBitmapDown.getWidth(),
                mTripBitmapDown.getHeight(), ma, true);
        int x = mWidth;
        for (int i = 0; i < MAX_TRIPS; i++) {
            mTrips[i] = new trips(this.getContext(), mTripBitmapUp, mTripBitmapDown, mWidth,
                    mHeight);
            mTrips[i].setX(x);
            x += trips.TRIPS_WIDTH * 4;
        }
    }
    public void onStop(){
        
        isRunning =false;
    }
    void drawTrips(int status) {
        int x = mWidth;
        for (int i = 0; i < MAX_TRIPS; i++) {
            if(status == STATUS_INIT){            
                mTrips[i].setX(x);
                x += trips.TRIPS_WIDTH * 4;  
            }
            mTrips[i].draw(mCanvas,status);
        }
    }

    void drawBg() {
        if (mBg != null) {
            mCanvas.drawBitmap(mBg, null, mPanelRect, null);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("joe", "onSizeChanged w =" + w + " h = " + h + "oldw = " + oldw + "oldh = " + oldh);
        mWidth = w;
        mHeight = h;
        mPanelRect.set(0, 0, w, h);
        mBird = new LeoBird(mContext, mWidth, mHeight);
        createTrips();
        mGameStatus = STATUS_INIT;
        // drawInitObjs();
    }

    void drawLeoBird(int status) {
        if (mBird != null) {
            mBird.draw(mCanvas, status);
        }
    }

    void drawInitObjs() {
        drawBg();
        drawLeoBird(STATUS_INIT);
        drawWellcome();
        drawTrips(STATUS_INIT);
    }

    private void drawWellcome() {
        Paint paint = new Paint();
        paint.setTextSize(28);   
        mCanvas.drawText("看看你能拿多少股票，轻触屏幕开始", 4, 250, paint);
    }
    void doJudge(){     
        for(trips tp :mTrips){
            if(tp.isbirdCrash(mBird.mXpos, mBird.mYpos, mBird.mWidth, mBird.mHeight)&& mGameStatus== STATUS_RUNNING){
                mGameStatus = STATUS_STOP;
                return;
            }
            if(tp.birdCross(mBird.mXpos, mBird.mYpos, mBird.mWidth, mBird.mHeight)){             
                mPoint++;
            }
        }
        
        
    }
    void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                doJudge();
                if (mGameStatus == STATUS_RUNNING) {        
                    drawBg();
                    drawLeoBird(STATUS_RUNNING);
                    drawTrips(STATUS_RUNNING);
                    drawPoints(STATUS_RUNNING);
                } else if (mGameStatus == STATUS_INIT) {
                    drawInitObjs();
                } else if (mGameStatus == STATUS_STOP) {
                    drawBg();
                    drawLeoBird(STATUS_STOP);
                    drawTrips(STATUS_STOP);
                    drawPoints(STATUS_STOP);
                }
            }
            mHolder.unlockCanvasAndPost(mCanvas);
        } catch (java.lang.IllegalStateException e) {
        } catch (java.lang.IllegalArgumentException e){
            
        } 
        finally {
            
        }

    }

    private void drawPoints(int status) {
        Paint paint = new Paint();
        paint.setTextSize(28);
        if(status == STATUS_RUNNING){
        mCanvas.drawText("你得到了: "+mPoint+"千股", 80, 250, paint);
        }
        else if(status == STATUS_STOP){     
            mCanvas.drawText("你完蛋了，只拿到了"+mPoint+"千股，可怜的娃", 0, 250, paint);
            paint.setTextSize(64);
            mCanvas.drawText("不服再来... ", 40, 500, paint);
        }
        
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean down = event.getAction() == MotionEvent.ACTION_DOWN;
        if (mGameStatus == STATUS_INIT) {
            if (down) {
                mGameStatus = STATUS_RUNNING;
                mPoint =0;
                mBird.touch();
            }
        } else if (mGameStatus == STATUS_RUNNING) {
            if (mBird != null && down) {
                mBird.touch();
            }
        }else if(mGameStatus == STATUS_STOP){
             if(down){                
                 mGameStatus = STATUS_INIT;
             }
            
            
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        mRenderThread = new Thread(this);
        isRunning = true;
        mRenderThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        isRunning = false;

    }

    @Override
    public void run() {
        while (isRunning) {
            draw();
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }

    }
}
