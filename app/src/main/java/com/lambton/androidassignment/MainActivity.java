package com.lambton.androidassignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.graphics.Point;
import java.util.Random;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Handler frame = new Handler();
    //Velocity includes the speed and the direction of our sprite motion
    private Point sprite1Velocity;
    private Point sprite2Velocity;
    private Point sprite3Velocity;
    private int sprite1MaxX;
    private int sprite1MaxY;
    private int sprite2MaxX;
    private int sprite2MaxY;
    private int sprite3MaxX;
    private int sprite3MaxY;
    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    private static final int FRAME_RATE = 20; //50 frames per second
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler h = new Handler();
        ((Button)findViewById(R.id.the_button)).setOnClickListener(this);
        //We can't initialize the graphics immediately because the layout manager
        //needs to run first, thus call back in a sec.
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
    }

    private Point getRandomVelocity() {
        Random r = new Random();
        int min = 20;
        int max = 35;
        int x = r.nextInt(max-min+1)+min;
        int y = r.nextInt(max-min+1)+min;
        return new Point (x,y);
    }
    private Point getRandomPoint() {
        Random r = new Random();
        int minX = 0;
        int maxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width();
        int x = 0;
        int minY = 0;
        int maxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Height();
        int y = 0;
        x = r.nextInt(maxX-minX+1)+minX;
        y = r.nextInt(maxY-minY+1)+minY;
        return new Point (x,y);
    }
    synchronized public void initGfx() {
        ((GameBoard)findViewById(R.id.the_canvas)).resetStarField();
        Point p1, p2, p3;
        do {
            p1 = getRandomPoint();
            p2 = getRandomPoint();
            p3 = new Point(findViewById(R.id.the_canvas).getWidth()/2 -100,findViewById(R.id.the_canvas).getHeight()/2-100);
        } while (Math.abs(p1.x - p2.x) <
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width());
        ((GameBoard)findViewById(R.id.the_canvas)).setSprite1(p1.x, p1.y);
        ((GameBoard)findViewById(R.id.the_canvas)).setSprite2(p2.x, p2.y);
        ((GameBoard)findViewById(R.id.the_canvas)).setSprite3(p3.x, p3.y);
        //Give the asteroid a random velocity
        sprite1Velocity = getRandomVelocity();
        //Fix the ship velocity at a constant speed for now
        sprite2Velocity = getRandomVelocity();
        sprite3Velocity = new Point(1,1);
        //Set our boundaries for the sprites
        sprite1MaxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width();
        sprite1MaxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Height();
        sprite2MaxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Width();
        sprite2MaxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Height();
        sprite3MaxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite3Width();
        sprite3MaxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite3Height();
        ((Button)findViewById(R.id.the_button)).setEnabled(true);
        frame.removeCallbacks(frameUpdate);
        ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }
    @Override
    synchronized public void onClick(View v) {
        initGfx();
    }
    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            //Before we do anything else check for a collision
            if (((GameBoard)findViewById(R.id.the_canvas)).wasCollisionDetected()) {
                Point collisionPoint =
                        ((GameBoard)findViewById(R.id.the_canvas)).getLastCollision();
                if (collisionPoint.x>=0) {
                    //collision happened.
                }
                //turn off the animation until reset gets pressed
                return;
            }
            frame.removeCallbacks(frameUpdate);
            Point sprite1 = new Point
                    (((GameBoard)findViewById(R.id.the_canvas)).getSprite1X(),
                            ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Y()) ;
            Point sprite2 = new Point
                    (((GameBoard)findViewById(R.id.the_canvas)).getSprite2X(),
                            ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Y());
            Point sprite3 = new Point
                    (((GameBoard)findViewById(R.id.the_canvas)).getSprite3X(),
                            ((GameBoard)findViewById(R.id.the_canvas)).getSprite3Y());
            sprite1.x = sprite1.x + sprite1Velocity.x;
            if (sprite1.x > sprite1MaxX || sprite1.x < 5) {
                sprite1Velocity.x *= -1;
            }
            sprite1.y = sprite1.y + sprite1Velocity.y;
            if (sprite1.y > sprite1MaxY || sprite1.y < 5) {
                sprite1Velocity.y *= -1;
            }
            sprite2.x = sprite2.x + sprite2Velocity.x;
            if (sprite2.x > sprite2MaxX || sprite2.x < 5) {
                sprite2Velocity.x *= -1;
            }
            sprite2.y = sprite2.y + sprite2Velocity.y;
            if (sprite2.y > sprite2MaxY || sprite2.y < 5) {
                sprite2Velocity.y *= -1;
            }
            ((GameBoard)findViewById(R.id.the_canvas)).setSprite1(sprite1.x,
                    sprite1.y);
            ((GameBoard)findViewById(R.id.the_canvas)).setSprite2(sprite2.x, sprite2.y);
            ((GameBoard)findViewById(R.id.the_canvas)).setSprite3(sprite3.x, sprite3.y);
            ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };
}
