package com.example.thesnakeygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    //list of snake point/ snake length
    private final List<Snakepoints> snakepointsList = new ArrayList<>();
    private SurfaceView surfaceView;
    private TextView scoreTV;

    //surface holder to draw snake on surface
    private SurfaceHolder surfaceHolder;

    //snake moving position
    private String movingPosition = "right";

    // score
    private int score = 0;

    // snake size
    private static final int pointSize = 28;

    //default snake tale
    private static final int defaultTalePoints = 3;

    //snake color
    private static final int snakeColor = Color.YELLOW;

    //snake moving speed
    private static final int snakeMovingSpeed = 800;


    private int positionX = 0, positionY = 0;

    private Timer timer;

    private Canvas canvas = null;

    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting surfaceview and score textfile from xml files
        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.scoreTV);


        // getting imagebuttons from xml file
        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);


        // adding callback to surfaceview
        surfaceView.getHolder().addCallback(this);


        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (movingPosition.equals("bottom")) {
                    movingPosition = "top";
                }
            }
        });


        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (movingPosition.equals("left")) {

                    movingPosition = "right";

                }

            }
        });


        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (movingPosition.equals("right")) {
                    movingPosition = "left";
                }


            }
        });


        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!movingPosition.equals("top")) {

                    movingPosition = "bottom";

                }

            }
        });


    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        // when surface is created then get surfaceholder from it and assign to surfaceholder
        this.surfaceHolder = surfaceHolder;

        // init data for the snake
        init();


    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }


    private void init() {

        //clear snake points / snake points
        snakepointsList.clear();


        //set default score as 0
        scoreTV.setText("0");

        // make score 0
        score = 0;

        // setting default moving position
        movingPosition = "right";

        //default starting point on the screen
        int startPositionX = (pointSize) * defaultTalePoints;

        //the snake default length/points
        for (int i = 0; i < defaultTalePoints; i++) {

            Snakepoints snakePoints = new Snakepoints(startPositionX, pointSize);
            snakepointsList.add(snakePoints);


            startPositionX = startPositionX - (pointSize * 2);


        }

        addPoints();


        moveSnake();
    }

    private void addPoints() {

        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        if ((randomXPosition % 2) != 0) {
            randomXPosition = randomXPosition + 1;
        }

        if ((randomYPosition % 2) != 0) {
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;

    }


    private void moveSnake() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                int headPositionX = snakepointsList.get(0).getPositionX();
                int headPositionY = snakepointsList.get(0).getPositionY();

                if (headPositionX == positionX && positionY == headPositionY) {

                    growSnake();

                    addPoints();
                }

                switch (movingPosition) {
                    case "right":
                        snakepointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakepointsList.get(0).setPositionY(headPositionY);
                        break;


                    case "left":
                        snakepointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakepointsList.get(0).setPositionY(headPositionY);
                        break;

                    case "top":
                        snakepointsList.get(0).setPositionX(headPositionX);
                        snakepointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;

                    case "bottom":
                        snakepointsList.get(0).setPositionX(headPositionX);
                        snakepointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }

                if (checkGameOver(headPositionX, headPositionY)){

                    timer.purge();
                    timer.cancel();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Your Score = " + score);
                    builder.setTitle("Game Over");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            init();
                        }
                    });


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                } else {

                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                    canvas.drawCircle(snakepointsList.get(0).getPositionX(), snakepointsList.get(0).getPositionY(), pointSize, createPointColor());

                    canvas.drawCircle(positionX, positionY, pointSize, createPointColor());

                    for (int i = 1; i < snakepointsList.size(); i++){

                        int getTempPositionX = snakepointsList.get(i).getPositionX();
                        int getTempPositionY = snakepointsList.get(i).getPositionY();

                        snakepointsList.get(i).setPositionX(headPositionX);
                        snakepointsList.get(i).setPositionY(headPositionY);
                        canvas.drawCircle(snakepointsList.get(i).getPositionX(), snakepointsList.get(i).getPositionY(), pointSize, createPointColor());

                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;

                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }

    private void growSnake(){

        Snakepoints snakepoints = new Snakepoints(0,0);

        snakepointsList.add(snakepoints);

        score++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    private boolean checkGameOver(int headPositionX, int headPositionY) {

        boolean gameOver = false;

        if (snakepointsList.get(0).getPositionX() < 0 ||
                snakepointsList.get(0).getPositionY() < 0 ||
                snakepointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakepointsList.get(0).getPositionY() >= surfaceView.getHeight())
        {

            gameOver = true;
        }
        else{

            for (int i = 1; i < snakepointsList.size(); i++){

                if (headPositionX == snakepointsList.get(i).getPositionX() &&
                        headPositionY == snakepointsList.get(i).getPositionY()){
                    gameOver = true;
                    break;

                }
            }

        }

        return gameOver;

    }

    private Paint createPointColor() {

        if (pointColor == null) {

            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);

        }
        return pointColor;

    }
}