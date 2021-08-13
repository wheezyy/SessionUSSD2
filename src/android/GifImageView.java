package com.ramymokako.plugin.ussd.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GifImageView extends View {
    private Context mContext;
    private int mHeight;
    private InputStream mInputStream;
    private Movie mMovie;
    private long mStart;
    private int mWidth;

    public GifImageView(Context context) {
        super(context);
        this.mContext = context;
    }

    public GifImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GifImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        if (attributeSet.getAttributeName(1).equals("background")) {
            setGifImageResource(Integer.parseInt(attributeSet.getAttributeValue(1).substring(1)));
        }
    }

    private void init() {
        setFocusable(true);
        Movie decodeStream = Movie.decodeStream(this.mInputStream);
        this.mMovie = decodeStream;
        this.mWidth = decodeStream.width();
        this.mHeight = this.mMovie.height();
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(this.mWidth, this.mHeight);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (this.mStart == 0) {
            this.mStart = uptimeMillis;
        }
        Movie movie = this.mMovie;
        if (movie != null) {
            int duration = movie.duration();
            if (duration == 0) {
                duration = 1000;
            }
            this.mMovie.setTime((int) ((uptimeMillis - this.mStart) % ((long) duration)));
            this.mMovie.draw(canvas, 0.0f, 0.0f);
            invalidate();
        }
    }

    public void setGifImageResource(int i) {
        this.mInputStream = this.mContext.getResources().openRawResource(i);
        init();
    }

    public void setGifImageUri(Uri uri) {
        try {
            this.mInputStream = this.mContext.getContentResolver().openInputStream(uri);
            init();
        } catch (FileNotFoundException unused) {
            Log.e("GIfImageView", "File not found");
        }
    }
}

// //package com.romellfudi.ussdlibrary;

// package com.ramymokako.plugin.ussd.android;

// import android.content.Context;
// import android.graphics.Canvas;
// import android.graphics.Movie;
// import android.net.Uri;
// import android.os.SystemClock;
// import android.util.AttributeSet;
// import android.util.Log;
// import android.view.View;

// import java.io.FileNotFoundException;
// import java.io.InputStream;

// public class GifImageView extends View {

//     private InputStream mInputStream;
//     private Movie mMovie;
//     private int mWidth, mHeight;
//     private long mStart;
//     private Context mContext;

//     public GifImageView(Context context) {
//         super(context);
//         this.mContext = context;
//     }

//     public GifImageView(Context context, AttributeSet attrs) {
//         this(context, attrs, 0);
//     }

//     public GifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
//         super(context, attrs, defStyleAttr);
//         this.mContext = context;
//         if (attrs.getAttributeName(1).equals("background")) {
//             int id = Integer.parseInt(attrs.getAttributeValue(1).substring(1));
//             setGifImageResource(id);
//         }
//     }

//     private void init() {
//         setFocusable(true);
//         mMovie = Movie.decodeStream(mInputStream);
//         mWidth = mMovie.width();
//         mHeight = mMovie.height();

//         requestLayout();
//     }

//     @Override
//     protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//         setMeasuredDimension(mWidth, mHeight);
//     }

//     @Override
//     protected void onDraw(Canvas canvas) {

//         long now = SystemClock.uptimeMillis();

//         if (mStart == 0) {
//             mStart = now;
//         }

//         if (mMovie != null) {

//             int duration = mMovie.duration();
//             if (duration == 0) {
//                 duration = 1000;
//             }

//             int relTime = (int) ((now - mStart) % duration);

//             mMovie.setTime(relTime);

//             mMovie.draw(canvas, 0, 0);
//             invalidate();
//         }
//     }

//     public void setGifImageResource(int id) {
//         mInputStream = mContext.getResources().openRawResource(id);
//         init();
//     }

//     public void setGifImageUri(Uri uri) {
//         try {
//             mInputStream = mContext.getContentResolver().openInputStream(uri);
//             init();
//         } catch (FileNotFoundException e) {
//             Log.e("GIfImageView", "File not found");
//         }
//     }
// }
