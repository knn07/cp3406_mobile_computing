package com.example.kongwenyao.educationalgame.mGameActivity;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by kongwenyao on 1/6/18.
 */

public class GameThread extends Thread {

    public static final int MAX_FPS = 30;
    private final SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;

    public GameThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long waitTime, startTime, timeTaken;
        long targetTime = 1000/ MAX_FPS;
        long totalTime = 0;
        int frameCount = 0;
        double averageFPS;
        Canvas canvas;

        while (running) {
            startTime = System.nanoTime(); //Current running JVM time source
            canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas(); //Lock canvas to prevent changes from elsewhere while drawing
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas); //Unlock if canvas is not null
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            timeTaken = (System.nanoTime() - startTime) / 1000000; //Time taken per frame
            waitTime = targetTime - timeTaken;
            try {
                if (waitTime > 0) {
                    sleep(waitTime); //Capping leftover wait time
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime; //Total time took to update all 30 frames in one second
            frameCount++;

            if (frameCount == 30) {
                averageFPS = (double) (1000 / ((totalTime / frameCount) / 1000000));

                //Reset values
                totalTime = 0;
                frameCount = 0;

                //Report average FPS
                //System.out.println("Average FPS: " + averageFPS);
            }
        }


    }
}
