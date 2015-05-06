
package com.example.mysmartbird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;

public class LeoBird {

    int mXpos;

    int mYpos;

    int mWidth;

    int mHeight;

    Bitmap mBitmap;

    int mPanelWidth;

    int mPandleHeight;

    Context mContext;

    RectF mRect = new RectF();

    long acc = 3; // x =vot +2.5*acc*t^2

    long v_down = 200; // ypos = v0*(t_now - t_last)+0.5*acc*(t_now-t_last)

    long mLastTime = -1;

    long up_duration = 1500;

    long max_v_up = (long) (-1.6 * v_down);

    double v_up = 0;

    long v_up_time = 0;
    public static int BIRD_WIDTH = 80;

    public LeoBird(Context context, int panelWidth, int panelHeight) {
        mPanelWidth = panelWidth;
        mPandleHeight = panelHeight;
        mContext = context;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leo_bird);
        mWidth = BIRD_WIDTH;// mBitmap.getWidth();
        mHeight = 80;// (int)( mWidth*(panelWidth*1.0f/panelHeight));
        mXpos = mPanelWidth /4;
        mYpos = mPandleHeight / 2;

    }

    public void draw(Canvas canvas,int status) {
        if(status !=0){ //running stopped
            getNextY();
        }else{
            mYpos =mPandleHeight / 2;
            mLastTime = -1;
        }
        mRect.set(mXpos, mYpos, mXpos + mWidth, mYpos + mHeight);
        canvas.drawBitmap(mBitmap, null, mRect, null);
    }

    public void setX(int x) {
        mXpos = x;
    }

    public void setY(int y) {
        mYpos = y;
    }

    private void getNextY() {
        long now = SystemClock.uptimeMillis();
        double move = 0;
        if (mLastTime == -1) {
            mLastTime = now;
        }
        double duration = 0.001 * (now - mLastTime);
        v_up = getVUp(now);
        move = (v_down + v_up) * (duration); // v_down +acc * duration *
                                               // duration;//v_start * duration
        mYpos += move;                                    // + 0.5 * acc * duration *
                                               // duration;
        if (mYpos >= (mPandleHeight - mHeight)) {
            mYpos = (mPandleHeight - mHeight);
        }
        if (mYpos <= 0) {
            mYpos = 0;
        }
        mLastTime = now;
    }

    private double getVUp(long now) {
        double curr = (now - v_up_time);
        double v = 0;

        if (curr >= 0 && curr <= up_duration) {
            // v(t)= -(vmax/tmax )*t +vmax

            v = -(1.0f * max_v_up / up_duration) * curr + max_v_up;
            return v;
        }
        return 0;
    }

    public void touch() {
        long now = SystemClock.uptimeMillis();
        v_up_time = now;
    }

}
