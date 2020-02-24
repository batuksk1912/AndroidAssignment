package com.lambton.androidassignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

public class GameBoard extends View{
    private Paint p;
    private List<Point> starField = null;
    private int starAlpha = 80;
    private int starFade = 2;
    private Rect sprite1Bounds = new Rect(0,0,0,0);
    private Rect sprite2Bounds = new Rect(0,0,0,0);
    private Rect sprite3Bounds = new Rect(0,0,0,0);
    private Point sprite1;
    private Point sprite2;
    private Point sprite3;
    private Bitmap bm1 = null;
    private Matrix m = null;
    private Bitmap bm2 = null;
    private Bitmap bm3 = null;
    //Collision flag and point
    private boolean collisionDetected = false;
    private Point lastCollision = new Point(-1,-1);
    private int sprite1Rotation = 0;
    private int sprite2Rotation = 0;
    private int sprite3Rotation = 0;
    private int goUp=1;
    private int goDown=-1;
    int direction=goUp;
    private int goUp2=1;
    private int goDown2=-1;
    int direction2=goUp2;
    private Bitmap background;

    private static final int NUM_OF_STARS = 25;
    //Allow our controller to get and set the sprite positions
    //sprite 1 setter
    synchronized public void setSprite1(int x, int y) {
        sprite1=new Point(x,y);
    }
    //sprite 1 getter
    synchronized public int getSprite1X() {
        return sprite1.x;
    }

    synchronized public int getSprite1Y() {
        return sprite1.y;
    }
    //sprite 2 setter
    synchronized public void setSprite2(int x, int y) {
        sprite2=new Point(x,y);
    }
    //sprite 2 getter
    synchronized public int getSprite2X() {
        return sprite2.x;
    }

    synchronized public int getSprite2Y() {
        return sprite2.y;
    }

    //sprite 3 setter
    synchronized public void setSprite3(int x, int y) {
        sprite3=new Point(x,y);
    }
    //sprite 2 getter
    synchronized public int getSprite3X() {
        return sprite3.x;
    }

    synchronized public int getSprite3Y() {
        return sprite3.y;
    }

    synchronized public void resetStarField() {
        starField = null;
    }
    //expose sprite bounds to controller
    synchronized public int getSprite1Width() {
        return sprite1Bounds.width();
    }

    synchronized public int getSprite1Height() {
        return sprite1Bounds.height();
    }

    synchronized public int getSprite2Width() {
        return sprite2Bounds.width();
    }

    synchronized public int getSprite2Height() {
        return sprite2Bounds.height();
    }

    synchronized public int getSprite3Width() {
        return sprite3Bounds.width();
    }

    synchronized public int getSprite3Height() {
        return sprite3Bounds.height();
    }
    //return the point of the last collision
    synchronized public Point getLastCollision() {
        return lastCollision;
    }
    //return the collision flag
    synchronized public boolean wasCollisionDetected() {
        return collisionDetected;
    }

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        p = new Paint();
        //load our bitmaps and set the bounds for the controller
        sprite1 = new Point(-1,-1);
        sprite2 = new Point(-1,-1);
        sprite3 = new Point(-1,-1);
        //Define a matrix so we can rotate the asteroid
        m = new Matrix();
        p = new Paint();
        bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
        bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.ufo);
        bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.spacestation);
        sprite1Bounds = new Rect(0,0, bm1.getWidth(), bm1.getHeight());
        sprite2Bounds = new Rect(0,0, bm2.getWidth(), bm2.getHeight());
        sprite3Bounds = new Rect(0,0, bm3.getWidth(), bm3.getHeight());

        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_resized);

    }

    synchronized private void initializeStars(int maxX, int maxY) {
        starField = new ArrayList<Point>();
        for (int i=0; i<NUM_OF_STARS; i++) {
            Random r = new Random();
            int x = r.nextInt(maxX-5+1)+5;
            int y = r.nextInt(maxY-5+1)+5;
            starField.add(new Point (x,y));
        }
        collisionDetected = false;
    }

    private boolean checkForCollision(Canvas canvas) {
        if (sprite1.x<0 && sprite2.x<0 && sprite3.x<0 && sprite1.y<0 && sprite2.y<0 && sprite3.y<0) return false;
        Rect r1 = new Rect(sprite1.x, sprite1.y, sprite1.x
                + sprite1Bounds.width(),  sprite1.y + sprite1Bounds.height());
        Rect r2 = new Rect(sprite2.x, sprite2.y, sprite2.x +
                sprite2Bounds.width(), sprite2.y + sprite2Bounds.height());
        Rect r3 = new Rect(sprite3.x, sprite3.y, sprite3.x +
                sprite3Bounds.width(), sprite3.y + sprite3Bounds.height());
        if(r3.intersect(r1) && r3.intersect(r2)) {
            Paint paint = new Paint();

            paint.setColor(Color.YELLOW);
            paint.setTextSize(100);
            canvas.drawText("Collision Detected!", 300, 500, paint);
            lastCollision = new Point(-1,-1);
            return true;
        }
        lastCollision = new Point(-1,-1);
        return false;
    }

    /**
     * Draws current state of the game Canvas.
     */
    private int mBGFarMoveY = 0;
    private int mBGNearMoveY = 0;

    @Override
    synchronized public void onDraw(Canvas canvas) {
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        //bg image here
        canvas.drawBitmap(background,0,0, null);

        mBGFarMoveY = mBGFarMoveY - 1;
        // decrement the near background
        mBGNearMoveY = mBGNearMoveY - 1;
        // calculate the wrap factor for matching image draw
        int newFarY = background.getHeight() - (-mBGFarMoveY);
        // if we have scrolled all the way, reset to start
        if (newFarY <= 0) {
            mBGFarMoveY = 0;
            // only need one draw
            canvas.drawBitmap(background, 0,mBGFarMoveY, null);
        } else {
            // need to draw original and wrap
            canvas.drawBitmap(background, 0,mBGFarMoveY, null);
            canvas.drawBitmap(background, 0,newFarY, null);
        }


        //star field codes
        /*if (starField==null) {
            initializeStars(canvas.getWidth(), canvas.getHeight());
        }
        p.setColor(Color.CYAN);
        p.setAlpha(starAlpha+=starFade);
        if (starAlpha>=252 || starAlpha <=80) starFade=starFade*-1;
        p.setStrokeWidth(5);
        for (int i=0; i<NUM_OF_STARS; i++) {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
        }*/



        if (sprite1.x>=0) {
            m.reset();
            m.postTranslate((float)(sprite1.x), (float)(sprite1.y));
            m.postRotate(sprite1Rotation,
                    (float)(sprite1.x+sprite1Bounds.width()/2.0),
                    (float)(sprite1.y+sprite1Bounds.width()/2.0));
            canvas.drawBitmap(bm1, m, null);
            sprite1Rotation+=5;
            if (sprite1Rotation >= 360) sprite1Rotation=0;
        }
        if (sprite2.x>=0) {
            m.reset();
            m.postTranslate((float)(sprite2.x), (float)(sprite2.y));
            m.postRotate(sprite2Rotation,
                    (float)(sprite2.x+sprite2Bounds.width()/2.0),
                    (float)(sprite2.y+sprite2Bounds.width()/2.0));
            canvas.drawBitmap(bm2, m, null);


            sprite2Rotation+=5*direction;

            if (sprite2Rotation >= 45){

                direction = goDown;
            }
            if (sprite2Rotation <= -45){

                direction = goUp;
            }



        }
        if (sprite3.x>=0) {
            boolean flag = false;
            // GRAVITY LOGIC
            if(sprite3.y <= canvas.getHeight() - (sprite3Bounds.height())){
                //KEEP APPLYING FORCE UNTIL IT HITS THE GROUND...
                sprite3.y+=5;
            }
            if(sprite3.y > canvas.getHeight() - (sprite3Bounds.height())) {
                flag = true;
            }
            m.reset();
            m.postTranslate((float)(sprite3.x), (float)(sprite3.y));
            //sprite3Rotation = 45;
            m.postRotate(sprite3Rotation,
                    (float)(sprite3.x+sprite3Bounds.width()/2.0),
                    (float)(sprite3.y+sprite3Bounds.width()/2.0));
            canvas.drawBitmap(bm3, m, null);
           if (flag != true) {
               sprite3Rotation+=5*direction2;

               if (sprite3Rotation >= 45){

                   direction2 = goDown2;
               }
               if (sprite3Rotation <= -45){

                   direction2 = goUp2;
               }
           }
        }
        //The last order of business is to check for a collision
        collisionDetected = checkForCollision(canvas);
        if (collisionDetected ) {
            //if there is one lets draw a red X
            p.setColor(Color.RED);
            p.setAlpha(255);
            p.setStrokeWidth(5);
            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
                    lastCollision.x + 5, lastCollision.y + 5, p);
            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
                    lastCollision.x - 5, lastCollision.y + 5, p);
        }
    }
}